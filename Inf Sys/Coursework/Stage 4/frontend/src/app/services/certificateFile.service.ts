import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

interface FileResponse {
  fileData: string;
  contentType: string;
  filename: string;
}

@Injectable({
  providedIn: 'root'
})
export class CertificateFileService {
  private apiUrl = 'http://localhost:8080/api/certificates';

  constructor(private http: HttpClient) { }


  getFile(certificateId: number) {
    return this.http.get(`${this.apiUrl}/${certificateId}`, { responseType: 'blob', observe: 'response', withCredentials:true});
  }

  getInfo(certificateId: number) {
    return this.http.get<any>(`${this.apiUrl}/${certificateId}/info`, {withCredentials:true});
  }

  deleteFile(certificateId: number) {
    return this.http.delete<any>(`${this.apiUrl}/${certificateId}/delete`, {withCredentials: true});
  }
}
