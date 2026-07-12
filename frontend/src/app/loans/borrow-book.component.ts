import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BookResponse } from '../books/book.model';
import { BooksService } from '../books/books.service';
import { LoansService } from './loans.service';

@Component({
  selector: 'app-borrow-book',
  imports: [FormsModule],
  templateUrl: './borrow-book.component.html',
  styleUrl: './borrow-book.component.css'
})
export class BorrowBookComponent implements OnInit {
  private readonly booksService = inject(BooksService);
  private readonly loansService = inject(LoansService);

  availableBooks: BookResponse[] = [];
  memberId = '';
  selectedBookId = '';
  errorMessage: string | null = null;
  successMessage: string | null = null;

  ngOnInit(): void {
    this.booksService.getBooks().subscribe((books) => {
      this.availableBooks = books.filter((book) => book.availableCopies > 0);
    });
  }

  borrow(): void {
    this.errorMessage = null;
    this.successMessage = null;

    this.loansService.borrow({ memberId: this.memberId, bookId: this.selectedBookId }).subscribe({
      next: () => {
        this.successMessage = 'Emprunt enregistré avec succès.';
        this.memberId = '';
        this.selectedBookId = '';
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = this.extractErrorMessage(error);
      }
    });
  }

  private extractErrorMessage(error: HttpErrorResponse): string {
    if (error.status === 409) {
      const businessMessage = (error.error as { message?: string } | null)?.message;
      if (businessMessage) {
        return businessMessage;
      }
    }
    return "Une erreur est survenue lors de l'emprunt.";
  }
}
