package com.frpc_capacitor.app;

import com.getcapacitor.BridgeActivity;

import android.content.Context;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class MainActivity extends BridgeActivity {
    private static void prepare(Context context) throws IOException {
        var aInfo = context.getApplicationInfo();
        File binFile = new File(aInfo.nativeLibraryDir, "libfrpc.so");
        FrpcPlugin.Init(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            prepare(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        registerPlugin(FrpcPlugin.class);
        super.onCreate(savedInstanceState);
    }
}