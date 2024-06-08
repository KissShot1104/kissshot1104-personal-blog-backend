package kissshot1104.personal.blog.category.controller;

import jakarta.validation.Valid;
import java.util.List;
import kissshot1104.personal.blog.category.dto.ModifyCategoryDto;
import kissshot1104.personal.blog.category.dto.ModifyCategoryDtos;
import kissshot1104.personal.blog.category.dto.request.ModifyCategoryRequest;
import kissshot1104.personal.blog.category.service.CategoryService;
import kissshot1104.personal.blog.global.security.prinipal.CurrentMember;
import kissshot1104.personal.blog.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class CategoryController {
    private final CategoryService categoryService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/save-category")
    public ResponseEntity<Void> saveCategoryChanges(@RequestBody @Valid ModifyCategoryDtos modifyCategoryDtos,
                                                    @CurrentMember Member member) {
        final List<ModifyCategoryRequest> modifyCategoryRequests =
                ModifyCategoryDto.toModifyCategoryRequests(modifyCategoryDtos.modifyCategoryDtos());
        categoryService.saveCategoryChanges(modifyCategoryRequests, member);
        return ResponseEntity.ok().build();
    }
}

