import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    standalone: true,
    name: 'renderContent'
})
export class RenderContentPipe implements PipeTransform {
    transform(value: any): any {
        if (Array.isArray(value)) {
            return value;
        } else {
            return [value];
        }
    }
}
