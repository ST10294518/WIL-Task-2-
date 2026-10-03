package co.za.rhythmandflow

import java.time.LocalDate
object RecordValidation {
    fun validDate(value:String):Boolean=try {LocalDate.parse(value)<=LocalDate.now()}catch(_:Exception){false}
    fun weight(value:String):Double?=value.trim().replace(',','.').toDoubleOrNull()?.takeIf{it.isFinite()&&it>0&&it<=1000}
}
