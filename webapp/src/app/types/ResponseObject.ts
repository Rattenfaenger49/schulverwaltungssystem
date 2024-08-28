import {Page} from "./page";

export type ResponseObject<T> = {
    status: string;
    message: string;
    data: T | Page<T> | any;

}
// TODO deine status as enum and provide all possibles one
