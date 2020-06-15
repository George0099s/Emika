//package com.emika.app.data
//
//import android.app.Application
//import android.content.Context
//import android.content.SharedPreferences
//import androidx.room.Room
//import com.emika.app.data.db.AppDatabase
//import com.emika.app.data.db.migration.Migration
//import com.emika.app.di.UserComponent
//import com.emika.app.di.UserModule
//import com.emika.app.presentation.utils.Constants
//import com.github.nkzawa.socketio.client.Manager
//import com.github.nkzawa.socketio.client.Socket
//import java.net.URI
//import java.net.URISyntaxException
//
//class EmikaApplication : Application() {
//    var sharedPreferences: SharedPreferences? = null
//
//    var database: AppDatabase? = null
//
//    var component: UserComponent? = null
//
//    var manager: Manager? = null
//
//    var socket: Socket? = null
//
//
//    override fun onCreate() {
//        super.onCreate()
//        instance = this
//        sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE)
//        database = Room.databaseBuilder(this, AppDatabase::class.java, "emika_db")
//                .addMigrations(Migration.MIGRATION_1_2)
//                .addMigrations(Migration.MIGRATION_2_3)
//                .addMigrations(Migration.MIGRATION_3_4)
//                .addMigrations(Migration.MIGRATION_4_5)
//                .addMigrations(Migration.MIGRATION_5_6)
//                .addMigrations(Migration.MIGRATION_6_7)
//                .addMigrations(Migration.MIGRATION_7_8)
//                .addMigrations(Migration.MIGRATION_8_9)
//                .addMigrations(Migration.MIGRATION_9_10)
//                .addMigrations(Migration.MIGRATION_10_11)
//                .addMigrations(Migration.MIGRATION_11_12) //                .fallbackToDestructiveMigration()
//                .build()
//        component = DaggerUserComponent
//                .builder()
//                .userModule(UserModule())
//                .build()
//        socket = manager!!.socket("/all")
//        socket?.connect()
//    }
//
//    companion object {
//        lateinit var instance: EmikaApplication
//    }
//
//    init {
//        manager = try {
//            Manager(URI(Constants.BASIC_URL))
//        } catch (e: URISyntaxException) {
//            throw RuntimeException(e)
//        }
//    }
//}