import {ContractStatus} from "./ContractStatus";

export enum ContractType {
    WEEK = "wöchentlich",
    MONTH = "monatlich",
    PERIOD = "zeitraum",
    INDIVIDUALLY = "individuell",
    ANY = "Alle"
}

export const ContractTypeList = Object.entries(ContractType)
    .map(([key, value]) => ({key, value}))
