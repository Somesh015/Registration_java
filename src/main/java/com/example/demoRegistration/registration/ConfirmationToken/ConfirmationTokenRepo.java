package com.example.demoRegistration.registration.ConfirmationToken;

import jakarta.transaction.Transactional;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepo extends JpaRepository<ConfirmationToken,Long> {
    // GET TOKEN//
    Optional<ConfirmationToken> findByToken(String token);

    // SET CONFIRMED AT //
    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken c " +
            "SET c.confirmedAt = ?2 " +
            "WHERE c.token = ?1")
    int updateConfirmAt(String token, LocalDateTime confirmedAt);


}
