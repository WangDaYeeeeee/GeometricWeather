package db

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import wangdaye.com.geometricweather.db.GreenDaoToRoomMigration

class GreenDaoToRoomMigrationTest {

    @Test
    fun mapsGreenDaoPrimaryKeyColumnNames() {
        val greenDao = HashSet<String>()
        greenDao.add("_id")
        greenDao.add("CITY_ID")
        assertEquals("_id", GreenDaoToRoomMigration.resolveSourceColumn(greenDao, "ID"))

        val roomStyle = HashSet<String>()
        roomStyle.add("ID")
        assertEquals("ID", GreenDaoToRoomMigration.resolveSourceColumn(roomStyle, "ID"))
    }

    @Test
    fun mapsColumnsCaseInsensitively() {
        val columns = HashSet<String>()
        columns.add("formatted_id")
        assertEquals(
            "formatted_id",
            GreenDaoToRoomMigration.resolveSourceColumn(columns, "FORMATTED_ID")
        )
        assertNull(GreenDaoToRoomMigration.resolveSourceColumn(columns, "MISSING"))
    }
}
