import {AfterViewInit, Component, ElementRef, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {SaBlockComponent} from '../../common-ui/sa-block/sa-block.component';
import {SidebarComponent} from '../../common-ui/sidebar/sidebar.component';
import {NgForOf} from '@angular/common';
import {SnackbarComponent} from '../../common-ui/snackbar/snackbar.component';
import {HttpClient} from '@angular/common/http';
import {FormsModule} from '@angular/forms';

interface Level {
  id: number;
  weight: number;
  level: string;
  description: string;
}

@Component({
  selector: 'app-self-assessment',
  imports: [
    SaBlockComponent,
    SidebarComponent,
    NgForOf,
    SnackbarComponent,
    FormsModule
  ],
  templateUrl: './self-assessment.component.html',
  standalone: true,
  styleUrl: './self-assessment.component.css'
})
export class SelfAssessmentComponent implements AfterViewInit {
  @ViewChild('assessmentForm') assessmentForm: any;
  @ViewChildren('block', { read: ElementRef }) elements!: QueryList<ElementRef>;
  @ViewChild(SnackbarComponent) snackbar!: SnackbarComponent;


  constructor(private http: HttpClient) {
  }
  levels_for_blocks: Level[] = [];
  blocks = ["Hardware", "Software", "Processes"]

  ngAfterViewInit(): void {
    this.elements.get(0)?.nativeElement.classList.toggle('hidden');
    this.requestLevels()
  }

  requestLevels() {
    this.http.get<{levels: Level[]}>('http://localhost:8080/api/assessment/levels', { withCredentials: true })
      .subscribe({
        next: (response) => {
          console.log('Полученные уровни:', response.levels);
          this.levels_for_blocks = response.levels; // Сохраняем в переменной
        },
        error: (error) => {
          console.error('Ошибка при загрузке уровней:', error);
        }
      });
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

  getSelectedRadios(divId: string): { name: string; value: string }[] {
    const divElement = document.getElementById(divId);
    if (!divElement) {
      console.warn(`Div с id="${divId}" не найден.`);
      return [];
    }

    const selectedRadios: { name: string; value: string }[] = [];

    divElement.querySelectorAll<HTMLInputElement>('input[type=radio]:checked').forEach((radio) => {
      selectedRadios.push({ name: radio.name, value: radio.value });
    });

    return selectedRadios;
  }

  sendData(){
    let hw = this.getSelectedRadios('Hardware');
    let sw = this.getSelectedRadios('Software');
    let pr = this.getSelectedRadios('Processes');

    this.http.post('http://localhost:8080/api/assessment/submit-assessment', {'hw': hw,'sw': sw, 'pr': pr}, { withCredentials: true })
      .subscribe({
        next: (response) => {
          console.log('Форма успешно отправлена:', response);
        },
        error: (error) => {
          console.error('Ошибка при отправке формы:', error);
        }
      });
  }

}
