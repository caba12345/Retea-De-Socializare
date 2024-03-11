package com.example.laboratorjavafx.Paging;

import com.example.laboratorjavafx.domain.Entity;
import com.example.laboratorjavafx.repository.Repository;

public interface  PagingRepository<ID, E extends Entity<ID>> extends Repository<ID,E> {
    Page<E> findAll(Pageable pageable);
}
