import { inject, Injectable} from '@angular/core';
import {ResponseObject} from "../types/ResponseObject";
import {MatDialog} from "@angular/material/dialog";
import {
	DialogDataResponse,
	ResponseDialogComponent
} from "../components/dialogs/response-dialog/response-dialog.component";


@Injectable({
	providedIn: 'root'
})
export class ResponseService {
	
	dialog = inject(MatDialog);
	
	responseDialog(res: ResponseObject<any>, status: boolean, callback?: () => void) {
		const data: DialogDataResponse = {
			response: res,
			success: status
		};
		
		this.dialog.open(ResponseDialogComponent,
			{
				data
			}).afterClosed().subscribe({
			next: () => {
				if (callback)
					callback();
			}
		});
	}
	

}
/*
* ,
  "overrides": {
    "@angular-material-components/datetime-picker": {
      "@angular/platform-browser": ">=16.0.0",
      "@angular/common": ">=16.0.0",
      "@angular/core": ">=16.0.0",
      "@angular/forms": ">=16.0.0",
      "@angular/material": ">=16.0.0",
      "@angular/cdk": ">=16.0.0"
    },
    "@angular-material-components/moment-adapter": {
      "@angular/common": ">=16.0.0",
      "@angular/core": ">=16.0.0"
    },
    "@angular/material-moment-adapter": {
      "@angular/platform-browser": ">=16.0.0",
      "@angular/common": ">=16.0.0",
      "@angular/core": ">=16.0.0",
      "@angular/forms": ">=16.0.0",
      "@angular/material": ">=16.0.0",
      "@angular/cdk": ">=16.0.0"
    }
  }*/
