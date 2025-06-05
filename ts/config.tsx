import { message, Modal, notification } from 'antd';
import qs from 'qs';
import request, { extend } from 'umi-request';
import { ColumnItem, TableColumns } from './interfaces';
import { parameterValidatorByRequestUrlWithPathname } from './validation/pathname';
import { parameterValidatorByRequestUrl } from './validation/url';
import {
  getKeysWithTargetValue,
  isEmptyValues,
  showNotification,
} from '@uni/utils/src/utils';
import { v4 as uuidv4 } from 'uuid';
import { refreshToken } from './userService';
import { RespVO } from '@uni/commons/src/interfaces';
import { doLog, LogLevel } from './commonService';
import _ from 'lodash';
import ResponseMiddleware from './middleware';
import RequestMiddleware from './requestMiddleware';
import Constants from '@uni/utils/src/constants';

let tokenRefreshing = false;
let failedQueue = [];

const requestMiddlewareInstance = new RequestMiddleware();
const responseMiddlewareInstance = new ResponseMiddleware();

const exportUrl = [
  'Api/Dmr/DmrCardBundle/ExportCardPdf',
  'Api/Dmr/DmrCard/ExportCardWord',
  'Export/DmrAnalysis/ComboQuery/GetDetails',
  'Export/DmrAnalysis/ComboQuery/GetStats',
  'Api/DmrAnalysis/DiseaseAnalysis/ExportDetails',
  'Api/Sys/CodeSys/GetCodeDictionaryExcelTemplate',
  'Api/Sys/CodeSys/GetIcdeExcelTemplate',
  'Api/Sys/CodeSys/GetTcmIcdeExcelTemplate',
  'Api/Sys/CodeSys/GetPathologyExcelTemplate',
  'Api/Sys/CodeSys/GetCatyExcelTemplate',
  'Api/Sys/CodeSys/GetOperExcelTemplate',
  'Api/Report/Report/Export',
  'Api/Sys/ReportSys/Export',
  'Api/Report/Report/GetImportTemplate',
  'Api/Sys/BlobFileMetadata/DownloadBlobFile',
  'Api/Mr/BorrowRecord/SelectiveBorrowRecordPrint',
  'Api/Mr/BorrowRecord/BorrowRecordPrint',
  'Api/Sys/CodeSys/GetDmrIcdeAllCompareExcelTemplate',
  'Api/Sys/CodeSys/GetDmrPathologyAllCompareExcelTemplate',
  'Api/Sys/CodeSys/GetDmrTcmIcdeAllCompareExcelTemplate',
  'Api/Sys/CodeSys/GetDmrOperAllCompareExcelTemplate',
  'Api/Sys/CodeSys/GetDmrCodeDictionaryAllCompareExcelTemplate',
  'Api/Sys/CodeSys/GetHierarchyCliDeptAllCompareExcelTemplate',
  'Api/Sys/CodeSys/GetStdIcdCategoryExcelTemplate',
  'Api/Sys/CodeSys/GetStdOperCategoryExcelTemplate',
  'Api/Sys/CodeSys/GetDmrPathologyCompareExcelTemplate',
  'Api/Sys/CodeSys/GetDmrTcmIcdeCompareExcelTemplate',
  'Api/Sys/CodeSys/GetDmrOperCompareExcelTemplate',
  'Api/Sys/CodeSys/GetDmrCodeDictionaryCompareExcelTemplate',
  'Api/Sys/CodeSys/GetHierarchyCliDeptCompareExcelTemplate',
  'Api/Sys/CodeSys/GetDmrIcdeCompareExcelTemplate',
  'Api/Sys/CodeSys/GetHierarchyWardCompareExcelTemplate',
  'Api/Sys/DynHierarchyCompare/GetExcelTemplate',
  'Api/Drgs/DrgStats/UniDrgsProtoCoreRedirect',
  // dynddr
  'Api/Dyn-ddr/DailyInpatientAmt/GetTemplate',
  'Api/Dyn-ddr/DeptInpatientAmt/GetTemplate',
  'Api/Dyn-ddr/InpatientAmtOfWard/GetTemplate',
  'Api/Dyn-ddr/DailyInpatientAmtOfWard/GetTemplate',
  'Api/Dyn-ddr/DailyOutpatientAmt/GetTemplate',
  'Api/Dyn-ddr/DeptOutpatientAmt/GetTemplate',
  'Api/Dyn-ddr/DailyOutpatientDoctorAmt/GetTemplate',
  'Api/Dyn-ddr/DeptOutpatientDoctorAmt/GetTemplate',
  'Api/Dyn-ddr/DailyObsPatientAmt/GetTemplate',
  'Api/Dyn-ddr/DeptObsPatientAmt/GetTemplate',
  // 特病单议
  'Api/CenterSettle/AppealTaskImport/GetSpecialCaseImportTemplate',
  // 医保下发明细
  'Api/CenterSettle/Import/GetFileImportTemplate',
  // 示踪 病案出库
  'Api/Mr/TraceRecord/PrintDmrSignedOutList',

  'Api/Common/Blob/Download',

  'Api/Report/Report/BundleExport',
  'Api/Mr/BorrowApplication/ExportBorrowApplicationManifest',
];

