package com.javachat.service;

import java.util.List;
import java.util.stream.Collectors;

import com.javachat.model.Board;
import com.javachat.model.UserConfirmation;
import com.javachat.model.UserForgotemail;
import com.javachat.repository.BoardRepository;
import com.javachat.repository.BoardResponseRepository;
import com.javachat.repository.IpStringRepository;
import com.javachat.repository.UserConfirmationRepository;
import com.javachat.repository.UserForgotemailRepository;
import com.javachat.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class BatchService {
    private static final Logger logger = LoggerFactory.getLogger(BatchService.class);
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
    @Autowired
    IpStringRepository ipStringRepository;

    @Transactional
    @Modifying(clearAutomatically = true)
    public boolean deleteUnncessaryData() {
        try {
            // truncate the ip strings table
            ipStringRepository.truncateIpString();
            
            // delete unverified old tokens
            List<UserConfirmation> userConfirmations = userConfirmationRepository.getOldUnverifiedUserConfirmations();
            // CascadeType.REMOVE is specified toward user, so user is removed with the confirmation token.
            if (userConfirmations.size() > 0) {
                userConfirmationRepository.deleteAll(userConfirmations);
            }
            
            // delete old forgot email data
            List<UserForgotemail> userForgotemails = userForgotemailRepository.getOldUserForgotemails();
            if (userForgotemails.size() > 0) {
                userForgotemailRepository.deleteAll(userForgotemails);
            }
            
            // delete old tokens
            List<UserConfirmation> userConfirmationsIncludesVerified = userConfirmationRepository.getOldUserConfirmations();
            List<String> oldTokens = userConfirmationsIncludesVerified.stream() // Stream<Entity>
                                        .map(UserConfirmation::getConfirmationToken) // Stream<String>
                                        .collect(Collectors.toList()); // List<String>
            if (oldTokens.size() > 0) {
                userConfirmationRepository.deleteOnlyTokens(oldTokens);
            }
            
            // delete unnecessary boards that have 1000 res
            List<Board> boards = boardRepository.findBoardsWithMaxRes();
            if (boards.size() > 0) {
                for (Board board: boards) {
                    System.out.println(board.getBoardResponses());
                    this.saveResponesInLog(board);
                    boardResponseRepository.deleteAll(board.getBoardResponses());
                }
                boardRepository.deleteAll(boards);
            }

            // delete unnecessary responses
            List<Board> deletedBoards = boardRepository.getDeletedBoards();
            if (deletedBoards.size() > 0) {
                for (Board board: deletedBoards) {
                    System.out.println(board.getBoardResponses());
                    this.saveResponesInLog(board);
                    boardResponseRepository.deleteAll(board.getBoardResponses());
                }
                // delete board whose isDeleted is true 
                boardRepository.deleteAll(deletedBoards);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    private void saveResponesInLog(Board b) {
        logger.warn("board name: " + b.getName());
        logger.warn("detail: " + b.getDetail());
        b.getBoardResponses()
        .stream()
        .forEach(br -> {
            logger.warn("cdate: " + br.getCreated());
            logger.warn("udate: " + br.getUpdated());
            logger.warn("isDeleted: " + br.isDeleted());
            if (br.getIpString() != null) logger.warn("ip: " + br.getIpString().getIpAddress());
            if (br.getResponse() != null)logger.warn("res: " + br.getResponse());
        });
    }
}
