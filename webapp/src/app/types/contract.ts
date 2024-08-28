import {Student} from "./student";
import {Modul} from "./modul";
import {Contact} from "./contact";
import { Address } from "./address";
import { ContractType } from "./enums/ContractType";

export type Contract = {
    id: number;
    contractNumber: string;
    referenceContractNumber: string;
    startAt: Date;
    endAt: Date;
    status: string;
    address: Address;
    institution: string;
    comment: string;
    student: Student;
    moduls: Modul[];
    contact: Contact;
    contractType: ContractType;

}