const percentUrl = [
  'Api/Emr/EmrSettleCheckWarning/GetSettleCheckCardDetails',
  'Api/FundSupervise/LatestSettleCheckReport/GetSettleCheckCardDetails',
];

const errorModalHandlerUrl = [
  // 'Api/Mr/Tracing/MrRoomSignIn',
  // 'Api/Mr/Tracing/WarehouseSignIn',
  // 'Api/Mr/Borrowing/Lend',
];

const specialUrlWithFilters = [
  {
    url: 'Api/Sys/CodeSys/GetIcdeWithCompares',
    handleFilters: ['Types'],
    handleType: 'ChangeValueIntoKeys',
  },
  {
    url: 'Api/Sys/CodeSys/GetDmrIcdeWithAllCompares',
    handleFilters: ['Types'],
    handleType: 'ChangeValueIntoKeys',
  },
];

// 204 的时候不弹框的 api url
const url204NoModals = [
  'ExportCenter/GetExportRecord',
  'switch/advance',
  'switch/discharge',
];

const paramsValidations = [
  parameterValidatorByRequestUrlWithPathname,
  parameterValidatorByRequestUrl,
];

export const initializeRequest = () => {
  request.interceptors.request.use(requestInterceptors[0] as any);
  request.interceptors.response.use(responseInterceptors[0] as any);
  request.extendOptions({ errorHandler, paramsSerializer });
};

export const paramsSerializer = (params) => {
  return qs.stringify(params, { arrayFormat: 'indices' });
};

