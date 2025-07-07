import { Form, Input } from 'antd';
import React, { useEffect, useState } from 'react';
import './index.less';
import { Emitter, EventConstant } from '@uni/utils/src/emitter';
import {
  getDeletePressEventKey,
  numberInputRestrictKeyDown,
} from '../../utils';
import { isEmptyValues } from '@uni/utils/src/utils';

interface RestrictInputNumberProps {
  itemId?: string;
  form?: any;
  min?: number;
  max?: number;
  formKey?: string | string[];
  width?: number;

  step?: number;

  precious?: number;

  value?: any;
  onChange?: (value: any) => void;

  disabled?: boolean;

  disableCondition?: (value: any) => boolean;

  conditionKey?: string;
}

const RestrictInputNumber = (props: RestrictInputNumberProps) => {
  const [value, setValue] = useState(props?.value);

  const formValue = Form.useWatch(props?.formKey, props?.form);

  useEffect(() => {
    Emitter.on(getDeletePressEventKey(props?.formKey), (key) => {
      if (key.includes(props?.formKey)) {
        // 表示就是当前组件的formKey
        setValue(undefined);
        props?.onChange && props?.onChange(undefined);
      }
    });

    return () => {
      Emitter.off(getDeletePressEventKey(props?.formKey));
    };
  }, []);

  let conditionValue = undefined;
  if (props?.conditionKey) {
    const form = Form.useFormInstance();
    conditionValue = Form.useWatch(props?.conditionKey, form);
  }

  useEffect(() => {
    setValue(formValue ?? props?.value);
  }, [formValue, props?.value]);

  return (
    <div className={'input-remove-arrow'}>
      <Input
        min={props?.min || 0}
        max={props?.max || Number.MAX_VALUE}
        id={props?.itemId ?? `formItem#${props.formKey}#RestrictInputNumber`}
        className={'restrict-input-number-container'}
        style={{ width: props?.width }}
        bordered={false}
        value={value ?? ''}
        type={'text'}
        step={props?.step || 0.01}
        contentEditable={true}
        disabled={
          props?.disabled ||
          (props?.disableCondition
            ? props?.disableCondition(conditionValue)
            : false)
        }
        onKeyDown={(event) => {
          let extraKeys = [];
          if ((props?.min || 0) < 0) {
            // 最小值小于0可以允许-
            extraKeys.push('-');
          }

          if (!isEmptyValues(props?.precious) && props?.precious !== 0) {
            // 指定precious 并且 precious为0 允许 dot
            extraKeys.push('.');
          }

          numberInputRestrictKeyDown(event, extraKeys);
        }}
        onBlur={(event) => {
          if (value) {
            if (parseFloat(value) < (props.min || 0)) {
              setValue(props?.min || 0);
              props?.onChange && props?.onChange(props?.min || 0);
            }
          }
        }}
        onChange={(event) => {
          let value = event.target.value;

          // 点位之后的东西
          if (!isEmptyValues(props?.precious)) {
            if (props?.precious === 0) {
              if (/^\d+\.\d*$/.test(value?.toString())) {
                value = Math.trunc(parseFloat(value))?.toString();
              }
            } else {
              let regex = new RegExp(`^\\d+\\.\\d{${props?.precious + 1},}$`);
              if (regex.test(value?.toString())) {
                value = parseFloat(value)?.toFixed(props?.precious);
              }
            }
          }

          // 最大最小值
          if (props?.max) {
            if (parseFloat(value) > props.max) {
              value = props?.max.toString();
            }
          }

          if (/^-.*$/.test(value?.toString())) {
            value = '';
          }

          setValue(value);
          props?.onChange && props?.onChange(value);
        }}
      />
    </div>
  );
};

export default RestrictInputNumber;
