const DEFAULT_RATIO = 1.5;
const TEN_EIGHTY_P_RATIO = 1.1;

export const getDevicePixelRatio = () => {
  return window.devicePixelRatio;
};

export const isDevicePixelRatioMax = () => {
  const screenWidth = window.screen.width;
  if (screenWidth === 1920) {
    return getDevicePixelRatio() >= 1.25;
  }

  return getDevicePixelRatio() >= DEFAULT_RATIO;
};

export const matchMediaQueryString = `(resolution: ${window.devicePixelRatio}dppx)`;

export const getZoomLevel = () => {
  let devicePixelRatio = window.devicePixelRatio;

  const screenWidth = window.screen.width;
  if (screenWidth === 1920) {
    if (devicePixelRatio <= TEN_EIGHTY_P_RATIO) {
      return 1;
    }

    return (
      Math.round((TEN_EIGHTY_P_RATIO / window.devicePixelRatio) * 100) / 100
    );
  }

  if (devicePixelRatio <= DEFAULT_RATIO) {
    return 1;
  }

  return Math.round((DEFAULT_RATIO / window.devicePixelRatio) * 100) / 100;
};
