import {Component, ElementRef, HostListener} from '@angular/core';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'sa-block',
  imports: [
    NgForOf
  ],
  templateUrl: './sa-block.component.html',
  standalone: true,
  styleUrl: './sa-block.component.css'
})
export class SaBlockComponent {
  questions = [
    { title: 'Раздел 1', items: [{name: 'Название 1', options: ['опция1', 'опция2', 'опция3']}, {name: 'Название 2', options: ['опция1', 'опция2', 'опция3']}, {name: 'Название 3', options: ['опция1', 'опция2', 'опция3']}]},
    { title: 'Раздел 2', items: [{name: 'Название 1.1', options: ['опция1', 'опция2', 'опция3']}, {name: 'Название 2.1', options: ['опция1', 'опция2', 'опция3']}, {name: 'Название 3.1', options: ['опция1', 'опция2', 'опция3']}] },
    { title: 'Раздел 3', items: [{name: 'Название 1.2', options: ['опция1', 'опция2', 'опция3']}, {name: 'Название 2.2', options: ['опция1', 'опция2', 'опция3']}, {name: 'Название 3.2', options: ['опция1', 'опция2', 'опция3']}] }
  ];

  constructor(private el: ElementRef) {}

  @HostListener('change', ['$event'])
  onRadioChange(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.type === 'radio') {
      const div = target.closest('.question') as HTMLDivElement // Получаем div, содержащий вопрос
      div.classList.add("question_answered")
      console.log('Выбран вариант:', target.value);
      console.log('Вопросный блок:', div);
    }
  }
}
