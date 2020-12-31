package com.javachat.service;

import com.javachat.model.User;
import com.javachat.model.Board;
import com.javachat.model.BoardResponse;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {
    public String getUniqueUrlName();
    public long createBoard(Board board);
    public BoardResponse createBoardResponse(Board board, BoardResponse boardResponse);
    public BoardResponse createBoardResponse(BoardResponse boardResponse);
    public boolean deleteBoard(Board board);
    public boolean deleteBoardResponse(BoardResponse boardResponse);
    public boolean deleteBoardResponse(long id);
    public boolean deleteBoard(long id);
    public Board findBoardById(long id);
    public Board findBoardByTableUrlName(String tableUrlName);
    public List<Board> findBoardsByUser(User user);
    public List<Board> findBoardsByUser(User user, Pageable pageable);
    public BoardResponse findBoardResponseById(long id);
    public ArrayList<BoardResponse> findBoardResponsesByBoard(Board board, Pageable pageable);
    public Page<BoardResponse> findBoardResponsesByUser(User user, Pageable pageable);
    public Page<BoardResponse> findBoardResponsesByBoardAndUser(Board board, User user, Pageable pageable);
    public Long getCountOfBoardsByUser(User user);
    public List<Board> getPublicBoards();
    public List<Board> getPublicBoards(Pageable pageable);
    public Page<Board> getPublicBoardPages(Pageable pageable);
    public Long getHowManyUnseenResponses(Board board, User user);
    public Long getHowManyUnseenResponses(User user);
    public List<BoardResponse> getLatestResponseTimePerBoard(long user_id);
}
