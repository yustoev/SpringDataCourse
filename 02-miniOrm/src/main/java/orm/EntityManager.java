package orm;

import orm.annotation.Entity;
import orm.annotation.Id;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Arrays;

public class EntityManager<E> implements DbContext<E>{
    private Connection connection;

    public EntityManager(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean persist(E entity) throws IllegalAccessException {
        Field primary = getIdField(entity.getClass());
        primary.setAccessible(true);
        Object value = primary.get(entity);

        return (value != null && (int) value > 0) ? doUpdate(entity, primary) : doInsert(entity, primary);
    }


    @Override
    public Iterable<E> find(Class<E> table) {
        return null;
    }

    @Override
    public Iterable<E> find(Class<E> table, String where) {
        return null;
    }

    @Override
    public E findFirst(Class<E> table) {
        return null;
    }

    @Override
    public E findFirst(Class<E> table, String where) {
        return null;
    }

    //Utility methods

    private boolean doUpdate(E entity, Field primary) {
        return false;
    }

    private boolean doInsert(E entity, Field primary) {
        return false;
    }
    private Field getIdField(Class entity){
        return Arrays.stream(entity.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Entity does not have a primary key."));
    }

    private String getTableName(Class<?> entity){
        Entity entityAnnotation = entity.getAnnotation(Entity.class);
        if(entityAnnotation != null && entityAnnotation.name() != null && entityAnnotation.name().length() > 0){
            return entityAnnotation.name();
        }else {
            return entity.getSimpleName();
        }
    }
}
