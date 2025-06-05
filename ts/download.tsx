        //   downloadFile(
        //     `住院病案首页-${originDmrCardInfo?.CardFlat?.PatName}`,
        //     exportResponse?.response,
        //   );


import dayjs from 'dayjs';
import * as mime from 'mime-types';

export enum UseDispostionEnum {
  nouse = 'NOUSE',
  replace = 'REPLACE',
  combineWithNameFront = 'COMBINEWITHNAMEFRONT',
  combineWithDispostionFront = 'combineWithDispostionFront',
  custom = 'CUSTOM',
}

const DispositionToString = (contentDisposition) => {
  let filenameRegex =
    /(?:.*filename\*|filename)=(?:([^'"]*)''|("))([^;]+)\2(?:[;`\n]|$)/;
  let matches = filenameRegex.exec(contentDisposition);
  if (matches != null && matches?.length) {
    return matches?.at(-1).replace(/['"]/g, '');
  }
  return '';
};

export const downloadFile = (
  exportName: string,
  response: any,
  useDispostionType: UseDispostionEnum = UseDispostionEnum.nouse,
) => {
  let contentType = response.headers.get('Content-Type');
  let contentDisposition = response.headers.get('Content-Disposition');
  let dispoStr = decodeURI(DispositionToString(contentDisposition)).split('.');
  let fileExtensionByContentType = mime.extension(contentType) ?? 'bin';
  console.log('contentDisposition', contentDisposition);
  switch (useDispostionType) {
    case UseDispostionEnum.custom:
      break;
    case UseDispostionEnum.replace:
      exportName =
        (dispoStr?.at(0) ?? exportName) +
        `-${dayjs().format('YYYYMMDD_HHmmss')}` +
        `.${dispoStr?.at(1) ?? fileExtensionByContentType}`;
      break;
    case UseDispostionEnum.combineWithNameFront:
    case UseDispostionEnum.combineWithDispostionFront:
      exportName =
        (useDispostionType === UseDispostionEnum.combineWithNameFront
          ? exportName + dispoStr?.at(0)
          : dispoStr?.at(0) + exportName) +
        `-${dayjs().format('YYYYMMDD_HHmmss')}` +
        `.${dispoStr?.at(1) ?? fileExtensionByContentType}`;
      break;
    case UseDispostionEnum.nouse:
    default:
      exportName =
        exportName +
        `-${dayjs().format('YYYYMMDD_HHmmss')}` +
        `.${dispoStr?.at(1) ?? fileExtensionByContentType}`;
      break;
  }

  response.blob().then((blobStream) => {
    const blob = new Blob([blobStream], {
      type: contentType,
    });
    let link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = exportName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  });
};
