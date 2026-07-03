package com.pulseisland.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

/**
 * 无障碍管理器安装引导
 * 
 * 仅在 Pulse Island API 首次校验时触发一次
 * 负责：
 * 1. 检测无障碍管理器是否已安装
 * 2. 引导下载
 * 3. 引导授权（自启动 + 电池无限制 + 锁定无障碍权限）
 */
public class AccessibilityManagerInstaller {

    // 无障碍管理器的包名（后面替换为实际包名）
    private static final String MANAGER_PACKAGE = "com.pulseisland.accessibilitymanager";
    // 下载地址（后面替换为实际地址）
    private static final String MANAGER_DOWNLOAD_URL = "https://github.com/yourrepo/accessibility-manager/releases/latest";

    private Activity activity;
    private SharedPreferences prefs;
    private Runnable onComplete;
    private Runnable onFailed;

    public AccessibilityManagerInstaller(Activity activity) {
        this.activity = activity;
        this.prefs = activity.getSharedPreferences("pulse_island_prefs", Context.MODE_PRIVATE);
    }

    /**
     * 开始检测，如果已安装且权限齐全则直接回调 onComplete
     */
    public void checkAndInstall(Runnable onComplete, Runnable onFailed) {
        this.onComplete = onComplete;
        this.onFailed = onFailed;

        if (isManagerInstalled() && isManagerPermissionsGranted()) {
            // 已安装且权限齐全，直接通过
            if (onComplete != null) onComplete.run();
            return;
        }

        if (!isManagerInstalled()) {
            showDownloadDialog();
        } else {
            showPermissionGuide();
        }
    }

    /**
     * 检查无障碍管理器是否已安装
     */
    private boolean isManagerInstalled() {
        try {
            activity.getPackageManager().getPackageInfo(MANAGER_PACKAGE, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 检查无障碍管理器是否已获得必要权限
     */
    private boolean isManagerPermissionsGranted() {
        // 检查自启动权限（简化判断，实际各厂商不同）
        boolean autoStart = prefs.getBoolean("manager_auto_start_granted", false);
        // 检查电池无限制
        boolean batteryOptimization = isIgnoringBatteryOptimizations(MANAGER_PACKAGE);
        // 检查无障碍权限是否已锁定
        boolean accessibilityLocked = prefs.getBoolean("manager_accessibility_locked", false);

        return autoStart && batteryOptimization && accessibilityLocked;
    }

    private boolean isIgnoringBatteryOptimizations(String packageName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
            return pm.isIgnoringBatteryOptimizations(packageName);
        }
        return true;
    }

    /**
     * 第一步：下载引导对话框
     */
    private void showDownloadDialog() {
        new AlertDialog.Builder(activity)
                .setTitle("Install Accessibility Manager")
                .setMessage(
                        "Pulse Island requires the \"Accessibility Manager\" app to securely manage accessibility permissions.\n\n" +
                        "Without it, the security layer cannot operate and the app will close.\n\n" +
                        "Please download and install it to continue."
                )
                .setCancelable(false)
                .setPositiveButton("Download", (dialog, which) -> {
                    openDownloadLink();
                    // 下载后引导下一步
                    showPostInstallGuide();
                })
                .setNegativeButton("Exit", (dialog, which) -> {
                    if (onFailed != null) onFailed.run();
                })
                .show();
    }

    /**
     * 打开下载链接
     */
    private void openDownloadLink() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MANAGER_DOWNLOAD_URL));
            activity.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(activity, "Unable to open download link", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 第二步：安装后引导授权
     */
    private void showPostInstallGuide() {
        new AlertDialog.Builder(activity)
                .setTitle("Setup Accessibility Manager")
                .setMessage(
                        "After installation, please complete these steps:\n\n" +
                        "1. Grant \"Auto-start\" permission to the Accessibility Manager\n" +
                        "2. Set battery usage to \"Unrestricted\"\n" +
                        "3. Open the Accessibility Manager and LOCK Pulse Island's accessibility permission\n\n" +
                        "This ensures the security layer remains active at all times."
                )
                .setCancelable(false)
                .setPositiveButton("Open Manager", (dialog, which) -> {
                    openManager();
                })
                .setNegative
