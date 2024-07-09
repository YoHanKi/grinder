package com.grinder.service.implement;

import com.grinder.domain.dto.FollowDTO;
import com.grinder.domain.entity.Follow;
import com.grinder.domain.entity.Member;
import com.grinder.repository.FollowRepository;
import com.grinder.repository.MemberRepository;
import com.grinder.repository.queries.FollowQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Import({FollowServiceImpl.class, FollowQueryRepository.class})
@ActiveProfiles("test")
class FollowServiceImplTest {
    @Autowired
    public FollowServiceImpl followService;
    @Autowired
    public FollowQueryRepository followQueryRepository;
    @Autowired
    public FollowRepository followRepository;
    @Autowired
    public MemberRepository memberRepository;

    public Member follower;
    public  Member following;

    @BeforeEach
    void setUp() {
        follower = memberRepository.save(Member.builder().email("test1@test.com").nickname("test-user-1").password("1234").phoneNum("01012345678").build());
        following = memberRepository.save(Member.builder().email("test2@test.com").nickname("test-user-2").password("1234").phoneNum("01012345678").build());
    }
    @Test
    void findAllFollowSlice() {
        followRepository.save(Follow.builder().member(follower).following(following).build());
        Pageable pageable = PageRequest.of(0, 10);

        List<FollowDTO.findAllFollowingResponse> list = followService.findAllFollowingSlice(follower.getEmail(), pageable).getContent();

        assertEquals(list.get(0).getFollowEmail(), following.getEmail());
    }

    @Test
    void findAllFollowerSlice() {
        followRepository.save(Follow.builder().member(follower).following(following).build());
        Pageable pageable = PageRequest.of(0, 10);

        List<FollowDTO.findAllFollowerResponse> list = followService.findAllFollowerSlice(following.getEmail(), pageable).getContent();

        assertEquals(list.get(0).getFollowEmail(), follower.getEmail());
    }

    @Test
    void addFollow() {
        boolean result = followService.addFollow(follower.getEmail(), following.getEmail());

        assertTrue(result);
        assertTrue(followQueryRepository.existsByMemberEmailAndFollowEmail(follower.getEmail(), following.getEmail()));
    }

    @Test
    void deleteFollow() {
        followRepository.save(Follow.builder().member(follower).following(following).build());
        boolean result = followService.deleteFollow(follower.getEmail(), following.getEmail());

        assertTrue(result);
        assertFalse(followQueryRepository.existsByMemberEmailAndFollowEmail(follower.getEmail(), following.getEmail()));
    }

    @Test
    void existFollow() {
        followRepository.save(Follow.builder().member(follower).following(following).build());
        boolean result = followService.existFollow(follower.getEmail(), following.getEmail());

        assertTrue(result);
    }
}