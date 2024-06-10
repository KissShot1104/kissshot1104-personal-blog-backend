package kissshot1104.personal.blog.post.service;

import kissshot1104.personal.blog.category.entity.Category;
import kissshot1104.personal.blog.category.service.CategoryService;
import kissshot1104.personal.blog.global.exception.AuthException;
import kissshot1104.personal.blog.global.exception.BusinessException;
import kissshot1104.personal.blog.global.exception.ErrorCode;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.post.dto.request.AuthenticationData;
import kissshot1104.personal.blog.post.dto.request.CreatePostRequest;
import kissshot1104.personal.blog.post.dto.response.FindPostResponse;
import kissshot1104.personal.blog.post.entity.Post;
import kissshot1104.personal.blog.post.entity.PostSecurity;
import kissshot1104.personal.blog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final CategoryService categoryService;

    @Transactional
    public Long createPost(final CreatePostRequest createPostRequest, final Member member) {
        final PostSecurity postSecurity = PostSecurity.checkPostSecurity(createPostRequest.postSecurity());
        final Category category = categoryService.findByCategoryId(createPostRequest.categoryId());
        final Post post = Post.builder()
                .title(createPostRequest.title())
                .content(createPostRequest.content())
                .postPassword(createPostRequest.postPassword())
                .postSecurity(postSecurity)
                .member(member)
                .category(category)
                .build();
        final Post savedPost = postRepository.save(post);

        return savedPost.getId();
    }

    public FindPostResponse findPost(final Long postId, final AuthenticationData request, final Member member) {
        final Post post = findByPostId(postId);
        checkAuthentication(post, request, member);
        final FindPostResponse response = FindPostResponse.of(post);
        return response;
    }

    private void checkAuthentication(final Post post, final AuthenticationData request, final Member member) {
        if (post.getPostSecurity() == PostSecurity.PRIVATE &&
                post.getMember() != member) {
            throw new AuthException(ErrorCode.UNAUTHORIZED_USER);
        }

        if (post.getPostSecurity() == PostSecurity.PROTECTED &&
        post.getMember() != member) {
            if (post.getPostPassword() != request.postPassword()) {
                throw new AuthException(ErrorCode.UNAUTHORIZED_USER);
            }
        }
    }

    public Post findByPostId(final Long postId) {
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        return post;
    }
}
