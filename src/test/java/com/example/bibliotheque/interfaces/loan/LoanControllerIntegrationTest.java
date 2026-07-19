package com.example.bibliotheque.interfaces.loan;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bibliotheque.domain.book.Book;
import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.book.ISBN;
import com.example.bibliotheque.domain.loan.Loan;
import com.example.bibliotheque.domain.loan.LoanId;
import com.example.bibliotheque.domain.loan.LoanRepository;
import com.example.bibliotheque.domain.member.Email;
import com.example.bibliotheque.domain.member.Member;
import com.example.bibliotheque.domain.member.MemberId;
import com.example.bibliotheque.domain.member.MembershipStatus;
import com.example.bibliotheque.infrastructure.book.BookMapper;
import com.example.bibliotheque.infrastructure.book.SpringDataBookRepository;
import com.example.bibliotheque.infrastructure.member.MemberMapper;
import com.example.bibliotheque.infrastructure.member.SpringDataMemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles({"dev", "test"})
@Transactional
class LoanControllerIntegrationTest {

    private static final LocalDateTime BORROWED_AT_IN_THE_PAST = LocalDateTime.now().minusDays(30);

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringDataMemberRepository springDataMemberRepository;

    @Autowired
    private SpringDataBookRepository springDataBookRepository;

    @Autowired
    private LoanRepository loanRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private MemberId givenAnActiveMember() {
        MemberId memberId = MemberId.generate();
        Member member = new Member(memberId, "Jane Doe", new Email("jane.doe@example.com"),
                MembershipStatus.ACTIVE);
        springDataMemberRepository.save(MemberMapper.toEntity(member));
        return memberId;
    }

    private MemberId givenASuspendedMember() {
        MemberId memberId = MemberId.generate();
        Member member = new Member(memberId, "Jane Doe", new Email("jane.doe@example.com"),
                MembershipStatus.SUSPENDED);
        springDataMemberRepository.save(MemberMapper.toEntity(member));
        return memberId;
    }

    private BookId givenAnAvailableBook() {
        BookId bookId = BookId.generate();
        Book book = Book.create(bookId, new ISBN("9780134685991"), "Effective Java", "Joshua Bloch", 1);
        springDataBookRepository.save(BookMapper.toEntity(book));
        return bookId;
    }

    private BookId givenABookWithNoAvailableCopy() {
        BookId bookId = BookId.generate();
        Book book = new Book(bookId, new ISBN("9780134685991"), "Effective Java", "Joshua Bloch", 1, 0);
        springDataBookRepository.save(BookMapper.toEntity(book));
        return bookId;
    }

    private String borrowRequestBody(MemberId memberId, BookId bookId) throws Exception {
        return objectMapper.writeValueAsString(
                new BorrowBookRequest(memberId.toString(), bookId.toString()));
    }

