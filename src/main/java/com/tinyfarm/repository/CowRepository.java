package com.tinyfarm.repository;

import com.tinyfarm.entity.Cow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CowRepository extends JpaRepository<Cow, Long> {

    List<Cow> findByFarmOwnerGithubLoginOrderById(String githubLogin);
}
