import { configure } from 'quasar/wrappers';

export default configure(function (/* ctx */) {
  return {
    eslint: { warnings: true, errors: true },
    boot: [],
    css: [],
    extras: [ 'roboto-font', 'material-icons' ],
    build: {
      target: {
        browser: [ 'es2019', 'edge88', 'firefox78', 'chrome87', 'safari13.1' ],
        node: 'node20'
      },
      vueRouterMode: 'history',
      vitePlugins: []
    },
    devServer: {
      open: true,
      port: 9000
    },
    framework: {
      config: {},
      plugins: ['Notify', 'Loading', 'Dialog']
    },
    animations: [],
    ssr: { pwa: false, prodPort: 3000, middlewares: ['render'] },
    pwa: { workboxMode: 'generateSW', injectPwaMetaTags: true },
    cordova: {},
    capacitor: { hideSplashscreen: true },
    electron: { inspectPort: 5858, bundler: 'packager', builder: { appId: 'frontend' } },
    bex: { contentScripts: ['my-content-script'] }
  }
});
