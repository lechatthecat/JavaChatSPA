package com.javachat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import com.javachat.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.id = :id and isDeleted = false and is_verified = true")
    User findById(@Param("id") long id);
    @Query("select u from User u where u.name = :name and isDeleted = false and is_verified = true")
    User findByName(String name);
    @Query("select u from User u where u.email = :email and isDeleted = false and is_verified = true")
    User findByEmail(@Param("email") String email);
    @Query("select u from User u where u.email = :email and is_verified = false")
    User findByEmailIncludesDeleted(@Param("email") String email);
    @Query("select u from User u where u.email = :email and isDeleted = false and email <> 'Anonymous guest'")
    User findByEmailIncludesUnverified(@Param("email") String email);
    @Query("select u from User u where u.name = :name and isDeleted = false ORDER BY u.id DESC")
    List<User> findUsersByName(@Param("name") String name);
    @Query("select u from User u where u.email = :email and isDeleted = false ORDER BY u.id DESC")
    List<User> findUsersByEmail(@Param("email") String email);
    @Modifying(clearAutomatically = true)
    @Query("update User u set isDeleted = true where u.id = :id")
    int deleteUserById(@Param("id") long id);
    @Query("select count(u)>0 from User u where u.name = :name and isDeleted = false")
    boolean existsByName(String name);
    @Query("select count(u)>0 from User u where u.email = :email and isDeleted = false")
    boolean existsByEmail(String email);
    @Modifying(clearAutomatically = true)
    @Query("update User u set u.failTimes = 0")
    int resetNumberOfFailedLoginAttempsAllUsers();
}
