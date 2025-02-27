import { Routes } from '@angular/router';
import {AuthComponent} from './pages/auth/auth.component';
import {MainComponent} from './pages/main/main.component';
import {SelfAssessmentComponent} from './pages/self-assessment/self-assessment.component';
import {AdminComponent} from './pages/admin/admin.component';
import {CertificatesComponent} from './pages/certificates/certificates.component';
import {ProfileComponent} from './pages/profile/profile.component';
import {SearchComponent} from './pages/search/search.component';
import {CertificateComponent} from './pages/certificate/certificate.component';
import {AuthGuard} from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: AuthComponent },
  { path: 'main', component: MainComponent, canActivate: [AuthGuard]},
  { path: 'self-assessment', component: SelfAssessmentComponent, canActivate: [AuthGuard]},
  { path: 'admin', component: AdminComponent, canActivate: [AuthGuard]},
  { path: 'certificates', component: CertificatesComponent, canActivate: [AuthGuard]},
  { path: 'certificate', component: CertificateComponent, canActivate: [AuthGuard]},
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard]},
  { path: 'search', component: SearchComponent, canActivate: [AuthGuard]},
  { path: '**', redirectTo: '' }
];
