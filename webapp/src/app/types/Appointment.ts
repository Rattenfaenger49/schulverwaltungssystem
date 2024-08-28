
import {ContractType} from "./enums/ContractType";
import { Person } from "./person";

export type Appointment = {
    id: number;
    title: string;
    content: string;
    startAt: string;
    endAt: string;
    organizer: Person;
    attendees: Person[];
    contractType: ContractType;
    status: string;

}

