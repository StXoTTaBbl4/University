import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {SnackbarComponent} from '../../common-ui/snackbar/snackbar.component';
import {SidebarComponent} from '../../common-ui/sidebar/sidebar.component';
import {FormsModule} from '@angular/forms';
import {NgIf} from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {CertificateFileService} from '../../services/certificateFile.service';

interface certificateInfo {
  "name": string,
  "id": number,
  "category": string,
  "subCategory": string,
  "employee_name": string
}
@Component({
  selector: 'app-certificate',
  imports: [
    SnackbarComponent,
    SidebarComponent,
    FormsModule,
    NgIf
  ],
  templateUrl: './certificate.component.html',
  standalone: true,
  styleUrl: './certificate.component.css'
})
export class CertificateComponent implements OnInit ,AfterViewInit{
  @ViewChild(SnackbarComponent) snackbar!: SnackbarComponent;
  @ViewChild('certificate', { static: false }) certificateDiv!: ElementRef;
  fileUrl: string | null = null;
  fileBlob: Blob | null = null;
  fileName: string = '';
  isModalOpen: boolean = false;
  info: certificateInfo | null = null;

  certificateId = -1;


  constructor(
    private route: ActivatedRoute,
    private fileService: CertificateFileService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.certificateId = +params.get('id')!;
    });

    this.getCertificateFile();
    this.getCertificateInfo();

  }

  ngAfterViewInit(): void {
  }

  deleteCertificate(){
    this.fileService.deleteFile(this.certificateId).subscribe({
      next: () => {
        this.router.navigate(['/certificates']);
      },
      error: (error: any) => {
        console.log(error)
      }
    })

  }

  openModal() {
    this.isModalOpen = true;
  }

  closeModal() {
    this.snackbar.showSnackbar("Успешно удалён. Или нет. Не знаю")
    this.isModalOpen = false;
  }

  protected getCertificateFile(){
    if (this.certificateId) {
      this.fileService.getFile(this.certificateId).subscribe(response => {
        const contentType = response.headers.get('Content-Type');
        this.fileBlob = new Blob([response.body!], { type: contentType! });
        this.fileName = response.headers.get('Content-Disposition')?.split('filename=')[1] || 'file';

        this.fileUrl = URL.createObjectURL(this.fileBlob);

        const container = this.certificateDiv.nativeElement;
        container.innerHTML = '';

        if (contentType?.startsWith('image/')) {
          const img = document.createElement('img');
          img.src = this.fileUrl;
          img.style.maxWidth = '100%';
          img.style.maxHeight = '100%';
          container.appendChild(img);
        } else if (contentType === 'application/pdf') {
          const iframe = document.createElement('iframe');
          iframe.src = this.fileUrl;
          iframe.width = '100%';
          iframe.height = '100%';
          container.appendChild(iframe);
        } else {
          container.innerHTML = 'Файл не может быть отображён. Используйте кнопку "Скачать".';
        }
      }, error => {
        console.error('Ошибка загрузки файла', error);
      });
    }
  }


  private getCertificateInfo(){
    this.fileService.getInfo(this.certificateId).subscribe({
      next: (response: certificateInfo) => {
        this.info = response;
    },
      error: (error: any) => {
        console.log(error)
      }
    })
  }

  protected downloadFile() {
    if (this.fileBlob) {
      const link = document.createElement('a');
      link.href = this.fileUrl!;
      link.download = this.fileName;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  }

}
