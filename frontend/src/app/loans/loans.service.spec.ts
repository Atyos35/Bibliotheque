import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoanResponse } from './loan.model';
import { LoansService } from './loans.service';

describe('LoansService', () => {
  let service: LoansService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(LoansService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('posts a borrow request to POST /api/loans', () => {
    const expected: LoanResponse = {
      id: 'loan-1',
      bookId: 'book-1',
      memberId: 'member-1',
      borrowedAt: '2026-07-12T10:00:00',
      dueDate: '2026-08-02T10:00:00',
      returnedAt: null,
      active: true
    };

    service.borrow({ memberId: 'member-1', bookId: 'book-1' }).subscribe((loan) => {
      expect(loan).toEqual(expected);
    });

    const req = httpMock.expectOne('/api/loans');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ memberId: 'member-1', bookId: 'book-1' });
    req.flush(expected);
  });
});
