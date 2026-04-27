package org.softsystem.holywave.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.softsystem.holywave.exception.ResourceNotFoundException;
import org.softsystem.holywave.model.dto.CategoryCreateDto;
import org.softsystem.holywave.model.dto.CategoryDto;
import org.softsystem.holywave.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryCreateDto categoryCreateDto){
        CategoryDto categoryDto = categoryService.createCategory(categoryCreateDto);
        return new ResponseEntity<>(categoryDto, HttpStatus.CREATED);
    }

    @GetMapping
    ResponseEntity<List<CategoryDto>> findAllCategories(){
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<CategoryDto> getCategoryById(@PathVariable UUID id){
        CategoryDto categoryDto = categoryService.findCategoryById(id)
                .orElseThrow(()-> new ResourceNotFoundException(String.format("There is no category with the given id. [id=%s]", id)));

        return new ResponseEntity<>(categoryDto, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    ResponseEntity<CategoryDto> getCategoryByName(@PathVariable String name){
        CategoryDto categoryDto = categoryService.findCategoryByName(name)
                .orElseThrow(()-> new ResourceNotFoundException(String.format("There is no category with the given name. [name=%s]", name)));

        return new ResponseEntity<>(categoryDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteCategoryById(@PathVariable UUID id){
        categoryService.deleteCategory(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