    @Test
    void borrowingAvailableBookAsActiveMemberSucceeds() throws Exception {
        MemberId memberId = givenAnActiveMember();
        BookId bookId = givenAnAvailableBook();

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(borrowRequestBody(memberId, bookId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.memberId").value(memberId.toString()))
                .andExpect(jsonPath("$.bookId").value(bookId.toString()))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.returnedAt").doesNotExist());
    }

    @Test
    void borrowingWithSuspendedMemberIsRefusedWithConflict() throws Exception {
        MemberId memberId = givenASuspendedMember();
        BookId bookId = givenAnAvailableBook();

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(borrowRequestBody(memberId, bookId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    void borrowingWithUnknownMemberIsRefusedWithNotFound() throws Exception {
        BookId bookId = givenAnAvailableBook();

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(borrowRequestBody(MemberId.generate(), bookId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void borrowingUnknownBookIsRefusedWithNotFound() throws Exception {
        MemberId memberId = givenAnActiveMember();

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(borrowRequestBody(memberId, BookId.generate())))
                .andExpect(status().isNotFound());
    }

    @Test
    void borrowingWhenMemberAlreadyHasThreeActiveLoansIsRefusedWithConflict() throws Exception {
        MemberId memberId = givenAnActiveMember();
        BookId bookId = givenAnAvailableBook();
        Loan firstLoan = Loan.borrow(LoanId.generate(), BookId.generate(), memberId, LocalDateTime.now(),
                List.of());
        Loan secondLoan = Loan.borrow(LoanId.generate(), BookId.generate(), memberId, LocalDateTime.now(),
                List.of(firstLoan));
        Loan thirdLoan = Loan.borrow(LoanId.generate(), BookId.generate(), memberId, LocalDateTime.now(),
                List.of(firstLoan, secondLoan));
        loanRepository.save(firstLoan);
        loanRepository.save(secondLoan);
        loanRepository.save(thirdLoan);

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(borrowRequestBody(memberId, bookId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    void borrowingWhenMemberHasAnOverdueLoanIsRefusedWithConflict() throws Exception {
        MemberId memberId = givenAnActiveMember();
        BookId bookId = givenAnAvailableBook();
        Loan overdueLoan = Loan.borrow(LoanId.generate(), BookId.generate(), memberId,
                BORROWED_AT_IN_THE_PAST, List.of());
        loanRepository.save(overdueLoan);

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(borrowRequestBody(memberId, bookId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    void borrowingWhenBookHasNoAvailableCopyIsRefusedWithConflict() throws Exception {
        MemberId memberId = givenAnActiveMember();
        BookId bookId = givenABookWithNoAvailableCopy();

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(borrowRequestBody(memberId, bookId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    void returningAnActiveLoanSucceeds() throws Exception {
        MemberId memberId = givenAnActiveMember();
        BookId bookId = givenABookWithNoAvailableCopy();
        Loan loan = Loan.borrow(LoanId.generate(), bookId, memberId, LocalDateTime.now(), List.of());
        loanRepository.save(loan);

        mockMvc.perform(post("/api/loans/" + loan.id() + "/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.returnedAt", notNullValue()));
    }

    @Test
    void returningAnAlreadyReturnedLoanIsRefusedWithConflict() throws Exception {
        MemberId memberId = givenAnActiveMember();
        BookId bookId = givenAnAvailableBook();
        Loan loan = Loan.borrow(LoanId.generate(), bookId, memberId, LocalDateTime.now(), List.of());
        loan.returnBook(LocalDateTime.now());
        loanRepository.save(loan);

        mockMvc.perform(post("/api/loans/" + loan.id() + "/return"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    void returningAnUnknownLoanIsRefusedWithNotFound() throws Exception {
        mockMvc.perform(post("/api/loans/" + LoanId.generate() + "/return"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listingActiveLoansReturnsOnlyTheMembersActiveLoans() throws Exception {
        MemberId memberId = givenAnActiveMember();
        BookId activeBookId = givenAnAvailableBook();
        Loan activeLoan = Loan.borrow(LoanId.generate(), activeBookId, memberId, LocalDateTime.now(),
                List.of());
        Loan returnedLoan = Loan.borrow(LoanId.generate(), BookId.generate(), memberId, LocalDateTime.now(),
                List.of(activeLoan));
        returnedLoan.returnBook(LocalDateTime.now());
        loanRepository.save(activeLoan);
        loanRepository.save(returnedLoan);

        mockMvc.perform(get("/api/loans").param("memberId", memberId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(activeLoan.id().toString()))
                .andExpect(jsonPath("$[0].bookId").value(activeBookId.toString()))
                .andExpect(jsonPath("$[0].borrowedAt", notNullValue()))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void listingActiveLoansForMemberWithoutLoansReturnsEmptyList() throws Exception {
        MemberId memberId = givenAnActiveMember();

        mockMvc.perform(get("/api/loans").param("memberId", memberId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void listingActiveLoansForUnknownMemberIsRefusedWithNotFound() throws Exception {
        mockMvc.perform(get("/api/loans").param("memberId", MemberId.generate().toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", notNullValue()));
    }
}
