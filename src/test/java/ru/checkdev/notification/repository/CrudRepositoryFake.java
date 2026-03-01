package ru.checkdev.notification.repository;

import javax.persistence.Id;
import org.springframework.data.repository.CrudRepository;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class CrudRepositoryFake<T, ID> implements CrudRepository<T, ID> {

    protected final Map<ID, T> memory = new HashMap<>();

    @SuppressWarnings("unchecked")
    private <S> ID getId(S entity) {
        try {
            Class<?> cl = entity.getClass();
            for (Field field : cl.getDeclaredFields()) {
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    if (annotation instanceof Id) {
                        field.setAccessible(true);
                        return (ID) field.get(entity);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("Entity does not contain @Id field.");
    }

    @Override
    public <S extends T> S save(S entity) {
        memory.put(getId(entity), entity);
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(memory.get(id));
    }

    @Override
    public Iterable<T> findAll() {
        return memory.values();
    }

    @Override
    public boolean existsById(ID id) {
        return memory.containsKey(id);
    }

    @Override
    public long count() {
        return memory.size();
    }

    @Override
    public void deleteById(ID id) {
        memory.remove(id);
    }

    @Override
    public void delete(T entity) {
        memory.remove(getId(entity));
    }

    @Override
    public void deleteAll() {
        memory.clear();
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::save);
        return entities;
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        throw new UnsupportedOperationException();
    }
}
// todo создал класс на замену такого же но джакарта
