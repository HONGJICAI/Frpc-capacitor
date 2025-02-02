# FRPC-Capacitor

This is a frpc mobile client created by capacitor framework. Currently Android implementation is release.

# Build

- Download the required frpc binary file from (github release)[https://github.com/fatedier/frp/releases] and save the binary file as `frpc-capacitor\my-app\android\app\src\main\jniLibs\<platform>\libfrpc.so`

- Build frontend by below command

```
cd my-app
npm run install
npm run build
npx cap sync
```

- Build Android by Android Studio

# FAQ

* Program works well on emulator, but didn't work on mobile

You can try to disable the backgroud battry control in application setting.