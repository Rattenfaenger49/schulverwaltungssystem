import {Component, EventEmitter, inject, input, model, Output, signal} from '@angular/core';

import {finalize, of} from "rxjs";
import {HttpEventType, HttpResponse} from "@angular/common/http";

import {UserService} from "../../../_services/data/user.service";
import {MatDialog} from "@angular/material/dialog";
import {DProgressBar} from "../../../_services/directivs/DProgressBar";
import {MatFormField, MatInput} from "@angular/material/input";
import {MatMiniFabButton} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {ResponseService} from "../../../_services/ResponseService";
import {FileMetadata, FileUploadMetadata} from "../../../types/FileMetadata";
import {Invoice} from "../../../types/Invoice";
import {TooltipModule} from "ngx-bootstrap/tooltip";

interface FileInfo {
    fileName: string;
    progress: number;

}
@Component({
  selector: "ys-upload-file",
  standalone: true,
  imports: [DProgressBar, MatInput, MatMiniFabButton, MatFormField, MatIconModule, TooltipModule],
  templateUrl: "./upload-file.component.html",
  styleUrls: ["./upload-file.component.scss"],
})
export class UploadFileComponent {
  fileList =  signal<FileInfo[]>([]);
  files = model<File[]>([]);

  selectedFile!: File;
  @Output()fileUploaded: EventEmitter<FileMetadata| Invoice| null> = new EventEmitter<FileMetadata| Invoice| null>();
  fileTypes = input<string[]>([]);
  metadata = input.required<FileUploadMetadata>();
  responseService = inject(ResponseService);
  userService = inject(UserService);
  dialog = inject(MatDialog);


  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0] as File;
    this.files.update(a => [...a, this.selectedFile]);
    const extension = this.getFileExtension(this.selectedFile.name);
    if (!this.fileTypes().includes(extension)) {
      return;
    }
    if(!this.selectedFile) return;

  this.uploadFile();
  }
  getFileExtension(filename: string): string {
    const lastDotIndex = filename.lastIndexOf('.');
    if (lastDotIndex === -1) {
      return ''; // No extension found
    }
    return filename.slice(lastDotIndex + 1);
  }
  uploadFile(): void {
    if (!this.selectedFile) {
      return;
    }

    const formData: FormData = new FormData();
    const fileName = this.selectedFile.name;
    formData.append('file', this.selectedFile, fileName);
    formData.append('metadata', JSON.stringify(this.metadata()));
    
    this.fileList.set([...this.fileList(), {fileName: fileName, progress: 0}]);
    this.userService.uploadFile(formData).pipe(
        finalize(() => {
          this.fileList.set(this.fileList().filter(file => file.fileName !== fileName)) ;
        })
    ).subscribe(
        {
          next: (res) => {
            if (res.type === HttpEventType.UploadProgress) {
              const percentDone = Math.round(100 * res.loaded / (res.total ?? 1));
              const file = this.fileList().find(file => file.fileName === fileName);
              if (file) {
                file.progress = percentDone;
              }

            } else if (res instanceof HttpResponse) {
              this.fileUploaded.emit(res?.body?.data);
            }
          },
          error: (err) => {
            // this.fileUploaded.emit(null);
            console.error(err);
          }
        });
  }


  protected readonly of = of;
}
