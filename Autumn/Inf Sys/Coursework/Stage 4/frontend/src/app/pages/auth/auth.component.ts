import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpClient} from "@angular/common/http";
import {NgIf} from '@angular/common';
import {SnackbarComponent} from '../../common-ui/snackbar/snackbar.component';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'auth',
  imports: [FormsModule, NgIf, SnackbarComponent],
  templateUrl: './auth.component.html',
  standalone: true,
  styleUrl: './auth.component.css'
})

export class AuthComponent  implements AfterViewInit{
  @ViewChild('login') loginForm!: ElementRef;
  @ViewChild('registration') regForm!: ElementRef;
  @ViewChild(SnackbarComponent) snackbar!: SnackbarComponent;

  reg_pwd: string = '';
  re_reg_pwd: string = '';

  loginFormData = {email:'', password:''}
  registrationFormData = {email:'', password:'', name:''}

  isButtonDisabled: boolean = true;


  constructor(private http: HttpClient, private router: Router, private authService: AuthService) {
  }



  validatePassword(){
    this.isButtonDisabled = this.registrationFormData.password !== this.re_reg_pwd;
  }

  goToRegistration() {
    this.loginForm.nativeElement.classList.toggle("auth__form_active");
    this.regForm.nativeElement.classList.toggle("auth__form_active");
  }

  loginUser(form: any) {
    if (form.valid) {
      this.http.post('http://localhost:8080/api/auth/login' , this.loginFormData, { withCredentials: true }).subscribe({
        next: (response: any) => {
          localStorage.setItem("access-token", response.accessToken)
          localStorage.setItem('expires-at', response.accessTokenExpiresAt);
          localStorage.setItem('user-email', this.loginFormData.email);
          localStorage.setItem('user-roles', response.roles);
          localStorage.setItem('user-id', response.id);
          this.snackbar.showSnackbar(response.message)

          this.authService.scheduleTokenRefresh();
          this.router.navigate(['/main'])
        },
        error: (error: any) => {
          console.error('Ошибка:', error);
          this.snackbar.showSnackbar(error.error.message);
        }
      });
    }
  }

  createUser(form: any){
    if (form.valid) {
      console.log("validated")
      this.http.post('http://localhost:8080/api/auth/registration', this.registrationFormData).subscribe({
        next: (response: any) => {
          console.log('Ответ от сервера:', response);
          this.snackbar.showSnackbar(response.message);
        },
        error: (error: any) => {
          console.error('Ошибка:', error);
          this.snackbar.showSnackbar(error.error.message);
        }
      });
    } else {
      this.snackbar.showSnackbar("Все поля должны быть заполнены корректно. ")
    }
  }

  ngAfterViewInit(): void {
  }

}


