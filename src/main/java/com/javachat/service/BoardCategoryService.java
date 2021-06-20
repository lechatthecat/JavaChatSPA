package com.javachat.service;

import com.javachat.model.BoardCategory;
import com.javachat.model.Category;

import org.springframework.data.domain.Page;

import java.util.List;

import com.javachat.model.Board;

public interface BoardCategoryService {
    public long createBoardCategory(BoardCategory boardCategory);
    public long createBoardCategory(Board board, String username);
    public Page<List<String>> findBoardsByCategory(long category_id, Category category, int pageNumber);
}
