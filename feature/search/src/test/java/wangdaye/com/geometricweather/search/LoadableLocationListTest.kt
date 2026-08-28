package wangdaye.com.geometricweather.search

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LoadableLocationListTest {

    @Test
    fun statusValuesMatchUiContract() {
        assertEquals(
            LoadableLocationList.Status.SUCCESS,
            LoadableLocationList(emptyList(), LoadableLocationList.Status.SUCCESS).status
        )
        assertEquals(
            LoadableLocationList.Status.LOADING,
            LoadableLocationList(emptyList(), LoadableLocationList.Status.LOADING).status
        )
        assertEquals(
            LoadableLocationList.Status.ERROR,
            LoadableLocationList(emptyList(), LoadableLocationList.Status.ERROR).status
        )
    }
}