export const errorHandler = async (error: any) => {
  console.error('errorHandler', error?.request);

  // 存在traceId 即删除
  if (error?.request?.headers?.['traceId']) {
    delete global[error?.request?.headers?.['traceId']];
  }
  // abort 情况下的取消请求
  if (error?.request?.options?.headers?.['traceId']) {
    delete global[error?.request?.options?.headers?.['traceId']];
  }

  if (error instanceof request.Cancel) {
    delete global[error?.message];

    // 表示手动取消接口
    return {
      code: 0,
      statusCode: 200,
      data: {},
      cancelled: true,
    };
  }

  // _health 单独处理
  if (error?.response?.url?.includes('_health')) {
    let responseBody = await parseJsonResponse(error?.response);
    return {
      code: responseBody?.Status ?? 1,
      statusCode: responseBody?.Status === 0 ? 200 : responseBody?.Status,
      data: responseBody?.Entries,
    };
  }

  // GetUserInfo error
  if (
    error?.response?.url?.includes('GetUserInfo') ||
    error?.response?.url?.includes('connect/token')
  ) {
    localStorage.removeItem('uni-connect-token');
    localStorage.removeItem('uni-connect-timestamp');
    localStorage.removeItem('expireItem');

    switch (error?.data?.error) {
      case 'invalid_grant':
      case 'invalid_scope':
      case 'unsupported_grant_type':
        message.warning(error?.data?.error_description);
        break;
      default:
        message.warning('您尚未登录或登录已过期，请先登录');
        doLog(LogLevel.Trace, { title: 'GetUserInfo / connectToken' });
        global?.window.location.replace('/login');
        return {
          code: 1,
          statusCode: error?.response?.status,
        };
    }
  }

  // 异常信息统一处理
  if (error?.type === 'AbortError') return 'Abort';

  if (error?.type === 'SyntaxError') {
    localStorage.removeItem('uni-connect-token');
    localStorage.removeItem('uni-connect-timestamp');
    localStorage.removeItem('expireItem');
    message.warning('登录已过期，请先登录');
    if (global?.window?.location?.pathname !== '/login') {
      doLog(LogLevel.Trace, { title: 'SyntaxError', error: error });
      global?.window.location.replace('/login');
    }
    return {
      code: 1,
      statusCode: 500,
    };
  }

  // 示踪特殊提示 单独提出来处理...
  if (
    errorModalHandlerUrl.findIndex(
      (item) => error?.response?.url?.indexOf(item) > -1,
    ) !== -1
  ) {
    return {
      code: 1,
      statusCode: error?.response?.status,
      error: error?.response?.data,
    };
  }

  if (error?.response?.status === 404) {
    if (!error?.response?.url?.includes('Error/404')) {
      // 弹窗
      Modal.info({
        className: 'fourofour-container',
        closable: false,
        title: `未查询到数据`,
        content: '当前接口未查询到数据',
        mask: true,
        maskClosable: false,
        zIndex: 9999,
      });
    }

    return {
      code: 1,
      statusCode: error?.response?.status,
    };
  }

  // param validation error
  if (error?.name === 'ParameterValidateError') {
    showNotification(error.errorKey ?? uuidv4(), error?.message);
    return {
      code: 1,
    };
  }

  if (error.data?.error_description === 'invalid_username_or_password') {
    message.error('账号或密码错误');
    return {
      code: 1,
    };
  }

  if (error?.response?.status === 401) {
    if (process.env.NODE_ENV === 'production') {
      localStorage.removeItem('uni-connect-token');
      localStorage.removeItem('uni-connect-timestamp');
      localStorage.removeItem('expireItem');
      message.warning('您尚未登录或登录已过期，请先登录');
      doLog(LogLevel.Trace, { title: 'PRD 401' });
      global?.window.location.replace('/login');
    }
    return {
      code: 1,
      statusCode: error?.response?.status,
    };
  }

  if (error?.response?.status === 400) {
    let errorBody = await error?.response?.json();
    console.error('errorBody', errorBody);

    if (errorBody?.detail) {
      notification.warning({
        message: errorBody?.detail,
        description: errorDescriptionGenerator(errorBody?.errors),
        duration: 10,
        key: 'NOTIFICATION_WITH_DETAILS',
        placement: 'top',
      });
    } else {
      if (errorBody?.errors) {
        let messages = [];
        Object.keys(errorBody?.errors)?.forEach((key) => {
          messages.push(...(errorBody?.errors?.[key] || []));
        });

        if (messages) {
          message.warning(messages.join(';'));
        }
      }
    }
    return {
      code: 1,
      statusCode: error?.response?.status,
      errors: errorBody?.errors,
      errorBody: errorBody,
    };
  }

  if (error?.response?.status === 409) {
    let errorBody = await error?.response?.json();
    if (errorBody?.errors) {
      let messages = [];
      Object.keys(errorBody?.errors)?.forEach((key) => {
        messages.push(...(errorBody?.errors?.[key] || []));
      });

      if (messages) {
        message.warning(messages.join(';'));
      } else {
        message.error('出现冲突，操作失败');
      }
    }
    return {
      code: 1,
      statusCode: error?.response?.status,
    };
  }

  if (error?.response?.code !== 0 && error?.response?.message) {
    message.error(error?.response?.message);
    return {
      code: 1,
      statusCode: error?.response?.status,
    };
  }

  // /Mr Error
  let responseBody = await parseJsonResponse(error?.response);
  if (error?.response?.code !== 0 && responseBody?.errors) {
    message.error(responseBody?.errors[''][0]);
    return {
      code: 1,
      statusCode: error?.response?.status,
    };
  }
  console.error('error', error);
  message.error('未知错误，请稍后重试');
  return {
    code: 1,
    statusCode: error?.response?.status,
    message: error?.response?.message,
    requestUrl: error?.request?.url,
  };
};

