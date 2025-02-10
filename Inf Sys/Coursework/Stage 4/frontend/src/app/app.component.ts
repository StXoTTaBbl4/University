import {Component, importProvidersFrom, NgModule} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {SidebarComponent} from './common-ui/sidebar/sidebar.component';
import {AuthComponent} from './pages/auth/auth.component';
import {SnackbarComponent} from './common-ui/snackbar/snackbar.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, SidebarComponent, AuthComponent, SnackbarComponent],
  templateUrl: './app.component.html',
  standalone: true,
  styleUrl: './app.component.css',
})

export class AppComponent {
  title = 'frontend';
}
