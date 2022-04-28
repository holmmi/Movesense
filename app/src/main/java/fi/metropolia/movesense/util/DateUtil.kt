package fi.metropolia.movesense.util

import java.text.DateFormat
import java.util.*

class DateUtil {
    companion object {
        fun getFormattedDate(date: Long): String {
            val dateFormatter =
                DateFormat.getDateTimeInstance(DateFormat.DATE_FIELD, DateFormat.SHORT)
            return dateFormatter.format(Date(date))
        }
    }
}