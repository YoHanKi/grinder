package com.grinder.service.implement;

import com.grinder.domain.dto.CommentDTO;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.Role;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.CommentRepository;
import com.grinder.repository.FeedRepository;
import com.grinder.repository.MemberRepository;
import com.grinder.repository.queries.CommentQueryRepository;
import com.grinder.service.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Spy
    @InjectMocks
    CommentServiceImpl commentService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    FeedServiceImpl feedService;
    @Mock
    MemberServiceImpl memberService;
    @Mock
    CommentQueryRepository commentQueryRepository;

    private Member member;
    private Cafe cafe;
    private Feed feed;
    private Comment comment;
    private Comment cComment;
    @BeforeEach
    void setUp() {
        member = Member.builder().email("member1@example.com").nickname("user1").password("password1").role(Role.MEMBER).phoneNum("1234567890").build();
        cafe = Cafe.builder().name("Cafe A").address("123 Main St, City A").phoneNum("1112223333").averageGrade(4).build();
        feed = Feed.builder().member(member).cafe(cafe).content("Great coffee and atmosphere!").isVisible(true).grade(5).build();
        comment = Comment.builder().commentId("testId").member(member).feed(feed).content("good").build();
        cComment = Comment.builder().commentId("testId").parentComment(comment).member(member).feed(feed).content("good").build();
    }
    @Test
    void findComment() {
        Optional<Comment> optionalComment = Optional.of(comment);
        doReturn(optionalComment).when(commentRepository).findById(anyString());

        Comment result = commentService.findComment("test");

        assertThat(result).extracting("commentId").isEqualTo(comment.getCommentId());
        assertThat(result).extracting("feed").extracting("feedId").isEqualTo(comment.getFeed().getFeedId());
    }

    @Test
    void findParentCommentList() {
        List<Comment> commentList = List.of(comment);
        doReturn(commentList).when(commentRepository).findByFeed_FeedIdAndParentCommentIsNullAndIsVisibleTrue(anyString());

        List<Comment> result = commentService.findParentCommentList("test");

        assertThat(result).extracting("commentId").contains(comment.getCommentId());
    }

    @Test
    void findChildrenCommentList() {
        List<Comment> commentList = List.of(cComment);
        doReturn(commentList).when(commentRepository).findByParentComment_CommentIdAndIsVisibleTrue(anyString());

        List<Comment> result = commentService.findChildrenCommentList("test");

        assertThat(result).extracting("commentId").contains(comment.getCommentId());
    }

    @Test
    void saveComment() {
        doReturn(member).when(memberService).findMemberByEmail(anyString());
        doReturn(feed).when(feedService).findFeed(anyString());
        doReturn(comment).when(commentService).findComment(anyString());
        Comment response = Comment.builder().content(comment.getContent()).member(member).feed(feed).parentComment(comment).build();
        doReturn(response).when(commentRepository).save(any(Comment.class));

        CommentDTO.CommentRequestDTO requestDTO = new CommentDTO.CommentRequestDTO("test", "test");
        Comment result = commentService.saveComment(requestDTO, "test", "test");

        assertThat(result).extracting("content").isEqualTo(comment.getContent());
    }

    @Test
    void updateComment() {
        doReturn(comment).when(commentService).findComment(anyString());
        Comment response = Comment.builder().content(comment.getContent()).member(member).feed(feed).parentComment(comment).build();
        doReturn(response).when(commentRepository).save(any(Comment.class));

        Comment result = commentService.updateComment(anyString(), anyString());

        assertThat(result).extracting("content").isEqualTo("good");
    }

    @Test
    void deleteComment() {
        doReturn(comment).when(commentService).findComment(anyString());
        Comment response = Comment.builder().content(comment.getContent()).member(member).feed(feed).parentComment(comment).build();
        doReturn(response).when(commentRepository).save(any(Comment.class));

        commentService.deleteComment(anyString());

        //캡쳐하여 확인
        ArgumentCaptor<Comment> commentCaptor = forClass(Comment.class);
        verify(commentRepository).save(commentCaptor.capture());

        Comment savedComment = commentCaptor.getValue();
        assertThat(savedComment.getIsVisible()).isEqualTo(false);
    }

    @Test
    void findCommentForAdmin() {
        CommentDTO.FindCommentDTO findCommentDTO = new CommentDTO.FindCommentDTO("testId", "content", "testName");
        doReturn(Optional.of(findCommentDTO)).when(commentQueryRepository).findComment(anyString());

        CommentDTO.FindCommentDTO result = commentService.findCommentForAdmin(anyString());

        assertThat(result).extracting("commentId").isEqualTo(findCommentDTO.getCommentId());
        assertThat(result).extracting("content").isEqualTo(findCommentDTO.getContent());
        assertThat(result).extracting("nickname").isEqualTo(findCommentDTO.getNickname());
    }
}