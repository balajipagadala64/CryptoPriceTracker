package balaji.project.cryptopricetracker.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


@Composable
fun PriceHistoryChart(prices: List<Double>) {

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.text = "7 Day Price History"
            }
        },
        update = { chart ->

            val entries = prices.mapIndexed { index, price ->
                Entry(index.toFloat(), price.toFloat())
            }

            val dataSet = LineDataSet(entries, "Price")
            dataSet.color = Color.Blue.hashCode()
            dataSet.valueTextColor = Color.Black.hashCode()
            dataSet.setDrawCircles(false)
            dataSet.setDrawFilled(true)

            chart.data = LineData(dataSet)
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}
