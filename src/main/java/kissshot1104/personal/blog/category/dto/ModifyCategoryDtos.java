package kissshot1104.personal.blog.category.dto;

import jakarta.validation.Valid;
import java.util.List;
import lombok.Builder;

@Builder
public record ModifyCategoryDtos(@Valid List<ModifyCategoryDto> modifyCategoryDtos) {
}
