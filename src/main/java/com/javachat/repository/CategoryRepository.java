package com.javachat.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.javachat.model.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    @Query("select c from Category c where c.id = :id and c.isDeleted = false")
    Category findCategoryById(@Param("id") long id);
    @Query("select c from Category c where c.urlName = :urlName and c.isDeleted = false")
    Category findCategoryByUrlname(@Param("urlName") String urlName);
    @Query("select c from Category c where c.isDeleted = false order by display_order asc")
    List<Category> findAllUndeletedCategories();
}
