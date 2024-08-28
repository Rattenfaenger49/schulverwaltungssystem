// dynamic-style.directive.ts
import {Directive, Input, ElementRef, Renderer2, OnChanges, numberAttribute} from '@angular/core';

@Directive({
  standalone: true,
  selector: "[DProgressBar]",
})
export class DProgressBar implements OnChanges {
  @Input({ transform: numberAttribute }) percent!: number;

  constructor(
    private el: ElementRef,
    private renderer: Renderer2,
  ) {}

  ngOnChanges() {
    this.updateStyle();
  }

  private updateStyle() {
    // Example: Apply dynamic width based on the percent value
    const width = this.percent * 100;
    this.renderer.setStyle(
      this.el.nativeElement,
      "width",
      width.toFixed(2) + "%",
    );
    this.el.nativeElement.innerText = Math.round(width) + "%";

    // You can add more dynamic styling logic here based on your requirements
  }
}
