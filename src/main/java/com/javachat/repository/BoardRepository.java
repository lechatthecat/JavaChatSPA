package com.javachat.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.javachat.model.Board;
import com.javachat.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface BoardRepository extends JpaRepository<Board,Long>{
    @Query("select count(b)>0 from Board b where b.tableUrlName = :tableUrlName and b.isDeleted = false")
    boolean existsByUrlName(@Param("tableUrlName") String tableUrlName);
    @Query("select b from Board b where b.id = :id and isDeleted = false")
    Board findBoardById(@Param("id") long id);
    @Query("select b from Board b where b.tableUrlName = :tableUrlName and isDeleted = false")
    Board findBoardByTableUrlName(@Param("tableUrlName") String tableUrlName);
    @Query("select b from Board b where b.isPrivate = false and isDeleted = false order by b.updated desc")
    List<Board> getPublicBoards();
    @Query("select b from Board b where b.isPrivate = false and isDeleted = false order by b.updated desc")
    List<Board> getPublicBoards(Pageable pageable);
    @Query("select b from Board b where b.isPrivate = false and isDeleted = false order by b.updated desc")
    Page<Board> getPublicBoardPages(Pageable pageable);
    @Query("select b from Board b JOIN User on b = b.user where b.user = :user and b.isDeleted = false")
    List<Board> findBoardsByUser(@Param("user") User user);
    @Query("select b from Board b JOIN User on b = b.user where b.user = :user and b.isDeleted = false order by b.updated desc")
    List<Board> findBoardsByUser(@Param("user") User user, Pageable pageable);
    @Query("select count(b) from Board b where b.user = :user and b.isDeleted = false")
    Long getCountOfBoardsByUser(@Param("user") User user);
    @Query(value="select * from boards where created <= date_trunc('day', now()) - interval '21 day'", nativeQuery=true)
    List<Board> getOldBoards();
    @Query(value="select b.* " 
    + "from boards as b " 
    + "inner join (" 
                    + "select board_id, count(id) as br_count_num "
                    + "from board_responses "
                    + "where is_deleted = false "
                    + "group by board_id "
                + ") br_count on br_count.board_id = b.id "
    + "where br_count.br_count_num >= 1000 "
    + " ", nativeQuery=true)
    List<Board> findBoardsWithMaxRes();
    @Query(value="select * from boards where is_deleted = true", nativeQuery=true)
    List<Board> getDeletedBoards();
    @Modifying(clearAutomatically = true)
    @Query("update Board b set isDeleted = true where b.id = :id")
    int deleteBoardById(@Param("id") long id);
}