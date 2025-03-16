import {Component, OnInit} from '@angular/core';
import {SidebarComponent} from '../../common-ui/sidebar/sidebar.component';
import {NgForOf, NgIf, NgOptimizedImage} from '@angular/common';

interface Commit {
  commit: {
    author: {
      name: string;
      date: string;
    };
    message: string;
  };
}


@Component({
  selector: 'app-main',
  imports: [
    SidebarComponent,
    NgOptimizedImage,
    NgIf,
    NgForOf
  ],
  templateUrl: './main.component.html',
  standalone: true,
  styleUrl: './main.component.css'
})
export class MainComponent implements OnInit{
  commits: Commit[] = [];
  repoOwner: string = "StXoTTaBbl4"; // Владелец репозитория
  repoName: string = "University"; // Название репозитория
  branch: string = "master"; // Ветка, откуда брать коммиты

  ngOnInit(): void {
    this.fetchCommits()
  }

  async fetchCommits(): Promise<void> {
    const url: string = `https://api.github.com/repos/${this.repoOwner}/${this.repoName}/commits?sha=${this.branch}`;

    try {
      const response: Response = await fetch(url);
      if (!response.ok) {
        throw new Error(`Ошибка HTTP: ${response.status}`);
      }

      this.commits = await response.json();

    } catch (error) {
      console.error("Ошибка при получении коммитов:", error);
    }
  }
}
