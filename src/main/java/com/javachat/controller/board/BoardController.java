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
        
		try {
            String jwt = jwtUtils.parseJwt(request);
            final boolean isJwtValid = jwtUtils.validateJwtToken(jwt);
            User user = null;
            if (jwt != null && isJwtValid) {
                final String username = jwtUtils.getUsernameFromToken(jwt);
                user = (User) userDetailsService.loadUserByUsername(username);
            } else {
                user = userService.findByEmail("Anonymous guest");
            }

            final JSONObject resultJson = new JSONObject();
            board.setTableUrlName(boardService.getUniqueUrlName());
            boardValidator.validate(board, bindingResult);
            if(!bindingResult.hasErrors()){
                long boardCategoryId = boardCategoryService.createBoardCategory(board, user.getUsername());
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
        
        } catch (UsernameNotFoundException e) {
			return ResponseEntity.badRequest().body(new MessageResponse("Email address or password is wrong. Auth failed."));
		}
    }

    @GetMapping(path = "/get_boards/{urlName}/{pageNumber}")
    public ResponseEntity<?> getCategory(
            @PathVariable("urlName") String urlName,
            @PathVariable("pageNumber") int pageNumber,
            HttpServletRequest request,
            @PageableDefault(value=30, page=0) Pageable pageable
        ) {

        try {
            final Category category = categoryRepository.findCategoryByUrlname(urlName);
            if (category == null) {
                return new ResponseEntity<>(null, HttpStatus.OK);
            }
            final Page<List<String>> boards = boardCategoryService.findBoardsByCategory(category.getId(), category, pageNumber);
            return new ResponseEntity<>(boards, HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            logger.error(e.toString());
            return ResponseEntity.badRequest().body(new MessageResponse("Error detected."));
		}
    }

    @GetMapping(path = "/get_board_info/{tableUrlName}")
    public ResponseEntity<?> getBoardInfo(
            @PathVariable("tableUrlName") String tableUrlName,
            HttpServletRequest request
        ) {

        try {
            final Board board = boardService.findBoardByTableUrlName(tableUrlName);
            if (board == null) {
                return new ResponseEntity<>(null, HttpStatus.OK);
            }
            final HashMap<String, String> result = new HashMap<String, String>();
            result.put("name", board.getName());
            result.put("tableUrlName", board.getTableUrlName());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            logger.error(e.toString());
            return ResponseEntity.badRequest().body(new MessageResponse("Error detected."));
		}
    }

    @GetMapping(path = "/get_board_responses/{tableUrlName}/{pageNumber}")
    public ResponseEntity<?> getBoardResponses(
            @PathVariable("tableUrlName") String tableUrlName,
            @PathVariable("pageNumber") int pageNumber,
            HttpServletRequest request
        ) {

        try {
            final Board board = boardService.findBoardByTableUrlName(tableUrlName);
            if (board == null) {
                return new ResponseEntity<>(null, HttpStatus.OK);
            }
            final ArrayList<BoardResponse> responses = boardService.findBoardResponsesByBoard(board, PageRequest.of(pageNumber, 100));
            if (responses.get(0).getId() > responses.get(responses.size()-1).getId()) {
                Collections.reverse(responses);
            }
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
                if (user.getId() == boardResponse.getUser().getId() && !boardResponse.isFirst()) {
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
            return ResponseEntity.badRequest().body(new MessageResponse("Email address or password is wrong. Auth failed."));
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
                final String tableUrlName = board.getTableUrlName();
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
            return ResponseEntity.badRequest().body(new MessageResponse("Email address or password is wrong. Auth failed."));
        }
    }
}
