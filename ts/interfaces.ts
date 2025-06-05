export interface RespVO<T> {
  code?: number;
  requestUrl?: string;
  statusCode?: number;
  message?: string;
  data?: T;

  errors?: any;
  response?: any;
  errorBody?: any;

  cancelled?: boolean;
}
