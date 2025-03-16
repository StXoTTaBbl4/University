import {Component, ViewChild} from '@angular/core';
import {SidebarComponent} from '../../common-ui/sidebar/sidebar.component';
import {SnackbarComponent} from '../../common-ui/snackbar/snackbar.component';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TaskService} from '../../services/task.service';

interface TaskRequest {
  name: string;
  description: string;
  employeeIds: number[];
  taskId:number;
  levelId: number;
}

@Component({
  selector: 'app-tasks',
  imports: [
    SidebarComponent,
    SnackbarComponent,
    ReactiveFormsModule
  ],
  templateUrl: './tasks.component.html',
  standalone: true,
  styleUrl: './tasks.component.css'
})
export class TasksComponent {
  @ViewChild(SnackbarComponent) snackbar!: SnackbarComponent;
  taskForm: FormGroup;
  errorMsg: string ="";

  constructor(private fb: FormBuilder, private taskService: TaskService) {
    this.taskForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      employeeIds: [''],
      taskId: ['', Validators.required],
      levelId: ['',  Validators.required]
    });
  }

  onSubmit() {
    if (this.taskForm.valid) {
      const taskData = this.taskForm.value;
        taskData.employeeIds = taskData.employeeIds
        ? taskData.employeeIds.split(',').map((id: string) => parseInt(id.trim(), 10))
        : [];

      this.taskService.createTask(taskData).subscribe({
        next: (response: any) => {
          this.snackbar.showSnackbar("Задача создана, работники назначены");
        },
        error: (error: any) => {
          console.log(error)
          this.errorMsg = error.error.message;
        }
      })
    }
  }
}
