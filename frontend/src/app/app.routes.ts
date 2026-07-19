import { Routes } from '@angular/router';
import { BookListComponent } from './books/book-list.component';
import { BorrowBookComponent } from './loans/borrow-book.component';

/**
 * Routes de l'application. La racine redirige vers le catalogue des livres,
 * qui est la page d'entrée naturelle de la démo.
 */
export const routes: Routes = [
  { path: '', redirectTo: 'books', pathMatch: 'full' },
  { path: 'books', component: BookListComponent },
  { path: 'loans', component: BorrowBookComponent }
];
