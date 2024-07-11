package com.grinder.service.implement;

import com.grinder.domain.dto.FeedDTO;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.ContentType;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.FeedRepository;
import com.grinder.repository.queries.FeedQueryRepository;
import com.grinder.service.*;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
//    private final AwsS3Service awsS3Service;

    @Override
    public Feed findFeed(String feedId) {
        return feedRepository.findById(feedId).orElseThrow(() -> new IllegalArgumentException("feed id(" + feedId + ")를 찾울 수 없습니다."));
    }

    @Override
    @Transactional
    public Feed saveFeed(FeedDTO.FeedRequestDTO request, String memberEmail, List<MultipartFile> imageList){
        // Feed 저장
        Member member = memberService.findMemberByEmail(memberEmail);
        Cafe cafe = cafeRepository.findById(request.getCafeId()).orElse(null);
        Feed feed = Feed.builder()
                .content(request.getContent())
                .grade(request.getGrade())
                .member(member)
                .cafe(cafe)
                .build();

        feed = feedRepository.save(feed);

        // Tag 저장
        tagService.saveTag(feed, request.getTagNameList());

        // Image 저장, TODO: S3 저장 로직 추가
        if (imageList == null) {
            imageList = new ArrayList<>();
        }
//        for (MultipartFile image : imageList) {
//            awsS3Service.uploadSingleImageBucket(image, feed.getFeedId(), ContentType.FEED);
//        }

        return feed;
    }

    @Override
    public List<Feed> findAllFeed() {   // isVisible == true 인 피드만 조회
        return feedRepository.findAllByIsVisibleTrue();
    }

    @Override
    public Feed updateFeed(String feedId, FeedDTO.FeedRequestDTO request, List<MultipartFile> imageList) {
        // 피드 수정
        Feed feed = findFeed(feedId);
        Cafe cafe = cafeRepository.findById(request.getCafeId()).orElse(null);
        feed.updateFeed(cafe, request.getContent(), request.getGrade());

        // 태그 수정
        // TODO: 선택된 것만 수정하기
        tagService.deleteTag(feedId);
        tagService.saveTag(feed, request.getTagNameList());

        // 이미지 수정
        // TODO: 선택된 것만 수정하기
        imageService.deleteFeedImage(feedId, ContentType.FEED);
        if (imageList == null) {
            imageList = new ArrayList<>();
        }
//        for (MultipartFile image : imageList) {
//            awsS3Service.uploadSingleImageBucket(image, feed.getFeedId(), ContentType.FEED);
//        }

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
        return feedQueryRepository.findFeed(feedId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 피드입니다."));
    }

//    public List<FeedResponseDTO> findFeedsByCafeId(String cafeId) {
//        List<Feed> feedList = feedRepository.findFeedsByCafe_CafeId(cafeId);
//
//        // 조회된 피드 리스트를 FeedResponseDTO로 변환합니다.
//        List<FeedResponseDTO> feedDTOList = new ArrayList<>();
//        for (Feed feed : feedList) {
//            FeedResponseDTO responseDTO = new FeedResponseDTO(feed);
//            feedDTOList.add(responseDTO);
//        }
//
//        return feedDTOList;
//    }

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
    
    @Override
    public Slice<FeedDTO.FeedWithImageResponseDTO> findRecentFeedWithImage(String email, Pageable pageable) {
        return feedQueryRepository.findRecentFeedWithImage(email, pageable);
    }

    @Override
    public Slice<FeedDTO.FeedWithImageResponseDTO> RecommendFeedWithImage(String email, Pageable pageable) {
        return feedQueryRepository.RecommendFeedWithImage(email, pageable);
    }

    @Override
    public List<FeedDTO.FeedWithImageResponseDTO> findFeedForComment(String email,String feedId) {
        return feedQueryRepository.findFeedWithImage(email, feedId);
    }
}
