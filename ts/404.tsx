import React from 'react';
import { Result, Button } from 'antd';
import { useModel } from 'umi';
interface ICodeMessage {
  [key: number]: string;
}
export const codeMessage: ICodeMessage = {
  200: '服务器成功返回请求的数据。',
  201: '新建或修改数据成功。',
  202: '一个请求已经进入后台排队（异步任务）。',
  204: '删除数据成功。',
  400: '发出的请求有错误，服务器没有进行新建或修改数据的操作。',
  401: '用户没有权限（令牌、用户名、密码错误）。',
  403: '您的权限等级未达到访问此网页的权限等级。',
  404: '访问的网址不存在，服务器没有进行操作。',
  406: '请求的格式不可得。',
  410: '请求的资源被永久删除，且不会再次获得。',
  422: '当创建一个对象时，发生一个验证错误。',
  500: '服务器发生错误，请检查服务器。',
  502: '网关错误。',
  503: '服务不可用，服务器暂时过载或维护。',
  504: '网关超时。',
};

const status = [404, 403, 500];
export default ({ history, location }: any) => {
  const { initialState }: any = useModel('@@initialState');

  return (
    <Result
      style={{
        position: 'absolute',
        transform: 'translate(-50%, -50%)',
        top: '50%',
        left: '50%',
      }}
      status={
        location.query.code &&
        status.some((v) => v.toString() === location.query.code)
          ? location.query.code
          : 'error'
      }
      title={location.query.code ? location.query.code : 'error'}
      subTitle={
        location.query.code ? codeMessage[location.query.code] : '未知错误'
      }
    />
  );
};
