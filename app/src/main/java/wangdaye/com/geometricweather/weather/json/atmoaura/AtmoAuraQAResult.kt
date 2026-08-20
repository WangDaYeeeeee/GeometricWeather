@file:kotlinx.serialization.UseSerializers(wangdaye.com.geometricweather.common.json.GsonCompatibleDateSerializer::class)
package wangdaye.com.geometricweather.weather.json.atmoaura

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class AtmoAuraQAResult(
    @JvmField @SerialName("bon_geste") val advice: Advice? = null,
    @JvmField @SerialName("indices") val indexs: MultiDaysIndexs? = null,
    @JvmField @SerialName("dispositif") val measure: Measure? = null
) {
    @Serializable
    class Advice(
        @JvmField @SerialName("contextes") val contextList: List<AdviceContext>? = null,
        @JvmField @SerialName("visuel") val iconUrl: String? = null,
        @JvmField @SerialName("message_long") val messageLong: String? = null,
        @JvmField @SerialName("message_court") val messageShort: String? = null,
        @JvmField @SerialName("saison") val season: String? = null,
        @JvmField @SerialName("type") val type: String? = null
    ) {
        @Serializable
        class AdviceContext(
            @JvmField @SerialName("niveau") val level: String? = null,
            @JvmField @SerialName("population") val population: Array<String>? = null
        )
    }

    @Serializable
    class MultiDaysIndexs(
        @JvmField @SerialName("indice_j") val today: MultiIndex? = null,
        @JvmField @SerialName("indice_j+1") val tomorrow: MultiIndex? = null,
        @JvmField @SerialName("indice_j+2") val inTwoDays: MultiIndex? = null,
        @JvmField @SerialName("indice_j-1") val yesterday: MultiIndex? = null
    ) {
        @Serializable
        class MultiIndex(
            @JvmField @SerialName("precision") val accuracy: String? = null,
            @JvmField @SerialName("indice_multipolluant") val aggregatedIndex: Index? = null,
            @JvmField @SerialName("date") val date: Date? = null,
            @JvmField @SerialName("sous_indice_no2") val no2: Index? = null,
            @JvmField @SerialName("sous_indice_o3") val o3: Index? = null,
            @JvmField @SerialName("sous_indice_pm10") val pm10: Index? = null,
            @JvmField @SerialName("type") val type: String? = null
        ) {
            @Serializable
            class Index(
                @JvmField @SerialName("couleur_html") val color: String? = null,
                @JvmField @SerialName("qualificatif") val quali: String? = null,
                @JvmField @SerialName("valeur") val `val`: Double = 0.0
            )
        }
    }

    @Serializable
    class Measure(
        @JvmField @SerialName("commentaire") val comment: String? = null,
        @JvmField @SerialName("date_fin") val endDate: Date? = null,
        @JvmField @SerialName("niveau") val level: String? = null,
        @JvmField @SerialName("date_modification") val modificationDate: Date? = null,
        @JvmField @SerialName("nom_procedure") val name: String? = null,
        @JvmField @SerialName("polluant") val pollutant: String? = null,
        @JvmField @SerialName("date_debut") val startDate: Date? = null,
        @JvmField @SerialName("seuil") val threshold: String? = null,
        @JvmField @SerialName("zone") val zone: String? = null
    )
}
