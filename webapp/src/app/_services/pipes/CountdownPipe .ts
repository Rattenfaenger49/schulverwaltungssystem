import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    standalone: true,
    name: 'countdown'
})
export class CountdownPipe implements PipeTransform {
    
    transform(ms: number): string {
        const totalSeconds = Math.max(Math.floor(ms / 1000), 0);
        const hours = Math.floor(totalSeconds / 3600);
        const minutes = Math.floor((totalSeconds % 3600) / 60);
        const seconds = totalSeconds % 60;
        
        return [
            String(hours).padStart(2, '0'),
            String(minutes).padStart(2, '0'),
            String(seconds).padStart(2, '0')
        ].join(':');
    }
}
