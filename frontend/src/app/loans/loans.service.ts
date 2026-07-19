import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { BorrowBookRequest, LoanResponse } from './loan.model';

/** Service HTTP dédié aux emprunts — consomme POST /api/loans. */
@Injectable({ providedIn: 'root' })
export class LoansService {
  private readonly http = inject(HttpClient);
  private readonly loansUrl = '/api/loans';

  borrow(request: BorrowBookRequest): Observable<LoanResponse> {
    return this.http.post<LoanResponse>(this.loansUrl, request);
  }
}
