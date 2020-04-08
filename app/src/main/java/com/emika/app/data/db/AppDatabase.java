package com.emika.app.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.emika.app.data.db.dao.EpicLinksDao;
import com.emika.app.data.db.dao.MemberDao;
import com.emika.app.data.db.dao.MessagesDao;
import com.emika.app.data.db.dao.ProjectDao;
import com.emika.app.data.db.dao.TaskDao;
import com.emika.app.data.db.dao.TokenDao;
import com.emika.app.data.db.dao.UserDao;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.MemberEntity;
import com.emika.app.data.db.entity.MessageEntity;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.db.entity.TaskEntity;
import com.emika.app.data.db.entity.TokenEntity;
import com.emika.app.data.db.entity.UserEntity;
import com.emika.app.di.Project;

@Database(entities = {UserEntity.class, TokenEntity.class, TaskEntity.class,
        MemberEntity.class, ProjectEntity.class, EpicLinksEntity.class, MessageEntity.class}, version = 9, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract TokenDao tokenDao();
    public abstract TaskDao taskDao();
    public abstract MemberDao memberDao();
    public abstract ProjectDao projectDao();
    public abstract EpicLinksDao epicLinksDao();
    public abstract MessagesDao messagesDao();
}