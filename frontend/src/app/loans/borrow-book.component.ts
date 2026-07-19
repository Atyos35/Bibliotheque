import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BookResponse } from '../books/book.model';
import { BooksService } from '../books/books.service';
import { LoansService } from './loans.service';

/**
 * Formulaire d'emprunt : sélectionne un livre disponible et un membre (par ID,
 * aucun GET /members disponible côté backend), et affiche les erreurs métier
 * renvoyées par le backend dans un bloc stylé plutôt qu'un message générique.
 */
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
  loadingBooks = true;
  submitting = false;
  memberId = '';
  selectedBookId = '';
  errorMessage: string | null = null;
  successMessage: string | null = null;

  /** Charge les livres empruntables pour peupler le sélecteur du formulaire. */
  ngOnInit(): void {
    this.booksService.getBooks().subscribe((books) => {
      this.availableBooks = books.filter((book) => book.availableCopies > 0);
      this.loadingBooks = false;
    });
  }

  /** Soumet la demande d'emprunt et réinitialise le formulaire en cas de succès. */
  borrow(): void {
    this.errorMessage = null;
    this.successMessage = null;
    this.submitting = true;

    this.loansService.borrow({ memberId: this.memberId, bookId: this.selectedBookId }).subscribe({
      next: () => {
        this.submitting = false;
        this.successMessage = 'Emprunt enregistré avec succès.';
        this.memberId = '';
        this.selectedBookId = '';
      },
      error: (error: HttpErrorResponse) => {
        this.submitting = false;
        this.errorMessage = this.extractErrorMessage(error);
      }
    });
  }

  /**
   * Distingue un 409 métier (règle de domaine violée côté backend, ex. livre
   * indisponible) — dont le message est affiché tel quel — d'une erreur
   * technique, pour laquelle on affiche un message générique plutôt que de
   * fuiter un détail d'infrastructure à l'utilisateur.
   */
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
