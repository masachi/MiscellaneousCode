import { defineConfig } from 'umi';
import AntdDayjsWebpackPlugin from 'antd-dayjs-webpack-plugin';
import CompressionPlugin from 'compression-webpack-plugin';
import {
  configurationRoutes,
  escalateRoutes,
  institutionRoutes,
  insurConfigurationRoutes,
} from './src/routes';
import { title } from './.umirc.commom';
import { apps } from './apps';

export default defineConfig({
  favicon: '/favicon.ico',
  nodeModulesTransform: {
    type: 'none',
  },
  hash: true,
  ignoreMomentLocale: true,
  chainWebpack(config: any) {
    // 添加额外插件
    config.plugin('moment2dayjs').use(AntdDayjsWebpackPlugin);
    config.plugin('compression-webpack-plugin').use(CompressionPlugin, [
      {
        test: /\.js$|\.html$|\.css$/,
        exclude: /\/public\/*/,
        threshold: 10240,
        algorithm: 'gzip',
        deleteOriginalAssets: false,
      },
    ]);

    config.module
      .rule('mjs$')
      .test(/\.mjs$/)
      .include.add(/node_modules/)
      .end()
      .type('javascript/auto');
  },
  title: title,
  // better xlsx 不兼容 mfsu
  // mfsu: {},
  fastRefresh: {},
  dva: { immer: true, hmr: true, skipModelValidate: false },
  locale: {
    default: 'zh-CN',
    antd: true,
    title: false,
    baseNavigator: true,
    baseSeparator: '-',
  },
  outputPath: 'dist', // build打包生成product时候输出的目录
  publicPath: process.env.NODE_ENV === 'development' ? '/' : '/', // public文件打包的时候的前缀
  define: {
    'process.env.REQUEST_PREFIX': '',
  },
  // 用于权限控制 需要preset-react >= 2.1.0
  access: {
    strictMode: true,
  },
  lessLoader: {
    // golbalVars: {
    //   'root-entry-name': 'default'
    // }
    modifyVars: {
      'root-entry-name': 'default',
    },
  },
  terserOptions: {
    compress: {
      drop_console: true,
      drop_debugger: true,
      pure_funcs: ['console.log', 'console.table'],
    },
  },
  extraBabelPlugins: [
    '@babel/plugin-proposal-optional-chaining', // 支持可选链操作符 ?.
    '@babel/plugin-proposal-nullish-coalescing-operator', // 支持空值合并操作符 ??
    [
      'import',
      {
        libraryName: 'lodash',
        libraryDirectory: '',
        camel2DashComponentName: false,
      },
      'import-lodash',
    ],
    [
      'import',
      { libraryName: '@umijs/hooks', camel2DashComponentName: false },
      'import-@umijs/hooks',
    ],
    [
      'import',
      {
        libraryName: '@ant-design/icons',
        libraryDirectory: '',
        camel2DashComponentName: false,
      },
      'import-@ant-design/icons',
    ],
  ],
  extraBabelIncludes: [
    '@tanstack/react-virtual',
    '@tanstack/virtual-core', // 如果需要编译第三方库
  ],
  plugins: [
    require.resolve('./packages/commons/src/plugins/corejs.ts'),
    require.resolve('./packages/commons/src/plugins/inject-env.ts'),
    require.resolve('./packages/commons/src/plugins/dependencies-version.ts'),
  ],
  externals: {},
  // dynamicImport: {},
  devServer: {
    port: 8001,
  },
  routes: [
    {
      path: '/',
      component: '@/layouts/init',
      routes: [
        {
          path: '/error',
          component: '@/pages/404.tsx',
        },
        {
          exact: true,
          path: '/login',
          component: '@/pages/login/index',
        },
        // sso
        {
          exact: true,
          path: '/sso',
          component: '@/pages/singleSignOn/index',
        },
        {
          exact: true,
          path: '/_health',
          component: '@/pages/_health/index',
        },
        {
          exact: true,
          path: '/docs',
          component: '@/pages/docs/index',
        },
        {
          path: '/external',
          exact: true,
          microApp: 'external',
          microAppProps: {
            autoSetLoading: true,
          },
        },
        {
          exact: true,
          path: '/',
          redirect: '/main',
          wrappers: ['@/wrappers/auth'],
        },
        {
          exact: true,
          path: '/main',
          component: '@/pages/main/index',
          wrappers: ['@/wrappers/auth'],
        },

        {
          path: '/*',
          component: '@/layouts/index',
          wrappers: ['@/wrappers/auth'],
          routes: [
            // 系统设置
            ...configurationRoutes,
            ...escalateRoutes,
            ...institutionRoutes,
            ...insurConfigurationRoutes,
            // 子系统
            {
              path: '/dmr/examine',
              exact: true,
              microApp: 'quality-examine',
              microAppProps: {
                autoSetLoading: true,
              },
              wrappers: ['@/layouts/base-layout'],
              headerKeys: {
                // '/management': 'settlementAnalysis',
                // '/statistic': 'settlementAnalysis'
              },
            },
            {
              path: '/dmr',
              exact: true,
              microApp: 'dmrIndex',
              microAppProps: {
                autoSetLoading: true,
              },
              wrappers: ['@/layouts/base-layout'],
              headerKeys: {
                // '/management': 'settlementAnalysis',
                // '/statistic': 'settlementAnalysis'
              },
            },
        ]
    }
]
}
  ],

  // qiankun
  qiankun: {
    master: {
      // 注册子应用信息
      apps: apps.filter((item) => item.bundle === true),
    },
  },

  proxy: {
    // 同cra的setupProxy,代理中转实现dev版本的跨域
    '/common': {
      target: 'http://172.16.3.152:5181',
      changeOrigin: true,
      pathRewrite: { '^/common': '' },
      secure: false, // https的dev后端的话需要配
    },
  },
});
