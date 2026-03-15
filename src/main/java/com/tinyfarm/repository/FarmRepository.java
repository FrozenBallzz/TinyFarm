package com.tinyfarm.repository;

import com.tinyfarm.entity.Farm;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmRepository extends JpaRepository<Farm, Long> {

    Optional<Farm> findByOwnerGithubLogin(String githubLogin);
}
