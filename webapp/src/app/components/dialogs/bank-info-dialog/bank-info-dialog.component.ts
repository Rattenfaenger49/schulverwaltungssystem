import {Component, ElementRef, Inject, inject, input, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {ibanValidator} from "../../../_services/iban-validator";
import {Subscription} from "rxjs";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef, MatDialogTitle
} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import {FormErrorDirective} from "../../../_services/directivs/form-error-directive";
import {UserService} from "../../../_services/data/user.service";
import {ToastrService} from "../../../_services/ToastrService";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";

@Component({
  selector: 'ys-bank-info-dialog',
  standalone: true,
    imports: [
        MatButton,
        MatDialogActions,
        MatDialogContent,
        MatDialogClose,
        MatDialogTitle,
        FormErrorDirective,
        ReactiveFormsModule,
        MatLabel,
        MatFormField,
        MatInput
    ],
  templateUrl: './bank-info-dialog.component.html',
  styleUrl: './bank-info-dialog.component.scss'
})
export class BankInfoDialogComponent {
  
  el = inject(ElementRef);
  toastr = inject(ToastrService);
  fb = inject(FormBuilder);
  userService = inject(UserService);
  form!: FormGroup;
  userId = signal<number | null>(null);
  constructor(      @Inject(MAT_DIALOG_DATA) public data: any,
                    private dialogRef: MatDialogRef<BankInfoDialogComponent>,
                    
  ) {
    this.userId.set(data);
    this.userService.getBankInfo(data).subscribe({
      next: res => {
        this.initiateForm(res.data);
      },
      error: (err) =>{
        console.error(err)
      }
    })

  }
 initiateForm(data: any){
  this.form = this.fb.group({
     id: [data?.id ?? null],
     accountHolderName: [data?.accountHolderName ?? "", Validators.required],
     bankName: [data?.bankName ?? "", [Validators.required, Validators.minLength(3)]],
     iban: [data?.iban ?? "", [ibanValidator()]],
     bic: [data?.bic ?? "", [Validators.required, Validators.minLength(8)],],
     user: [data?.user ?? {id: this.userId()}],
   });
 }
  onSave(){
    if(this.userId() == null ){
      this.toastr.error("Fehler", "Der Benutzer-ID wurde nicht gefunden!");
      return;
    }
    if( this.form.invalid ){
      this.toastr.error("Fehler", "Form ist nicht gültig!");
      return;
    }
    this.userService.saveBankInfo(this.form.value).subscribe({
      next: () => {
        this.dialogRef.close(true);
        this.toastr.success("Erfolgreich", "Bankverbindung wurde gespeichert!");
      }
    });
  
  }
  onClose(){
  
  }
}
