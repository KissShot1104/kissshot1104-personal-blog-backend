package kissshot1104.personal.blog.post.service;

import kissshot1104.personal.blog.category.entity.Category;
import kissshot1104.personal.blog.category.service.CategoryService;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.post.dto.request.CreatePostRequest;
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
}
