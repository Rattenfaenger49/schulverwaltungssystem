import {Address} from "./address";
import { Person } from "./person";

export type Admin = Person &{

    comment: string;
    verified:boolean;
    markedForDeletion:boolean;
    address: Address;

}

