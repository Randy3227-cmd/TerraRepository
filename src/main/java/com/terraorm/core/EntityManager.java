package com.terraorm.core;

import com.terraorm.annotation.*;
import com.terraorm.tools.Db;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class EntityManager {

    public <T> T save(T entity) throws Exception {

        Class<?> clazz = entity.getClass();
        String table = clazz.getAnnotation(Table.class).name();

        List<Field> fields = getColumnFields(clazz);

        StringBuilder sql = new StringBuilder("INSERT INTO " + table + " (");
        StringBuilder values = new StringBuilder("VALUES (");

        for (Field f : fields) {
            sql.append(f.getAnnotation(Column.class).name()).append(",");
            values.append("?,");
        }

        sql.deleteCharAt(sql.length() - 1).append(") ");
        values.deleteCharAt(values.length() - 1).append(")");

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql + values.toString())) {

            int i = 1;
            for (Field f : fields) {
                f.setAccessible(true);
                ps.setObject(i++, f.get(entity));
            }

            ps.executeUpdate();
        }

        return entity;
    }

    public <T, ID> T findById(Class<T> clazz, ID id) throws Exception {

        String table = clazz.getAnnotation(Table.class).name();
        Field pk = getPrimaryKey(clazz);

        String sql = "SELECT * FROM " + table + " WHERE "
                + pk.getAnnotation(Column.class).name() + " = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResult(clazz, rs);
            }
        }

        return null;
    }

    public <T> List<T> findAll(Class<T> clazz) throws Exception {

        List<T> list = new ArrayList<>();
        String table = clazz.getAnnotation(Table.class).name();

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResult(clazz, rs));
            }
        }

        return list;
    }

    private <T> T mapResult(Class<T> clazz, ResultSet rs) throws Exception {
        T obj = clazz.getDeclaredConstructor().newInstance();

        for (Field f : getColumnFields(clazz)) {
            f.setAccessible(true);
            f.set(obj, rs.getObject(f.getAnnotation(Column.class).name()));
        }

        return obj;
    }

    private List<Field> getColumnFields(Class<?> clazz) {
        List<Field> list = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(Column.class)) list.add(f);
        }
        return list;
    }

    private Field getPrimaryKey(Class<?> clazz) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(PrimaryKey.class)) return f;
        }
        throw new RuntimeException("No primary key defined");
    }

    public <T> T update(T entity) throws Exception {

        Class<?> clazz = entity.getClass();
        String table = clazz.getAnnotation(Table.class).name();

        Field pk = getPrimaryKey(clazz);
        pk.setAccessible(true);

        List<Field> fields = getColumnFields(clazz);

        StringBuilder sql = new StringBuilder("UPDATE " + table + " SET ");

        for (Field f : fields) {
            if (f.equals(pk)) continue;
            sql.append(f.getAnnotation(Column.class).name()).append("=?,");
        }

        sql.deleteCharAt(sql.length() - 1);
        sql.append(" WHERE ")
        .append(pk.getAnnotation(Column.class).name())
        .append("=?");

        try (Connection conn = Db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int i = 1;
            for (Field f : fields) {
                if (f.equals(pk)) continue;
                f.setAccessible(true);
                ps.setObject(i++, f.get(entity));
            }

            ps.setObject(i, pk.get(entity));
            ps.executeUpdate();
        }

        return entity;
    }

    public <T, ID> void deleteById(Class<T> clazz, ID id) throws Exception {

        String table = clazz.getAnnotation(Table.class).name();
        Field pk = getPrimaryKey(clazz);

        String sql = "DELETE FROM " + table +
                " WHERE " + pk.getAnnotation(Column.class).name() + "=?";

        try (Connection conn = Db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);
            ps.executeUpdate();
        }
    }

    public <T> void delete(T entity) throws Exception {
        Field pk = getPrimaryKey(entity.getClass());
        pk.setAccessible(true);
        deleteById(entity.getClass(), pk.get(entity));
    }

    public <T> T saveOrUpdate(T entity) throws Exception {

        Field pk = getPrimaryKey(entity.getClass());
        pk.setAccessible(true);

        Object id = pk.get(entity);

        if (id == null || findById(entity.getClass(), id) == null) {
            return save(entity);
        } else {
            return update(entity);
        }
    }

    public <T> List<T> findWhere(Class<T> clazz, String where, Object... params) throws Exception {

        List<T> list = new ArrayList<>();
        String table = clazz.getAnnotation(Table.class).name();

        String sql = "SELECT * FROM " + table + " WHERE " + where;

        try (Connection conn = Db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapResult(clazz, rs));
            }
        }

        return list;
    }

    public <T> List<T> findPage(Class<T> clazz, int offset, int limit) throws Exception {

        String table = clazz.getAnnotation(Table.class).name();

        String sql = "SELECT * FROM " + table + " LIMIT ? OFFSET ?";

        List<T> list = new ArrayList<>();

        try (Connection conn = Db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapResult(clazz, rs));
            }
        }

        return list;
    }

}
