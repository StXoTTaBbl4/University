import {Component, importProvidersFrom, NgModule, OnInit} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {SidebarComponent} from './common-ui/sidebar/sidebar.component';
import {AuthComponent} from './pages/auth/auth.component';
import {SnackbarComponent} from './common-ui/snackbar/snackbar.component';
import {AuthService} from './services/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, SidebarComponent, AuthComponent, SnackbarComponent],
  templateUrl: './app.component.html',
  standalone: true,
  styleUrl: './app.component.css',
})

export class AppComponent implements OnInit{
  constructor(private authService: AuthService) {}
  title = 'frontend';

  ngOnInit(): void {
    if (this.authService.hasValidToken()) {
      this.authService.scheduleTokenRefresh();
    }
  }
}
