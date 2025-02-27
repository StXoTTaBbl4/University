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
  protected isModalOpen: boolean = false;
  certificate: any = {
    name: '',
    type: '',
    cType: '',
    subType: '',
    date: '',
    file: null
  };

  categories: any[] = [{id: 1, name:"type1"}, {id: 2, name:"type2"}]; // Категории (тип)
  subCategories: any[] = [{id: 1, name:"subtype1"}, {id: 2, name:"subtype2"}]; // Подкатегории (подтип)


  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadCertificates()
    // this.loadCategories();
    // this.loadSubCategories();
  }

  loadCertificates() {
    this.http.get<any[]>('http://localhost:8080/api/files/certificates').subscribe({
      next: (response) => {
        this.data = response; // Привязываем ответ сервера к переменной data
        console.log('Данные загружены:', this.data);
      },
      error: (error) => {
        console.error('Ошибка загрузки сертификатов', error);
      }
    });
  }

  loadCategories() {
    this.http.get('http://localhost:8080/api/files/categories').subscribe((data: any) => {
      this.categories = data;
    });
  }

  loadSubCategories() {
    if (this.certificate.type) {
      this.http.get(`http://localhost:8080/api/files/categories/${this.certificate.type}/subcategories`).subscribe((data: any) => {
        this.subCategories = data;
      });
    }
  }


  ngAfterViewInit(): void {
  }

  openModal() {
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  onFileSelected(event: any) {
    if (event.target.files.length > 0) {
      this.certificate.file = event.target.files[0];
    }
  }

  upload() {
    if (!this.certificate.file) {
      alert('Выберите файл');
      return;
    }

    const formData = new FormData();
    formData.append('file', this.certificate.file);
    formData.append('categoryId', this.certificate.cType);
    formData.append('subCategoryId', this.certificate.subType);
    formData.append('name', this.certificate.name);

    this.http.post('http://localhost:8080/api/files/upload-certificate', formData, {withCredentials: true}).subscribe({
      next: (response) => {
        console.log('Файл успешно загружен');
        this.loadCertificates();
      },
      error: (error) => {
        console.log('Ошибка загрузки файла');
      }
    });
  }

}
