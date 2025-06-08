import request, { extend } from 'umi-request';
import { appendPortWhenPortMissing } from './processor';

export const basePayload = {
  method: 'GET',
};

export const commonBusinessDomain =
  appendPortWhenPortMissing(
    (window as any).externalConfig?.domain?.dmrCommonDomain,
  ) ??
  process.env.dmrCommonDomain ??
  'http://172.16.3.152:5181';

export const uniCommonService = (url: string, payload?: any) => {
  console.log(commonBusinessDomain);

  let requestParams = {
    ...basePayload,
    method: payload?.method,
    requestType: payload?.requestType || 'json',
    params: payload?.params,
    data: payload?.data,
    headers: payload?.headers || {},
    responseType: payload?.responseType || 'json',
    signal: payload?.signal, // AbortController
  };

  return request(`${commonBusinessDomain}/${url}`, requestParams);
};

export const uniBaseService = (domain: string, url: string, payload?: any) => {
  let requestParams = {
    ...basePayload,
    method: payload?.method,
    requestType: payload?.requestType || 'json',
    params: payload?.params,
    data: payload?.data,
    headers: payload?.headers || {},
    responseType: payload?.responseType || 'json',
  };
  return request(`${domain}/${url}`, requestParams);
};

export const LogLevel = {
  Trace: 'Trace',
  Debug: 'Debug',
  Info: 'Info',
  Warn: 'Warn',
  Error: 'Error',
  Fatal: 'Fatal',
};

export const doLog = (level, message) => {
  let payload: any = {
    method: 'POST',
    requestType: 'json',
    data: {
      Level: level,
      Message: message,
    },
  };
  request(`${commonBusinessDomain}/Api/Sys/ClientKitSys/WebLog`, payload);
};

// blob response
export const uniBatchBlobService = async (url: string, payload?: any) => {
  console.log(commonBusinessDomain);

  let requestParams = {
    ...basePayload,
    method: payload?.method,
    requestType: payload?.requestType || 'json',
    params: payload?.params,
    data: payload?.data,
    headers: payload?.headers || {},
    responseType: 'blob' as any,
  };

  let apiResult = await request(
    `${commonBusinessDomain}/${url}`,
    requestParams,
  );

  let result = null;
  if (apiResult.statusCode === 200) {
    result = await apiResult?.response?.blob();
  }

  return {
    blodId: payload?.params?.BlobId,
    result: result,
  };
};
