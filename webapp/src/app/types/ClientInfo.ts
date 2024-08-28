import {Address} from "./address";
import {BankData} from "./BankData";
import {ClientPreferences} from "./ClientPreferences";

export interface ClientInfo {
	id?: string; // Optional because it may not be available when creating new entries
	tenantId: string;
	companyName: string;
	abbreviation: string;
	firstName: string;
	lastName: string;
	bankData: BankData;
	email: string;
	supportEmail: string;
	phone: string;
	supportPhone: string;
	street: string;
	address: Address;
	website: string;
	taxNumber: string;
	logo: string;
	privacyPolicyUrl: string;
	extras: string;
	preferences: ClientPreferences;
	createdAt?: string; // Using string to represent ISO date format
	updatedAt?: string;
	updatedBy?: string;
}
