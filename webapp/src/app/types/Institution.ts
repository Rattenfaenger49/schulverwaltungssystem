import {Address} from "./address";
import {Contact} from "./contact";

export type Institution = {
    id: number;
    name: string;
    email: string;
    phoneNumber: string;
    address: Address;
    contacts : Contact[];
}
