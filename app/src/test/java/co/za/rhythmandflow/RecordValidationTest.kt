package co.za.rhythmandflow
import org.junit.Assert.*
import org.junit.Test
class RecordValidationTest {
    @Test fun weightAcceptsDecimalComma(){assertEquals(65.5,RecordValidation.weight("65,5")!!,0.001)}
    @Test fun weightRejectsInvalidValues(){listOf("", "zero", "0", "-1", "NaN", "Infinity", "1001").forEach{assertNull(RecordValidation.weight(it))}}
    @Test fun dateRejectsImpossibleOrFutureDays(){assertFalse(RecordValidation.validDate("2026-02-30"));assertFalse(RecordValidation.validDate(java.time.LocalDate.now().plusDays(1).toString()))}
    @Test fun dateAllowsHistoricalRecord(){assertTrue(RecordValidation.validDate("2020-02-29"));assertTrue(RecordValidation.validDate(java.time.LocalDate.now().toString()))}
}
