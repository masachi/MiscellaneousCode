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




import { useLocation } from 'umi';
import { createContext, useContext } from 'react';

export const useRouteProps = () => {
  const routes = useRouteContext()?.routes ?? [];
  const location = useLocation();
  // use `useLocation` get location without `basename`, not need `basename` param
  const currentRoute = routes
    ?.filter((item) => item?.path === location.pathname)
    ?.at(0);
  return currentRoute?.params ?? {};
};

interface RouteContextItem {
  routes?: any[];
}

const defaultValue: RouteContextItem = {};

export const useRouteContext = () => useContext(RouteContext);

export const RouteContext = createContext(defaultValue);

export default (props: any) => {
  return (
    <RouteContext.Provider
      value={{
        routes: props?.routes,
      }}
    >
      {props.children}
    </RouteContext.Provider>
  );
};
