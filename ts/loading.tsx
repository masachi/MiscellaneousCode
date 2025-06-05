import React from 'react';
import './loading.less';

export const Loading = () => {
  return (
    <div id="loading">
      <span className="loader"></span>

      <span className={'loading-label'}>加载中</span>
    </div>
  );
};

export default Loading;
