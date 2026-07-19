/** Corps de la requête envoyée à POST /api/loans pour créer un emprunt. */
export interface BorrowBookRequest {
  memberId: string;
  bookId: string;
}

/** Représentation d'un emprunt telle que renvoyée par l'API des emprunts. */
export interface LoanResponse {
  id: string;
  bookId: string;
  memberId: string;
  borrowedAt: string;
  dueDate: string;
  returnedAt: string | null;
  active: boolean;
}
