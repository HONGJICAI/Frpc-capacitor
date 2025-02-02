<script lang="ts">
	import { AppBar, Tabs, Switch } from '@skeletonlabs/skeleton-svelte';

	import IconScrollText from 'lucide-svelte/icons/scroll-text';
	import IconSettings from 'lucide-svelte/icons/settings';
	import IconPower from 'lucide-svelte/icons/power';
	import { registerPlugin } from '@capacitor/core';
	interface EchoPlugin {
		getConfig(options?: {}): Promise<{ config: string }>;
		updateConfig(options?: { config: string }): Promise<{ status: number }>;
		start(options?: {}): Promise<{ status: number }>;
		stop(options?: {}): Promise<{ status: number }>;
		getLog(options?: {}): Promise<{ log: string }>;
	}
	const Frpc = registerPlugin<EchoPlugin>('Frpc');
	let frpcRunning = $state(false);
	let msg = $state('');
	let config = $state('');
	let log = $state('');
	let editMode = $state(false);
	let configRows = $derived(config.split('\n').length);
	let logRows = $derived(log.split('\n').length);

	let group: 'Service' | 'Setting' | 'Log' = $state('Service');

	$effect(() => {
		switch (group) {
			case 'Service':
				break;
			case 'Setting':
				Frpc.getConfig().then((ret) => (config = ret.config));
				break;
			case 'Log':
				Frpc.getLog().then((ret) => (log = ret.log));
				break;
		}
	});

	async function runFrpc() {
		try {
			const ret = await Frpc.start();
			msg = ret.status.toString();
			frpcRunning = true;
		} catch (e) {
			console.error(e);
		}
	}

	async function stopFrpc() {
		try {
			const ret = await Frpc.stop();
			msg = ret.status.toString();
			frpcRunning = false;
		} catch (e) {
			console.error(e);
		}
	}

	async function updateConfig() {
		try {
			const ret = await Frpc.updateConfig({ config });
			msg = ret.status.toString();
		} catch (e) {
			console.error(e);
		}
	}

	async function getLog() {
		try {
			const ret = await Frpc.getLog();
			log = ret.log;
		} catch (e) {
			console.error(e);
		}
	}
</script>

<AppBar>
	<span>FRPC</span>
</AppBar>

<Tabs bind:value={group} listJustify="justify-center">
	{#snippet list()}
		<Tabs.Control value="Service">
			{#snippet lead()}<IconPower size={20} />{/snippet}
			Service
		</Tabs.Control>
		<Tabs.Control value="Setting">
			{#snippet lead()}<IconSettings size={20} />{/snippet}
			Setting
		</Tabs.Control>
		<Tabs.Control value="Log">
			{#snippet lead()}<IconScrollText size={20} />{/snippet}
			Log
		</Tabs.Control>
	{/snippet}
	{#snippet content()}
		<Tabs.Panel value="Service">
			<div class="flex h-[70vh] items-center justify-center">
				<div class="scale-[3]">
					<Switch
						name="enable"
						label="Enable"
						bind:checked={frpcRunning}
						onCheckedChange={() => {
							if (!frpcRunning) {
								runFrpc();
							} else {
								stopFrpc();
							}
						}}
					/>
				</div>
			</div>
		</Tabs.Panel>
		<Tabs.Panel value="Setting">
			<div>
				{#if !editMode}
					<button
						class="btn-primary btn preset-filled-primary-500"
						onclick={() => (editMode = true)}>Edit</button
					>
				{:else}
					<button
						class="btn-primary btn preset-filled-primary-500"
						onclick={() => {
							updateConfig().then(() => {
								editMode = false;
							});
						}}>Save</button
					>
				{/if}
				<label class="label">
					<span class="label-text">Setting</span>
					<textarea class="textarea" rows="10" bind:value={config} disabled={!editMode}></textarea>
				</label>
			</div>
		</Tabs.Panel>
		<Tabs.Panel value="Log">
			<button class="btn-primary btn preset-filled-primary-500" onclick={getLog}>Refresh</button>
			<label class="label">
				<span class="label-text">Log</span>
				<textarea class="textarea" rows="10" bind:value={log} disabled={true}></textarea>
			</label>
		</Tabs.Panel>
	{/snippet}
</Tabs>

<style>
</style>
