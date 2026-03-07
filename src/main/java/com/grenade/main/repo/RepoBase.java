package com.grenade.main.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RepoBase<T ,ID> extends JpaRepository<T ,ID>{

    Optional<T> findByUuid(ID uuid);
    void deleteByUuid(ID uuid);
}