import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {SnackbarComponent} from '../../common-ui/snackbar/snackbar.component';
import {SidebarComponent} from '../../common-ui/sidebar/sidebar.component';
import {FormsModule} from '@angular/forms';
import {NgIf} from '@angular/common';

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
export class CertificateComponent implements AfterViewInit{
  @ViewChild(SnackbarComponent) snackbar!: SnackbarComponent;
  isModalOpen: boolean = false;

  ngAfterViewInit(): void {
  }

  deleteCertificate(){
    this.openModal();
  }

  openModal() {
    this.isModalOpen = true;
  }

  closeModal() {
    this.snackbar.showSnackbar("Успешно удалён. Или нет. Не знаю")
    this.isModalOpen = false;
  }

}
