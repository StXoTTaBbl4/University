import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {SidebarComponent} from '../../common-ui/sidebar/sidebar.component';
import {SnackbarComponent} from '../../common-ui/snackbar/snackbar.component';
import {HttpClient} from '@angular/common/http';
import {NgFor, NgIf} from '@angular/common';
import {MatFormField} from '@angular/material/form-field';
import {FormsModule} from '@angular/forms';
import {RouterLink} from '@angular/router';


interface Category {
  id: number;
  name: string;
  subCategories: string[];
}

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
    category: '',
    subcategory: '',
    subType: '',
    file: null
  };

  categories: Category[] = [];
  subCategories:string[] = []


  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadCertificates()
    this.loadCategories();
  }

  loadCertificates() {
    this.http.get<any[]>('http://localhost:8080/api/certificates/certificates').subscribe({
      next: (response) => {
        this.data = response;
        this.snackbar.showSnackbar('Данные загружены');
      },
      error: (error) => {
        console.error('Ошибка загрузки сертификатов', error);
        this.snackbar.showSnackbar('Ошибка загрузки сертификатов, статус: ' + error.status);
      }
    });
  }

  loadCategories() {
    this.http.get<Category[]>('http://localhost:8080/api/certificates/categories').subscribe({
      next: (data) => {
        this.categories = data;
      },
      error: (error) => {
        console.error('Ошибка загрузки категорий:', error);
        this.snackbar.showSnackbar('Ошибка загрузки категорий.')
      }
    });
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

  upload(form:any) {
    if (form.valid) {
      const formData = new FormData();
      formData.append('file', this.certificate.file);
      formData.append('category', this.certificate.category);
      formData.append('subcategory', this.certificate.subcategory);
      formData.append('name', this.certificate.name);

      this.http.post('http://localhost:8080/api/certificates/upload-certificate', formData, {withCredentials: true}).subscribe({
        next: () => {
          this.snackbar.showSnackbar('Файл успешно загружен');
          this.loadCertificates();
        },
        error: (error) => {
          console.log(error);
          this.snackbar.showSnackbar(error.status + ": " + error.error.message);
        }
      });
      this.closeModal();
    } else {
      this.snackbar.showSnackbar("Заполните все поля формы для загрузки!")
    }
  }

  onCategoryChange() {
    this.subCategories = [];
    this.certificate.subcategory = null;
    for (const category in this.categories) {
      console.log(this.categories[category].subCategories)
      if(Number(this.categories[category].id) === Number(this.certificate.category)) {
        this.subCategories = this.categories[category].subCategories;
        break;
      }
    }

    console.log(this.subCategories)
  }

}
