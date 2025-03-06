import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

interface TaskRequest {
  name: string;
  description: string;
  employeeIds: number[];
}

@Injectable({
  providedIn: 'root',
})

export class TaskService {
  private apiUrl = 'http://localhost:8080/api/tasks/create';

  constructor(private http: HttpClient) {}

  createTask(task: TaskRequest): Observable<any> {
    return this.http.post<any>(this.apiUrl, task);
  }
}
