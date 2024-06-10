package kissshot1104.personal.blog.member.controller;

import kissshot1104.personal.blog.global.security.prinipal.CurrentMember;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("profileImage")
    public ResponseEntity<String>viewProfileImage(@CurrentMember final Member member) {
        String imagePath = memberService.viewProfileImage(member);
        return ResponseEntity.ok(imagePath);
    }

    @PatchMapping("/profile")
    public ResponseEntity<Void> modifyProfile(@RequestPart("image") MultipartFile image,
                                              @CurrentMember final Member member) {
        memberService.modifyMember(image, member);
        return ResponseEntity.ok().build();
    }
}
