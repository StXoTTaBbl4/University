import { Component } from '@angular/core';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-auth',
  imports: [RouterOutlet],
  templateUrl: './auth.component.html',
  standalone: true,
  styleUrl: './auth.component.css'
})

export class AuthComponent {
   reg_password:string = "";
   reg_password_repeat:string = "";
   reg_password_true:string = "";

   auth_register_classList:string[] = ["auth active"];

   goToRegistration(){
     let index:number = this.auth_register_classList.indexOf("active");
   }
}
