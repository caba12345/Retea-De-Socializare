package com.example.laboratorjavafx.repository.memory;

import com.example.laboratorjavafx.domain.Entity;
import com.example.laboratorjavafx.repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    protected Map<ID, E> entities;


    public InMemoryRepository(){
        entities = new HashMap<ID, E>();
    }

    @Override
    public Optional <E> findOne(ID id){
        if (id == null)
            throw new IllegalArgumentException("id must be not null!");
        E entity = entities.get(id);
        return Optional.ofNullable(entity);
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }

        ID id = entity.getId();
        if (id == null) {
            throw new IllegalArgumentException("Entity ID must not be null");
        }

        if (entities.containsKey(id)) {
            return Optional.ofNullable(entities.get(id)); // Entitatea cu ID-ul specificat deja există
        } else {
            entities.put(id, entity);
            return Optional.empty(); // Entitatea a fost salvată
        }
    }


    @Override
    public Optional<E> delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null!");
        }

        // Verificăm dacă entitatea există în repoziție
        ID entityId = entity.getId();
        E existingEntity = entities.get(entityId);

        if (existingEntity == null) {
            // Dacă entitatea nu există, nu putem să o actualizăm
            return Optional.ofNullable(entity);
        }

        try {
            // Actualizăm entitatea în repoziție
            entities.put(entityId, entity);

            return Optional.empty(); // Returnăm Optional gol pentru a indica actualizarea reușită
        } catch (Exception e) {
            // Dacă actualizarea eșuează, aruncăm excepție și returnăm entitatea originală într-un Optional
            System.err.println("Update error: " + e.getMessage());
            return Optional.of(existingEntity);
        }
    }


    @Override
    public int size(){
        return entities.size();
    }
}
