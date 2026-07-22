use anyhow::Result;
use std::fs;

#[derive(Debug, Clone, Copy, PartialEq)]
pub enum PatchMode {
    Independent = 0,
    KsuCompatible = 1,
}

const KSU_MAGIC: &[u8] = b"KernelSU";
const NEKOSU_MAGIC: &[u8] = b"NekoSU";

pub fn patch_boot_image(
    boot_image_path: &str,
    output_path: &str,
    mode: PatchMode,
) -> Result<()> {
    let boot_data = fs::read(boot_image_path)?;
    let parsed = parse_boot_image(&boot_data)?;
    let kernel_data = &boot_data[parsed.kernel_offset..parsed.kernel_offset + parsed.kernel_size];

    let patched_kernel = match mode {
        PatchMode::KsuCompatible => patch_kernel_ksu_compatible(kernel_data)?,
        PatchMode::Independent => patch_kernel_independent(kernel_data)?,
    };

    let mut output = boot_data.clone();
    output[parsed.kernel_offset..parsed.kernel_offset + parsed.kernel_size]
        .copy_from_slice(&patched_kernel);
    fs::write(output_path, &output)?;

    Ok(())
}

fn patch_kernel_ksu_compatible(kernel_data: &[u8]) -> Result<Vec<u8>> {
    let mut result = kernel_data.to_vec();
    let hook_offset = find_hook_point(kernel_data)?;
    inject_ksu_hook(&mut result, hook_offset)?;
    let magic_offset = result.len().saturating_sub(KSU_MAGIC.len() + 4);
    if magic_offset < result.len() {
        result[magic_offset..magic_offset + KSU_MAGIC.len()].copy_from_slice(KSU_MAGIC);
    }
    Ok(result)
}

fn patch_kernel_independent(kernel_data: &[u8]) -> Result<Vec<u8>> {
    let mut result = kernel_data.to_vec();
    let hook_offset = find_hook_point(kernel_data)? + 4;
    inject_nekosu_hook(&mut result, hook_offset)?;
    let magic_offset = result.len().saturating_sub(NEKOSU_MAGIC.len() + 4);
    if magic_offset < result.len() {
        result[magic_offset..magic_offset + NEKOSU_MAGIC.len()].copy_from_slice(NEKOSU_MAGIC);
    }
    Ok(result)
}

fn find_hook_point(kernel_data: &[u8]) -> Result<usize> {
    let pattern = [0x53u8, 0x00, 0x00, 0x38];
    for i in 0..kernel_data.len().saturating_sub(pattern.len()) {
        if &kernel_data[i..i + pattern.len()] == &pattern {
            return Ok(i);
        }
    }
    Err(anyhow::anyhow!("Hook point not found in kernel"))
}

fn inject_ksu_hook(kernel: &mut [u8], offset: usize) -> Result<()> {
    if offset + 4 <= kernel.len() {
        kernel[offset] = 0x14;
        kernel[offset + 1] = 0x00;
        kernel[offset + 2] = 0x00;
        kernel[offset + 3] = 0x00;
    }
    Ok(())
}

fn inject_nekosu_hook(kernel: &mut [u8], offset: usize) -> Result<()> {
    if offset + 4 <= kernel.len() {
        kernel[offset] = 0x14;
        kernel[offset + 1] = 0x04;
        kernel[offset + 2] = 0x00;
        kernel[offset + 3] = 0x00;
    }
    Ok(())
}

pub fn is_ksu_kernel(kernel_data: &[u8]) -> bool {
    for i in 0..kernel_data.len().saturating_sub(KSU_MAGIC.len()) {
        if &kernel_data[i..i + KSU_MAGIC.len()] == KSU_MAGIC {
            return true;
        }
    }
    false
}

pub fn get_ksu_version(kernel_data: &[u8]) -> i32 {
    for i in 0..kernel_data.len().saturating_sub(KSU_MAGIC.len() + 4) {
        if &kernel_data[i..i + KSU_MAGIC.len()] == KSU_MAGIC {
            let v = &kernel_data[i + KSU_MAGIC.len()..i + KSU_MAGIC.len() + 4];
            return i32::from_le_bytes([v[0], v[1], v[2], v[3]]);
        }
    }
    0
}

struct BootImageInfo {
    kernel_offset: usize,
    kernel_size: usize,
}

fn parse_boot_image(data: &[u8]) -> Result<BootImageInfo> {
    if data.len() < 4096 || &data[0..8] != b"ANDROID!" {
        return Err(anyhow::anyhow!("Invalid boot image header"));
    }
    let kernel_size =
        u32::from_le_bytes([data[8], data[9], data[10], data[11]]) as usize;
    let page_size =
        u32::from_le_bytes([data[36], data[37], data[38], data[39]]) as usize;
    Ok(BootImageInfo {
        kernel_offset: page_size,
        kernel_size,
    })
}

#[cfg(target_os = "android")]
mod jni {
    use super::*;
    use jni::objects::{JClass, JString};
    use jni::JNIEnv;
    use jni::sys::jint;

    #[no_mangle]
    pub extern "system" fn Java_me_weishu_nekosu_ui_util_NekoBootPatcher_nativePatchBoot(
        mut env: JNIEnv,
        _class: JClass,
        boot_path: JString,
        output_path: JString,
        mode: jint,
    ) -> jint {
        let boot_path: String = env.get_string(&boot_path).unwrap().into();
        let output_path: String = env.get_string(&output_path).unwrap().into();
        let patch_mode = if mode == 1 {
            PatchMode::KsuCompatible
        } else {
            PatchMode::Independent
        };
        match patch_boot_image(&boot_path, &output_path, patch_mode) {
            Ok(()) => 0,
            Err(e) => {
                eprintln!("Boot patch error: {}", e);
                1
            }
        }
    }

    #[no_mangle]
    pub extern "system" fn Java_me_weishu_nekosu_ui_util_NekoBootPatcher_nativeIsKsuKernel(
        _env: JNIEnv,
        _class: JClass,
    ) -> jint {
        if me_weishu_nekosu_Natives::ksu_version() > 0 {
            1
        } else {
            0
        }
    }

    #[no_mangle]
    pub extern "system" fn Java_me_weishu_nekosu_ui_util_NekoBootPatcher_nativeGetKsuVersion(
        _env: JNIEnv,
        _class: JClass,
    ) -> jint {
        me_weishu_nekosu_Natives::ksu_version()
    }
}
