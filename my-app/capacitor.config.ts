import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
	appId: 'com.frpc_capacitor.app',
	appName: 'frpc-capacitor',
	webDir: 'build',
	android: {
		buildOptions: {
			releaseType: 'APK'
		}
	}
};

export default config;
