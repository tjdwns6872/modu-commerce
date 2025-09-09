package com.modu.commerce.category.service;

import com.modu.commerce.category.dto.CategoryRequest;
import com.modu.commerce.category.dto.CategoryOneResponse;
import com.modu.commerce.category.dto.CategoryOneSpec;
import com.modu.commerce.category.entity.ModuCategory;
import com.modu.commerce.category.exception.CategoryNotFoundException;
import com.modu.commerce.category.exception.DuplicateCategoryNameUnderSameParent;
import com.modu.commerce.category.exception.InvalidCategoryNameException;
import com.modu.commerce.category.exception.ParentCategoryNotFound;
import com.modu.commerce.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.*;

//실제 DB, Bean 주입 그대로 사용
@SpringBootTest
// 테스트마다 컨텍스트 초기화(DB 클린)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CategoryServiceImplTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    private ModuCategory root;

    //fixture 데이터 시드
    @BeforeEach
    void setUp(){
        root = categoryRepository.save(
            ModuCategory.builder()
                .name("ROOT")
                .depth(0).build()
        );
    }

    @Test
    @DisplayName("정상적으로 루트 카테고리를 생성할 수 있다.")
    void createRootCategory_Success(){
        CategoryRequest req = CategoryRequest.builder()
                .name("Shoes")
                .parentId(null).build();

        Long savedId = categoryService.createCategory(req);

        ModuCategory found = categoryRepository.findById(savedId).orElseThrow();

        ///assertThat 단순 값 검증
        assertThat(found.getName()).isEqualTo("Shoes");
        assertThat(found.getDepth()).isEqualTo(0);
    }

    @Test
    @DisplayName("같은 부모 아래 같은 이름이면 DuplicateCategoryNameUnderSameParent 발생")
    void createCategory_DuplicateName_Fail() {
        categoryRepository.save(
            ModuCategory.builder()
                .name("Sneakers")
                .parent(root)
                .depth(1)
                .build()
        );
        CategoryRequest dupReq = CategoryRequest.builder()
                                    .name("Sneakers")
                                    .parentId(root.getId())
                                    .build();

        // assertThatThrownBy 예외 테스트 패턴
        assertThatThrownBy(() -> categoryService.createCategory(dupReq)).isInstanceOf(DuplicateCategoryNameUnderSameParent.class);
    }
    @Test
    @DisplayName("단건 조회 - 존재하지 않는 ID 조회 시 CategoryNotFoundException 발생")
    void getCategory_NotFound_Fail() {
        Long notExistId = 999L;
        
        CategoryOneSpec spec = CategoryOneSpec.builder()
                                .id(notExistId)
                                .includeDeleted(false)
                                .build();

        assertThatThrownBy(() -> categoryService.categoryOne(spec)).isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    @DisplayName("단건 조회 - 정상 조회 시 DTO 매핑 검증")
    void getCategory_Success() {
        ModuCategory saved = categoryRepository.save(
                ModuCategory.builder()
                        .name("Outer")
                        .parent(root)
                        .depth(1)
                        .build()
        );
        CategoryOneSpec spec = CategoryOneSpec.builder()
                                .id(saved.getId())
                                .includeDeleted(false)
                                .build();
        CategoryOneResponse dto = categoryService.categoryOne(spec);

        assertThat(dto.getId()).isEqualTo(saved.getId());
        assertThat(dto.getName()).isEqualTo(saved.getName());
        assertThat(dto.getParentId()).isEqualTo(saved.getParent().getId());
        assertThat(dto.getDepth()).isEqualTo(saved.getDepth());
        assertThat(dto.getHasChildren()).isFalse();
    }

    @Test
    @DisplayName("정상적으로 자식 카테고리를 생성할 수 있다.")
    void createChildCategory_Success(){

        CategoryRequest req = CategoryRequest.builder()
                .name("Sneakers")
                .parentId(root.getId()).build();

        Long savedId = categoryService.createCategory(req);

        //숨은 제약 위반 조기 포착
        categoryRepository.flush();

        ModuCategory child = categoryRepository.findById(savedId).orElseThrow();
        ModuCategory parent = categoryRepository.findById(root.getId()).orElseThrow();

        assertThat(child.getId()).isEqualTo(savedId);
        assertThat(child.getName()).isEqualTo("Sneakers");
        assertThat(child.getParent()).isNotNull();
        assertThat(child.getParent().getId()).isEqualTo(root.getId());
        assertThat(child.getDepth()).isEqualTo(parent.getDepth()+1);

        //트리 불변식: 부모 depth 변경 없음
        assertThat(parent.getDepth()).isEqualTo(root.getDepth());
    }

    @Test
    @DisplayName("존재하지 않는 부모 밑 카테고리 생성 시 ParentCategoryNotFound 발생")
    void createChildCategory_ParentNotFound_Fail(){

        long before = categoryRepository.count();

        CategoryRequest dupReq = CategoryRequest.builder()
                                    .name("Sneakers")
                                    .parentId(999L)
                                    .build();
        assertThatThrownBy(() -> categoryService.createCategory(dupReq)).isInstanceOf(ParentCategoryNotFound.class);

        assertThat(categoryRepository.count()).isEqualTo(before);
    }

    @ParameterizedTest(name = "[{index}] name=\"{0}\" → 예외")
    @NullAndEmptySource // null, "" 자동 공급
    @ValueSource(strings = {" ", "   ", "\n", "\t", " \r "}) // 트림 후 빈 문자열 케이스
    @DisplayName("카테고리 생성 시 이름이 null이거나 공백이면 예외 발생")
    void createCategory_BlankOrNullName_Fail(String badName){

        long before = categoryRepository.count();

        CategoryRequest req = CategoryRequest.builder()
            .name(badName)
            .parentId(null) // 루트로 시도 (부모 검증 영향 최소화)
            .build();

        assertThatThrownBy(() -> categoryService.createCategory(req))
                .isInstanceOfAny(InvalidCategoryNameException.class, jakarta.validation.ValidationException.class);
        assertThat(categoryRepository.count()).isEqualTo(before);
    }

    @ParameterizedTest(name = "[{index}] badName=\"{0}\" → 중복 예외")
    @ValueSource(strings = {" Outer ", "Outer ", " Outer", "\tOuter", "Outer\t"})
    @DisplayName("트림 후 같은 부모 아래 같은 이름이면 DuplicateCategoryNameUnderSameParent 발생")
    void createCategory_DuplicateName_Trimmed_Fail(String badName){
        
        categoryRepository.save(
            ModuCategory.builder()
                .name("Outer")
                .parent(root)
                .depth(root.getDepth()+1)
                .build()
        );
        long before = categoryRepository.count();

        CategoryRequest dupReq = CategoryRequest.builder()
                                    .name(badName)
                                    .parentId(root.getId())
                                    .build();

        assertThatThrownBy(() -> categoryService.createCategory(dupReq)).isInstanceOf(DuplicateCategoryNameUnderSameParent.class);
        assertThat(categoryRepository.count()).isEqualTo(before);
    }

    @ParameterizedTest(name = "[{index}] caseVariant=\"{0}\" → 대소문자 중복 예외")
    @ValueSource(strings = {"outer", "OUTER", "OuTeR"})
    @DisplayName("같은 부모에서 대소문자만 다른 이름은 DuplicateCategoryNameUnderSameParent 발생")
    void createCategory_DuplicateName_CaseInsensitive_Fail(String caseVariant){
        
        categoryRepository.save(
            ModuCategory.builder()
                .name("Outer")
                .parent(root)
                .depth(root.getDepth()+1)
                .build()
        );
        long before = categoryRepository.count();

        CategoryRequest dupReq = CategoryRequest.builder()
                                    .name(caseVariant)
                                    .parentId(root.getId())
                                    .build();

        assertThatThrownBy(() -> categoryService.createCategory(dupReq)).isInstanceOf(DuplicateCategoryNameUnderSameParent.class);
        assertThat(categoryRepository.count()).isEqualTo(before);
    }
}
