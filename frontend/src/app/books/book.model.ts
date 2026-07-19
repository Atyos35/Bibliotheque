/** Représentation d'un livre telle que renvoyée par GET /api/books. */
export interface BookResponse {
  id: string;
  isbn: string;
  title: string;
  author: string;
  totalCopies: number;
  availableCopies: number;
}
