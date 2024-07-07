package com.grinder.repository.queries;

import com.grinder.domain.dto.CommentDTO;
import com.grinder.domain.dto.FeedDTO;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.ContentType;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;


@Repository
@EnableAsync
public class FeedQueryRepository {
    private final JPAQueryFactory queryFactory;

    public FeedQueryRepository(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public Optional<FeedDTO.FindFeedDTO> findFeed(String feedId) {
        QFeed feed = QFeed.feed;
        QImage image = QImage.image;
        QTag tag = QTag.tag;

        Feed result = queryFactory
                .select(feed)
                .from(feed)
                .where(feed.feedId.eq(feedId))
                .fetchOne();

        List<Tuple> tagImage = queryFactory
                .select(tag, image.imageUrl)
                .from(tag, image)
                .where(tag.feed.eq(result).or(image.contentType.eq(ContentType.FEED).and(image.contentId.eq(result.getFeedId()))))
                .distinct()
                .fetch();

        List<String> tagNames = tagImage.stream()
                .map(tuple -> tuple.get(tag.tagName.stringValue()))
                .collect(Collectors.toList());

        List<String> imageUrls = tagImage.stream()
                .map(tuple -> tuple.get(image.imageUrl))
                .collect(Collectors.toList());

        FeedDTO.FindFeedDTO feedDTO = new FeedDTO.FindFeedDTO(result, result.getMember(), result.getCafe(), imageUrls, tagNames);

        return Optional.ofNullable(feedDTO);
    }

    /**
     * @param email  : 로그인한 회원의 email
     * @param feedId : 가져오려는 feed의 ID
     * @return : 해당 feed의 ID를 통해 feed를 가져오고 email에 맞춘 데이터를 반환합니다.
     */
    public List<FeedDTO.FeedWithImageResponseDTO> findFeedWithImage(String email, String feedId) {

        QFeed feed = QFeed.feed;
        QHeart heart = QHeart.heart;
        QComment comment = QComment.comment;
        QComment subComment = QComment.comment;
        QMember member = QMember.member;
        QCafe cafe = QCafe.cafe;
        QTag tag = QTag.tag;
        QImage image = QImage.image;

        List<Feed> feeds = queryFactory
                .selectFrom(feed)
                .leftJoin(feed.member, member).fetchJoin()
                .leftJoin(feed.cafe, cafe).fetchJoin()
                .where(feed.isVisible.isTrue())
                .where(feed.feedId.eq(feedId))
                .orderBy(feed.updatedAt.desc())
                .fetch();

        return FindCommentInfo(feeds, email, Pageable.ofSize(1), tag, image, comment, subComment, heart, feed).getContent();
    }

    /**
     * TODO : 점수별로 feed 슬라이스(유저가 follow한 사람 중 최신 피드면  기존 랭크 +5,
     *                          최근 2개월 내 게시물은 랭크 +3
     *                          작성일 기준으로 7일로 나누어 나눈 값만큼 -
     *                          이중 rank가 높은 순으로 슬라이스)
     *                          내가 작성한 피드는 제외, 블랙리스트 피드 제외
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Slice<FeedDTO.FeedWithImageResponseDTO> RecommendFeedWithImage(String email, Pageable pageable) {
        QFeed feed = QFeed.feed;
        QHeart heart = QHeart.heart;
        QComment comment = QComment.comment;
        QComment subComment = QComment.comment;
        QFollow follow = QFollow.follow;
        QMember member = QMember.member;
        QBlacklist blacklist = QBlacklist.blacklist;
        QTag tag = QTag.tag;
        QImage image = QImage.image;
        QCafe cafe = QCafe.cafe;

        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(2);

        DateTimeTemplate<LocalDateTime> nowTemplate = Expressions.dateTimeTemplate(LocalDateTime.class, "CURRENT_TIMESTAMP");
        DateTimeTemplate<LocalDateTime> createdAtTemplate = Expressions.dateTimeTemplate(LocalDateTime.class, "{0}", feed.createdAt);

        NumberExpression<Integer> daysSinceCreated = Expressions.numberTemplate(Integer.class,
                "DATEDIFF(day, {0}, {1})", createdAtTemplate, nowTemplate);
        NumberExpression<Integer> weeksSinceCreated = daysSinceCreated.divide(7);


        NumberExpression<Integer> calculatedRank = feed.rank
                .add(new CaseBuilder()
                        .when(feed.createdAt.goe(twoMonthsAgo)).then(3)
                        .otherwise(0))
                .add(new CaseBuilder()
                        .when(follow.following.memberId.isNotNull()).then(5)
                        .otherwise(0))
                .subtract(weeksSinceCreated);

        BooleanExpression isNotBlacklisted = blacklist.blockedMember.isNull();
        BooleanExpression isVisible = feed.isVisible.eq(true);
        BooleanExpression isNotCurrentUser = feed.member.email.ne(email);
        BooleanExpression isFollowed = follow.following.email.eq(email);

        List<Tuple> tuples = queryFactory
                .select(feed, calculatedRank)
                .from(feed)
                .leftJoin(feed.cafe, cafe).fetchJoin()
                .leftJoin(feed.member, member).fetchJoin()
                .leftJoin(follow).on(follow.following.memberId.eq(feed.member.memberId)
                        .and(isFollowed))
                .leftJoin(blacklist).on(blacklist.blockedMember.memberId.eq(feed.member.memberId)
                        .and(blacklist.member.email.eq(email)))
                .where(isVisible
                        .and(isNotCurrentUser)
                        .and(isNotBlacklisted))
                .orderBy(calculatedRank.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset() + 1)
                .fetch();

        List<Feed> feeds = tuples.stream()
                .map(tuple -> tuple.get(feed))
                .collect(Collectors.toList());

        return FindCommentInfo(feeds, email, pageable, tag, image, comment, subComment, heart, feed);
    }

    /**
     * @param email : 접속한 유저
     * @return : 피드를 최신순으로 접속한 유저와 관련된 정보를 추출하여 보여줍니다.
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Slice<FeedDTO.FeedWithImageResponseDTO> findRecentFeedWithImage(String email, Pageable pageable) {
        QFeed feed = QFeed.feed;
        QCafe cafe = QCafe.cafe;
        QMember member = QMember.member;
        QImage image = QImage.image;
        QTag tag = QTag.tag;
        QComment comment = QComment.comment;
        QComment subComment = QComment.comment;
        QHeart heart = QHeart.heart;

        List<Feed> feeds = queryFactory
                .select(feed)
                .from(feed)
                .leftJoin(feed.cafe, cafe).fetchJoin()
                .leftJoin(feed.member, member).fetchJoin()
                .where(feed.isVisible.isTrue())
                .orderBy(feed.updatedAt.desc())
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return FindCommentInfo(feeds, email, pageable, tag, image, comment, subComment, heart, feed);

//        List<CompletableFuture<FeedDTO.FeedWithImageResponseDTO>> futures = feeds.stream()
//                .map(result -> asyncFindCommentInfo(result, email, tag, image, comment, subComment, heart, feed))
//                .toList();
//
//        List<FeedDTO.FeedWithImageResponseDTO> list = futures.stream()
//                .map(CompletableFuture::join)
//                .collect(Collectors.toList());
//
//        boolean hasNext = list.size() > pageable.getPageSize();
//        List<FeedDTO.FeedWithImageResponseDTO> content = hasNext ? list.subList(0, pageable.getPageSize()) : list;
//        return new SliceImpl<>(content, pageable, hasNext);
    }

    /**
     * @param email : 접속한 유저
     * @param query : 검색 쿼리
     * @return : content에서 검색 쿼리에 맞는 조건을 찾은 후 최신 순으로 나열합니다.
     */
    public Slice<FeedDTO.FeedWithImageResponseDTO> findSearchRecentFeedWithImage(String email, String query, Pageable pageable) {
        QFeed feed = QFeed.feed;
        QImage image = QImage.image;
        QTag tag = QTag.tag;
        QComment comment = QComment.comment;
        QComment subComment = QComment.comment;
        QHeart heart = QHeart.heart;
        QMember member = QMember.member;
        QCafe cafe = QCafe.cafe;

        List<Feed> feeds = queryFactory
                .selectFrom(feed)
                .leftJoin(feed.member, member)
                .leftJoin(feed.cafe, cafe)
                .where(feed.isVisible.isTrue())
                .where(feed.content.containsIgnoreCase(query)
                        .or(feed.cafe.name.containsIgnoreCase(query))
                        .or(feed.member.nickname.containsIgnoreCase(query)))
                .orderBy(feed.updatedAt.desc(), feed.content.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 2)
                .fetch();

        return FindCommentInfo(feeds, email, pageable, tag, image, comment, subComment, heart, feed);
    }

    /**
     * @param email       : 접속한 유저
     * @param writerEmail : 유저정보페이지 이메일
     * @return : 유저 정보 페이지에서 해당 유저가 작성한 피드를 모아서 보여줍니다.
     */
    public Slice<FeedDTO.FeedWithImageResponseDTO> FindMemberFeedWithImage(String email, String writerEmail, Pageable pageable) {
        QFeed feed = QFeed.feed;
        QImage image = QImage.image;
        QTag tag = QTag.tag;
        QComment comment = QComment.comment;
        QComment subComment = QComment.comment;
        QHeart heart = QHeart.heart;
        QMember member = QMember.member;

        // 먼저, 피드에 대한 기본 쿼리를 수행
        List<Feed> feeds = queryFactory
                .selectFrom(feed)
                .leftJoin(feed.member, member)
                .where(feed.isVisible.isTrue(), feed.member.email.eq(writerEmail))
                .orderBy(feed.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return FindCommentInfo(feeds, email, pageable, tag, image, comment, subComment, heart, feed);
    }

    /**
     * @param email  : 접속한 회원 email
     * @param cafeId : 확인하려는 cafeId
     * @return : 카페페이지에 접속 시 띄워줄 해당 카페 관련 피드를 호출합니다.
     */
    public Slice<FeedDTO.FeedWithImageResponseDTO> FindCafeFeedWithImage(String email, String cafeId, Pageable pageable) {
        QFeed feed = QFeed.feed;
        QImage image = QImage.image;
        QTag tag = QTag.tag;
        QComment comment = QComment.comment;
        QComment subComment = QComment.comment;
        QHeart heart = QHeart.heart;
        QMember member = QMember.member;
        QCafe cafe = QCafe.cafe;

        List<Feed> feeds = queryFactory
                .selectFrom(feed)
                .leftJoin(feed.member, member)
                .leftJoin(feed.cafe, cafe)
                .where(feed.isVisible.isTrue(), feed.cafe.cafeId.eq(cafeId))
                .orderBy(feed.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return FindCommentInfo(feeds, email, pageable, tag, image, comment, subComment, heart, feed);
    }

    /**
     * @param feeds : 검색이 완료된 feed List
     * @return : 태그 네임과, 이미지, 부모 댓글과 자식 댓글을 찾아 반환합니다.
     */
    private Slice<FeedDTO.FeedWithImageResponseDTO> FindCommentInfo(List<Feed> feeds, String email, Pageable pageable, QTag tag, QImage image, QComment comment, QComment subComment, QHeart heart, QFeed feed) {
        List<FeedDTO.FeedWithImageResponseDTO> list = feeds.stream().map(result -> {
            List<Tuple> tagImage = queryFactory
                    .select(tag, image.imageUrl)
                    .from(tag, image)
                    .where(tag.feed.eq(result).or(image.contentType.eq(ContentType.FEED).and(image.contentId.eq(result.getFeedId()))))
                    .distinct()
                    .fetch();

            List<Tag> tagNames = tagImage.stream()
                    .map(tuple -> tuple.get(tag))
                    .collect(Collectors.toList());

            List<String> imageUrls = tagImage.stream()
                    .map(tuple -> tuple.get(image.imageUrl))
                    .collect(Collectors.toList());

            List<CommentDTO.ParentCommentResponseDTO> parentComments = queryFactory
                    .select(comment,
                            JPAExpressions.selectOne()
                                    .from(heart)
                                    .where(heart.contentType.eq(ContentType.COMMENT), heart.contentId.eq(comment.commentId), heart.member.email.eq(email))
                                    .exists(),
                            JPAExpressions.select(heart.count())
                                    .from(heart)
                                    .where(heart.contentType.eq(ContentType.COMMENT), heart.contentId.eq(comment.commentId)),
                            JPAExpressions.select(image.imageUrl)
                                    .from(image)
                                    .where(image.contentType.eq(ContentType.MEMBER), image.contentId.eq(comment.member.memberId))
                    )
                    .from(comment)
                    .where(comment.isVisible.isTrue(), comment.feed.eq(result), comment.parentComment.isNull())
                    .fetch()
                    .stream()
                    .map(tuple -> {
                        Comment parent = tuple.get(comment);
                        Boolean isHeart = tuple.get(1, Boolean.class);
                        Long heartNum = tuple.get(2, Long.class);
                        String memberImage = tuple.get(3, String.class);

                        List<CommentDTO.ChildCommentResponseDTO> childComments = queryFactory
                                .select(Projections.constructor(CommentDTO.ChildCommentResponseDTO.class, subComment,
                                        JPAExpressions.selectOne()
                                                .from(heart)
                                                .where(heart.contentType.eq(ContentType.COMMENT), heart.contentId.eq(subComment.commentId), heart.member.email.eq(email))
                                                .exists(),
                                        JPAExpressions.select(heart.count())
                                                .from(heart)
                                                .where(heart.contentType.eq(ContentType.COMMENT), heart.contentId.eq(subComment.commentId)),
                                        JPAExpressions.select(image.imageUrl)
                                                .from(image)
                                                .where(image.contentType.eq(ContentType.MEMBER), image.contentId.eq(subComment.member.memberId)))
                                )
                                .from(subComment)
                                .where(subComment.isVisible.isTrue(), subComment.parentComment.eq(parent))
                                .fetch();


                        return new CommentDTO.ParentCommentResponseDTO(parent, childComments, isHeart, heartNum, memberImage);
                    })
                    .collect(Collectors.toList());

            Tuple feedInfo = queryFactory
                    .select(
                            JPAExpressions.selectOne()
                                    .from(heart)
                                    .where(heart.contentType.eq(ContentType.FEED), heart.contentId.eq(result.getFeedId()), heart.member.email.eq(email))
                                    .exists(),
                            JPAExpressions.select(heart.count())
                                    .from(heart)
                                    .where(heart.contentType.eq(ContentType.FEED), heart.contentId.eq(result.getFeedId())),
                            JPAExpressions.select(image.imageUrl)
                                    .from(image)
                                    .where(image.contentType.eq(ContentType.MEMBER), image.contentId.eq(result.getMember().getMemberId()))
                    )
                    .from(feed)
                    .where(feed.feedId.eq(result.getFeedId()))
                    .fetchOne();

            boolean isHeart = feedInfo.get(0, Boolean.class);
            Long heartNum = feedInfo.get(1, Long.class);
            String memberImage = feedInfo.get(2, String.class);

            return new FeedDTO.FeedWithImageResponseDTO(result, tagNames, parentComments, imageUrls, isHeart, heartNum, memberImage);
        }).collect(Collectors.toList());

        boolean hasNext = list.size() > pageable.getPageSize();
        List<FeedDTO.FeedWithImageResponseDTO> content = hasNext ? list.subList(0, pageable.getPageSize()) : list;
        return new SliceImpl<>(content, pageable, hasNext);
    }
}