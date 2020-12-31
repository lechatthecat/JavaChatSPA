package com.javachat.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.javachat.model.Board;
import com.javachat.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import com.javachat.model.BoardUser;
import org.springframework.data.domain.Pageable;

@Repository
public interface BoardUserRepository extends JpaRepository<BoardUser,Long>{
    @Query("select bu from BoardUser bu where bu.board = :board")
    List<BoardUser> findBoardUsersByBoard(@Param("board") Board board);
    @Query("select bu from BoardUser bu where bu.board = :board")
    List<BoardUser> findBoardUsersByBoard(@Param("board") Board board, Pageable pageable);
    @Query("select bu from BoardUser bu where bu.user = :user")
    List<BoardUser> findBoardUsersByUser(@Param("user") User user);
    @Query("select bu from BoardUser bu where bu.user = :user")
    List<BoardUser> findBoardUsersByUser(@Param("user") User user, Pageable pageable);
    @Modifying(clearAutomatically = true)
    @Query("update BoardUser bu set is_deleted = true where bu.id = :id")
    int deleteBoardUserById(@Param("id") long id);
}