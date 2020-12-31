package com.javachat.repository;

import java.util.List;

import com.javachat.model.UserConfirmation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserConfirmationRepository extends JpaRepository<UserConfirmation,Long> {
    boolean existsByConfirmationToken(String confirmationToken);
    @Query("select uc from UserConfirmation uc where uc.confirmationToken = :confirmationToken and uc.isVerified = false")
    UserConfirmation findUserConfirmationByConfirmation(@Param("confirmationToken") String confirmationToken);
    @Query("select uc from UserConfirmation uc where uc.confirmationToken = :confirmationToken")
    UserConfirmation findUserConfirmationIncludesVerifiedByConfirmation(@Param("confirmationToken") String confirmationToken);
    @Query(value="select * from user_confirmations inner join users on users.id = user_confirmations.user_id where user_confirmations.is_verified = false and user_confirmations.created <= date_trunc('minute', now()) - interval '1440 minute'", nativeQuery=true)
    List<UserConfirmation> getOldUserConfirmations();
}
