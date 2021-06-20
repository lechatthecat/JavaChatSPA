package com.javachat.repository;

import java.util.List;

import com.javachat.model.BoardCategory;
import com.javachat.model.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface BoardCategoryRepository extends JpaRepository<BoardCategory,Long> {
    @Query("select bc from BoardCategory bc where bc.id = id and bc.isDeleted = false")
    BoardCategory findBoardCategoryById(@Param("id") long id);
    @Query("select bc from BoardCategory bc where bc.category = category and bc.isDeleted = false")
    BoardCategory findBoardCategoryByCategory(@Param("category") Category category);
    @Query(value="select b.name as name, LEFT(b.detail, 101) as detail, b.table_url_name, c.url_name, u.name as user_name, b.created, br_count.br_count_num " 
    + "from boards as b " 
    + "inner join board_categories as bc on b.id = bc.board_id " 
    + "inner join categories as c on c.id = bc.category_id " 
    + "inner join board_users as bu on b.id = bu.board_id "
    + "inner join users u on u.id = bu.user_id "
    + "inner join (" 
                    + "select board_id, MAX(created) AS last_date "
                    + "from board_responses "
                    + "where is_deleted = false "
                    + "group by board_id "
                + ") br on br.board_id = b.id "
    + "inner join (" 
                    + "select board_id, count(id) as br_count_num "
                    + "from board_responses "
                    + "where is_deleted = false "
                    + "group by board_id "
                + ") br_count on br_count.board_id = b.id "
    + "where bc.category_id = :category_id and b.is_deleted = false and u.is_deleted = false and u.is_deleted = false "
    + "order by br.last_date desc", nativeQuery=true)
    Page<List<String>> findBoardsByCategory(@Param("category_id") long category_id, Pageable pageable);
}
