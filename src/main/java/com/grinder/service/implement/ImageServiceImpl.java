package com.grinder.service.implement;

import com.grinder.domain.dto.ImageDTO;
import com.grinder.domain.entity.Image;
import com.grinder.domain.entity.Member;
import com.grinder.domain.enums.ContentType;
import com.grinder.repository.ImageRepository;
import com.grinder.repository.MemberRepository;
import com.grinder.repository.queries.ImageQueryRepository;
import com.grinder.service.AwsS3Service;
import com.grinder.service.ImageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{
    private final ImageQueryRepository imageQueryRepository;
    private final ImageRepository imageRepository;
//    private final AwsS3Service awsS3Service;
    private final MemberRepository memberRepository;

    @Override
    public Image findImage(String imageId) {
        return imageRepository.findById(imageId).orElseThrow(() -> new IllegalArgumentException("image id(" + imageId + ")를 찾울 수 없습니다."));
    }

    @Override
    public String findImageUrlByContentId(String id) {
        return imageQueryRepository.findImageUrlByContentId(id);
    }

    @Override
    public List<Image> findAllImage(String contentId, ContentType contentType) {
        return imageRepository.findByContentIdAndContentType(contentId, contentType);
    }

//    @Override
//    public void saveFeedImage(String contentId, ContentType contentType, List<String> imageUrlList) {
//        for (String imageUrl : imageUrlList) {
//            imageRepository.save(
//                    Image.builder()
//                            .imageUrl(imageUrl)
//                            .contentId(contentId)
//                            .contentType(contentType)
//                            .build()
//                    );
//        }
//    }
    @Override
    public void deleteFeedImage(String contentId, ContentType contentType) {
        List<Image> ImageList = findAllImage(contentId, contentType);
//        for(Image image : ImageList){
//            try {
//                awsS3Service.deleteFile(image.getImageUrl());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
        imageRepository.deleteAll(ImageList);
    }

    @Override
    public Image findImageByImageUrl(String imageUrl) {
        return imageRepository.findByImageUrl(imageUrl).orElseThrow(() -> new NoSuchElementException("Url: " + imageUrl + "에 해당하는 이미지가 존재하지 않습니다."));
    }

    @Override
    public boolean saveProfile(ImageDTO.UpdateRequest request, String email) {
        Image image;
        if (request.getCafeId() != null) {
            if (imageRepository.existsAllByContentTypeAndContentId(ContentType.CAFE, request.getCafeId())) {
                imageRepository.deleteByContentTypeAndContentId(ContentType.CAFE, request.getCafeId());
            }
//            image = awsS3Service.uploadSingleImageBucket(request.getImage(), request.getCafeId(), ContentType.CAFE);
        } else {
            Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
            if (imageRepository.existsAllByContentTypeAndContentId(ContentType.MEMBER, member.getMemberId())) {
                imageRepository.deleteByContentTypeAndContentId(ContentType.MEMBER, member.getMemberId());
            }
//            image = awsS3Service.uploadSingleImageBucket(request.getImage(), member.getMemberId(), ContentType.MEMBER);
        }
//        imageRepository.save(image);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteCafeProfile(String cafeId) {
        Image image = imageRepository.findByContentTypeAndContentId(ContentType.CAFE, cafeId)
                .orElseThrow(() -> new EntityNotFoundException("이미 존재하지 않습니다."));
        imageRepository.delete(image);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteProfile(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
        Image image = imageRepository.findByContentTypeAndContentId(ContentType.MEMBER, member.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("이미 존재하지 않습니다."));
//        try {
//            awsS3Service.deleteFile(image.getImageUrl());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        imageRepository.delete(image);
        return true;
    }
}
