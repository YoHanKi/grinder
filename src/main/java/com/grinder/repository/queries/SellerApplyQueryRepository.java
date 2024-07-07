package com.grinder.repository.queries;

import static com.grinder.domain.dto.SellerApplyDTO.*;

import com.grinder.domain.entity.QSellerApply;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SellerApplyQueryRepository {

//    private final JPAQueryFactory queryFactory;
//
//    public SellerApplyQueryRepository(EntityManager entityManager) {
//        this.queryFactory = new JPAQueryFactory(entityManager);
//    }
//
//    public Slice<FindSellerApplyDTO> searchSellerApplyByNameAndAddressSlice(String keyword, Pageable pageable) {
//        QSellerApply sellerApply = QSellerApply.sellerApply;
//
//        BooleanBuilder builder = new BooleanBuilder();
//
//        if (keyword != null) {
//            builder.and(sellerApply.cafe.name.like("%"+keyword+"%"))
//                    .or(sellerApply.cafe.address.like("%"+keyword+"%"));
//        }
//
//        long limit = pageable.getPageSize() + 1;
//        long offset = pageable.getOffset();
//
//        List<FindSellerApplyDTO> content = queryFactory
//                .select(Projections.constructor(FindSellerApplyDTO.class))
//                .from(sellerApply)
//                .where(builder)
//                .limit(limit)
//                .offset(offset)
//                .fetch();
//
//        boolean hasNext = content.size() > pageable.getPageSize();
//        if (hasNext) {
//            content.remove(content.size() - 1);
//        }
//
//        return new SliceImpl<>(content, pageable, hasNext);
//    }
}
