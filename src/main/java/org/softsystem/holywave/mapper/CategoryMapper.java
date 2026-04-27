package org.softsystem.holywave.mapper;

import org.mapstruct.Mapper;
import org.softsystem.holywave.model.dto.CategoryDto;
import org.softsystem.holywave.model.entities.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryDto categoryDto);
    List<CategoryDto> toDtoList(List<Category> categories);
    CategoryDto toCategoryDto(Category category);
}