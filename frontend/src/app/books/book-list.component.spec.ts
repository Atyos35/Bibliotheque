import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { BookListComponent } from './book-list.component';
import { BookResponse } from './book.model';
import { BooksService } from './books.service';

describe('BookListComponent', () => {
  let fixture: ComponentFixture<BookListComponent>;

  const books: BookResponse[] = [
    {
      id: '1',
      isbn: '9780134685991',
      title: 'Effective Java',
      author: 'Joshua Bloch',
      totalCopies: 2,
      availableCopies: 1
    },
    {
      id: '2',
      isbn: '9780132350884',
      title: 'Clean Code',
      author: 'Robert C. Martin',
      totalCopies: 1,
      availableCopies: 0
    }
  ];

  beforeEach(async () => {
    const booksServiceSpy = jasmine.createSpyObj<BooksService>('BooksService', ['getBooks']);
    booksServiceSpy.getBooks.and.returnValue(of(books));

    await TestBed.configureTestingModule({
      imports: [BookListComponent],
      providers: [{ provide: BooksService, useValue: booksServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(BookListComponent);
    fixture.detectChanges();
  });

  it('keeps only books with available copies', () => {
    expect(fixture.componentInstance.availableBooks.length).toBe(1);
    expect(fixture.componentInstance.availableBooks[0].title).toBe('Effective Java');
  });

  it('renders the available book but not the unavailable one', () => {
    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(text).toContain('Effective Java');
    expect(text).not.toContain('Clean Code');
  });
});
