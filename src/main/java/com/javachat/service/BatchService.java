package com.javachat.service;

import java.util.List;

import com.javachat.model.Board;
import com.javachat.model.BoardResponse;
import com.javachat.model.UserConfirmation;
import com.javachat.model.UserForgotemail;
import com.javachat.repository.BoardRepository;
import com.javachat.repository.BoardResponseRepository;
import com.javachat.repository.UserConfirmationRepository;
import com.javachat.repository.UserForgotemailRepository;
import com.javachat.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class BatchService {
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardResponseRepository boardResponseRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserConfirmationRepository userConfirmationRepository;
    @Autowired
    UserForgotemailRepository userForgotemailRepository;

    @Transactional
    @Modifying(clearAutomatically = true)
    public boolean deleteUnncessaryData() {
        try {
            List<UserConfirmation> userConfirmations = userConfirmationRepository.getOldUserConfirmations();
            // CascadeType.REMOVE is specified toward user, so user is removed with the confirmation token.
            userConfirmationRepository.deleteAll(userConfirmations);
            System.out.println(userConfirmations);
            List<UserForgotemail> userForgotemails = userForgotemailRepository.getOldUserForgotemails();
            userForgotemailRepository.deleteAll(userForgotemails);
            System.out.println(userForgotemails);
            List<Board> boards = boardRepository.getOldBoards();
            boardRepository.deleteAll(boards);
            List<Board> deletedBoards = boardRepository.getDeletedBoards();
            boardRepository.deleteAll(deletedBoards);
            System.out.println(deletedBoards);
            List<BoardResponse> deletedBoardResponses = boardResponseRepository.getDeletedBoardResponses();
            boardResponseRepository.deleteAll(deletedBoardResponses);
            System.out.println(deletedBoardResponses);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }
}
