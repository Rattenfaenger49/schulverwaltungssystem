import {translateKey} from "../translations/translations";

export function getKeys(data: any, filteredKeys: string []): { name: string, translation: string }[] {
	if(!data)
		return [];
	return Object.keys(data).filter(
		(header) =>  header != '' && !filteredKeys.includes(header)
	).map(header => ({name: header, translation: translateKey(header)}));
}

export function getSortableKeys(data:  { name: string, translation: string }[], isNotSortable: string []): { name: string, translation: string }[] {
	if(data.length  === 0)
		return [];
	return data.filter(e => e.name !== '' &&  !isNotSortable.includes(e.name) );
}
