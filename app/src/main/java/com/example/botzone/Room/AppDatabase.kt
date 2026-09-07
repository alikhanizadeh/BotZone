package com.example.botzone.Room



import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room
import com.example.botzone.Room.Conferences.ConferenceDao
import com.example.botzone.Room.Conferences.ConferenceEntity
import com.example.botzone.Room.Order.OrderDao
import com.example.botzone.Room.Order.OrderEntity
import com.example.botzone.Room.Payment.PaymentDao
import com.example.botzone.Room.Payment.PaymentEntity
import com.example.botzone.Room.Registration.RegistrationDao
import com.example.botzone.Room.Registration.RegistrationEntity
import com.example.botzone.Room.Store.ProductDao
import com.example.botzone.Room.Store.ProductEntity

@Database(entities = [ProductEntity::class,OrderEntity::class, ConferenceEntity::class, RegistrationEntity::class,PaymentEntity ::class], version = 9, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun orderDao() : OrderDao
    abstract fun conferenceDao(): ConferenceDao
    abstract fun registrationDao(): RegistrationDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "robotics_store_db"
                )
                    .fallbackToDestructiveMigration() // اگر نسخه تغییر کرد DB پاک و دوباره ساخته شود
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
