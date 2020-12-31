package com.javachat.service;

import com.javachat.model.BoardCategory;
import com.javachat.model.Board;

public interface BoardCategoryService {
    public long createBoardCategory(BoardCategory boardCategory);
    public long createBoardCategory(Board board, String username);
}
