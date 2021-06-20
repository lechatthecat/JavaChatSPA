package com.javachat.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.javachat.model.Board;
import com.javachat.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import com.javachat.model.BoardResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface BoardResponseRepository extends JpaRepository<BoardResponse,Long>{
    @Query(value="select MAX(res_number) from board_responses br where br.board_id = :boardId group by br.board_id",nativeQuery = true)
    int findLastBRIdByBoardId(@Param("boardId") long boardId);
    @Query(value="select MAX(res_number) from board_responses br left join boards b on b.id = br.board_id where b.table_url_name = :tableUrlName group by br.board_id",nativeQuery = true)
    int findLastBRIdByTableUrlName(@Param("tableUrlName") String tableUrlName);
    @Query("select br from BoardResponse br where br.id = :id")
    BoardResponse findBoardResponseById(@Param("id") long id);
    @Query("select br from BoardResponse br where br.user = :user and br.isDeleted = false")
    List<BoardResponse> findBoardResponsesByUser(@Param("user") User user);
    @Query("select br from BoardResponse br where br.user = :user and br.isDeleted = false")
    Page<BoardResponse> findBoardResponsesByUser(@Param("user") User user, Pageable pageable);
    @Query("select br from BoardResponse br "
     + "inner join User u on br.user = u "
     + "inner join Board b on br.board = b "
     + "where br.board = :board "
     + "and br.isDeleted = false "
     + "and b.isDeleted = false "
     + "order by br.created desc")
    ArrayList<BoardResponse> findBoardResponsesByBoard(@Param("board") Board board, Pageable pageable);
    @Query("select br from BoardResponse br "
    + "inner join User u on u = br.user "
    + "inner join Board b on b = br.board "
    + "where br.board = :board "
    + "and br.user = :user "
    + "and br.isDeleted = false "
    + "and b.isDeleted = false "
    + "order by br.created desc")
    Page<BoardResponse> findBoardResponsesByBoardAndUser(@Param("board") Board board, @Param("user") User user, Pageable pageable);
    @Modifying(clearAutomatically = true)
    @Query("update BoardResponse br set br.isDeleted = true where br.id = :id")
    int deleteBoardResponseById(@Param("id") long id);
    @Query(value =   "SELECT * FROM "
                   +   "(select * from board_responses where board_id in ((select distinct br.board_id from board_responses br left join boards b on br.board_id = b.id where b.user_id = :user_id or br.user_id = :user_id))) b1 " 
                   + "left join "
                   +   "(select * from board_responses where board_id in ((select distinct br.board_id from board_responses br left join boards b on br.board_id = b.id where b.user_id = :user_id or br.user_id = :user_id))) b2 " 
                   + "on " 
                   +   "b1.board_id = b2.board_id and b1.created < b2.created " 
                   + "where " 
                   +   "b2.id is null;"
           ,nativeQuery = true)
    List<BoardResponse> getLatestResponsePerBoard(@Param("user_id") long user_id);
    @Query(value="select * from board_responses inner join boards on boards.id = board where board.id = board_responses.board_id where board.created <= date_trunc('week', now()) - interval '3 week'", nativeQuery=true)
    List<BoardResponse> getOldBoardResponses();
    @Query(value="select br from BoardResponse br where br.isDeleted = true")
    List<BoardResponse> getDeletedBoardResponses();
}