package com.grinder.service.implement;

import com.grinder.domain.dto.FeedDTO;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.ContentType;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.FeedRepository;
import com.grinder.repository.queries.FeedQueryRepository;
import com.grinder.service.FeedService;
import com.grinder.service.ImageService;
import com.grinder.service.MemberService;
import com.grinder.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final FeedRepository feedRepository;
    private final ImageService imageService;
    private final TagService tagService;
    private final CafeRepository cafeRepository;
    private final MemberService memberService;
    private final FeedQueryRepository feedQueryRepository;

    @Override
    public Feed findFeed(String feedId) {
        return feedRepository.findById(feedId).orElseThrow(() -> new IllegalArgumentException("feed id(" + feedId + ")를 찾울 수 없습니다."));
    }

    @Override
    public Feed saveFeed(FeedDTO.FeedRequestDTO request, String memberEmail, MultipartFile file){
        // Feed 저장
        Member member = memberService.findMemberByEmail(memberEmail);
        Cafe cafe = cafeRepository.findById(request.getCafeId()).orElseThrow(() -> new IllegalArgumentException("cafe id를 찾울 수 없습니다.")); // todo: CafeService로 수정
        Feed feed = Feed.builder()
                .content(request.getContent())
                .grade(request.getGrade())
                .member(member)
                .cafe(cafe)
                .build();

        // Tag 저장
        tagService.saveTag(feed, request.getTagNameList());

        // Image 저장, TODO: S3 저장 로직 추가
        imageService.saveFeedImage(feed.getFeedId(), ContentType.FEED, request.getImageUrlList());

        return feedRepository.save(feed);
    }

    @Override
    public List<Feed> findAllFeed() {   // isVisible == true 인 피드만 조회
        return feedRepository.findAllByIsVisibleTrue();
    }

    @Override
    public Feed updateFeed(String feedId, FeedDTO.FeedRequestDTO request) {
        // 피드 수정
        Feed feed = findFeed(feedId);
        Cafe cafe = cafeRepository.findById(request.getCafeId()).orElseThrow(() -> new IllegalArgumentException("cafe id를 찾울 수 없습니다.")); // todo: CafeService로 수정
        feed.updateFeed(cafe, request.getContent(), request.getGrade());

        // 태그 수정
        // TODO: 선택된 것만 수정하기
        tagService.deleteTag(feedId);
        tagService.saveTag(feed, request.getTagNameList());

        // 이미지 수정
        // TODO: 선택된 것만 수정하기
        imageService.deleteFeedImage(feedId, ContentType.FEED);
        imageService.saveFeedImage(feedId, ContentType.FEED, request.getImageUrlList());

        return feed;
    }

    @Override
    public void deleteFeed(String feedId) {
        Feed feed = findFeed(feedId);
        feed.notVisible();
        feedRepository.save(feed);
    }

    @Override
    public FeedDTO.FindFeedDTO findFeedForAdmin(String feedId) {
        FeedDTO.FindFeedDTO feedDTO = feedQueryRepository.findFeed(feedId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 피드입니다."));
        return feedDTO;
    }

    @Override
    public Feed findFeedByCafeId(String cafeId) {
        //작성중ㄱㄷ
        return null;
    }

    @Override
    public Slice<FeedDTO.FeedWithImageResponseDTO> findMyPageFeedWithImage(String connectEmail, String myPageEmail, Pageable pageable) {
        return feedQueryRepository.FindMemberFeedWithImage(connectEmail, myPageEmail, pageable);
    }

    @Override
    public Slice<FeedDTO.FeedWithImageResponseDTO> findCafeFeedWithImage(String connectEmail, String cafeId, Pageable pageable) {
        return feedQueryRepository.FindCafeFeedWithImage(connectEmail, cafeId, pageable);
    }

    @Override
    public Slice<FeedDTO.FeedWithImageResponseDTO> searchFeed(String email, String query, Pageable pageable) {
        return feedQueryRepository.findSearchRecentFeedWithImage(email, query, pageable);
    }
}
