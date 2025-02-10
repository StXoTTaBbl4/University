import {AfterViewInit, Component, ElementRef, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {SaBlockComponent} from '../../common-ui/sa-block/sa-block.component';
import {SidebarComponent} from '../../common-ui/sidebar/sidebar.component';
import {NgForOf} from '@angular/common';
import {SnackbarComponent} from '../../common-ui/snackbar/snackbar.component';

@Component({
  selector: 'app-self-assessment',
  imports: [
    SaBlockComponent,
    SidebarComponent,
    NgForOf,
    SnackbarComponent
  ],
  templateUrl: './self-assessment.component.html',
  standalone: true,
  styleUrl: './self-assessment.component.css'
})
export class SelfAssessmentComponent implements AfterViewInit {
  @ViewChildren('block', { read: ElementRef }) elements!: QueryList<ElementRef>;
  @ViewChild(SnackbarComponent) snackbar!: SnackbarComponent;

  blocks = ['Hardware', 'Software', 'Products']

  ngAfterViewInit(): void {
    this.elements.get(0)?.nativeElement.classList.toggle('hidden');
  }

  switchBlock(index: number) {
    for (let i = 0; i < this.elements.length; i++) {

      if (i != index) {
        // console.log(this.elements.get(i)?.nativeElement.id)
        this.elements.get(i)?.nativeElement.classList.add("hidden");
        continue;
      }
      // console.log("hit " + this.elements.get(i)?.nativeElement.id)
      this.elements.get(i)?.nativeElement.classList.remove("hidden");
    }
  }

  sendData(){
    this.snackbar.showSnackbar("Отправлено")
  }

}
