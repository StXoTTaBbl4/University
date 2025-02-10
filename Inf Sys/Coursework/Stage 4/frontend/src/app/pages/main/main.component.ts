import { Component } from '@angular/core';
import {SidebarComponent} from '../../common-ui/sidebar/sidebar.component';
import {NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-main',
  imports: [
    SidebarComponent,
    NgOptimizedImage
  ],
  templateUrl: './main.component.html',
  standalone: true,
  styleUrl: './main.component.css'
})
export class MainComponent {

}