export const requestInterceptors = [
  async (url, options: any) => {
    const { headers, responseType, method } = options;
    console.log('appts middleware');

    url = url.replace(/([^:]\/)\/+/g, '$1');
    // 替换 部分url 带了 >= 2个 slash
    url = url.replace(/(?<!:)\/\{2,}/gm, '/');

    let addHeaders: any = {};
    let cacheOption = {
      useCache: true, // 启用缓存
      // ttl: 0, // 让缓存永久存在，可以自行修改时间
    };

    let token = localStorage.getItem('uni-connect-token')
      ? localStorage.getItem('uni-connect-token')
      : null;
    if (token) {
      addHeaders['Authorization'] = `Bearer ${token}`;
    }

    // 校验部分header必填项
    paramsValidations?.forEach((validator) => {
      validator(url, options);
    });

    options = await requestMiddlewareInstance.process(options);
    url = options?.url;

    retrieveApiRedirectionHeaderProcessor(options);
    await hasColumnRetrieveDataRequestWaiting(options);

    tableFilterSorterMiddleware(options);
    useApiRedirectionHeaderProcessor(options);

    // new middleware
    // optionalAllReplaceProcessor(options);

    if (options?.headers?.['traceId']) {
      const traceId = options?.headers?.['traceId'];
      if (global[traceId] === null || global[traceId] === undefined) {
        global[traceId] = true;
      } else {
        // 取消当前的 请求 因为是 重复的....
        throw new request.Cancel(options?.headers?.['traceId']);
      }
    }

    return {
      url: url,
      options: {
        ...options,
        ...cacheOption,
        headers: {
          ...options.headers,
          ...headers,
          ...addHeaders,
        },
        getResponse: !!(url.includes('Export') || responseType === 'blob'),
      },
    };
  },
];

export const tokenRefresh = (refreshResponse) => {
  if (refreshResponse?.code === 0) {
    if (refreshResponse?.data) {
      if (refreshResponse?.data?.access_token) {
        localStorage.setItem(
          'uni-connect-token',
          refreshResponse?.data?.access_token,
        );

        if (refreshResponse?.data?.refresh_token) {
          localStorage.setItem(
            'uni-refresh-token',
            refreshResponse?.data?.refresh_token,
          );
        }
      }
    }
  }
};

export const tableFilterSorterMiddleware = (options) => {
  let { data } = options;

  let tableParameters = Object.assign({}, global?.tableParameters);

  // hi-jack filter 和 sorter
  if (data?.DtParam && !data?.skipFilterSorterMiddleware) {
    if (!isEmptyValues(tableParameters)) {
      // 仅支持一个请求 消费
      // FIXME WARNING: 现在仍然保留tableParameters 当且仅当路由切换的时候会清除
      // delete global['tableParameters'];

      let filterSorter = {
        order: [],
        columns: [],
        ...(_.pick(data?.DtParam, ['order', 'columns']) || {}),
      };
      // hi-jack filter & search
      if (tableParameters?.filters) {
        // TODO 特殊处理part
        let specialPart = specialUrlWithFilters.find(
          (d) => options.url.indexOf(d.url) > -1,
        );

        Object.keys(tableParameters?.filters)?.forEach((key) => {
          let filteredValue = tableParameters?.filters[key];

          // TODO 可以更优雅的处理
          if (
            specialPart &&
            specialPart.handleFilters.findIndex((d) => d === key) > -1
          ) {
            switch (specialPart.handleType) {
              case 'ChangeValueIntoKeys':
                filteredValue &&
                  filteredValue.forEach((v) => {
                    filterSorter?.columns.push({
                      data: v,
                      name: v,
                      searchable: true,
                      search: {
                        value: true,
                        regex: true,
                      },
                    });
                  });
              default:
                break;
            }
          } else {
            filterSorter?.columns.push({
              data: key,
              name: key,
              searchable: true,
              search: {
                value: filteredValue
                  ? Array.isArray(filteredValue)
                    ? filteredValue.join(',')
                    : filteredValue
                  : null,
                regex: true,
              },
            });
          }
        });
      }

      // hi-jack sorter
      if (tableParameters?.sorter) {
        let sorter = tableParameters?.sorter;
        if (Array.isArray(sorter)) {
          let sortedSorters = sorter.sort(
            (a, b) =>
              (b?.column?.sorter?.multiple ?? 0) -
              (a?.column?.sorter?.multiple ?? 0),
          );
          sortedSorters.forEach((sorterItem, index) => {
            if (sorterItem?.order) {
              filterSorter?.columns.push({
                Data: sorterItem?.columnKey || sorterItem?.field,
              });
              filterSorter?.order.push({
                column: index,
                dir: sorterItem?.order === 'ascend' ? 'asc' : 'desc',
              });
            }
          });
        } else {
          if (sorter?.order) {
            filterSorter?.columns.push({
              Data: sorter?.columnKey || sorter?.field,
            });
            filterSorter?.order.push({
              column: filterSorter?.columns?.length - 1,
              dir: sorter?.order === 'ascend' ? 'asc' : 'desc',
            });
          }
        }
      }

      data['DtParam'] = {
        ...data['DtParam'],
        ...filterSorter,
      };
    }
  }

  return options;
};

