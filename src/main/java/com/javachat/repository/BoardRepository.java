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
    @Query("select count(b)>0 from Board b where b.table_url_name = :table_url_name and b.is_deleted = false")
    boolean existsByUrlName(@Param("table_url_name") String table_url_name);
    @Query("select b from Board b where b.id = :id and is_deleted = false")
    Board findBoardById(@Param("id") long id);
    @Query("select b from Board b where b.table_url_name = :table_url_name and is_deleted = false")
    Board findBoardByTableUrlName(@Param("table_url_name") String table_url_name);
    @Query("select b from Board b where b.is_private = false and is_deleted = false order by b.updated desc")
    List<Board> getPublicBoards();
    @Query("select b from Board b where b.is_private = false and is_deleted = false order by b.updated desc")
    List<Board> getPublicBoards(Pageable pageable);
    @Query("select b from Board b where b.is_private = false and is_deleted = false order by b.updated desc")
    Page<Board> getPublicBoardPages(Pageable pageable);
    @Query("select b from Board b JOIN User on b = b.user where b.user = :user and b.is_deleted = false")
    List<Board> findBoardsByUser(@Param("user") User user);
    @Query("select b from Board b JOIN User on b = b.user where b.user = :user and b.is_deleted = false order by b.updated desc")
    List<Board> findBoardsByUser(@Param("user") User user, Pageable pageable);
    @Query("select count(b) from Board b where b.user = :user and b.is_deleted = false")
    Long getCountOfBoardsByUser(@Param("user") User user);
    @Query(value="select * from boards where created <= date_trunc('week', now()) - interval '3 week'", nativeQuery=true)
    List<Board> getOldBoards();
    @Query(value="select * from boards where is_deleted = true", nativeQuery=true)
    List<Board> getDeletedBoards();
    @Modifying(clearAutomatically = true)
    @Query("update Board b set is_deleted = true where b.id = :id")
    int deleteBoardById(@Param("id") long id);
}