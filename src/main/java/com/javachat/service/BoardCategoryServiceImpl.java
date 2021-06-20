package com.javachat.service;

import java.time.ZonedDateTime;
import java.util.List;

import com.javachat.model.Board;
import com.javachat.model.BoardCategory;
import com.javachat.model.BoardResponse;
import com.javachat.model.BoardUser;
import com.javachat.model.Category;
import com.javachat.model.IpString;
import com.javachat.model.User;
import com.javachat.repository.BoardCategoryRepository;
import com.javachat.repository.BoardRepository;
import com.javachat.repository.BoardResponseRepository;
import com.javachat.repository.BoardUserRepository;
import com.javachat.repository.UserRepository;
import com.javachat.repository.CategoryRepository;
import com.javachat.util.HttpReqRespUtils;
import com.javachat.util.Utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class BoardCategoryServiceImpl implements BoardCategoryService {
	@Autowired
	BoardService boardService;
	@Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardResponseRepository boardResponseRepository;
    @Autowired
    BoardCategoryRepository boardCategoryRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BoardUserRepository boardUserRepository;
    @Autowired
    Utility utility;

    @Override
    @Transactional(rollbackFor=Exception.class)
    @CacheEvict(cacheNames={"boards_category","board_tableUrl","last_BR_id","board","boards_user","boards_user_page","boards_resoponse_user","boards_resoponse_board_user","count_boards","public_boards","public_boards_pages","public_board_pages","latest_res_time_per_board"}, allEntries=true)
    public long createBoardCategory(BoardCategory boardCategory) {
        try{
            ZonedDateTime now = utility.getCurrentSystemLocalTime();
            boardCategory.setCreated(now);
            boardCategory.setUpdated(now);
            return this.boardCategoryRepository.save(boardCategory).getId();
        } catch(DataAccessException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return 0;
        }
    }
    
    @Override
    @Transactional(rollbackFor=Exception.class)
    @CacheEvict(cacheNames={"boards_category","board_tableUrl","last_BR_id","board","boards_user","boards_user_page","boards_resoponse_user","boards_resoponse_board_user","count_boards","public_boards","public_boards_pages","public_board_pages","latest_res_time_per_board"}, allEntries=true)
    public long createBoardCategory(Board board, String username) {
        try {
            User user = userRepository.findByEmail(username);
            board.setAgreesTerm(true);
            board.setUser(user);
            boardService.createBoard(board);
            Category category = categoryRepository.findCategoryByUrlname(board.getUrlName());
            BoardCategory boardCategory = new BoardCategory();
            boardCategory.setBoard(board);
            boardCategory.setCategory(category);
            ZonedDateTime now = utility.getCurrentSystemLocalTime();;
            boardCategory.setCreated(now);
            boardCategory.setUpdated(now);
            BoardResponse boardResponse = new BoardResponse();
            boardResponse.setUser(user);
            boardResponse.setBoard(board);
            boardResponse.setResponse(board.getDetail());
            boardResponse.setResNumber(1);
            boardResponse.setFirst(true);
            String ipAddress = HttpReqRespUtils.getClientIpAddressIfServletRequestExist();
            IpString ipString = boardService.createIpString(ipAddress);
            boardResponse.setIpString(ipString);
            boardResponse.setIpAddress(ipString.getIpAddress());
            boardResponse.setStringId(ipString.getStringId());
            boardResponse.setUpdated(now);
            boardResponse.setCreated(now);
            BoardUser boardUser = new BoardUser();
            boardUser.setBoard(board);
            boardUser.setUser(user);
            boardUser.setCreated(now);
            boardUser.setUpdated(now);
            this.boardUserRepository.save(boardUser);
            this.boardResponseRepository.save(boardResponse);
            return this.boardCategoryRepository.save(boardCategory).getId();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    @Override
    @Cacheable("boards_category")
    public Page<List<String>> findBoardsByCategory(long category_id, Category category, int pageNumber){
        return boardCategoryRepository.findBoardsByCategory(category.getId(), PageRequest.of(pageNumber, 50));
    }
}
