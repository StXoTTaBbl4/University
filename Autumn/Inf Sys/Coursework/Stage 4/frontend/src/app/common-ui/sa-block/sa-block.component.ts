import {AfterViewInit, Component, ElementRef, HostListener, Input, OnInit} from '@angular/core';
import {KeyValuePipe, NgForOf} from '@angular/common';
import {MatSnackBar} from '@angular/material/snack-bar';
import {HttpClient} from '@angular/common/http';
import {FormsModule} from '@angular/forms';


interface ProductTask {
  id: number;
  product: { id: number; product: string; blockType: string };
  task: string;
}

interface Level {
  id: number;
  weight: number;
  level: string;
  description: string;
}

@Component({
  selector: 'sa-block',
  imports: [
    NgForOf,
    KeyValuePipe,
    FormsModule
  ],
  templateUrl: './sa-block.component.html',
  standalone: true,
  styleUrl: './sa-block.component.css'
})
export class SaBlockComponent implements OnInit {
  @Input() blockType!: string;
  @Input() levels!: Level[];

  groupedTasks: { [key: number]: ProductTask[] } = {};

  ngOnInit(): void {
    this.requestTasks(this.blockType);
  }

  constructor(private http: HttpClient, private snackbar: MatSnackBar, private el: ElementRef) {}

  requestTasks(block: string) {
    this.http.get<{ tasks: ProductTask[] }>(`http://localhost:8080/api/assessment/${block}/tasks`, { withCredentials: true })
      .subscribe({
        next: (response) => {
          console.log('Ответ от сервера:', response);
          this.groupedTasks = this.groupByProductId(response.tasks);
          console.log(this.groupedTasks);
        },
        error: (error) => {
          console.error('Ошибка:', error);
          this.snackbar.open(error.error?.message || 'Ошибка запроса', 'Закрыть', { duration: 3000 });
        }
      });
  }

  private groupByProductId(tasks: ProductTask[]): { [key: number]: ProductTask[] } {
    if (!Array.isArray(tasks)) {
      console.error('Ожидался массив, получено:', tasks);
      return {};
    }
    return tasks.reduce((acc, task) => {
      const productId = task.product.id;
      if (!acc[productId]) {
        acc[productId] = [];
      }
      acc[productId].push(task);
      return acc;
    }, {} as { [key: number]: ProductTask[] });
  }



  @HostListener('change', ['$event'])
  onRadioChange(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.type === 'radio') {
      const div = target.closest('.question') as HTMLDivElement // Получаем div, содержащий вопрос
      div.classList.add("question_answered")
      // console.log('Выбран вариант:', target.value);
      // console.log('Вопросный блок:', div);
    }
  }
}
