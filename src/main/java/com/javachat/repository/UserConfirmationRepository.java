package com.javachat.repository;

import java.util.List;

import com.javachat.model.UserConfirmation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
    List<UserConfirmation> getOldUnverifiedUserConfirmations();
    @Query(value="select * from user_confirmations inner join users on users.id = user_confirmations.user_id where user_confirmations.created <= date_trunc('minute', now()) - interval '1440 minute'", nativeQuery=true)
    List<UserConfirmation> getOldUserConfirmations();
    @Modifying(clearAutomatically = true)
    @Query(value="delete from user_confirmations where confirmation_token = :confirmation_token", nativeQuery=true)
    int deleteOnlyToken(@Param("confirmation_token") String confirmation_token);
    @Modifying(clearAutomatically = true)
    @Query(value="delete from user_confirmations where confirmation_token in :confirmation_tokens", nativeQuery=true)
    int deleteOnlyTokens(@Param("confirmation_tokens") List<String> confirmation_token);
}
