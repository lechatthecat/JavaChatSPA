package com.javachat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.javachat.model.UserImage;
import com.javachat.model.User;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage,Long>{
    @Query("select ui from UserImage ui where ui.user= :user and isDeleted = false")
    List<UserImage> findImagesByUser(@Param("user") User user);
}