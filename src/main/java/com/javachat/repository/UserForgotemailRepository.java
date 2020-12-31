package com.javachat.repository;

import java.util.List;

import com.javachat.model.UserForgotemail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserForgotemailRepository extends JpaRepository<UserForgotemail,Long> {
    boolean existsByConfirmationToken(String confirmationToken);
    @Query("select uf from UserForgotemail uf where uf.confirmationToken = :confirmationToken and uf.isUsed = false")
    UserForgotemail findUserConfirmationByConfirmation(@Param("confirmationToken") String confirmationToken);
    @Query(value="select * from user_forgotemails where user_forgotemails.created <= date_trunc('minute', now()) - interval '60 minute'", nativeQuery=true)
    List<UserForgotemail> getOldUserForgotemails();
}
