package com.frpc_capacitor.app;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@CapacitorPlugin(name = "Frpc")
public class FrpcPlugin extends Plugin {
    static String BinFilePath = null;
    static String ConfigDirPath = null;
    static File config = null;
    static File log = null;

    public static void Init(Context context) throws IOException {
        var aInfo = context.getApplicationInfo();
        BinFilePath = new File(aInfo.nativeLibraryDir, "libfrpc.so").getAbsolutePath();
        ConfigDirPath = context.getFilesDir().getAbsolutePath();

        config = new File(ConfigDirPath, "frpc.toml");
        log = new File(ConfigDirPath, "frpc.log");
        if (!config.exists()) {
            try (InputStream is = context.getAssets().open("frpc.default.toml");
                    FileOutputStream fos = new FileOutputStream(config)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    is.transferTo(fos);
                } else {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
        }
    }

    @PluginMethod()
    public void getConfig(PluginCall call) {
        String value = null;
        try {
            if (config.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    value = new String(java.nio.file.Files.readAllBytes(config.toPath()));
                } else {
                    java.io.FileInputStream fis = new java.io.FileInputStream(config);
                    byte[] data = new byte[(int) config.length()];
                    fis.read(data);
                    fis.close();
                    value = new String(data);
                }
            }
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
        JSObject ret = new JSObject();
        ret.put("config", value);
        call.resolve(ret);
    }

    @PluginMethod()
    public void updateConfig(PluginCall call) {
        String value = call.getString("config");
        try {
            java.nio.file.Files.write(config.toPath(), value.getBytes());
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
        JSObject ret = new JSObject();
        ret.put("status", 200);
        call.resolve(ret);
    }

    @PluginMethod()
    public void start(PluginCall call) {
        try {
            Intent serviceIntent = new Intent(getContext(), FrpcService.class);
            serviceIntent.putExtra("binFilePath", BinFilePath);
            serviceIntent.putExtra("configPath", config.getAbsolutePath());
            serviceIntent.putExtra("logPath", log.getAbsolutePath());

            getContext().startForegroundService(serviceIntent);

            JSObject ret = new JSObject();
            ret.put("status", 200);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    @PluginMethod()
    public void stop(PluginCall call) {
        getContext().stopService(new Intent(getContext(), FrpcService.class));
        JSObject ret = new JSObject();
        ret.put("status", 200);
        call.resolve(ret);
    }

    @PluginMethod()
    public void getLog(PluginCall call) {
        String value = null;
        try {
            if (log.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    value = new String(java.nio.file.Files.readAllBytes(log.toPath()));
                } else {
                    java.io.FileInputStream fis = new java.io.FileInputStream(log);
                    byte[] data = new byte[(int) log.length()];
                    fis.read(data);
                    fis.close();
                    value = new String(data);
                }
            }
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
        JSObject ret = new JSObject();
        ret.put("log", value);
        call.resolve(ret);
    }
}