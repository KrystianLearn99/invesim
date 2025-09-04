package org.kris.invesim.userms;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String> {

    Optional<UserProfile> findById(UUID id);

    Optional<UserProfile> findByExternalId(UUID externalId);
}
