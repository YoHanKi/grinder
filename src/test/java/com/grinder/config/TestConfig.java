package com.grinder.config;

import com.grinder.repository.queries.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @PersistenceContext
    private EntityManager entityManager;
    @Bean
    public AnalysisTagQueryRepository analysisTagQueryRepository() {
        return new AnalysisTagQueryRepository(entityManager);
    }
    @Bean
    public BlacklistQueryRepository blacklistQueryRepository() {
        return new BlacklistQueryRepository(entityManager);
    }
    @Bean
    public BookmarkQueryRepository bookmarkQueryRepository() {
        return new BookmarkQueryRepository(entityManager);
    }
    @Bean
    public CafeQueryRepository cafeQueryRepository() {
        return new CafeQueryRepository(entityManager);
    }
    @Bean
    public CommentQueryRepository commentQueryRepository() {
        return new CommentQueryRepository(entityManager);
    }
    @Bean
    public FeedQueryRepository feedQueryRepository() {
        return new FeedQueryRepository(entityManager);
    }
    @Bean
    public FollowQueryRepository followQueryRepository() {
        return new FollowQueryRepository(entityManager);
    }
    @Bean
    public ImageQueryRepository imageQueryRepository() {
        return new ImageQueryRepository(entityManager);
    }
    @Bean
    public MessageQueryRepository messageQueryRepository() {
        return new MessageQueryRepository(entityManager);
    }
    @Bean
    public MenuQueryRepository menuQueryRepository() {
        return new MenuQueryRepository(entityManager);
    }
    @Bean
    public MemberQueryRepository memberQueryRepository() {
        return new MemberQueryRepository(entityManager);
    }
    @Bean
    public ReportQueryRepository reportQueryRepository() {
        return new ReportQueryRepository(entityManager);
    }
    @Bean
    public SearchQueryRepository searchQueryRepository() {
        return new SearchQueryRepository(entityManager);
    }
    @Bean
    public SellerInfoQueryRepository sellerInfoQueryRepository() {
        return new SellerInfoQueryRepository(entityManager);
    }
}
