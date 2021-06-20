package com.javachat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.security.SecureRandom;
import java.time.ZonedDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.dao.DataAccessException;
import com.javachat.util.Utility;
import com.javachat.model.User;
import com.javachat.repository.BoardRepository;
import com.javachat.repository.BoardResponseRepository;
import com.javachat.repository.IpStringRepository;
import com.javachat.model.Board;
import com.javachat.model.BoardResponse;
import com.javachat.model.IpString;

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
    private IpStringRepository ipStringRepository;
    @Autowired
    Utility utility;

    @Override
    public IpString createIpString(String ipAddress){
        IpString ipString = this.createAndSaveIpString(ipAddress);
        return ipString;
    }

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
    @Cacheable("board_tableUrl")
    public Board findBoardByTableUrlName(String tableUrlName){
        return this.boardRepository.findBoardByTableUrlName(tableUrlName);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    @Cacheable("last_BR_id")
    public int findLastBRIdByTableUrlName(String tableUrlName){
        return this.boardResponseRepository.findLastBRIdByTableUrlName(tableUrlName);
    }

    // public BoardResponse findBoardResponseByResNumBoardUrl(long resNum, String boardUrl) {

    // }

    @Override
    @Transactional(rollbackFor=Exception.class)
    @CacheEvict(cacheNames={"boards_category","board_tableUrl","last_BR_id","board","boards_user","boards_user_page","boards_resoponse_user","boards_resoponse_board_user","count_boards","public_boards","public_boards_pages","public_board_pages","latest_res_time_per_board","boards_response","last_BR_id","boards_resoponse_user","boards_resoponse_board_user","count_unseen_res_user","latest_res_time_per_board"}, allEntries=true)
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
    @CacheEvict(cacheNames={"boards_category","board_tableUrl","last_BR_id","board","boards_user","boards_user_page","boards_resoponse_user","boards_resoponse_board_user","count_boards","public_boards","public_boards_pages","public_board_pages","latest_res_time_per_board"}, allEntries=true)
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
    @CacheEvict(cacheNames={"boards_category","board_tableUrl","last_BR_id","board","boards_user","boards_user_page","boards_resoponse_user","boards_resoponse_board_user","count_boards","public_boards","public_boards_pages","public_board_pages","latest_res_time_per_board"}, allEntries=true)
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
    @CacheEvict(cacheNames={"boards_category","board_tableUrl","boards_response","last_BR_id","board","boards_user","boards_user_page","boards_resoponse_user","boards_resoponse_board_user","count_unseen_res_user","latest_res_time_per_board"}, allEntries=true)
    public BoardResponse createBoardResponse(User user, String tableUrlName, 
                                                String ipAddress, BoardResponse boardResponse) {
        Board board = boardRepository.findBoardByTableUrlName(tableUrlName); 
        IpString ipString = this.createAndSaveIpString(ipAddress);
        boardResponse.setIpString(ipString);
        boardResponse.setIpStringForView(ipString.getStringId());
        boardResponse.setIpAddress(ipString.getIpAddress());
        boardResponse.setStringId(ipString.getStringId());
        boardResponse.setUser(user);
        boardResponse.setUserImagePath(user.getUserMainImage().getPath());
        boardResponse.setBoard(board);
        boardResponse.setSender(user.getUsernameNonEmail());
        int lastResNumber = boardResponseRepository.findLastBRIdByBoardId(board.getId());
        boardResponse.setResNumber(lastResNumber + 1);
        ZonedDateTime now = utility.getCurrentSystemLocalTime();
        boardResponse.setCreated(now);
        boardResponse.setUpdated(now);
        this.boardResponseRepository.save(boardResponse);
        boardResponse.setUserImagePath(boardResponse.getUser().getUserMainImage().getPath());
        return boardResponse;
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    @CacheEvict(cacheNames={"boards_category","board_tableUrl","boards_response","last_BR_id","board","boards_user","boards_user_page","boards_resoponse_user","boards_resoponse_board_user","count_unseen_res_user","latest_res_time_per_board"}, allEntries=true)
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
    @CacheEvict(cacheNames={"boards_response","last_BR_id","boards_resoponse_user","boards_resoponse_board_user","count_unseen_res_user","latest_res_time_per_board"}, allEntries=true)
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
    @Cacheable("board")
    public Board findBoardById(long id){
        return boardRepository.findBoardById(id);
    };

    @Transactional(rollbackFor=Exception.class)
    @Cacheable("boards_user")
    public List<Board> findBoardsByUser(User user){
        return boardRepository.findBoardsByUser(user);
    };

    @Override
    @Transactional(rollbackFor=Exception.class)
    @Cacheable("boards_user_page")
    public List<Board> findBoardsByUser(User user, Pageable pageable){
        return boardRepository.findBoardsByUser(user, pageable);
    };

    @Override
    @Transactional(rollbackFor=Exception.class)
    @Cacheable("boards_response")
    public BoardResponse findBoardResponseById(long id){
        return boardResponseRepository.findBoardResponseById(id);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    @Cacheable("boards_response_board")
    public ArrayList<BoardResponse> findBoardResponsesByBoard(Board board, Pageable pageable){
        return boardResponseRepository.findBoardResponsesByBoard(board, pageable);
    };

    @Override
    @Transactional(rollbackFor=Exception.class)
    @Cacheable("boards_resoponse_user")
    public Page<BoardResponse> findBoardResponsesByUser(User user, Pageable pageable){
        return boardResponseRepository.findBoardResponsesByUser(user, pageable);
    };

    @Override
    @Transactional(rollbackFor=Exception.class)
    @Cacheable("boards_resoponse_board_user")
    public Page<BoardResponse> findBoardResponsesByBoardAndUser(Board board, User user, Pageable pageable){
        return boardResponseRepository.findBoardResponsesByBoardAndUser(board, user, pageable);
    };

    @Override
    @Transactional(rollbackFor=Exception.class)
    @Cacheable("count_boards")
    public Long getCountOfBoardsByUser(User user){
        return  boardRepository.getCountOfBoardsByUser(user);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    @Cacheable("public_boards")
    public List<Board> getPublicBoards(){
        return  boardRepository.getPublicBoards();
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    @Cacheable("public_boards_pages")
    public List<Board> getPublicBoards(Pageable pageable){
        return  boardRepository.getPublicBoards(pageable);
    }

    @Override
    @Cacheable("public_board_pages")
    @Transactional(rollbackFor=Exception.class)
    public Page<Board> getPublicBoardPages(Pageable pageable){
        return  boardRepository.getPublicBoardPages(pageable);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    @Cacheable("count_unseen_res")
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
    @Cacheable("count_unseen_res_user")
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
    @Cacheable("latest_res_time_per_board")
    public List<BoardResponse> getLatestResponseTimePerBoard(long user_id){
        List<BoardResponse> boardResponses = boardResponseRepository.getLatestResponsePerBoard(user_id);
        return boardResponses;
    }

    private IpString createAndSaveIpString(String ipAddress) {
        IpString ipString = ipStringRepository.findByIpAddress(ipAddress);
        if (ipString != null) {
            return ipString;
        }
        ipString = new IpString();
        String randomString = RandomStringUtils.random(8, 0, 0, true, true, null, new SecureRandom());
        ipString.setIpAddress(ipAddress);
        ipString.setStringId(randomString);
        ipStringRepository.save(ipString);
        return ipString;
    }
}