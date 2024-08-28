import {Person} from "./person";
import {InvoiceStatus} from "./enums/InvoiceStatus";

export type Invoice = &{
    
    id: number;
    invoiceNumber: string;
    invoiceDate: string;
    totalAmount: number;
    subtotal: number;
    taxes: number;
    fileId: number;
    invoiceStatus: InvoiceStatus;
    paymentDate: string;
    teacher: Person;
    signed: boolean;
    

}