const hasColumnRetrieveDataRequestWaiting = (options: any) => {
  let { headers } = options;
  let pathname = new URL(options?.url).pathname?.slice(1);
  let locationPathname = window.location.pathname?.slice(1);
  let intervalID = null;
  return new Promise((resolve, reject) => {
    if (headers['Retrieve-Column-Definitions'] !== undefined) {
      // 列请求相关跳过
      resolve(null);
    } else {
      if (
        global['ColumnDefinitionRequestFlags']?.[locationPathname]?.[
          pathname
        ] === undefined
      ) {
        resolve(null);
      } else {
        intervalID = setInterval(checkForColumnDefinition, 1000);
        function checkForColumnDefinition() {
          let inProgressFlag =
            global['ColumnDefinitionRequestFlags']?.[locationPathname]?.[
              pathname
            ];
          if (inProgressFlag === undefined) {
            clearInterval(intervalID);
            resolve(null);
          }
        }
      }
    }
  });
};

const retrieveApiRedirectionHeaderProcessor = (options: any) => {
  console.log('retrieveApiRedirectionHeaderProcessor', options, options?.url);
  let { headers } = options;
  let pathname = new URL(options?.url).pathname?.slice(1);
  let locationPathname = window.location.pathname?.slice(1);
  if (headers['Retrieve-Column-Definitions'] !== undefined) {
    headers['Retrieve-Api-Redirection'] = 1;

    // 新增global 变量用来记录 ColumnDef 请求
    if (global['ColumnDefinitionRequestFlags'] === undefined) {
      global['ColumnDefinitionRequestFlags'] = {};
    }

    if (
      global['ColumnDefinitionRequestFlags'][locationPathname] === undefined
    ) {
      global['ColumnDefinitionRequestFlags'][locationPathname] = {};
    }

    global['ColumnDefinitionRequestFlags'][locationPathname][pathname] =
      'INPROGRESS';
  }
};

const useApiRedirectionHeaderProcessor = (options: any) => {
  let { headers } = options;
  let pathname = new URL(options?.url).pathname?.slice(1);
  let locationPathname = window.location.pathname?.slice(1);

  if (
    global['UseApiRedirectionHeaderUrl'] &&
    global['UseApiRedirectionHeaderUrl'][locationPathname] &&
    global['UseApiRedirectionHeaderUrl']?.[locationPathname]?.[pathname] !==
      undefined
  ) {
    // header 中追加 UseApiRedirection
    headers['Use-Api-Redirection'] =
      global['UseApiRedirectionHeaderUrl'][locationPathname][pathname];
  }
};

// % 替换为 total的middleware
const optionalAllReplaceProcessor = (options: any) => {
  let { headers, data, method } = options;
  // 过滤接口 只留 (1)POST (2)非重定向 (3)抓数据 (4)有%为值 的接口
  // 特殊的页面 不要对 % 做处理
  if (
    method?.toLowerCase() === 'post' &&
    !headers?.['Retrieve-Column-Definitions'] &&
    !headers?.['Retrieve-Api-Redirection'] &&
    !headers?.['Use-Api-Redirection'] &&
    percentUrl.findIndex((item) => options?.url?.indexOf(item) > -1) === -1
  ) {
    let keys = getKeysWithTargetValue(data, '%');
    if (keys?.length > 0) {
      keys.forEach((key) => {
        data[key] = global['GloablDictData'][
          Constants.SearchOptsDictType?.[key?.toLowerCase()]
        ]?.map((d) => d?.Code);
      });
    }
  }
  return options;
};

