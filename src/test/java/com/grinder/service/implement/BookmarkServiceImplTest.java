package com.grinder.service.implement;

import com.grinder.domain.dto.BookmarkDTO;
import com.grinder.domain.entity.Bookmark;
import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.Member;
import com.grinder.repository.BookmarkRepository;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.MemberRepository;
import com.grinder.repository.queries.BookmarkQueryRepository;
import com.grinder.service.BookmarkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({BookmarkServiceImpl.class, BookmarkQueryRepository.class})
@ActiveProfiles("test")
class BookmarkServiceImplTest {
    @Autowired
    public BookmarkServiceImpl bookmarkService;
    @Autowired
    public BookmarkRepository bookmarkRepository;
    @Autowired
    public BookmarkQueryRepository bookmarkQueryRepository;
    @Autowired
    public MemberRepository memberRepository;
    @Autowired
    public CafeRepository cafeRepository;

    public  Member member;
    public Cafe cafe;
    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.builder().email("test1@test.com").nickname("test-user-1").password("1234").phoneNum("01012345678").build());
        cafe = cafeRepository.save(Cafe.builder().name("그라인더").address("사랑시 고백구 행복동 794-2").phoneNum("01012345678").build());
    }

    @Test
    void findAllBookmarksSlice() {
        bookmarkRepository.save(Bookmark.builder().member(member).cafe(cafe).build());
        Pageable pageable = PageRequest.of(0, 10);

        List<BookmarkDTO.findAllResponse> list = bookmarkService.findAllBookmarksSlice(member.getEmail(), pageable).getContent();

        assertEquals(list.get(0).getCafeName(), cafe.getName());
    }

    @Test
    void addBookmark() {
        boolean result = bookmarkService.addBookmark(member.getEmail(), cafe.getCafeId());

        assertTrue(result);
        assertTrue(bookmarkRepository.existsByMemberAndCafe(member,cafe));
    }

    @Test
    void deleteBookmark() {
        bookmarkRepository.save(Bookmark.builder().cafe(cafe).member(member).build());

        boolean result = bookmarkService.deleteBookmark(member.getEmail(), cafe.getCafeId());

        assertTrue(result);
        List<Bookmark> booklist = bookmarkRepository.findAll();
        assertEquals(booklist.size(), 0);
    }

    @Test
    void existsBookmarkByEmailAndCafeId() {
        bookmarkRepository.save(Bookmark.builder().cafe(cafe).member(member).build());
        boolean result = bookmarkService.existsBookmarkByEmailAndCafeId(member.getEmail(), cafe.getCafeId());

        assertTrue(result);
    }
}