import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { BookResponse } from '../books/book.model';
import { BooksService } from '../books/books.service';
import { BorrowBookComponent } from './borrow-book.component';
import { LoanResponse } from './loan.model';
import { LoansService } from './loans.service';

describe('BorrowBookComponent', () => {
  let fixture: ComponentFixture<BorrowBookComponent>;
  let loansServiceSpy: jasmine.SpyObj<LoansService>;

  const books: BookResponse[] = [
    {
      id: 'book-1',
      isbn: '9780134685991',
      title: 'Effective Java',
      author: 'Joshua Bloch',
      totalCopies: 2,
      availableCopies: 1
    }
  ];

  beforeEach(async () => {
    const booksServiceSpy = jasmine.createSpyObj<BooksService>('BooksService', ['getBooks']);
    booksServiceSpy.getBooks.and.returnValue(of(books));
    loansServiceSpy = jasmine.createSpyObj<LoansService>('LoansService', ['borrow']);

    await TestBed.configureTestingModule({
      imports: [BorrowBookComponent],
      providers: [
        { provide: BooksService, useValue: booksServiceSpy },
        { provide: LoansService, useValue: loansServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(BorrowBookComponent);
    fixture.detectChanges();
  });

  it('submits the borrow request and shows a success message', () => {
    const loan: LoanResponse = {
      id: 'loan-1',
      bookId: 'book-1',
      memberId: 'member-1',
      borrowedAt: '2026-07-12T10:00:00',
      dueDate: '2026-08-02T10:00:00',
      returnedAt: null,
      active: true
    };
    loansServiceSpy.borrow.and.returnValue(of(loan));

    const component = fixture.componentInstance;
    component.memberId = 'member-1';
    component.selectedBookId = 'book-1';

    component.borrow();
    fixture.detectChanges();

    expect(loansServiceSpy.borrow).toHaveBeenCalledWith({ memberId: 'member-1', bookId: 'book-1' });
    expect(component.errorMessage).toBeNull();
    expect(component.successMessage).toBe('Emprunt enregistré avec succès.');
    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(text).toContain('Emprunt enregistré avec succès.');
  });

  it('shows the business error message returned by the backend on a 409 refusal', () => {
    const conflict = new HttpErrorResponse({
      status: 409,
      error: { message: 'Le membre a atteint la limite de 3 emprunts actifs.' }
    });
    loansServiceSpy.borrow.and.returnValue(throwError(() => conflict));

    const component = fixture.componentInstance;
    component.memberId = 'member-1';
    component.selectedBookId = 'book-1';

    component.borrow();
    fixture.detectChanges();

    expect(component.successMessage).toBeNull();
    expect(component.errorMessage).toBe('Le membre a atteint la limite de 3 emprunts actifs.');
    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(text).toContain('Le membre a atteint la limite de 3 emprunts actifs.');
  });

  it('shows a generic message when the error is not a business rule conflict', () => {
    const serverError = new HttpErrorResponse({ status: 500, error: { message: 'boom' } });
    loansServiceSpy.borrow.and.returnValue(throwError(() => serverError));

    const component = fixture.componentInstance;
    component.memberId = 'member-1';
    component.selectedBookId = 'book-1';

    component.borrow();
    fixture.detectChanges();

    expect(component.errorMessage).toBe("Une erreur est survenue lors de l'emprunt.");
  });
});
