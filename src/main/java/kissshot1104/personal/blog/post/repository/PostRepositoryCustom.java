package kissshot1104.personal.blog.post.repository;

import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.post.dto.response.FindPostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    Page<FindPostResponse> findAllByKeyword(final String kw,
                                            final String kwType,
                                            final Pageable pageable,
                                            final Member requester);
}
