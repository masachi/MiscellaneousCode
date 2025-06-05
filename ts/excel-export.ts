import { File } from 'better-xlsx';
import { saveAs } from 'file-saver';
import max from 'lodash/max';
import dayjs from 'dayjs';
import { isEmptyValues, valueNullOrUndefinedReturnDash } from './utils';
import ExcelJS from 'exceljs';
import streamSaver from 'streamsaver';
import Constants from './constants';

export const DEFAULT_INTENT_STANDARD = 10;

const exportNoTranslateTypes = [
  Constants.DateFormatType.Currency,
  Constants.DateFormatType.CurrencyWithoutSuffix,
];

const enableExportExcelNG =
  (window as any).externalConfig?.['common']?.enableExportExcelNG ?? false;

const defaultExportExcludeIndexes = ['operation'];

function flattenColumns(columns: any[]) {
  let result = [];
  columns.forEach((item) => {
    result.push(item);
    if (item.children && Array.isArray(item.children)) {
      result = result.concat(flattenColumns(item.children));
    }
  });

  return result;
}

export function exportExcel(
  inputColumns: any[],
  tableDataSource: any[],
  fileName: string,
  exportExcludeIndexes: string[] = [],
) {
  console.error('export start', inputColumns, tableDataSource);

  // columns filter
  let columns = inputColumns?.filter((columnItem) => {
    return (
      // columnItem?.visible &&
      columnItem?.exportable &&
      columnItem?.valueType !== 'option' &&
      columnItem?.dataIndex !== 'operation' &&
      columnItem?.key !== 'operation'
    );
  });

  if (enableExportExcelNG === true) {
    exportExcelNG(columns, tableDataSource, fileName, exportExcludeIndexes);

    return;
  }
  exportExcludeIndexes = [
    ...exportExcludeIndexes,
    ...defaultExportExcludeIndexes,
  ];
  // 新建 工作表
  const file = new File();
  // 新建sheet
  const sheet = file.addSheet('sheet1');

  let columnItemRows = columns.map((columnItem) => {
    return getColumnDepth(columnItem);
  });
  let maximumRowNumber = max(columnItemRows);
  let allExportDataIndexes = getDataIndexes(columns).filter(
    (item) => !exportExcludeIndexes.includes(item),
  );
  // 初始化表头行
  for (let index = 0; index < maximumRowNumber; index++) {
    let row = sheet.addRow();
    for (let index = 0; index < allExportDataIndexes.length; index++) {
      row.addCell();
    }
  }
  // 构建表头 header
  calculateColumnItemRowColumnIndex(columns, 0, 0, 0);
  buildExcelColumnHeader(columns);

  // 构建datasource
  let hasIndentColumnIndexes = {};

  let flattenedColumns = flattenColumns(columns);
  flatDataSource(tableDataSource).forEach((dataSourceItem) => {
    let dataRow = sheet.addRow();
    allExportDataIndexes.forEach((key, index) => {
      let columnData = flattenedColumns.find(
        (columnItem) => columnItem.dataIndex === key,
      );
      let dataCell = dataRow.addCell();
      dataCell.value = valueNullOrUndefinedReturnDash(
        dataSourceItem[key],
        columnData?.['dataType'],
        columnData?.['scale'],
        exportNoTranslateTypes,
      );
      dataCell.style.align.v = 'center';
      // 默认横向在中间
      dataCell.style.align.h = 'center';

      if (dataSourceItem['left'] && columnData?.indent) {
        dataCell.style.align.indent =
          dataSourceItem['left'] / DEFAULT_INTENT_STANDARD;
        dataCell.style.align.h = 'left';

        hasIndentColumnIndexes[index] = Math.max(
          hasIndentColumnIndexes[index] || 0,
          dataSourceItem['left'] / DEFAULT_INTENT_STANDARD,
        );
      }
    });
  });

  // 设定宽度
  for (let i = 0; i < allExportDataIndexes.length; i++) {
    sheet.col(i).width = 15;
  }

  // 带有缩进 给到相对大一点 宽度 同时包含 缩进的列 只要不是horizontal right 全部改为 left
  if (Object.keys(hasIndentColumnIndexes).length > 0) {
    Object.keys(hasIndentColumnIndexes).forEach((index) => {
      let columnIndex = parseInt(index);
      sheet.col(columnIndex).width = 15 + hasIndentColumnIndexes[index] * 2;

      // 修改left
      for (
        let rowIndex = maximumRowNumber;
        rowIndex <= maximumRowNumber + tableDataSource.length;
        rowIndex++
      ) {
        let cell = sheet.cell(rowIndex, columnIndex);
        // TODO 这里还是做个配置项吧
        if (cell.style.align.h !== 'right' && cell.value !== '总计') {
          cell.style.align.h = 'left';
        }
      }
    });
  }

  console.error('export end', dayjs().format('YYYY-MM-DD HH:mm:ss'));

  file.saveAs('blob').then((content: any) => {
    saveAs(content, fileName + '.xlsx');
  });

  // 计算当前column格子的rowIndex， columnIndex，占多少格
  function calculateColumnItemRowColumnIndex(
    columns,
    sheetRowIndex,
    sheetColumnIndex,
    depth,
  ) {
    let latestIndex = sheetColumnIndex;
    let needExportColumns = columns
      .slice()
      .filter((item) => !exportExcludeIndexes.includes(item.dataIndex));

    needExportColumns.map((columnItem) => {
      columnItem['rowIndex'] = sheetRowIndex;
      columnItem['columnIndex'] = latestIndex;
      // 纵向
      columnItem['vMerge'] = columnItem?.children
        ? 0
        : maximumRowNumber - depth;
      // 横向
      columnItem['hMerge'] = columnItem?.children
        ? columnItem?.children?.length - 1
        : 0;

      if (columnItem.children && columnItem.children?.length > 0) {
        latestIndex = calculateColumnItemRowColumnIndex(
          columnItem.children,
          columnItem['rowIndex'] + 1,
          columnItem['columnIndex'],
          depth + 1,
        );
      } else {
        latestIndex = columnItem['columnIndex'];
      }

      latestIndex++;
    });

    return needExportColumns[needExportColumns.length - 1].columnIndex;
  }

  function buildExcelColumnHeader(columns) {
    columns
      .filter((item) => !exportExcludeIndexes.includes(item.dataIndex))
      .forEach((columnItem) => {
        let currentCell = sheet.cell(
          columnItem['rowIndex'],
          columnItem['columnIndex'],
        );

        currentCell.value = columnItem.titleLabel ?? columnItem.title;
        currentCell.style.align.h = 'center';
        currentCell.style.align.v = 'center';

        if (columnItem.hMerge > 0) {
          currentCell.hMerge = columnItem.hMerge;
        }

        if (columnItem.vMerge > 0) {
          currentCell.vMerge = columnItem.vMerge;
        }

        // 追加一个dataType和scale
        currentCell['dataType'] = columnItem?.['dataType'];
        currentCell['scale'] = columnItem?.['scale'];

        if (columnItem.children && columnItem?.children?.length > 0) {
          buildExcelColumnHeader(columnItem.children.slice());
        }
      });
  }
}

