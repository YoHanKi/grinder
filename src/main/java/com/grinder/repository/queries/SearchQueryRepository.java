package com.grinder.repository.queries;

import com.grinder.domain.dto.CafeDTO;
import com.grinder.domain.dto.FeedDTO;
import com.grinder.domain.dto.MemberDTO;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.ContentType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SearchQueryRepository {
    private final JPAQueryFactory queryFactory;

    public SearchQueryRepository(EntityManager entityManager) {
        queryFactory = new JPAQueryFactory(entityManager);
    }

    //회원 닉네임이나 이메일 검색
    public Slice<MemberDTO.FindMemberAndImageDTO> searchMembersByNicknameOrEmail(String query, Pageable pageable) {
        QMember member = QMember.member;
        QImage image = QImage.image;

        long limit = pageable.getPageSize() + 1;
        long offset = pageable.getOffset();

        List<MemberDTO.FindMemberAndImageDTO> content =  queryFactory
                .select(Projections.constructor(MemberDTO.FindMemberAndImageDTO.class, member, image.imageUrl))
                .from(member)
                .leftJoin(image).on(image.contentId.eq(member.memberId))
                .where(member.nickname.containsIgnoreCase(query)
                        .or(member.email.containsIgnoreCase(query)))
                .offset(offset)
                .limit(limit)
                .fetch();
        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(content.size() - 1);
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }
}
