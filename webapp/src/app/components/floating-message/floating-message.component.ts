import {
    Component,
    EventEmitter, inject,
    input,
    OnChanges,
    Output,
    SimpleChanges,
} from "@angular/core";
import {fadeInOut} from "../../animations/fade-in-out";
import {
  InfoMessageType,
  InfoMessageTypeClasses,
} from "../../types/Info-message";
import { NgClass } from '@angular/common';

@Component({
    selector: 'ys-floating-message',
    templateUrl: './floating-message.component.html',
    styleUrls: ['./floating-message.component.css'],
    animations: [fadeInOut],
    standalone: true,
    imports: [NgClass]
})
export class FloatingMessageComponent {
  show = input.required<boolean>() ;
  message= input.required<string>() ;
  closeAfter= input<number>(5) ;
  type= input<InfoMessageType>(InfoMessageType.WARNING) ;
  

  hidden = true;
  @Output()
  reset = new EventEmitter<void>();
  close(){
    this.hidden = true;
    this.reset.emit();
  }


  protected readonly InfoMessageTypeClasses = InfoMessageTypeClasses;

  ngOnChanges(changes: SimpleChanges): void {

    if (this.show()) {
        this.hidden = false;
      setTimeout(() => {
        this.close();

      }, this.closeAfter() * 1000); // Hide after 5 seconds
    }
  }
}


