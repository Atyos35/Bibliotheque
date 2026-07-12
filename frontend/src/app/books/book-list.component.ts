import { Component, OnInit, inject } from '@angular/core';
import { BookResponse } from './book.model';
import { BooksService } from './books.service';

@Component({
  selector: 'app-book-list',
  imports: [],
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.css'
})
export class BookListComponent implements OnInit {
  private readonly booksService = inject(BooksService);

  availableBooks: BookResponse[] = [];

  ngOnInit(): void {
    this.booksService.getBooks().subscribe((books) => {
      this.availableBooks = books.filter((book) => book.availableCopies > 0);
    });
  }
}
