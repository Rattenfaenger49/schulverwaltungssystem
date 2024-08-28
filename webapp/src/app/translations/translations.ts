export const translations: { [key: string]: string } = {
	gender: 'Geschlecht',
	username: 'Email',
	firstName: 'Vorname',
	lastName: 'Name',
	startAt: 'Beginn',
	endAt: 'Ende',
	phoneNumber: 'Telefone',
	contractNumber: 'Vertragsnummer',
	student: 'Schüler/in',
	students: 'Schüler',
	teacher: 'Lehrer/in',
	status: 'Status',
	modul: 'Fach',
	isSigned: 'Unterschrieben',
	birthdate: 'Geburtsdatum',
	referenceContractNumber:'Referenznummer',
	contractType :'Vertragstype',
	// Add more translations as needed
};
export function translateKey(headerName: string) {
	// Translate each header name using the translations object
	return translations[headerName] || headerName;
}
