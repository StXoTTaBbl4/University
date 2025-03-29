import {Component, ElementRef, ViewChild, AfterViewInit} from '@angular/core';

@Component({
  selector: 'snackbar',
  imports: [],
  templateUrl: './snackbar.component.html',
  standalone: true,
  styleUrl: './snackbar.component.css'
})
export class SnackbarComponent implements AfterViewInit{
  message: string = '';

  @ViewChild('snackbar') snackbar!: ElementRef;

  ngAfterViewInit() {
  }

  showSnackbar(msg: string){
    if (!msg){
      return;
    }

    let snackbarEl = this.snackbar.nativeElement;

    this.message = msg;
    snackbarEl.classList.add("show");
    // snackbarEl.addEventListener('animationend', () => {
    //   snackbarEl.classList.remove('show');
    // }, { once: true });
    setTimeout(() =>{ this.snackbar.nativeElement.classList.remove("show"); }, 3500);
  }
}
