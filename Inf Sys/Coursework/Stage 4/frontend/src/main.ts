import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { AuthComponent } from './app/auth/auth.component';

bootstrapApplication(AuthComponent, appConfig)
  .catch((err) => console.error(err));
