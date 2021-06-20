package com.javachat.repository;

import com.javachat.model.IpString;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IpStringRepository extends JpaRepository<IpString,Long> {
    @Query("select ips from IpString ips where ips.ipAddress = :ipAddress")
    IpString findByIpAddress(@Param("ipAddress") String ipAddress);
    @Modifying(clearAutomatically = true)
    @Query(value = "truncate table ip_strings",nativeQuery = true)
    void truncateIpString();
}
