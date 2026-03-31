package com.grenade.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.grenade.main.dto.PageDTO;
import com.grenade.main.repo.RepoBase;

import jakarta.persistence.EntityNotFoundException;

public abstract class ServiceBase<T, D, ID, R extends RepoBase<T, ID>>{
    
    protected final R repository;

    protected ServiceBase(R repository) {
        this.repository = repository;
    }

    public abstract D toDTO(T entity);

    public D getById(@NonNull ID uuid){
        return repository.findByUuid(uuid)
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Grenade not found with id: " + uuid));
    }

    protected String getCurrentUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    public PageDTO<D> getAll(@NonNull Pageable pageable){
        Page<T> page = repository.findAll(pageable);
        return new PageDTO<D>(page.getContent().stream().map(this::toDTO).toList(), page.getNumber() + 1,page.getTotalPages());
    }

   @Transactional
    public void delete(@NonNull ID uuid){
        T entity = repository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException());
        repository.delete(entity);
    }
}
