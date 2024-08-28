import {Admin} from "./admin";
import {Contract} from "./contract";
import {Person} from "./person";

export type Student = Admin &{
    level: string;
    teachers: Person[];
    contracts: Contract[];
    parent: Parent;
}

export type Parent = {
    gender: string;
    firnstName: string;
    lastName: string;
    Email: string;
    phoneNumber: string;
}
