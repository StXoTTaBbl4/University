import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {NgForOf} from '@angular/common';
import {SidebarComponent} from '../../common-ui/sidebar/sidebar.component';
import {RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';

interface employee  {
  "id": number,
  "name": string,
  "email": string
}

@Component({
  selector: 'app-search',
  imports: [
    NgForOf,
    SidebarComponent,
    RouterLink,
    FormsModule
  ],
  templateUrl: './search.component.html',
  standalone: true,
  styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit{
  employees: employee[] = [];
  filteredEmployees = [...this.employees];

  searchCriteria = {
    id: null,
    name: '',
    email: ''
  };

  constructor(
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(){
    this.http.get<employee[]>("http://localhost:8080/api/employee/load", {withCredentials: true}).subscribe({
      next: (response: any) => {
        this.employees = response;
      },
      error: (error: any) => {
        console.log(error)
      }
    })
  }

  filterData() {
    this.filteredEmployees = this.employees.filter(employee => {
      return (
        (this.searchCriteria.id ? employee.id === this.searchCriteria.id : true) &&
        (this.searchCriteria.name ? employee.name.toLowerCase().includes(this.searchCriteria.name.toLowerCase()) : true) &&
        (this.searchCriteria.email ? employee.email.toLowerCase().includes(this.searchCriteria.email.toLowerCase()) : true)
      );
    });
  }
}
