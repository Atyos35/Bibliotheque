import { Component, OnInit, inject } from '@angular/core';
import { BookResponse } from './book.model';
import { BooksService } from './books.service';

/**
 * Page catalogue : liste les livres ayant au moins un exemplaire disponible,
 * avec un état de chargement (squelette) pendant l'appel au backend.
 */
@Component({
  selector: 'app-book-list',
  imports: [],
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.css'
})
export class BookListComponent implements OnInit {
  private readonly booksService = inject(BooksService);

  availableBooks: BookResponse[] = [];
  loading = true;
  readonly skeletonRows = [0, 1, 2];

  /** Charge le catalogue au montage et ne retient que les livres empruntables. */
  ngOnInit(): void {
    this.booksService.getBooks().subscribe((books) => {
      this.availableBooks = books.filter((book) => book.availableCopies > 0);
      this.loading = false;
    });
  }
}
