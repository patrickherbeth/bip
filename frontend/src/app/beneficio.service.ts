import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Beneficio, TransferenciaRequest } from './beneficio.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class BeneficioService {
  private readonly http = inject(HttpClient);
  private readonly api = 'http://localhost:8080/api/v1/beneficios';

  list(): Observable<Beneficio[]> { return this.http.get<Beneficio[]>(this.api); }
  create(payload: Beneficio): Observable<Beneficio> { return this.http.post<Beneficio>(this.api, payload); }
  update(id: number, payload: Beneficio): Observable<Beneficio> { return this.http.put<Beneficio>(`${this.api}/${id}`, payload); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${this.api}/${id}`); }
  transfer(payload: TransferenciaRequest): Observable<unknown> { return this.http.post(`${this.api}/transferir`, payload); }
}
