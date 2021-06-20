package com.javachat.controller.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.javachat.jwt.payload.response.MessageResponse;
import com.javachat.model.Category;
import com.javachat.repository.CategoryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class CategoryController {
    @Autowired
    CategoryRepository categoryRepository;
    Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @GetMapping(path = "/get_categories")
    public ResponseEntity<?> getCategory(HttpServletRequest request) throws Exception {
        
        try {
            final List<Category> categories = categoryRepository.findAllUndeletedCategories();
            final List<HashMap<String, String>> resultCategories = new ArrayList<>();
            for (Category category : categories) {
                final HashMap<String, String> result = new HashMap<String, String>();
                result.put("name", category.getName());
                result.put("urlName", category.getUrlName());
                result.put("icon", category.getIcon());
                resultCategories.add(result);
            }
            return new ResponseEntity<>(resultCategories, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseEntity.badRequest().body(new MessageResponse("Error detected."));
		}
    }
}
