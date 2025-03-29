import {Component, OnInit, ViewChild} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SidebarComponent} from '../../common-ui/sidebar/sidebar.component';
import {SnackbarComponent} from '../../common-ui/snackbar/snackbar.component';
import {NgForOf, NgIf} from '@angular/common';

interface tokenEntity   {
  "employee_id": number,
  "expiresAt": Date,
  "revoked": boolean
}

interface rolesEntity  {
  "employeeId": number,
  "roles": string[]
}

@Component({
  selector: 'app-admin',
  imports: [
    SidebarComponent,
    SnackbarComponent,
    NgForOf,
    NgIf
  ],
  templateUrl: './admin.component.html',
  standalone: true,
  styleUrl: './admin.component.css'
})
export class AdminComponent implements OnInit{
  @ViewChild(SnackbarComponent) snackbar!: SnackbarComponent;

  tokens: tokenEntity[] = [];
  roles: rolesEntity[] = [];

  constructor(
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(){
    this.http.get<tokenEntity[]>("http://localhost:8080/api/admin/tokens", {withCredentials: true}).subscribe({
      next: (response: any) => {
        this.tokens = response;
      },
      error: (error: any) => {
        console.log(error)
      }
    })

    this.http.get<rolesEntity[]>("http://localhost:8080/api/admin/roles", {withCredentials: true}).subscribe({
      next: (response: any) => {
        this.roles = response;
        console.log(this.roles)
      },
      error: (error: any) => {
        console.log(error)
      }
    })
  }

  protected revokeToken(employeeId: number){
    return this.http.post<any>("http://localhost:8080/api/admin/revokeToken", { employeeId: employeeId }, {withCredentials: true}).subscribe({
      next: () =>{
        this.snackbar.showSnackbar("Токен отозван");
        this.loadData();
      }, error: (error: any) => {
        console.log(error);
        this.snackbar.showSnackbar("Ошибка, запрос отклонен.");
      }
    })
  }

  protected addRole(employeeId: number, role: string){
    return this.http.post<any>("http://localhost:8080/api/admin/addRole", { employeeId: employeeId, role: role }, {withCredentials: true}).subscribe({
      next: () =>{
        this.snackbar.showSnackbar("Роль добавлена");
        this.loadData();
      }, error: (error: any) => {
        console.log(error);
        this.snackbar.showSnackbar("Ошибка, запрос отклонен.");
      }
    })
  }

  protected removeRole(employeeId: number, role: string){
    return this.http.post<any>("http://localhost:8080/api/admin/removeRole", { employeeId: employeeId, role: role }, {withCredentials: true}).subscribe({
      next: () =>{
        this.snackbar.showSnackbar("Роль удалена");
        this.loadData();
      }, error: (error: any) => {
        console.log(error);
        this.snackbar.showSnackbar("Ошибка, запрос отклонен.");
      }
    })
  }


}
