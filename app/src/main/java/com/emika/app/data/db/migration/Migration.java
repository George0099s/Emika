package com.emika.app.data.db.migration;

import androidx.annotation.NonNull;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration {
    public static final androidx.room.migration.Migration MIGRATION_1_2 = new androidx.room.migration.Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Task` (`id` TEXT NOT NULL, `name` TEXT, `order` INTEGER, `description` TEXT, `type` TEXT, `status` TEXT, `assignee` TEXT, `priority` TEXT, `createdBy` TEXT, `planDate`" +
                    " TEXT, `planEmika` INTEGER, `planPeriod` TEXT, `planTime` TEXT," +
                    " `planOrder` INTEGER, `deadlineDate` TEXT, `deadlineEmika` INTEGER, `deadlinePeriod` TEXT, `deadlineTime` TEXT," +
                    " `duration` INTEGER, `durationActual` INTEGER, `epicLinksEmika` INTEGER, `companyId` TEXT, `projectId` TEXT, `updatedAt` TEXT, `createdAt` TEXT, `parentTaskId` TEXT, `sectionId` TEXT, `durationLogged` TEXT, PRIMARY KEY(`id`))");
        }
    };
    public static final androidx.room.migration.Migration MIGRATION_2_3 = new androidx.room.migration.Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `User` (`id` TEXT NOT NULL, `token` TEXT,`status` TEXT,isAdmin INTEGER," +
                    "`inviteCode` TEXT,`activationEmailRequestedAt` TEXT,`activationCode` TEXT,`emailConfirmed` INTEGER,`email` TEXT," +
                    "`firstName` TEXT,`lastName` TEXT,`lang` TEXT, `gender` TEXT, `bio` TEXT, `jobTitle` TEXT, `isLeader` INTEGER, `isRemote` INTEGER," +
                    "`context` TEXT, `updatedAt` TEXT,     PRIMARY KEY(`id`))");
        }
    };
    public static final androidx.room.migration.Migration MIGRATION_3_4 = new androidx.room.migration.Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE user  ADD COLUMN `pictureUrl` TEXT");
        }
    };

    public static final androidx.room.migration.Migration MIGRATION_4_5 = new androidx.room.migration.Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Member`  (`id` TEXT NOT NULL, `firstName` TEXT, `lastName` TEXT, `pictureUrl` TEXT, `jobTitle` TEXT, PRIMARY KEY (`id`))");
        }
    };
}



