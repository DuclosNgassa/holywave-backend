package org.softsystem.holywave.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.softsystem.holywave.exception.ResourceAlreadyExistsException;
import org.softsystem.holywave.mapper.CategoryMapper;
import org.softsystem.holywave.model.dto.CategoryCreateDto;
import org.softsystem.holywave.model.dto.CategoryDto;
import org.softsystem.holywave.model.entities.Category;
import org.softsystem.holywave.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public Optional<CategoryDto> findCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toCategoryDto);
    }

    public List<CategoryDto> findAllDtoById(List<UUID> ids) {
        return categoryRepository.findAllById(ids)
                .stream().map(categoryMapper::toCategoryDto)
                .toList();
    }

    public List<Category> findAllById(List<UUID> ids) {
        return categoryRepository.findAllById(ids);
    }

    public Optional<CategoryDto> findCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .map(categoryMapper::toCategoryDto);
    }

    public CategoryDto createCategory(CategoryCreateDto categoryCreateDto) {
        if (findCategoryByName(categoryCreateDto.name()).isPresent()) {
            throw new ResourceAlreadyExistsException(String.format("There is already a category with the given name. [name=%s]", categoryCreateDto.name()));
        }
        Category categoryToSave = new Category();
        categoryToSave.setName(categoryCreateDto.name());

        Category categorySaved = categoryRepository.save(categoryToSave);
        log.debug("New category saved. [id={}]", categorySaved.getId());
        return categoryMapper.toCategoryDto(categorySaved);
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
    }

    public void deleteCategory(UUID id) {
        log.info("Deleting category. [id={}]", id);
        categoryRepository.deleteById(id);
        log.info("Category deleted successfully. [id={}]", id);
    }
}
