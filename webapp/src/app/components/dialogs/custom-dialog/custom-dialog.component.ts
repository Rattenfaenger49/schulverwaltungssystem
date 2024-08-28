import {Component, EventEmitter, Inject, Output, Input} from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from "@angular/material/dialog";
import {zoomAnimation} from "../../../animations/zoomAnimation";
import { UpperCasePipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';


export interface DialogData {
  // 'warning' | 'success' | 'ask' | 'hint'
  type: string;
  message: string;
  buttons?: { text: string; value: any }[] ;
}
// TODO clean the component
// customize better for each type
// warning has to be confirm and buttns send with the openDialog method data input
@Component({
    selector: 'ys-custom-dialog',
    templateUrl: './custom-dialog.component.html',
    styleUrls: ['./custom-dialog.component.css'],
    animations: [zoomAnimation],
    standalone: true,
    imports: [MatDialogModule, MatIconModule, MatButtonModule, UpperCasePipe]
})
export class CustomDialogComponent {
  @Input() data: DialogData;
  @Output() dialogResult = new EventEmitter<any>();

  constructor(
      public dialogRef: MatDialogRef<CustomDialogComponent>,
      @Inject(MAT_DIALOG_DATA) public dialogData: DialogData
  ) {
    this.data = dialogData;
  }

  getIcon(): string {
    // Determine icon based on the type (customize as needed)
    switch (this.data.type) {
      case 'warning':
        return 'warning';
      case 'success':
        return 'check_circle';
      case 'ask':
        return 'help';
      case 'hint':
        return 'lightbulb';
      default:
        return 'info';
    }
  }

  onClick(value: any): void {
    this.dialogResult.emit(value);
    this.dialogRef.close();
  }


}
