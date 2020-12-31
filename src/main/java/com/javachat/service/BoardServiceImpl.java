package com.javachat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.ZonedDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.dao.DataAccessException;
import com.javachat.util.Utility;
import com.javachat.model.User;
import com.javachat.repository.BoardRepository;
import com.javachat.repository.BoardResponseRepository;
import com.javachat.model.Board;
import com.javachat.model.BoardResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class BoardServiceImpl implements BoardService{
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private BoardResponseRepository boardResponseRepository;
    @Autowired
    Utility utility;

    @Override
    @Transactional(rollbackFor=Exception.class)
    public String getUniqueUrlName() {
        String uuid = String.valueOf(UUID.randomUUID()) + "-" + String.valueOf(UUID.randomUUID());
        if (!boardRepository.existsByUrlName(uuid)) {
            return uuid;
        } else {
            return this.getUniqueUrlName();
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Board findBoardByTableUrlName(String tableUrlName){
        return this.boardRepository.findBoardByTableUrlName(tableUrlName);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public long createBoard(Board board) {
        try{
            ZonedDateTime now = utility.getCurrentSystemLocalTime();
            board.setCreated(now);
            board.setUpdated(now);
            return this.boardRepository.save(board).getId();
        }catch(DataAccessException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public boolean deleteBoard(Board board) {
        try{
            this.boardRepository.deleteBoardById(board.getId());
            return true;
        }catch(DataAccessException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public boolean deleteBoard(long id) {
        try{
            this.boardRepository.deleteBoardById(id);
            return true;
        }catch(DataAccessException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public BoardResponse createBoardResponse(Board board, BoardResponse boardResponse) {
        ZonedDateTime now = utility.getCurrentSystemLocalTime();
        boardResponse.setBoard(board);
        boardResponse.setCreated(now);
        boardResponse.setUpdated(now);
        this.boardResponseRepository.save(boardResponse);
        boardResponse.setUserImagePath(boardResponse.getUser().getUserMainImage().getPath());
        return boardResponse;
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public BoardResponse createBoardResponse(BoardResponse boardResponse) {
        ZonedDateTime now = utility.getCurrentSystemLocalTime();
        boardResponse.setCreated(now);
        boardResponse.setUpdated(now);
        this.boardResponseRepository.save(boardResponse);
        boardResponse.setUserImagePath(boardResponse.getUser().getUserMainImage().getPath());
        return boardResponse;
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public boolean deleteBoardResponse(BoardResponse boardResponse) {
        try{
            this.boardResponseRepository.deleteBoardResponseById(boardResponse.getId());
            return true;
        }catch(DataAccessException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public boolean deleteBoardResponse(long id) {
        try{
            this.boardResponseRepository.deleteBoardResponseById(id);
            return true;
        }catch(DataAccessException e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Board findBoardById(long id){
        return boardRepository.findBoardById(id);
    };

    @Transactional(rollbackFor=Exception.class)
    public List<Board> findBoardsByUser(User user){
        return boardRepository.findBoardsByUser(user);
    };

    @Override
    @Transactional(rollbackFor=Exception.class)
    public List<Board> findBoardsByUser(User user, Pageable pageable){
        return boardRepository.findBoardsByUser(user, pageable);
    };

    @Override
    @Transactional(rollbackFor=Exception.class)
    public BoardResponse findBoardResponseById(long id){
        return boardResponseRepository.findBoardResponseById(id);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public ArrayList<BoardResponse> findBoardResponsesByBoard(Board board, Pageable pageable){
        return boardResponseRepository.findBoardResponsesByBoard(board, pageable);
    };

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Page<BoardResponse> findBoardResponsesByUser(User user, Pageable pageable){
        return boardResponseRepository.findBoardResponsesByUser(user, pageable);
    };

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Page<BoardResponse> findBoardResponsesByBoardAndUser(Board board, User user, Pageable pageable){
        return boardResponseRepository.findBoardResponsesByBoardAndUser(board, user, pageable);
    };

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Long getCountOfBoardsByUser(User user){
        return  boardRepository.getCountOfBoardsByUser(user);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public List<Board> getPublicBoards(){
        return  boardRepository.getPublicBoards();
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public List<Board> getPublicBoards(Pageable pageable){
        return  boardRepository.getPublicBoards(pageable);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Page<Board> getPublicBoardPages(Pageable pageable){
        return  boardRepository.getPublicBoardPages(pageable);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Long getHowManyUnseenResponses(Board board, User user){
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        Root<BoardResponse> from = cq.from(BoardResponse.class);
        cq.select(qb.count(from));
        Predicate boardCondition = qb.equal(from.get("board"), board);
        Predicate userCondition = qb.notEqual(from.get("user"), user);
        Predicate condition = qb.and(boardCondition, userCondition);
        cq.where(condition);
        return em.createQuery(cq).getSingleResult();
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Long getHowManyUnseenResponses(User user){
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        Root<BoardResponse> from = cq.from(BoardResponse.class);
        cq.select(qb.count(from));
        Predicate userCondition = qb.notEqual(from.get("user"), user);
        cq.where(userCondition);
        return em.createQuery(cq).getSingleResult();
    }

    @Override
    public List<BoardResponse> getLatestResponseTimePerBoard(long user_id){
        List<BoardResponse> boardResponses = boardResponseRepository.getLatestResponsePerBoard(user_id);
        return boardResponses;
    }
}