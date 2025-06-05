import { Effect, ImmerReducer } from 'umi';
import { modulesMetaDataService } from '@uni/services/src/metaDataService';
import merge from 'lodash/merge';

export type EffectWithType = [Effect, { type: string }];

export interface UniDictData {
  name: string;
  dictData: {
    [key: string]: any[];
  };
}

export interface UniSelectModelType {
  namespace: 'uniDict';
  state: UniDictData;
  effects: {
    fetchDictionaryData: EffectWithType;
    saveDictionaryData: Effect;
  };
  reducers: {
    saveDictData: ImmerReducer<UniDictData>;
  };
}

const modulesDataProcessor = (data: any, fetchedDictItem: any, param: any) => {
  let dictData = {};
  if (Array.isArray(data)) {
    dictData[fetchedDictItem?.module] = data?.map((item) => {
      if (param?.isHeader) {
        return {
          ...item,
          title: item.Name,
          value: item.Code,
        };
      } else {
        return item;
      }
    });
  } else {
    if (fetchedDictItem?.moduleGroup) {
      let currentGroupDictData = dictData[fetchedDictItem?.moduleGroup] || {};
      currentGroupDictData = Object.assign({}, data);
      dictData[fetchedDictItem?.moduleGroup] = currentGroupDictData;
    } else {
      Object.keys(data)?.forEach((key) => {
        dictData[key] = data[key];
      });
    }
  }

  return dictData;
};

const UniDictModel: UniSelectModelType = {
  namespace: 'uniDict',
  state: {
    name: 'uniDict',
    dictData: {},
  },
  effects: {
    fetchDictionaryData: [
      /**
       * param需要 传modules, moduleGroup, isHeader 是否用在header上
       * @param param
       * @param select
       * @param call
       * @param put
       * @param all
       */
      function* ({ param }, { select, call, put, all }) {
        let dictData = {},
          result: any = null;
        let fetchRequestList: any[] = [];
        let fetchDataItems: any[] = [];

        fetchDataItems.push(param);
        fetchRequestList.push(
          call(modulesMetaDataService, param?.modules, param?.moduleGroup),
        );
        if (fetchRequestList.length > 0) {
          result = yield all(fetchRequestList);

          for (let index = 0; index < fetchDataItems.length; index++) {
            const fetchedDictItem = fetchDataItems[index];
            // TODO fetchedDictItem object
            const fetchedDictItemResponse = result[index];
            if (fetchedDictItemResponse?.code === 0) {
              if (fetchedDictItemResponse?.data) {
                dictData = modulesDataProcessor(
                  fetchedDictItemResponse?.data,
                  fetchedDictItem,
                  param,
                );
              }
            }
          }
        }

        if (param?.noSave !== true) {
          yield put({
            type: 'saveDictData',
            payload: {
              dictData: dictData,
            },
          });
        }

        return {
          dictData: dictData,
        };
      },
      { type: 'takeEvery' },
    ],

    *saveDictionaryData({ data }, { select, call, put, all }) {
      yield put({
        type: 'saveDictData',
        payload: data,
      });
    },
  },
  reducers: {
    saveDictData(state, { payload }) {
      state.dictData = merge({}, state.dictData, payload.dictData);
    },
  },
};

export default UniDictModel;
