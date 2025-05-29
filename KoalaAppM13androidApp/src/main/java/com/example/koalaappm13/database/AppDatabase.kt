package com.example.koalaappm13.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.koalaappm13.ConsentForm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@Database(
    entities = [
        User::class,
        Session::class,
        ConsentForm::class,
        Production::class,
        ProgramaTV::class
    ],
    version = 12,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionDao
    abstract fun consentFormDao(): ConsentFormDao
    abstract fun productionDao(): ProductionDao
    abstract fun programaTvDao(): ProgramaTvDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val SALT_LENGTH = 16
        private const val ITERATIONS = 10000
        private const val KEY_LENGTH = 256

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "koala_database"
                )
                    .addMigrations(
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9,
                        MIGRATION_9_10,
                        MIGRATION_10_11,
                        MIGRATION_11_12
                    )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    UPDATE productions 
                    SET created_by = usuarioCreador 
                    WHERE created_by IS NULL
                """)
                database.execSQL("""
                    UPDATE consent_forms 
                    SET created_by = usuarioCreador 
                    WHERE created_by IS NULL
                """)
                database.execSQL("CREATE INDEX IF NOT EXISTS index_productions_created_by ON productions(created_by)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_consent_forms_created_by ON consent_forms(created_by)")
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE productions ADD COLUMN apellidos TEXT")
                database.execSQL("ALTER TABLE productions ADD COLUMN dni TEXT")
                database.execSQL("ALTER TABLE productions ADD COLUMN telefono TEXT")
                database.execSQL("ALTER TABLE productions ADD COLUMN edadCategoria TEXT")
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE productions ADD COLUMN valida INTEGER")
                database.execSQL("ALTER TABLE productions ADD COLUMN noValida INTEGER")
            }
        }

        // ✅ NUEVA MIGRACIÓN PARA grabacion y aceptaTerminos
        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE productions ADD COLUMN grabacion TEXT")
                database.execSQL("ALTER TABLE productions ADD COLUMN aceptaTerminos INTEGER NOT NULL DEFAULT 0")
            }
        }
        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE productions ADD COLUMN direccion TEXT NOT NULL DEFAULT ''")
                // Progenitor 1
                database.execSQL("ALTER TABLE productions ADD COLUMN progenitor1Nombre TEXT")
                database.execSQL("ALTER TABLE productions ADD COLUMN progenitor1Apellidos TEXT")
                database.execSQL("ALTER TABLE productions ADD COLUMN progenitor1Dni TEXT")
                database.execSQL("ALTER TABLE productions ADD COLUMN progenitor1Telefono TEXT")
                database.execSQL("ALTER TABLE productions ADD COLUMN progenitor1Direccion TEXT")
                database.execSQL("ALTER TABLE productions ADD COLUMN progenitor1Consentimiento INTEGER")

                // Progenitor 2
                database.execSQL("ALTER TABLE productions ADD COLUMN progenitor2Nombre TEXT")
                database.execSQL("ALTER TABLE productions ADD COLUMN progenitor2Apellidos TEXT")
                database.execSQL("ALTER TABLE productions ADD COLUMN progenitor2Dni TEXT")
                database.execSQL("ALTER TABLE productions ADD COLUMN progenitor2Telefono TEXT")
                database.execSQL("ALTER TABLE productions ADD COLUMN progenitor2Direccion TEXT")
                database.execSQL("ALTER TABLE productions ADD COLUMN progenitor2Consentimiento INTEGER")

                // Solo un progenitor
                database.execSQL("ALTER TABLE productions ADD COLUMN soloUnProgenitor INTEGER")
            }
        }
        private val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE productions ADD COLUMN imagenPath TEXT")
                database.execSQL("ALTER TABLE productions ADD COLUMN descripcionImagen TEXT")
            }
        }


    }

    private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    createDefaultUsers(database.userDao())
                    createDefaultProgramas(database.programaTvDao())
                }
            }
        }

        private suspend fun createDefaultUsers(userDao: UserDao) {
            val adminExists = userDao.getUserByUsername("admin")
            if (adminExists == null) {
                val salt = generateSalt()
                val adminUser = User(
                    username = "admin",
                    passwordHash = hashPassword("12345", salt),
                    email = "admin@koala.com",
                    dni = "00000000A",
                    salt = salt,
                    securityToken = generateSecurityToken(),
                    lastPasswordChange = System.currentTimeMillis(),
                    failedLoginAttempts = 0,
                    accountLocked = false,
                    accountLockedUntil = null,
                    lastLoginDate = System.currentTimeMillis(),
                    created_at = System.currentTimeMillis(),
                    role = "admin"
                )
                userDao.insertUser(adminUser)
            }

            val editorExists = userDao.getUserByUsername("aalcalde")
            if (editorExists == null) {
                val salt = generateSalt()
                val editorUser = User(
                    username = "aalcalde",
                    passwordHash = hashPassword("11223344A", salt),
                    email = "aalcalde@demo.com",
                    dni = "11223344A",
                    salt = salt,
                    securityToken = generateSecurityToken(),
                    lastPasswordChange = System.currentTimeMillis(),
                    failedLoginAttempts = 0,
                    accountLocked = false,
                    accountLockedUntil = null,
                    lastLoginDate = System.currentTimeMillis(),
                    created_at = System.currentTimeMillis(),
                    role = "editor"
                )
                userDao.insertUser(editorUser)
            }
        }

        private suspend fun createDefaultProgramas(programaDao: ProgramaTvDao) {
            programaDao.insert(ProgramaTV(nombre = "A Toda Costa"))
            programaDao.insert(ProgramaTV(nombre = "Callejeando"))
        }

        private fun generateSalt(): ByteArray {
            val random = SecureRandom()
            val salt = ByteArray(SALT_LENGTH)
            random.nextBytes(salt)
            return salt
        }

        private fun hashPassword(password: String, salt: ByteArray): String {
            val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val hash = factory.generateSecret(spec).encoded
            return hash.joinToString("") { "%02x".format(it) }
        }

        private fun generateSecurityToken(): String {
            return UUID.randomUUID().toString()
        }
    }
}
