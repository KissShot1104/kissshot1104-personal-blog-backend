package kissshot1104.personal.blog.post_image.controller;

import kissshot1104.personal.blog.global.security.prinipal.CurrentMember;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.post_image.service.PostImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class PostImageController {

    private final PostImageService postImageService;
    @PostMapping("/upload-image")
    public ResponseEntity<String> savePostImage(@RequestParam(value = "image") MultipartFile image,
                                             @CurrentMember Member member) {
        String imagePath = postImageService.savePostImage(image, member);
        return ResponseEntity.ok(imagePath);
    }
}
