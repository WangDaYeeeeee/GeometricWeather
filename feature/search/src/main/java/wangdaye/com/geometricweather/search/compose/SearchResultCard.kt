package wangdaye.com.geometricweather.search.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.ui.widgets.Material3CardListItem
import wangdaye.com.geometricweather.search.LocationModel

@Composable
fun SearchResultCard(
    model: LocationModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val talkBack = remember(model) {
        model.subtitle + ", " +
            context.getString(R.string.content_desc_powered_by)
                .replace("$", model.weatherSource.getVoice(context))
    }

    Material3CardListItem {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .semantics { contentDescription = talkBack }
                .clickable(onClick = onClick)
                .padding(
                    horizontal = dimensionResource(R.dimen.normal_margin),
                    vertical = dimensionResource(R.dimen.normal_margin),
                ),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = model.title,
                color = searchThemeColor(R.attr.colorTitleText),
                fontSize = dimensionResource(R.dimen.title_text_size).value.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = model.subtitle,
                color = searchThemeColor(R.attr.colorCaptionText),
                fontSize = dimensionResource(R.dimen.subtitle_text_size).value.sp,
            )
            Text(
                text = "Powered by ${model.weatherSource.sourceUrl}",
                color = Color(model.weatherSource.sourceColor),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
