import {Person} from "./person";

export type BankData = {
	id: number;
	bankName: string;
	bic: string;
	iban: string;
	accountHolderName: string;
	user: Person;
}
