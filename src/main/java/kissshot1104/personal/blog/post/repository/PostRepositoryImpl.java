package kissshot1104.personal.blog.post.repository;

import static kissshot1104.personal.blog.post.entity.QPost.post;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.post.dto.response.FindPostResponse;
import kissshot1104.personal.blog.post.dto.response.QFindPostResponse;
import kissshot1104.personal.blog.post.entity.PostSecurity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class PostRepositoryImpl implements PostRepositoryCustom {

    @PersistenceContext
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<FindPostResponse> findAllByKeyword(final String sortCode,
                                                   final String kwType,
                                                   final String kw,
                                                   final Pageable pageable,
                                                   final Member member) {

        final List<FindPostResponse> postResponses = queryFactory
                .select(new QFindPostResponse(
                        post.id,
                        post.category.categoryName,
                        post.member.nickName,
                        post.title,
                        post.content,
                        post.postSecurity.stringValue()
                ))
                .from(post)
                .groupBy(post.id)
                .where(buildSearchPredicate(kwType, kw))
                .where(isNotPrivatePost()
                        .or(isPrivatePost()
                                .and(isPostOwnedByMember(member))))
                .distinct()
                .orderBy(sortType(sortCode))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final long total = postResponses.size();

        return new PageImpl<>(postResponses, pageable, total);
    }

    public BooleanExpression buildSearchPredicate(final String kwType, final String kw) {
        final BooleanExpression predicate;
        if (kwType != null && !kwType.isEmpty()) {
            predicate = hasKwType(kwType, kw);
            return predicate;
        }

        predicate = defaultSearch(kw);
        return predicate;
    }

    public BooleanExpression hasKwType(final String kwType, final String kw) {

        BooleanExpression predicate = Expressions.asBoolean(false).isTrue();

        final List<String> kwTypes = Arrays.stream(kwType.split(",")).toList();

        if (kwTypes.contains("title")) {
            predicate = predicate.or(postTitleContains(kw));
        }
        if (kwTypes.contains("content")) {
            predicate = predicate.or(postContentContains(kw));
        }
        if (kwTypes.contains("author")) {
            predicate = predicate.or(postAuthorContains(kw));
        }

        return predicate;
    }

    public BooleanExpression defaultSearch(final String kw) {
        BooleanExpression predicate;

        predicate = postTitleContains(kw)
                .or(postContentContains(kw))
                .or(postAuthorContains(kw));

        return predicate;
    }


    public BooleanExpression postTitleContains(String postTitleCond) {
        if (postTitleCond == null || postTitleCond.isEmpty()) {
            return Expressions.asBoolean(true).isTrue();
        }
        return post.title.contains(postTitleCond);
    }

    public BooleanExpression postContentContains(String postContentCond) {
        if (postContentCond == null || postContentCond.isEmpty()) {
            return Expressions.asBoolean(true).isTrue();
        }
        return post.content.contains(postContentCond);
    }

    public BooleanExpression postAuthorContains(String postAuthorCond) {
        if (postAuthorCond == null || postAuthorCond.isEmpty()) {
            return Expressions.asBoolean(true).isTrue();
        }
        return post.member.nickName.contains(postAuthorCond);
    }

    public BooleanExpression isNotPrivatePost() {
        return post.postSecurity.ne(PostSecurity.PRIVATE);
    }

    public BooleanExpression isPrivatePost() {
        return post.postSecurity.eq(PostSecurity.PRIVATE);
    }

    public BooleanExpression isPostOwnedByMember(final Member member) {
        return post.member.eq(member);
    }

    public OrderSpecifier<?> sortType(String sortCodeCond) {
        if (sortCodeCond == null || sortCodeCond.isEmpty() || sortCodeCond.equals("createdDateDesc")) {
            return post.createdDate.desc();
        }
        if (sortCodeCond.equals("createdDateAsc")) {
            return post.createdDate.asc();
        }
        if (sortCodeCond.equals("idDesc")) {
            return post.id.desc();
        }
        if (sortCodeCond.equals("idAsc")) {
            return post.id.asc();
        }
        return post.createdDate.desc();
    }


}
