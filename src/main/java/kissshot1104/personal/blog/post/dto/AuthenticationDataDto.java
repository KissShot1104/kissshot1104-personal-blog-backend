package kissshot1104.personal.blog.post.dto;

import kissshot1104.personal.blog.post.dto.request.AuthenticationDataRequest;
import lombok.Builder;

@Builder
public record AuthenticationDataDto(String postPassword) {
    public AuthenticationDataRequest toAuthenticationDataRequest() {
        final AuthenticationDataRequest request = AuthenticationDataRequest.builder()
                .postPassword(postPassword)
                .build();
        return request;
    }
}
