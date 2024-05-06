package kissshot1104.personal.blog.test;

import kissshot1104.personal.blog.global.security.prinipal.CurrentMember;
import kissshot1104.personal.blog.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @PostMapping("/test/{number}")
    public ResponseEntity<TestDto> testController(@PathVariable Long number,
                                                  @RequestParam Long param,
                                                  @RequestBody TestDto requestTestDto,
                                                  @CurrentMember Member member) {
        final TestDto testDto = testService.testFunc();

        return ResponseEntity.ok(testDto);
    }
}
