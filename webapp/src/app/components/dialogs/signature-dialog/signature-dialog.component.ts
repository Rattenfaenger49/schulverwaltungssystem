import {AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild} from '@angular/core';
import {MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import { ScreenOrientation } from '@capacitor/screen-orientation';
import {Capacitor} from '@capacitor/core';

import SignaturePad from "signature_pad";
import {colors} from "@angular/cli/src/utilities/color";

@Component({
  selector: "ys-signature-dialog",
  standalone: true,
  imports: [
    MatDialogActions,
    MatDialogContent,
    MatDialogTitle,
    MatButton,
  ],
  templateUrl: "./signature-dialog.component.html",
  styleUrl: "./signature-dialog.component.css",
})
export class SignatureDialogComponent implements AfterViewInit, OnInit {
  @ViewChild("canvas") public canvas!: ElementRef | null;
  currentOrientation: OrientationType | null = null;

  signaturePad!: SignaturePad;
  canvasWidth = 400;
  canvasHeight = 200;
  isNative: boolean;
  constructor(public dialogRef: MatDialogRef<SignatureDialogComponent>) {
    this.isNative = Capacitor.isNativePlatform() ? true : false;

    if(this.isNative)
      this.forceLandscapeOrientation();

  }
  ngOnInit() {
    //this.lockOrientation();
    window.screen.orientation.onchange = () => {
      this.currentOrientation = window.screen.orientation.type;
      //this.resize();

    };

// To unlock orientation which will default back to the global setting:
    window.screen.orientation.unlock();
  }
  lockOrientation() {
    ScreenOrientation.lock({
      orientation: 'landscape',
    });
  }
  ngAfterViewInit(): void {

    setTimeout(()=>{
      this.signaturePad = new SignaturePad(this.canvas!.nativeElement);
      this.signaturePad.minWidth = 6;
      this.signaturePad.dotSize =3;
      this.signaturePad.penColor = '#1537a5';
      this.resize();

    }, 50);



  }

  // FORCE to recreate signature pad
  @HostListener('window:resize', ['$event'])
  onResize() {
    this.resize();
    if(this.isNative)
      this.forceLandscapeOrientation();
  }

  resize() {
   // var ratio =   Math.max(window.devicePixelRatio || 1, 1);
    const ratio = 1;

    // Get the available width and height of the screen
    const screenWidth = window.innerWidth * 0.7;
    const screenHeight = window.innerHeight * 0.5;



    // Update the canvas width and height
    this.canvasWidth = screenWidth * ratio;
    this.canvasHeight = screenHeight * ratio;

     //Scale the canvas context
    const scaleFactor = screenWidth / this.canvas?.nativeElement.offsetWidth;
     const canvasContext = this.canvas?.nativeElement.getContext("2d");
     canvasContext.scale(scaleFactor , scaleFactor );

    // Clear the signature pad
    this.signaturePad?.clear();

  }

  dataURLToBlob(dataURL: any) {
    // Code taken from https://github.com/ebidel/filer.js
    var parts = dataURL.split(';base64,');
    var contentType = parts[0].split(":")[1];
    var raw = window.atob(parts[1]);
    var rawLength = raw.length;
    var uInt8Array = new Uint8Array(rawLength);

    for (var i = 0; i < rawLength; ++i) {
      uInt8Array[i] = raw.charCodeAt(i);
    }

    return new Blob([uInt8Array], { type: contentType });
  }



  save(): void {

    //const dataURL = this.signaturePad.toDataURL('image/svg+xml');

  }

  clearCanvas() {
    this.signaturePad.clear();

  }

  saveCanvas() {
    if (this.signaturePad.isEmpty()) {
      return;
    }
    const dataURL = this.signaturePad.toDataURL();
    this.dialogRef.close(dataURL);

  }
  closeDialog() {
    this.dialogRef.close();
  }
  async forceLandscapeOrientation() {
    try {
      const orientation = await ScreenOrientation.orientation();

      if (
          orientation.type === 'portrait-primary' ||
          orientation.type === 'portrait-secondary'
      ) {
        await ScreenOrientation.lock({orientation: 'landscape'});
      } else if (
          orientation.type === 'landscape-primary' ||
          orientation.type === 'landscape-secondary'
      ) {
        // Already in landscape mode, do nothing
      }
    } catch (error) {
      console.error('Error locking screen orientation:', error);
    }
  }

}