let parseJsonResponse = async (res) => {
  let data = await res?.text();

  if (data === null || data === undefined) {
    return null;
  }

  return data.length === 0 ? null : JSON.parse(data);
};

export const responseInterceptors = [
  async (response: Response, options) => {
    console.log('responseInterceptor', options);

    // traceId
    // 删除trace Id 解锁
    if (options?.headers?.['traceId']) {
      delete global[options?.headers?.['traceId']];
    }

    if (response?.status === 200) {
      let responseBody: any = {};
      // if (
      //   NoResponseUrl.findIndex((item) => response?.url?.indexOf(item) > -1) ===
      //   -1
      // ) {
      //   responseBody = await response?.json();
      // }
      // 导出的接口
      console.log(response);
      if (
        exportUrl.findIndex((item) => response?.url?.indexOf(item) > -1) !==
          -1 ||
        response?.url?.indexOf('/Export/') !== -1 ||
        options?.responseType === 'blob'
      ) {
        return {
          code: 0,
          requestUrl: response?.url,
          statusCode: response?.status,
          data: responseBody,

          response: response,
        };
      }

      let proceedResponseData = await responseMiddlewareInstance.process(
        response,
        options,
      );
      responseBody = proceedResponseData?.responseBody;

      // 额外的ResponseError
      if (
        responseBody?.ResponseStatus &&
        responseBody?.ResponseStatus !== 0 &&
        responseBody?.ResponseStatus !== 200
      ) {
        let extraErrorMessage =
          responseBody?.ResponseErrMsg ?? '服务出现错误，请尽快联系工程师';
        // 埋点
        doLog(
          LogLevel.Warn,
          `接口内报错：
          路由： ${window?.location?.pathname},
          错误接口： ${response?.url}
          错误信息： ${extraErrorMessage}
        `,
        );

        notification.warn({
          message: `发生错误`,
          description: extraErrorMessage,
          placement: 'bottom',
          duration: 5,
          closeIcon: null,
        });
      }

      return {
        code: 0,
        requestUrl: response?.url,
        statusCode: response?.status,
        data: responseBody ?? {},

        extraResponseCode: responseBody?.ResponseStatus ?? '0',
        extraResponseErrMsg: responseBody?.ResponseErrMsg ?? '',
      };
    }

    // 204 no content 弹窗
    if (
      response?.status === 204 &&
      url204NoModals?.findIndex((d) => response?.url?.includes(d)) === -1
    ) {
      let title = '未查询到数据';
      let content = '当前接口未查询到数据';
      if (response?.url?.includes('DmrCard/GetCardFlatResult')) {
        title = '未找到病案';
        content = `查询条件： ${options?.data?.HisId}`;
      }
      Modal.info({
        className: 'fourofour-container',
        closable: false,
        title: title,
        content: content,
        mask: true,
        maskClosable: false,
      });
    }

    // 401 刷新token
    if (response?.status === 401) {
      // 表示 refresh token 过期了
      if (response?.url.includes('connect/token')) {
        return response;
      }

      let refresh = localStorage.getItem('uni-refresh-token');
      if (refresh) {
        failedQueue.push({
          url: response?.url,
          options: options,
        });

        if (!tokenRefreshing) {
          if (refresh) {
            tokenRefreshing = true;
            let refreshResponse: RespVO<any> = await refreshToken(refresh);
            tokenRefresh(refreshResponse);
            if (failedQueue?.length > 0) {
              let queue = failedQueue?.slice();
              failedQueue = [];
              return queue.map((item) => {
                return request(item.url, item.options);
              });
            }
            tokenRefreshing = false;
          }
        } else {
          // token在刷新的时候的其他进来的request直接拦截掉返回200
          return {
            ...response,
            status: 200,
          };
        }
      }
    }

    return response;
  },
];

const errorDescriptionGenerator = (errors: any) => {
  let descriptions = [];
  Object.keys(errors)?.forEach((key) => {
    if (errors?.[key]) {
      descriptions.push(errors?.[key]);
    }
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column' }}>
      {descriptions?.map((item) => {
        return <span>{item}</span>;
      })}
    </div>
  );
};
