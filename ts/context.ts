import { createContext } from 'react';

interface GridItemContextItem {
  dynamicComponentsMap?: { [key: string]: any };
  externalConfig?: { [key: string]: any };
  eventNames?: { [key: string]: string };
  extra?: { [key: string]: any };
  dictDataGroup?: any;
  modelGroup?: 'Dmr' | 'Insur';
  underConfiguration?: boolean;
  globalState?: any;

  configurableDataIndex?: string[];
}

const defaultValue: GridItemContextItem = {};

const GridItemContext = createContext(defaultValue);

export default GridItemContext;
