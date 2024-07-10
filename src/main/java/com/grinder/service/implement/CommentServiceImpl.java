package com.grinder.service.implement;

import com.grinder.domain.dto.CommentDTO;
import com.grinder.domain.entity.Comment;
import com.grinder.domain.entity.Feed;
import com.grinder.domain.entity.Member;
import com.grinder.repository.CommentRepository;
import com.grinder.repository.queries.CommentQueryRepository;
import com.grinder.service.CommentService;
import com.grinder.service.FeedService;
import com.grinder.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final FeedService feedService;
    private final MemberService memberService;
    private final CommentQueryRepository commentQueryRepository;

    @Override
    public Comment findComment(String commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    @Override
    public List<Comment> findParentCommentList(String feedId) {
        return commentRepository.findByFeed_FeedIdAndParentCommentIsNullAndIsVisibleTrue(feedId);
    }

    @Override
    public List<Comment> findChildrenCommentList(String parentId) {
        return commentRepository.findByParentComment_CommentIdAndIsVisibleTrue(parentId);
    }

    @Override
    public Comment saveComment(CommentDTO.CommentRequestDTO request, String memberEmail, String feed_id) {
        Member member = memberService.findMemberByEmail(memberEmail);
        Feed feed = feedService.findFeed(feed_id);
        Comment parentComment = findComment(request.getParentCommentId());
        return commentRepository.save(
                Comment.builder()
                        .content(request.getContent())
                        .member(member)
                        .feed(feed)
                        .parentComment(parentComment)
                        .build()
        );
    }

    @Override
    public Comment updateComment(String commentId, String content) {
        Comment comment = findComment(commentId);
        comment.updateContent(content);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(String commentId) {
        Comment comment = findComment(commentId);
        comment.notVisible();
        commentRepository.save(comment);
    }

    @Override
    public CommentDTO.FindCommentDTO findCommentForAdmin(String commentId) {
        return commentQueryRepository.findComment(commentId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 댓글입니다."));
    }
}
