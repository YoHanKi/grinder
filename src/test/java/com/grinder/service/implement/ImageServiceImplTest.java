package com.grinder.service.implement;

import com.grinder.domain.dto.ImageDTO;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.Role;
import com.grinder.repository.ImageRepository;
import com.grinder.repository.MemberRepository;
import com.grinder.repository.queries.ImageQueryRepository;
import com.grinder.service.AwsS3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {
    @Spy
    @InjectMocks
    ImageServiceImpl imageService;
    @Mock
    ImageQueryRepository imageQueryRepository;
    @Mock
    ImageRepository imageRepository;
    @Mock
    MemberRepository memberRepository;

    private Member member;
    private Cafe cafe;
    private Feed feed;
    private Comment comment;
    private Comment cComment;
    private Heart feedHeart;
    private Image feedImage;
    private Image memberImage;
    private Image cafeImage;
    @BeforeEach
    void setUp() {
        member = Member.builder().memberId("test").email("member1@example.com").nickname("user1").password("password1").role(Role.MEMBER).phoneNum("1234567890").build();
        cafe = Cafe.builder().name("Cafe A").address("123 Main St, City A").phoneNum("1112223333").averageGrade(4).build();
        feed = Feed.builder().member(member).cafe(cafe).content("Great coffee and atmosphere!").isVisible(true).grade(5).build();
        comment = Comment.builder().commentId("testId").member(member).feed(feed).content("good").build();
        cComment = Comment.builder().commentId("testId").parentComment(comment).member(member).feed(feed).content("good").build();
        feedHeart = Heart.builder().heartId("heartId").member(member).contentType(ContentType.FEED).contentId(feed.getFeedId()).build();
        feedImage = Image.builder().imageId("imageId").imageUrl("test").contentId("contentId").contentType(ContentType.FEED).build();
        memberImage = Image.builder().imageId("imageId1").imageUrl("test1").contentId("memberId").contentType(ContentType.MEMBER).build();
        cafeImage = Image.builder().imageId("imageId2").imageUrl("test2").contentId("cafeId").contentType(ContentType.CAFE).build();
    }

    @Test
    void findImage() {
        doReturn(Optional.of(feedImage)).when(imageRepository).findById(anyString());

        Image result = imageService.findImage("test");

        assertThat(result).extracting("imageId").isEqualTo("imageId");
    }

    @Test
    void findImageUrlByContentId() {
        doReturn(feedImage.getImageUrl()).when(imageQueryRepository).findImageUrlByContentId(anyString());

        String result = imageService.findImageUrlByContentId("test");

        assertThat(result).isEqualTo(feedImage.getImageUrl());
    }

    @Test
    void findAllImage() {
        doReturn(List.of(feedImage)).when(imageRepository).findByContentIdAndContentType(anyString(), any(ContentType.class));

        List<Image> result = imageService.findAllImage("test", ContentType.FEED);

        assertThat(result).extracting("imageId").contains(feedImage.getImageId());
    }

    @Test
    void deleteFeedImage() {
        List<Image> list = List.of(feedImage);
        doReturn(list).when(imageService).findAllImage(anyString(), any(ContentType.class));
        doNothing().when(imageRepository).deleteAll(anyList());

        imageService.deleteFeedImage("test", ContentType.FEED);

        verify(imageRepository).deleteAll(list);
    }

    @Test
    void findImageByImageUrl() {
        doReturn(Optional.of(feedImage)).when(imageRepository).findByImageUrl(anyString());

        Image result = imageService.findImageByImageUrl("test");

        assertThat(result).extracting("imageId").isEqualTo("imageId");
    }

    @Test
    void saveProfile_CAFE() {
        ImageDTO.UpdateRequest request = new ImageDTO.UpdateRequest();
        request.setCafeId("test");
        request.setImage(Mockito.mock(MultipartFile.class));
        doReturn(true).when(imageRepository).existsAllByContentTypeAndContentId(any(ContentType.class), anyString());
        doNothing().when(imageRepository).deleteByContentTypeAndContentId(any(ContentType.class), anyString());

        boolean result = imageService.saveProfile(request, "test");

        assertThat(result).isTrue();
        verify(imageRepository).existsAllByContentTypeAndContentId(ContentType.CAFE, "test");
    }

    @Test
    void saveProfile_MEMBER() {
        ImageDTO.UpdateRequest request = new ImageDTO.UpdateRequest();
        request.setImage(Mockito.mock(MultipartFile.class));
        doReturn(Optional.of(member)).when(memberRepository).findByEmail(anyString());
        doReturn(true).when(imageRepository).existsAllByContentTypeAndContentId(any(ContentType.class), anyString());
        doNothing().when(imageRepository).deleteByContentTypeAndContentId(any(ContentType.class), anyString());

        boolean result = imageService.saveProfile(request, "test");

        assertThat(result).isTrue();
    }

    @Test
    void deleteCafeProfile() {
        doReturn(Optional.of(cafeImage)).when(imageRepository).findByContentTypeAndContentId(any(ContentType.class), anyString());
        doNothing().when(imageRepository).delete(any(Image.class));

        boolean result = imageService.deleteCafeProfile("test");

        assertThat(result).isTrue();
        verify(imageRepository).delete(cafeImage);
    }

    @Test
    void deleteProfile() {
        doReturn(Optional.of(member)).when(memberRepository).findByEmail(anyString());
        doReturn(Optional.of(memberImage)).when(imageRepository).findByContentTypeAndContentId(any(ContentType.class), anyString());
        doNothing().when(imageRepository).delete(any(Image.class));

        boolean result = imageService.deleteProfile("test");

        assertThat(result).isTrue();
        verify(imageRepository).delete(memberImage);
    }
}