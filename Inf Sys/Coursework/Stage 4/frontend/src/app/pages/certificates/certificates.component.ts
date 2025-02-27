import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {SidebarComponent} from '../../common-ui/sidebar/sidebar.component';
import {SnackbarComponent} from '../../common-ui/snackbar/snackbar.component';
import {HttpClient} from '@angular/common/http';
import {NgFor, NgIf} from '@angular/common';
import {MatFormField} from '@angular/material/form-field';
import {FormsModule} from '@angular/forms';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-certificates',
  imports: [
    SidebarComponent,
    SnackbarComponent,
    NgFor,
    MatFormField,
    FormsModule,
    NgIf,
    RouterLink
  ],
  templateUrl: './certificates.component.html',
  standalone: true,
  styleUrl: './certificates.component.css'
})
export class CertificatesComponent implements OnInit, AfterViewInit{
  @ViewChild(SnackbarComponent) snackbar!: SnackbarComponent;

  data: any[] = [];
  isModalOpen: boolean = false;
  name: string = '';
  type: string = '';
  sub_type: string = '';
  date: string = '';
  file: File | null = null;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    // this.http.get<any[]>('assets/data.json').subscribe(response => {
    //   this.data = response;
    // });

    this.data = [
      { "id": 1, "name": "Алексей", "type": "type1" },
      { "id": 2, "name": "Мария", "type": "type1"  },
      { "id": 3, "name": "Иван", "type": "type1" }
    ]
  }

  ngAfterViewInit(): void {
  }

  showUploadModal() {

  }

  openModal() {
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const selectedFile = input.files[0];
      if (selectedFile.type === 'application/pdf' || selectedFile.type.startsWith('image/png')) {
        this.file = selectedFile;
      } else {
        alert('Только PNG или PDF!');
        input.value = ''; // Очистка некорректного файла
      }
    }
  }

  upload() {
    console.log('Имя:', this.name);
    console.log('Тип:', this.type);
    console.log('Дата:', this.date);
    console.log('Файл:', this.file?.name);
    this.closeModal();
    this.snackbar.showSnackbar("Сертификат загружен")
    this.data.push({ "id": 4, "name": this.name, "type": this.type })
  }

}
