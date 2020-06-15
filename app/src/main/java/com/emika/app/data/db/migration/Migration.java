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
    public static final androidx.room.migration.Migration MIGRATION_5_6 = new androidx.room.migration.Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Project`  (`id` TEXT NOT NULL, `name` TEXT, `status` TEXT, `createdBy` TEXT, `members` TEXT," +
                    "`isCompanyWide` INTEGER, `isPersonal` INTEGER, `updatedAt` TEXT, `createdAt` TEXT, `companyId` TEXT, `defaultSectionId` TEXT,`color` TEXT, PRIMARY KEY (`id`))");
        }
    };
    public static final androidx.room.migration.Migration MIGRATION_6_7 = new androidx.room.migration.Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Epic links`  (`id` TEXT NOT NULL, `name` TEXT, `status` TEXT, `createdBy` TEXT," +
                    " `updatedAt` TEXT, `createdAt` TEXT,`projectId` TEXT, `order` TEXT, `emoji` TEXT, PRIMARY KEY (`id`))");
        }
    };

    public static final androidx.room.migration.Migration MIGRATION_7_8 = new androidx.room.migration.Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Messages`  (`id` TEXT NOT NULL, `type` TEXT, `isEmika` INTEGER, `accountId` TEXT," +
                    " `text` TEXT,`createdAt` TEXT, `chainPosition` INTEGER, `delay` INTEGER," +
                    "`isPassword` INTEGER,`isSeen` INTEGER,`updatedAt` TEXT, PRIMARY KEY (`id`))");
        }
    };

    public static final androidx.room.migration.Migration MIGRATION_8_9 = new androidx.room.migration.Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE user  ADD COLUMN `unreadCount` TEXT");
        }
    };
    public static final androidx.room.migration.Migration MIGRATION_9_10 = new androidx.room.migration.Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Section`  (`id` TEXT NOT NULL, `name` TEXT, `status` TEXT, `order` INTEGER," +
                    " `projectId` TEXT,`companyId` TEXT, `updatedAt` TEXT," +
                    "`createdAt` TEXT, PRIMARY KEY (`id`))");
        }
    };
    public static final androidx.room.migration.Migration MIGRATION_10_11 = new androidx.room.migration.Migration(10, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Actual Duration`  (`id` TEXT NOT NULL, `status` TEXT, `taskId` TEXT," +
                    " `projectId` TEXT,`companyId` TEXT, `date` TEXT, `person` TEXT,`value` INTEGER, `createdAt` TEXT," +
                    "`createdBy` TEXT, PRIMARY KEY (`id`))");
        }
    };

    public static final androidx.room.migration.Migration MIGRATION_11_12 = new androidx.room.migration.Migration(11, 12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Task  ADD COLUMN `epicLinks` TEXT");
        }
    };
    public static final androidx.room.migration.Migration MIGRATION_12_13 = new androidx.room.migration.Migration(12, 13) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Comments`  (`id` TEXT NOT NULL, `companyId` TEXT, `createdAt` TEXT," +
                    " `createdBy` TEXT,`taskId` TEXT, `text` TEXT, `updatedAt` TEXT, PRIMARY KEY (`id`))");
        }
    };
}




