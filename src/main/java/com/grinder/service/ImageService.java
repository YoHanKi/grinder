package com.grinder.service;

import com.grinder.domain.dto.ImageDTO;
import com.grinder.domain.entity.Image;
import com.grinder.domain.enums.ContentType;

import java.util.List;

public interface ImageService {
    // imageId로 이미지 찾기
    Image findImage(String imageId);
    // 해당 컨텐츠(피드)에서 사용한 모든 이미지 찾기
    List<Image> findAllImage(String contentId, ContentType contentType);
    // 해당 컨텐츠(피드)에서 사용할 이미지 모두 저장
//    void saveFeedImage(String contentId, ContentType contentType, List<String> imageUrlList);
    // 해당 컨텐츠(피드)에서 사용한 이미지 모두 삭제
    void deleteFeedImage(String contentId, ContentType contentType);
    String findImageUrlByContentId(String id);
    Image findImageByImageUrl(String imageUrl);
    boolean saveProfile(ImageDTO.UpdateRequest request, String email);
    boolean deleteCafeProfile(String cafeId);
    boolean deleteProfile(String email);
}
