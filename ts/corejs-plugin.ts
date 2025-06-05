import { IApi } from 'umi';

export default (api: IApi) => {
  api.modifyBabelPresetOpts((opts) => {
    return {
      ...opts,
      env: {
        ...opts.env,
        useBuiltIns: 'entry',
        corejs: '3.27.2',
        modules: false,
      },
      lockCoreJS3: false as unknown as object,
    };
  });
};
