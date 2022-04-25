package fi.metropolia.movesense.database

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "measurement_information")
data class MeasurementInformation(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val date: Long,
    val description: String? = null
)

@Entity(
    tableName = "measurement_accelerometer",
    foreignKeys = [
        ForeignKey(
            entity = MeasurementInformation::class,
            parentColumns = ["id"],
            childColumns = ["information_id"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ],
    indices = [Index("information_id")]
)
data class MeasurementAccelerometer(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo(name = "information_id")
    val informationId: Long,
    val x: Double,
    val y: Double,
    val z: Double
)

@Entity(
    tableName = "measurement_gyroscope",
    foreignKeys = [
        ForeignKey(
            entity = MeasurementInformation::class,
            parentColumns = ["id"],
            childColumns = ["information_id"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ],
    indices = [Index("information_id")]
)
data class MeasurementGyroscope(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo(name = "information_id")
    val informationId: Long,
    val x: Double,
    val y: Double,
    val z: Double
)

@Entity(
    tableName = "measurement_magnetometer",
    foreignKeys = [
        ForeignKey(
            entity = MeasurementInformation::class,
            parentColumns = ["id"],
            childColumns = ["information_id"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ],
    indices = [Index("information_id")]
)
data class MeasurementMagnetometer(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo(name = "information_id")
    val informationId: Long,
    val x: Double,
    val y: Double,
    val z: Double
)