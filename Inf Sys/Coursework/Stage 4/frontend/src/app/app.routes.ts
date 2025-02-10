import { Routes } from '@angular/router';
import {AuthComponent} from './pages/auth/auth.component';
import {MainComponent} from './pages/main/main.component';
import {SelfAssessmentComponent} from './pages/self-assessment/self-assessment.component';
import {AdminComponent} from './pages/admin/admin.component';
import {CertificatesComponent} from './pages/certificates/certificates.component';
import {ProfileComponent} from './pages/profile/profile.component';
import {SearchComponent} from './pages/search/search.component';
import {CertificateComponent} from './pages/certificate/certificate.component';

export const routes: Routes = [
  { path: '', component: AuthComponent },
  { path: 'main', component: MainComponent },
  { path: 'self-assessment', component: SelfAssessmentComponent},
  { path: 'admin', component: AdminComponent},
  { path: 'certificates', component: CertificatesComponent},
  { path: 'certificate', component: CertificateComponent},
  { path: 'profile', component: ProfileComponent},
  { path: 'search', component: SearchComponent},
  { path: '**', redirectTo: '' }
];
