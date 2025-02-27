import { Component } from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'sidebar',
  imports: [
    RouterLink
  ],
  templateUrl: './sidebar.component.html',
  standalone: true,
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  constructor(protected authService: AuthService) {

  }
}
