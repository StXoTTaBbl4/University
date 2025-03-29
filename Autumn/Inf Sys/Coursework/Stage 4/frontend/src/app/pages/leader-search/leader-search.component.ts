import {Component, OnInit, ViewChild} from '@angular/core';
import {SidebarComponent} from '../../common-ui/sidebar/sidebar.component';
import {SnackbarComponent} from '../../common-ui/snackbar/snackbar.component';
import {NgForOf, NgIf} from '@angular/common';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {FormsModule} from '@angular/forms';

interface filteredEmployee  {
  "employeeId": number,
  "name": string,
  "email": string,
  "hardwarePoints": number,
  "softwarePoints": number,
  "processesPoints": number
}


@Component({
  selector: 'app-leader-search',
  imports: [
    SidebarComponent,
    SnackbarComponent,
    NgForOf,
    NgIf,
    FormsModule
  ],
  templateUrl: './leader-search.component.html',
  standalone: true,
  styleUrl: './leader-search.component.css'
})
export class LeaderSearchComponent{
  @ViewChild(SnackbarComponent) snackbar!: SnackbarComponent;

  filteredEmployees: filteredEmployee[] = [];

  constructor(
    private http: HttpClient
  ) {}

  filters = {
    hw: 0,
    sw: 0,
    pr: 0
  };

  onSubmit(): void {
    this.sendFilters(this.filters).subscribe(response => {
      this.filteredEmployees = response;
    });
  }

  sendFilters(filters: any): Observable<any> {
    return this.http.post<filteredEmployee[]>('http://localhost:8080/api/leader/search/filterEmployees', filters, {withCredentials: true});
  }


}
