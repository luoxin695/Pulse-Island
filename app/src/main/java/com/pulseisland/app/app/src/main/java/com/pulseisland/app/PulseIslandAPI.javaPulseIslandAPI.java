package com.pulseisland.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Environment;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PulseIslandAPI {

    private final Context context;
    private final SharedPreferences prefs;
    
    // 原始签名哈希（编译时填入，你需要用你自己的签名生成一次然后替换这里）
    private static final String ORIGINAL_SIGNATURE_HASH = "YOUR_ORIGINAL_SIGNATURE_HASH";
    // 原始包名
    private static final String ORIGINAL_PACKAGE_NAME = "com.pulseisland.app";
    // 原始文件哈希列表的存储键
    private static final String PREFS_ORIGINAL_FILE_HASHES = "original_file_hashes";

    public PulseIslandAPI(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences("pulse_island_prefs", Context.MODE_PRIVATE);
    }

    // ========== 1. 签名校验 ==========
    public boolean checkSignature() {
        try {
            PackageInfo packageInfo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
                Signature[] signatures = packageInfo.signingInfo.getApkContentsSigners();
                if (signatures != null && signatures.length > 0) {
                    String hash = sha256(signatures[0].toByteArray());
                    return ORIGINAL_SIGNATURE_HASH.equals(hash);
                }
            } else {
                packageInfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), PackageManager.GET_SIGNATURES);
                Signature[] signatures = packageInfo.signatures;
                if (signatures != null && signatures.length > 0) {
                    String hash = sha256(signatures[0].toByteArray());
                    return ORIGINAL_SIGNATURE_HASH.equals(hash);
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    // ========== 2. 包名校验 ==========
    public boolean checkPackageName() {
        return ORIGINAL_PACKAGE_NAME.equals(context.getPackageName());
    }

    // ========== 3. 完整性校验（所有文件） ==========
    public boolean checkIntegrity() {
        try {
            // 获取已存储的原始哈希列表
            String storedHashes = prefs.getString(PREFS_ORIGINAL_FILE_HASHES, null);
            if (storedHashes == null) {
                // 首次运行，生成并存储原始哈希
                String currentHashes = generateAllFileHashes();
                prefs.edit().putString(PREFS_ORIGINAL_FILE_HASHES, currentHashes).apply();
                return true;
            }
            // 比对当前哈希与存储的原始哈希
            String currentHashes = generateAllFileHashes();
            return storedHashes.equals(currentHashes);
        } catch (Exception e) {
            return false;
        }
    }

    // 生成APK内所有文件的哈希
    private String generateAllFileHashes() {
        StringBuilder sb = new StringBuilder();
        try {
            String apkPath = context.getPackageCodePath();
            // 遍历assets目录
            String[] assetsFiles = context.getAssets().list("");
            if (assetsFiles != null) {
                for (String file : assetsFiles) {
                    sb.append(file).append(":").append(hashAssetFile(file)).append(";");
                }
            }
            // 记录APK本身的哈希
            sb.append("apk:").append(hashFile(apkPath)).append(";");
        } catch (Exception e) {
            return "";
        }
        return sb.toString();
    }

    private String hashAssetFile(String fileName) {
        try {
            byte[] data = new byte[context.getAssets().open(fileName).available()];
            context.getAssets().open(fileName).read(data);
            return sha256(data);
        } catch (Exception e) {
            return "error";
        }
    }

    private String hashFile(String path) {
        try {
            File file = new File(path);
            byte[] data = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(data);
            fis.close();
            return sha256(data);
        } catch (Exception e) {
            return "error";
        }
    }

    // ========== 4. 恶意文件扫描 ==========
    public boolean scanMaliciousFiles() {
        List<String> suspicious = new ArrayList<>();
        
        // 检查应用私有目录下是否有不该存在的文件
        File filesDir = context.getFilesDir();
        scanDirectory(filesDir, suspicious);
        
        // 检查外部存储是否有可疑文件关联
        File externalDir = context.getExternalFilesDir(null);
        i
