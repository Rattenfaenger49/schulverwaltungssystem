export enum ContractStatus {
	ACTIVE = "Aktive",
	INACTIVE = "Inaktive",
	TERMINATED = "Beendet",
	BLOCKED = "Gesperrt",
	IN_PROGRESS = "In Bearbeitung",
	ANY = "Alle",
}

export const ContractStatusList = Object.entries(ContractStatus)
.map(([key, value]) => ({key, value}))
