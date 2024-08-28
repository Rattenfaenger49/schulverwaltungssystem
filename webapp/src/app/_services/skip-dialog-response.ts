import { HttpContextToken } from "@angular/common/http";

export const SkipDialogResponse = new HttpContextToken<boolean>(() => false);
