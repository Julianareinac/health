package com.uniquindio.health.repositories;

import com.uniquindio.health.domain.Microservice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MicroserviceRepository extends JpaRepository<Microservice, Long> {
    Microservice findByName(String name);
    @Query("SELECT u.emails FROM Microservice u")
    List<String> findAllEmails();


}
