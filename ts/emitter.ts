import EventEmitter from 'eventemitter3';
const eventEmitter = new EventEmitter();

(window as any).eventEmitter = eventEmitter;

const Emitter = {
  // 修改一下on的实现
  on: (
    event: string,
    fn: (data: any) => void,
    singleton: boolean = false,
    replace: boolean = false,
  ) => {
    if (singleton === true) {
      // 当只允许 一个实例
      // 尤其是用于 tabs 这样的情况下 因为多个tab item 重复加载导致 弹多个窗
      // 现在只发现有 DetailTableModal 中可能出现
      let registeredEventNames = eventEmitter.eventNames();
      if (registeredEventNames?.includes(event)) {
        if (replace === true) {
          eventEmitter.off(event);
          eventEmitter.on(event, fn);
        }
        // 跳过注册 以防止 多个弹窗
        console.warn('已经注册：', event);
      } else {
        eventEmitter.on(event, fn);
      }
    } else {
      eventEmitter.on(event, fn);
    }
  },
  onMultiple: (events: string[], fn: (data: any) => void) => {
    events.forEach((event) => {
      eventEmitter.on(event, fn);
    });
  },
  once: (event: string, fn: (data: any) => void) =>
    eventEmitter.once(event, fn),
  off: (event: string) => eventEmitter.off(event),
  offMultiple: (events: string[]) => {
    events.forEach((event) => {
      eventEmitter.off(event);
    });
  },
  emit: (events: string | string[], payload: any = null) => {
    if (typeof events === 'string') {
      eventEmitter.emit(events, payload);
    } else if (Array.isArray(events)) {
      events.forEach((event) => {
        eventEmitter.emit(event, payload);
      });
    }
  },
};
Object.freeze(Emitter);