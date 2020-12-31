package com.javachat.controller.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.javachat.jwt.payload.response.MessageResponse;
import com.javachat.model.Board;
import com.javachat.model.BoardResponse;
import com.javachat.model.Category;
import com.javachat.model.User;
import com.javachat.repository.BoardCategoryRepository;
import com.javachat.repository.CategoryRepository;
import com.javachat.service.BoardCategoryServiceImpl;
import com.javachat.service.BoardService;
import com.javachat.service.UserDetailsServiceImpl;
import com.javachat.service.UserService;
import com.javachat.util.JwtUtils;
import com.javachat.validator.BoardValidator;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class BoardController {
	@Autowired
	BoardService boardService;
    @Autowired
    BoardCategoryRepository boardCategoryRepository;
    @Autowired
    BoardCategoryServiceImpl boardCategoryService;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    UserService userService;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    BoardValidator boardValidator;
    @Autowired
    MessageSource messageSource;
    Logger logger = LoggerFactory.getLogger(BoardController.class);
    
    @PostMapping(path = "/board_create", consumes = "application/json")
    public ResponseEntity<?> createBoard(
            HttpServletRequest request,
            @RequestBody Board board,
            final BindingResult bindingResult
        ) {
        
        String jwt = jwtUtils.parseJwt(request);
		try {
			final boolean isJwtValid = jwtUtils.validateJwtToken(jwt);
			if (jwt != null && isJwtValid) {
				final String username = jwtUtils.getUsernameFromToken(jwt);
                final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                final JSONObject resultJson = new JSONObject();
                board.setTable_url_name(boardService.getUniqueUrlName());
                boardValidator.validate(board, bindingResult);
                if(!bindingResult.hasErrors()){
                    long boardCategoryId = boardCategoryService.createBoardCategory(board, userDetails.getUsername());
                    if (boardCategoryId > 0) {
                        resultJson.put("isSuccess", true);
                        resultJson.put("message", "Success. Board created.");
                        return new ResponseEntity<>(resultJson.toMap(), HttpStatus.OK);
                    } else {
                        return ResponseEntity.badRequest().body(resultJson.toMap());
                    }
                } else {
                    final List<FieldError> errors = bindingResult.getFieldErrors();
                    final HashMap<String, String> errorList = new HashMap<>();
                    for (FieldError error : errors ) {
                        final String message = messageSource.getMessage(error, null);
                        errorList.put(error.getField(), message);
                    }
                    resultJson.put("errors", errorList);
                    resultJson.put("isSuccess", false);
                    return ResponseEntity.badRequest().body(resultJson.toMap());
                }
			} else {
                logger.error("Error: token is empty or invalid. jwt:" + jwt);
				return ResponseEntity.badRequest().body(new MessageResponse("Error: token is empty or invalid."));
			}
        } catch (UsernameNotFoundException e) {
			return ResponseEntity.badRequest().body(new MessageResponse("Email address or password is wrong. Please note you must use \"Email address\" not username."));
		}
    }

    @GetMapping(path = "/get_boards/{url_name}/{page_number}")
    public ResponseEntity<?> getCategory(
            @PathVariable("url_name") String url_name,
            @PathVariable("page_number") int page_number,
            HttpServletRequest request,
            @PageableDefault(value=30, page=0) Pageable pageable
        ) {

        try {
            final Category category = categoryRepository.findCategoryByUrlname(url_name);
            final Page<List<String>> boards = boardCategoryRepository.findBoardsByCategory(category.getId(), PageRequest.of(page_number, 50));
            return new ResponseEntity<>(boards, HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            logger.error(e.toString());
            return ResponseEntity.badRequest().body(new MessageResponse("Error detected."));
		}
    }

    @GetMapping(path = "/get_board_info/{table_url_name}")
    public ResponseEntity<?> getBoardInfo(
            @PathVariable("table_url_name") String table_url_name,
            HttpServletRequest request
        ) {

        try {
            final Board board = boardService.findBoardByTableUrlName(table_url_name);
            final HashMap<String, String> result = new HashMap<String, String>();
            result.put("name", board.getName());
            result.put("table_url_name", board.getTable_url_name());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            logger.error(e.toString());
            return ResponseEntity.badRequest().body(new MessageResponse("Error detected."));
		}
    }

    @GetMapping(path = "/get_board_responses/{table_url_name}/{page_number}")
    public ResponseEntity<?> getBoardResponses(
            @PathVariable("table_url_name") String table_url_name,
            @PathVariable("page_number") int page_number,
            HttpServletRequest request
        ) {

        try {
            final Board board = boardService.findBoardByTableUrlName(table_url_name);
            final ArrayList<BoardResponse> responses = boardService.findBoardResponsesByBoard(board, PageRequest.of(page_number, 100));
            Collections.reverse(responses);
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            logger.error(e.toString());
            return ResponseEntity.badRequest().body(new MessageResponse("Error detected."));
		}
    }

    @PostMapping(path = "/delete_board_response")
    public ResponseEntity<?> deleteBoardResponse(
            HttpServletRequest request,
            @RequestBody BoardResponse msg
        ) {

        String jwt = jwtUtils.parseJwt(request);
        try {
            final boolean isJwtValid = jwtUtils.validateJwtToken(jwt);
            if (jwt != null && isJwtValid) {
                final String username = jwtUtils.getUsernameFromToken(jwt);
                final User user = userService.findByEmail(username);
                final long id = msg.getId();
                final BoardResponse boardResponse = boardService.findBoardResponseById(id);
                if (user.getId() == boardResponse.getUser().getId() && !boardResponse.getIsFirst()) {
                    final boolean isDeleted = boardService.deleteBoardResponse(boardResponse);
                    final JSONObject resultJson = new JSONObject();
                    resultJson.put("result", isDeleted);
                    return new ResponseEntity<>(resultJson, HttpStatus.OK);
                }
                return ResponseEntity.badRequest().body(new MessageResponse("Error: token is empty or invalid."));
            } else {
                logger.error("Error: token is empty or invalid. jwt:" + jwt);
                return ResponseEntity.badRequest().body(new MessageResponse("Error: token is empty or invalid."));
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email address or password is wrong. Please note you must use \"Email address\" not username."));
        }
    }

    @PostMapping(path = "/delete_board")
    public ResponseEntity<?> deleteBoard(
            HttpServletRequest request,
            @RequestBody Board board
        ) {

        String jwt = jwtUtils.parseJwt(request);
        try {
            final boolean isJwtValid = jwtUtils.validateJwtToken(jwt);
            if (jwt != null && isJwtValid) {
                final String username = jwtUtils.getUsernameFromToken(jwt);
                final User user = userService.findByEmail(username);
                final String tableUrlName = board.getTable_url_name();
                final Board boardToDelete = boardService.findBoardByTableUrlName(tableUrlName);
                if (user.getId() == boardToDelete.getUser().getId()) {
                    final boolean isDeleted = boardService.deleteBoard(boardToDelete);
                    final JSONObject resultJson = new JSONObject();
                    resultJson.put("result", isDeleted);
                    return new ResponseEntity<>(resultJson, HttpStatus.OK);
                }
                return ResponseEntity.badRequest().body(new MessageResponse("Error: token is empty or invalid."));
            } else {
                logger.error("Error: token is empty or invalid. jwt:" + jwt);
                return ResponseEntity.badRequest().body(new MessageResponse("Error: token is empty or invalid."));
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email address or password is wrong. Please note you must use \"Email address\" not username."));
        }
    }
}
