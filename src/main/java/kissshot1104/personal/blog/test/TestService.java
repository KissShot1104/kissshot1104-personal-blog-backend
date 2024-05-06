package kissshot1104.personal.blog.test;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    public TestDto testFunc() {
        return TestDto.builder()
                .data("test")
                .build();
    }
}
