package fi.metropolia.movesense.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Insert
    suspend fun addMeasurementInformation(vararg measurementInformation: MeasurementInformation)

    @Query("DELETE FROM measurement_information WHERE id = :id")
    suspend fun deleteMeasurementInformation(id: Long)

    @Query("SELECT * FROM measurement_information")
    fun getMeasurementInformation(): Flow<List<MeasurementInformation>>

    @Query("SELECT * FROM measurement_accelerometer WHERE information_id = :informationId")
    suspend fun getAccelerometerData(informationId: Long): List<MeasurementAccelerometer>

    @Query("SELECT * FROM measurement_gyroscope WHERE information_id = :informationId")
    suspend fun getGyroscopeData(informationId: Long): List<MeasurementGyroscope>

    @Query("SELECT * FROM measurement_magnetometer WHERE information_id = :informationId")
    suspend fun getMagnetometerData(informationId: Long): List<MeasurementMagnetometer>
}

private const val DATABASE_NAME = "movesense"

@Database(
    entities = [
        MeasurementInformation::class,
        MeasurementAccelerometer::class,
        MeasurementGyroscope::class,
        MeasurementMagnetometer::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MovesenseDatabase : RoomDatabase() {
    abstract fun measurementDao(): MeasurementDao

    companion object {
        @Volatile
        private var INSTANCE: MovesenseDatabase? = null

        fun getInstance(context: Context): MovesenseDatabase = INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context,
                MovesenseDatabase::class.java,
                DATABASE_NAME
            )
                .build()
            INSTANCE = instance
            instance
        }
    }
}