function getColumnDepth(columnItem) {
  return Array.isArray(columnItem.children) &&
    columnItem?.children &&
    columnItem?.children?.length > 0
    ? 1 + Math.max(0, ...columnItem.children?.map(getColumnDepth))
    : 0;
}

function getDataIndexes(columns) {
  let result = [];
  columns.forEach((item) => {
    if (!item.children) {
      result.push(item.dataIndex);
    }
    if (item.children && Array.isArray(item.children)) {
      result = result.concat(getDataIndexes(item.children));
    }
  });

  return result;
}

function flatDataSource(tableDataSource: any[]) {
  let result = [];
  tableDataSource.forEach((item) => {
    result.push(item);
    if (item.childs && Array.isArray(item.childs)) {
      result = result.concat(flatDataSource(item.childs));
    }
  });

  return result;
}

export const exportExcelNG = async (
  columns: any[],
  tableDataSource: any[],
  fileName: string,
  exportExcludeIndexes: string[] = [],
) => {
  console.error('export ng start', dayjs().format('YYYY-MM-DD HH:mm:ss'));

  exportExcludeIndexes = [
    ...exportExcludeIndexes,
    ...defaultExportExcludeIndexes,
  ];

  // 新建 工作表
  const workbook = new ExcelJS.Workbook();
  workbook.created = new Date();
  // 新建sheet
  const sheet = workbook.addWorksheet('sheet1');

  let columnItemRows = columns.map((columnItem) => {
    return getColumnDepth(columnItem);
  });

  let maximumRowNumber = max(columnItemRows) + 1;
  let allExportDataIndexes = getDataIndexes(columns).filter(
    (item) => !exportExcludeIndexes.includes(item),
  );

  // 初始化表头行
  for (let index = 1; index <= maximumRowNumber; index++) {
    sheet.addRow([index]).commit();
  }
  console.log('ddddd', columns);
  // 构建表头 header
  calculateColumnItemRowColumnIndex(columns, 1, 1, 0);
  buildExcelColumnHeader(columns);

  console.log('export header build end', dayjs().format('YYYY-MM-DD HH:mm:ss'));

  // 构建datasource
  let hasIndentColumnIndexes = {};

  let flattenedColumns = flattenColumns(columns);
  flatDataSource(tableDataSource).forEach((dataSourceItem) => {
    let dataRow = sheet.addRow([]);
    allExportDataIndexes.forEach((key, index) => {
      let columnData = flattenedColumns.find(
        (columnItem) => columnItem.dataIndex === key,
      );
      let dataCell = dataRow.getCell(index + 1);
      dataCell.value = valueNullOrUndefinedReturnDash(
        dataSourceItem[key],
        columnData?.['dataType'],
        columnData?.['scale'],
        exportNoTranslateTypes,
      );
      // 默认横向在中间
      dataCell.style.alignment = {
        vertical: 'middle',
        horizontal: 'center',
        wrapText: false,
        shrinkToFit: true,
      };

      if (dataSourceItem['left'] && columnData?.indent) {
        dataCell.style.alignment = {
          vertical: 'middle',
          horizontal: 'left',
          wrapText: false,
          shrinkToFit: true,
          indent: dataSourceItem['left'] / DEFAULT_INTENT_STANDARD,
        };

        hasIndentColumnIndexes[index] = Math.max(
          hasIndentColumnIndexes[index] || 0,
          dataSourceItem['left'] / DEFAULT_INTENT_STANDARD,
        );
      }
    });
  });

  console.log('export data build end', dayjs().format('YYYY-MM-DD HH:mm:ss'));

  // 宽度自适应
  sheet.columns.forEach((column, i) => {
    let maxLength = 0;
    column['eachCell']({ includeEmpty: true }, function (cell) {
      const columnLength = cell.value ? cell.value.toString().length : 10;
      if (columnLength > maxLength) {
        maxLength = columnLength;
      }
    });
    column.width = maxLength < 10 ? 10 : maxLength;
  });

  // 设定宽度
  // for (let i = 0; i < allExportDataIndexes.length; i++) {
  //   sheet.col(i).width = 15;
  // }
  //
  // // 带有缩进 给到相对大一点 宽度 同时包含 缩进的列 只要不是horizontal right 全部改为 left
  // if (Object.keys(hasIndentColumnIndexes).length > 0) {
  //   Object.keys(hasIndentColumnIndexes).forEach((index) => {
  //     let columnIndex = parseInt(index);
  //     sheet.col(columnIndex).width = 15 + hasIndentColumnIndexes[index] * 2;
  //
  //     // 修改left
  //     for (
  //       let rowIndex = maximumRowNumber;
  //       rowIndex <= maximumRowNumber + tableDataSource.length;
  //       rowIndex++
  //     ) {
  //       let cell = sheet.cell(rowIndex, columnIndex);
  //       // TODO 这里还是做个配置项吧
  //       if (cell.style.align.h !== 'right' && cell.value !== '总计') {
  //         cell.style.align.h = 'left';
  //       }
  //     }
  //   });
  // }

  finalizeWorkbook(workbook);
  console.log('export write start', dayjs().format('YYYY-MM-DD HH:mm:ss'));
  // 输出一下看看 怎么样先
  const buffer = await workbook.xlsx.writeBuffer();
  console.log('export write end', dayjs().format('YYYY-MM-DD HH:mm:ss'));
  const blob = new Blob([buffer]);
  saveAs(blob, fileName + '.xlsx');

  // const fileStream = streamSaver.createWriteStream(fileName + '.xlsx', {
  //   writableStrategy: undefined, // (optional)
  //   readableStrategy: undefined, // (optional)
  // });
  // const writer = fileStream.getWriter();
  // workbook.xlsx.write(writer).then(() => {
  //   console.log("export write end", dayjs().format('YYYY-MM-DD HH:mm:ss'));
  //   writer.close();
  // });

  // https://github.com/exceljs/exceljs/issues/2041#issuecomment-1110845468
  function internStyle<T extends Partial<ExcelJS.Style>>(
    internedStyles: Map<string, T>,
    style: T,
    type: number,
  ) {
    let buf = `${JSON.stringify(style)}${type}`;

    const internedStyle = internedStyles.get(buf);
    if (internedStyle) {
      return internedStyle;
    }

    const newInternedStyle = Object.freeze(Object.assign({}, style));
    internedStyles.set(buf, newInternedStyle);
    return newInternedStyle;
  }

  function finalizeWorkbook(workbook: ExcelJS.Workbook) {
    const internedStyles = new Map<string, Partial<ExcelJS.Style>>();
    workbook.worksheets.forEach((worksheet) => {
      (worksheet as any)._rows.forEach((row: ExcelJS.Row) => {
        (row as any)._cells.forEach((cell: ExcelJS.Cell) => {
          cell.style = internStyle(internedStyles, cell.style, cell.type);
        });
      });
    });
  }

  function calculateColumnItemRowColumnIndex(
    columns,
    sheetRowIndex,
    sheetColumnIndex,
    depth,
  ) {
    let latestIndex = sheetColumnIndex;
    let needExportColumns = columns
      .slice()
      .filter((item) => !exportExcludeIndexes.includes(item.dataIndex));

    needExportColumns.map((columnItem) => {
      columnItem['rowIndex'] = sheetRowIndex;
      columnItem['columnIndex'] = latestIndex;
      // 纵向
      columnItem['vMerge'] = isEmptyValues(columnItem?.children)
        ? 0
        : maximumRowNumber - depth - 1;
      // 横向
      columnItem['hMerge'] = columnItem?.children
        ? columnItem?.children?.length - 1
        : 0;

      if (columnItem.children && columnItem.children?.length > 0) {
        latestIndex = calculateColumnItemRowColumnIndex(
          columnItem.children,
          columnItem['rowIndex'] + 1,
          columnItem['columnIndex'],
          depth + 1,
        );
      } else {
        latestIndex = columnItem['columnIndex'];
      }

      latestIndex++;
    });

    return needExportColumns[needExportColumns.length - 1].columnIndex;
  }

  function buildExcelColumnHeader(columns) {
    columns
      .filter((item) => !exportExcludeIndexes.includes(item.dataIndex))
      .forEach((columnItem) => {
        let currentCell = sheet.getCell(
          columnItem['rowIndex'],
          columnItem['columnIndex'],
        );

        currentCell.value = columnItem.titleLabel ?? columnItem.title;
        currentCell.style.alignment = {
          vertical: 'middle',
          horizontal: 'center',
          wrapText: false,
          shrinkToFit: true,
        };

        if (columnItem.hMerge > 0) {
          // currentCell.hMerge = columnItem.hMerge;
          sheet.mergeCells(
            columnItem['rowIndex'],
            columnItem['columnIndex'],
            columnItem['rowIndex'] + columnItem?.hMerge,
            columnItem['columnIndex'],
          );
        }

        if (columnItem.vMerge > 0) {
          // currentCell.vMerge = columnItem.vMerge;
          sheet.mergeCells(
            columnItem['rowIndex'],
            columnItem['columnIndex'],
            columnItem['rowIndex'],
            columnItem['columnIndex'] + columnItem?.vMerge,
          );
        }

        // 追加一个dataType和scale
        currentCell['dataType'] = columnItem?.['dataType'];
        currentCell['scale'] = columnItem?.['scale'];

        if (columnItem.children && columnItem?.children?.length > 0) {
          buildExcelColumnHeader(columnItem.children.slice());
        }
      });
  }
};

