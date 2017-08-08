package com.yc.english.base.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.yc.english.read.model.domain.CourseVersionInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "COURSE_VERSION_INFO".
*/
public class CourseVersionInfoDao extends AbstractDao<CourseVersionInfo, Long> {

    public static final String TABLENAME = "COURSE_VERSION_INFO";

    /**
     * Properties of entity CourseVersionInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CourseVersionId = new Property(1, String.class, "courseVersionId", false, "COURSE_VERSION_ID");
        public final static Property CourseVersionName = new Property(2, String.class, "courseVersionName", false, "COURSE_VERSION_NAME");
        public final static Property Type = new Property(3, int.class, "Type", false, "TYPE");
    }


    public CourseVersionInfoDao(DaoConfig config) {
        super(config);
    }
    
    public CourseVersionInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"COURSE_VERSION_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"COURSE_VERSION_ID\" TEXT," + // 1: courseVersionId
                "\"COURSE_VERSION_NAME\" TEXT," + // 2: courseVersionName
                "\"TYPE\" INTEGER NOT NULL );"); // 3: Type
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"COURSE_VERSION_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, CourseVersionInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String courseVersionId = entity.getCourseVersionId();
        if (courseVersionId != null) {
            stmt.bindString(2, courseVersionId);
        }
 
        String courseVersionName = entity.getCourseVersionName();
        if (courseVersionName != null) {
            stmt.bindString(3, courseVersionName);
        }
        stmt.bindLong(4, entity.getType());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, CourseVersionInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String courseVersionId = entity.getCourseVersionId();
        if (courseVersionId != null) {
            stmt.bindString(2, courseVersionId);
        }
 
        String courseVersionName = entity.getCourseVersionName();
        if (courseVersionName != null) {
            stmt.bindString(3, courseVersionName);
        }
        stmt.bindLong(4, entity.getType());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public CourseVersionInfo readEntity(Cursor cursor, int offset) {
        CourseVersionInfo entity = new CourseVersionInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // courseVersionId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // courseVersionName
            cursor.getInt(offset + 3) // Type
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, CourseVersionInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCourseVersionId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCourseVersionName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setType(cursor.getInt(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(CourseVersionInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(CourseVersionInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(CourseVersionInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
