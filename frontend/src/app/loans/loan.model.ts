export interface BorrowBookRequest {
  memberId: string;
  bookId: string;
}

export interface LoanResponse {
  id: string;
  bookId: string;
  memberId: string;
  borrowedAt: string;
  dueDate: string;
  returnedAt: string | null;
  active: boolean;
}