/**
 * 自定义字段处理器类型定义
 * 用于处理特定字段的数据转换
 * @param value 字段值
 * @param record 当前记录
 * @returns 处理后的值
 */
export type FieldProcessor = (value: any, record: any) => any;

/**
 * 列宽配置类型定义
 */
export interface ColumnWidthConfig {
  [key: string]: number;
}

/**
 * 导出Excel配置选项
 */
export interface ExportExcelOptions {
  /**
   * 自定义字段处理器
   * key: 字段名
   * value: 处理函数
   */
  customProcessors?: {
    [key: string]: FieldProcessor;
  };
  /**
   * 自定义列宽配置
   * key: 字段名
   * value: 列宽
   */
  columnWidths?: ColumnWidthConfig;
  /**
   * 需要排除导出的字段
   */
  exportExcludeIndexes?: string[];
}

/**
 * 导出Excel（支持自定义处理器）
 * @param inputColumns 列定义
 * @param tableDataSource 数据源
 * @param fileName 文件名
 * @param options 导出配置选项或排除索引数组（向后兼容）
 */
export function exportExcelByClaudeForCode(
  inputColumns: any[],
  tableDataSource: any[],
  fileName: string,
  options: ExportExcelOptions | string[] = {},
) {
  // 处理向后兼容的情况
  let customProcessors = {};
  let columnWidths = {};
  let exportExcludeIndexes: string[] = [];

  if (Array.isArray(options)) {
    // 如果是数组，按旧接口处理为排除索引
    exportExcludeIndexes = options;
  } else {
    // 新接口，提取选项
    customProcessors = options.customProcessors || {};
    columnWidths = options.columnWidths || {};
    exportExcludeIndexes = options.exportExcludeIndexes || [];
  }

  // 过滤不需要导出的列
  let columns = inputColumns?.filter((columnItem) => {
    return (
      columnItem?.exportable &&
      columnItem?.valueType !== 'option' &&
      columnItem?.dataIndex !== 'operation' &&
      columnItem?.key !== 'operation'
    );
  });

  const finalExcludeIndexes = [
    ...exportExcludeIndexes,
    ...defaultExportExcludeIndexes,
  ];

  // 创建工作簿和工作表
  const file = new File();
  const sheet = file.addSheet('sheet1');

  // 计算表头深度
  const getHeaderDepth = (column: any): number => {
    if (!column.children || column.children.length === 0) return 1;
    return 1 + Math.max(...column.children.map(getHeaderDepth));
  };

  const headerDepth = Math.max(...columns.map(getHeaderDepth));

  // 获取所有叶子节点的dataIndex
  const getLeafDataIndexes = (columns: any[]): string[] => {
    const indexes: string[] = [];
    const traverse = (column: any) => {
      if (!column.children || column.children.length === 0) {
        if (!finalExcludeIndexes.includes(column.dataIndex)) {
          indexes.push(column.dataIndex);
        }
      } else {
        column.children.forEach(traverse);
      }
    };
    columns.forEach(traverse);
    return indexes;
  };

  const dataIndexes = getLeafDataIndexes(columns);

  // 初始化表头行
  const headerRows: any[] = [];
  for (let i = 0; i < headerDepth; i++) {
    const row = sheet.addRow();
    for (let j = 0; j < dataIndexes.length; j++) {
      row.addCell();
    }
    headerRows.push(row);
  }

  // 处理表头合并单元格
  const processHeader = (column: any, rowIndex: number, colSpanInfo: any) => {
    const cell = sheet.cell(rowIndex, colSpanInfo.currentCol);
    cell.value = column.titleLabel ?? column.title;
    cell.style.align.h = 'center';
    cell.style.align.v = 'center';

    if (!column.children || column.children.length === 0) {
      // 叶子节点，需要向下合并
      if (rowIndex < headerDepth - 1) {
        cell.vMerge = headerDepth - rowIndex - 1;
      }
      colSpanInfo.currentCol += 1;
    } else {
      // 非叶子节点，需要向右合并
      const childCount = column.children.reduce((count: number, child: any) => {
        return count + (child.children ? getLeafCount(child) : 1);
      }, 0);
      if (childCount > 1) {
        cell.hMerge = childCount - 1;
      }
      // 递归处理子节点
      column.children.forEach((child: any) => {
        processHeader(child, rowIndex + 1, colSpanInfo);
      });
    }
  };

  // 计算叶子节点数量
  const getLeafCount = (column: any): number => {
    if (!column.children || column.children.length === 0) return 1;
    return column.children.reduce((sum: number, child: any) => {
      return sum + getLeafCount(child);
    }, 0);
  };

  // 处理表头
  const colSpanInfo = { currentCol: 0 };
  columns.forEach((column) => {
    processHeader(column, 0, colSpanInfo);
  });

  // 默认的QualityCheckResults处理器，保持向后兼容
  const defaultProcessQualityCheckResults = (results: any[]) => {
    if (!results || !Array.isArray(results)) return '';

    return results
      .map((result) => {
        const { ErrMsg } = result;
        return ErrMsg;
      })
      .join('\n');
  };

  // 如果没有自定义处理器，添加默认的处理器
  if (!customProcessors['QualityCheckResults']) {
    customProcessors['QualityCheckResults'] = defaultProcessQualityCheckResults;
  }

  // 处理数据行
  const flattenedColumns = flattenColumns(columns);
  flatDataSource(tableDataSource).forEach((dataItem) => {
    const row = sheet.addRow();
    dataIndexes.forEach((dataIndex) => {
      const cell = row.addCell();
      const columnConfig = flattenedColumns.find(
        (col) => col.dataIndex === dataIndex,
      );

      // 使用自定义处理器处理字段或数组类型字段
      if (
        customProcessors[dataIndex] &&
        (Array.isArray(dataItem[dataIndex]) ||
          dataItem[dataIndex] !== undefined)
      ) {
        cell.value = customProcessors[dataIndex](dataItem[dataIndex], dataItem);
      } else {
        cell.value = valueNullOrUndefinedReturnDash(
          dataItem[dataIndex],
          columnConfig?.['dataType'],
          columnConfig?.['scale'],
          exportNoTranslateTypes,
        );
      }

      cell.style.align.v = 'center';
      cell.style.align.h = 'center';

      // 处理缩进
      if (dataItem['left'] && columnConfig?.indent) {
        cell.style.align.indent = dataItem['left'] / DEFAULT_INTENT_STANDARD;
        cell.style.align.h = 'left';
      }
    });
  });

  // 设置列宽
  dataIndexes.forEach((dataIndex, index) => {
    // 使用自定义列宽配置
    if (columnWidths[dataIndex]) {
      sheet.col(index).width = columnWidths[dataIndex];
    } else if (dataIndex === 'QualityCheckResults') {
      // 为QualityCheckResults列保持原有的宽度设置
      sheet.col(index).width = 50;
    } else {
      sheet.col(index).width = 15; // 默认列宽
    }
  });

  // 导出文件
  file.saveAs('blob').then((content: any) => {
    saveAs(content, fileName + '.xlsx');
  });
}

/**
 * 通用数组处理器生成函数
 * 用于生成处理数组类型字段的处理器
 * @param formatter 每个元素的格式化函数
 * @param separator 元素之间的分隔符
 * @returns 处理器函数
 */
export const createArrayProcessor = (
  formatter: (item: any, index: number) => string,
  separator: string = '\n',
): FieldProcessor => {
  return (value, _record) => {
    if (!value || !Array.isArray(value)) return '';
    return value.map(formatter).join(separator);
  };
};
