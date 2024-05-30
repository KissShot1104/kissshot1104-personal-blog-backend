package kissshot1104.personal.blog.post.controller;

import jakarta.validation.Valid;
import java.net.URI;
import kissshot1104.personal.blog.global.security.prinipal.CurrentMember;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.post.dto.CreatePostDto;
import kissshot1104.personal.blog.post.dto.request.CreatePostRequest;
import kissshot1104.personal.blog.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post/")
public class PostController {
    private final PostService postService;

    @PostMapping("create")
    public ResponseEntity<Void> createNewPost(@Valid @RequestBody CreatePostDto request,
                                              @CurrentMember Member member) {
        final CreatePostRequest createPostRequest = request.toCreatePostRequest();
        final Long postId = postService.createPost(createPostRequest, member);
        return ResponseEntity.created(URI.create("/post/" + postId)).build();
    }
}
