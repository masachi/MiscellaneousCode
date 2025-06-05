import { ConfigProvider, message, notification, Spin } from 'antd';
import { history } from 'umi';
import jwt_decode from 'jwt-decode';
import { ParameterValidateError } from '@/exception/error';
import { parameterValidator } from '@/validation';
import { useEffect, useState } from 'react';
import {
  errorHandler,
  requestInterceptors,
  responseInterceptors,
} from '@uni/services/src/config';
import { RespVO, UserInfo } from '@uni/commons/src/interfaces';
import { getUserInfo } from '@uni/services/src/userService';
import { getAllMenusContainsHidden } from '@uni/components/src/menu-sider/utils';
import { menuData } from '@/layouts/menuData';
import {
  addGlobalUncaughtErrorHandler,
  removeGlobalUncaughtErrorHandler,
} from '@/utils/utils';
import {
  doLog,
  LogLevel,
  uniCommonService,
} from '@uni/services/src/commonService';
import Loading from '@/loading';
import {
  configurationProcessor,
  homePageProcessor,
  modeProcessor,
} from '@/processors';
import ErrorStackParser from 'error-stack-parser';
import { clearScrollingTitleTimeout } from '@uni/utils/src/utils';
import { ConfigurationPaths } from '@uni/utils/src/cwUtils';
import _ from 'lodash';
import zhCN from 'antd/es/locale/zh_CN';

const headerTitleWithModule = true;

ConfigProvider.config({
  locale: zhCN,
});

message.config({
  maxCount: 1,
});

notification.config({
  placement: 'topRight',
  duration: 3,
  maxCount: 10,
});

export async function getInitialState() {
  let accessToken = localStorage.getItem('uni-connect-token');
  let userBaseInfo = {};
  let userInfo: any = {};
  let configurationInfo = {};
  if (accessToken) {
    userBaseInfo = jwt_decode(accessToken);

    userInfo = await getUserProfile();

    homePageProcessor(userInfo, userInfo?.Preferences?.Menu);
    modeProcessor(userInfo, userInfo?.Preferences?.PublicMode);
  }

  configurationInfo = await getConfiguration();

  return {
    token: accessToken,
    userBaseInfo: userBaseInfo,
    userInfo: userInfo,
    configurationInfo: {
      ...configurationInfo,
      chsDefs: configurationProcessor(configurationInfo),
    },
  };
}

const getUserProfile = async () => {
  let response: RespVO<UserInfo> = await getUserInfo();

  if (response?.code === 0) {
    if (response?.statusCode === 200) {
      return response?.data;
    }
  }

  return {};
};

const getConfiguration = async () => {
  let response: RespVO<any> = await uniCommonService(
    'Api/Sys/ClientKitSys/GetConfiguration',
    {
      method: 'POST',
      data: {
        ConfigPaths: Object.keys(ConfigurationPaths)?.map(
          (key) => ConfigurationPaths[key],
        ),
      },
    },
  );

  if (response?.code === 0) {
    if (response?.statusCode === 200) {
      return response?.data;
    }
    return {};
  }

  return {};
};

export function onRouteChange({ location, routes, action }) {
  // TODO  埋点
  console.log('onRouteChange', location, routes, action);

  delete global['tableParameters'];

  if (headerTitleWithModule) {
    let currentSelectRouteItem = getAllMenusContainsHidden(menuData)?.find(
      (item) => item.route === location.pathname,
    );
    if (currentSelectRouteItem && currentSelectRouteItem?.name) {
      clearScrollingTitleTimeout();
      document.title = `${currentSelectRouteItem?.name}`;
      sessionStorage.setItem('titleByPath', document.title);
    }
  }
}

export const request: any = {
  errorHandler: errorHandler,
  requestInterceptors: requestInterceptors,
  responseInterceptors: responseInterceptors,
};

export const dva = {
  config: {
    initialState: {},
    onStateChange: (state) => {
      if (!_.isEmpty(state?.uniDict?.dictData)) {
        global['GloablDictData'] = state?.uniDict?.dictData;
      }
    },
    onError(e: any) {
      console.log(e);
      e.preventDefault();
    },
  },
};

// qiankun
export const useQiankunStateForSlave = () => {
  const [globalState, setQiankunGlobalState] = useState<any>({
    dictData: {},
    searchParams: {},
    userInfo: {},
    access: {},
    timestamp: Date.now(),
    externalConfig: (window as any)?.externalConfig,
  });

  useEffect(() => {
    // console.error('globalState', globalState);
  }, [globalState]);

  return {
    globalState,
    setQiankunGlobalState,
  };
};

export const qiankun = {
  lifeCycles: {
    beforeMount: (props) => {
      console.log('beforeMount', props);
      addGlobalUncaughtErrorHandler(globalErrorHandler);
    },
    beforeUnmount: (props) => {
      console.log('beforeUnmount', props);
      removeGlobalUncaughtErrorHandler(globalErrorHandler);
    },
  },
};

const globalErrorHandler = (event: Event) => {
  doLog(
    LogLevel.Fatal,
    `前端报错：
          路由： ${location?.pathname}
          错误信息： ${(event as any)?.error?.message}
          错误堆栈： ${(event as any)?.error?.stack},
          堆栈详细信息：${ErrorStackParser.parse((event as any)?.error)}
        `,
  );
};

/** When obtaining user information is slow, a loading */
export const initialStateConfig = {
  loading: <Loading />,
};
