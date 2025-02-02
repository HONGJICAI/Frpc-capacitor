import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';
import { copyFileSync } from 'fs';

export default defineConfig({
	plugins: [
		sveltekit() /* other plugins */,
		// copy app.html to index.html in npm build
		{
			name: 'copy-app-html',
			apply: 'build',
			closeBundle: async () => {
				copyFileSync('build/app.html', 'build/index.html');
			}
		}
	]
});
