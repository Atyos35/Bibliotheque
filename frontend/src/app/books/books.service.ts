import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { BookResponse } from './book.model';

/** Service HTTP dédié aux livres — consomme GET /api/books. */
@Injectable({ providedIn: 'root' })
export class BooksService {
  private readonly http = inject(HttpClient);
  private readonly booksUrl = '/api/books';

  getBooks(): Observable<BookResponse[]> {
    return this.http.get<BookResponse[]>(this.booksUrl);
  }
}
