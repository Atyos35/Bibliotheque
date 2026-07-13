import { Routes } from '@angular/router';
import { BookListComponent } from './books/book-list.component';
import { BorrowBookComponent } from './loans/borrow-book.component';

export const routes: Routes = [
  { path: '', redirectTo: 'books', pathMatch: 'full' },
  { path: 'books', component: BookListComponent },
  { path: 'loans', component: BorrowBookComponent }
];
