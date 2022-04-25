package fi.metropolia.movesense.model

data class MovesenseDataLoggerConfig(
    val config: Config
) {
    data class Config(
        val dataEntries: DataEntries
    )

    data class DataEntries(
        val dataEntry: List<DataEntry>
    )

    data class DataEntry(
        val path: String
    )
}
