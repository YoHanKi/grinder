package com.grinder.service;

import com.grinder.domain.dto.FeedDTO;
import com.grinder.domain.entity.Feed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FeedService {
    // feedId로 피드 찾기
    Feed findFeed(String feedId);
    // 피드 생성
    Feed saveFeed(FeedDTO.FeedRequestDTO request, String memberEmail, List<MultipartFile> imageList);
    // 피드 조회 (비회원)
    List<Feed> findAllFeed();
    // 피드 수정
    Feed updateFeed(String feedId, FeedDTO.FeedRequestDTO request, List<MultipartFile> imageList);
    // 피드 삭제(isVisible == false)
    void deleteFeed(String feedId);
    //관리자 페이지 피드 조회(신고 처리를 위해 사용)
    FeedDTO.FindFeedDTO findFeedForAdmin(String feedId);
    //cafeId로 Feed 찾기
    //Feed findFeedByCafeId(String cafeId);

//    List<FeedResponseDTO> findFeedsByCafeId(String cafeId);

    Slice<FeedDTO.FeedWithImageResponseDTO> findMyPageFeedWithImage(String connectEmail, String myPageEmail, Pageable pageable);
    Slice<FeedDTO.FeedWithImageResponseDTO> findCafeFeedWithImage(String connectEmail, String cafeId, Pageable pageable);

    Slice<FeedDTO.FeedWithImageResponseDTO> searchFeed(String email, String query, Pageable pageable);

    Slice<FeedDTO.FeedWithImageResponseDTO> findRecentFeedWithImage(String email, Pageable pageable);

    Slice<FeedDTO.FeedWithImageResponseDTO> RecommendFeedWithImage(String email, Pageable pageable);

    List<FeedDTO.FeedWithImageResponseDTO> findFeedForComment(String email, String feedId);
}
