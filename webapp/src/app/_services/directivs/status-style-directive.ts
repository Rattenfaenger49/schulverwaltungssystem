import {AfterViewInit, Directive, ElementRef, OnChanges, Renderer2} from '@angular/core';
@Directive({
    selector: '[status]',
    standalone: true,
})
export class StatusStyleDirective implements  AfterViewInit{

    private statusClassMap: { [key: string]: string } = {
        'ACTIVE': 'status-active',
        'INACTIV': 'status-inactive',
        'BLOCKED': 'status-archived',
        'IN_PROGRESS': 'status-archived',
        'TERMINATED': 'status-archived',
        // Add more status-class mappings as needed
    };
    constructor(private el: ElementRef, private renderer: Renderer2) {}



    ngAfterViewInit(): void {
        const status = this.el.nativeElement.innerText;
        this.renderer.addClass(this.el.nativeElement, this.statusClassMap[status]);
    }


}
