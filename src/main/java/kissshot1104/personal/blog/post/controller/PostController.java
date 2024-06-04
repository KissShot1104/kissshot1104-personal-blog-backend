package kissshot1104.personal.blog.post.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import kissshot1104.personal.blog.global.security.prinipal.CurrentMember;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.post.dto.AuthenticationDataDto;
import kissshot1104.personal.blog.post.dto.CreatePostDto;
import kissshot1104.personal.blog.post.dto.FindPostDto;
import kissshot1104.personal.blog.post.dto.ModifyPostDto;
import kissshot1104.personal.blog.post.dto.request.AuthenticationDataRequest;
import kissshot1104.personal.blog.post.dto.request.CreatePostRequest;
import kissshot1104.personal.blog.post.dto.request.PostModifyRequest;
import kissshot1104.personal.blog.post.dto.response.FindPostResponse;
import kissshot1104.personal.blog.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("{postId}")
    public ResponseEntity<FindPostDto> findPost(@PathVariable("postId") Long postId,
                                                @RequestBody AuthenticationDataDto authenticationDataDto,
                                                @CurrentMember Member member) {
        AuthenticationDataRequest request =
                authenticationDataDto.toAuthenticationDataRequest();
        FindPostDto response = FindPostDto.of(postService.findPost(postId, request, member));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<FindPostDto>> findAll(@RequestParam("kw") String kw,
                                                     @RequestParam("kw-type") String kwType,
                                                     Pageable pageable,
                                                     @CurrentMember Member member) {
        final Page<FindPostResponse> postResponses = postService.findAllPost(kw, kwType, pageable, member);
        final Page<FindPostDto> findPostDtos = FindPostDto.listOf(postResponses);
        return ResponseEntity.ok(findPostDtos);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Void> modifyPost(@PathVariable("postId") Long postId,
                                           @Valid @RequestBody ModifyPostDto modifyPostDto,
                                           @CurrentMember Member member) {
        final PostModifyRequest request = modifyPostDto.toPostModifyRequest();
        postService.modifyPost(postId, request, member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable("postId") Long postId,
                                           @CurrentMember Member member) {
        postService.deletePost(postId, member);
        return ResponseEntity.noContent().build();
    }
}
