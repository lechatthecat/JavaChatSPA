package com.javachat.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.javachat.model.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    @Query("select c from Category c where c.id = :id and c.is_deleted = false")
    Category findCategoryById(@Param("id") long id);
    @Query("select c from Category c where c.url_name = :url_name and c.is_deleted = false")
    Category findCategoryByUrlname(@Param("url_name") String urlName);
    @Query("select c from Category c where c.is_deleted = false")
    List<Category> findAllUndeletedCategories();
}
