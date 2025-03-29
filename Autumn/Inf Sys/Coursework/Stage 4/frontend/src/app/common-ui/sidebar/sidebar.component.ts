import {Component, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {AuthService} from '../../services/auth.service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'sidebar',
  imports: [
    RouterLink,
    NgIf
  ],
  templateUrl: './sidebar.component.html',
  standalone: true,
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent implements OnInit{
  constructor(protected authService: AuthService) {

  }

  roles: string[] = []
  userId: string | null = null;
  protected readonly localStorage = localStorage;

  ngOnInit(): void {
    this.roles = (localStorage.getItem("user-roles") ?? "").split(",");
    this.userId = (localStorage.getItem("user-id") ?? "");
  }
}
