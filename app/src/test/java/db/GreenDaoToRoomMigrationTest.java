package db;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import wangdaye.com.geometricweather.db.GreenDaoToRoomMigration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GreenDaoToRoomMigrationTest {

    @Test
    public void mapsGreenDaoPrimaryKeyColumnNames() {
        Set<String> greenDao = new HashSet<>();
        greenDao.add("_id");
        greenDao.add("CITY_ID");
        assertEquals("_id", GreenDaoToRoomMigration.INSTANCE.resolveSourceColumn(greenDao, "ID"));

        Set<String> roomStyle = new HashSet<>();
        roomStyle.add("ID");
        assertEquals("ID", GreenDaoToRoomMigration.INSTANCE.resolveSourceColumn(roomStyle, "ID"));
    }

    @Test
    public void mapsColumnsCaseInsensitively() {
        Set<String> columns = new HashSet<>();
        columns.add("formatted_id");
        assertEquals("formatted_id",
                GreenDaoToRoomMigration.INSTANCE.resolveSourceColumn(columns, "FORMATTED_ID"));
        assertNull(GreenDaoToRoomMigration.INSTANCE.resolveSourceColumn(columns, "MISSING"));
    }
}
