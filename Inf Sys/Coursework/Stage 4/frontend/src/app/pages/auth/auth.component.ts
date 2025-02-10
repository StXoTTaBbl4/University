import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpClient} from "@angular/common/http";
import {NgIf} from '@angular/common';
import {SnackbarComponent} from '../../common-ui/snackbar/snackbar.component';
import {Router} from '@angular/router';

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

  loginFormData = {login:'', password:''}
  registrationFormData = {login:'', password:''}

  isButtonDisabled: boolean = true;


  constructor(private http: HttpClient, private router: Router) {
  }

  validatePassword(){
    this.isButtonDisabled = this.reg_pwd !== this.re_reg_pwd;
  }

  goToRegistration() {
    this.loginForm.nativeElement.classList.toggle("auth__form_active");
    this.regForm.nativeElement.classList.toggle("auth__form_active");
  }

  loginUser(form: any) {
    if (form.valid) {
      this.http.post('http://localhost:8080/api/auth/login', this.loginFormData).subscribe({
        next: (response: any) => {
          this.router.navigate(['/main'])
          console.log('Ответ от сервера:', response);
        },
        error: (error: any) => {
          console.error('Ошибка:', error);
          this.snackbar.showSnackbar(error);
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
        },
        error: (error: any) => {
          console.error('Ошибка:', error);
          this.snackbar.showSnackbar(error);
        }
      });
    }
  }

  ngAfterViewInit(): void {
  }

}


