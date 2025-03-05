import {Component, OnInit} from '@angular/core';
import {SidebarComponent} from '../../common-ui/sidebar/sidebar.component';
import {SnackbarComponent} from '../../common-ui/snackbar/snackbar.component';
import {ActivatedRoute} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {NgForOf} from '@angular/common';

interface userInfo {
  "name": string,
  "email": string,
  "id": number,
  "roles": []
}

interface skillInfo   {
  "product": string,
  "task": string,
  "level": string
}

@Component({
  selector: 'app-profile',
  imports: [
    SidebarComponent,
    SnackbarComponent,
    NgForOf
  ],
  templateUrl: './profile.component.html',
  standalone: true,
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit{

  info: userInfo | null = null;
  userId: number = -1;
  hw_skills: skillInfo[] = []
  sw_skills: skillInfo[] = []
  pr_skills: skillInfo[] = []

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.userId = +params.get('id')!;
    });

    this.loadData()
    this.loadSkills()
  }

  private loadData() {
    this.http.get<any>(`http://localhost:8080/api/profile/${this.userId}/info`).subscribe({
      next: (response: userInfo) => {
        this.info = response;
      },
      error: (error: any) => {
        console.log(error)
      }
    })

  }

  private loadSkills(){
    this.http.get<skillInfo[]>(`http://localhost:8080/api/profile/${this.userId}/skills/Hardware`).subscribe({
      next: (response: any) => {
        this.hw_skills = response;
      },
      error: (error: any) => {
        console.log(error)
      }
    })
    this.http.get<skillInfo[]>(`http://localhost:8080/api/profile/${this.userId}/skills/Software`).subscribe({
      next: (response: any) => {
        this.sw_skills = response;
      },
      error: (error: any) => {
        console.log(error)
      }
    })
    this.http.get<skillInfo[]>(`http://localhost:8080/api/profile/${this.userId}/skills/Processes`).subscribe({
      next: (response: any) => {
        this.pr_skills = response;
      },
      error: (error: any) => {
      }
    })
  }

}
