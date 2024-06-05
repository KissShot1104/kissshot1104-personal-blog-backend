package kissshot1104.personal.blog.member.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record ModifyProfileRequest(MultipartFile profileImage) {
}
