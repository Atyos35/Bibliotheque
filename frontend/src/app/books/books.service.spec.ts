import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BookResponse } from './book.model';
import { BooksService } from './books.service';

describe('BooksService', () => {
  let service: BooksService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(BooksService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('fetches books from GET /api/books', () => {
    const expected: BookResponse[] = [
      {
        id: '1',
        isbn: '9780134685991',
        title: 'Effective Java',
        author: 'Joshua Bloch',
        totalCopies: 2,
        availableCopies: 1
      }
    ];

    service.getBooks().subscribe((books) => {
      expect(books).toEqual(expected);
    });

    const req = httpMock.expectOne('/api/books');
    expect(req.request.method).toBe('GET');
    req.flush(expected);
  });
});
