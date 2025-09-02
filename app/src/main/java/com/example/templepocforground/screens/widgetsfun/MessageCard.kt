package com.example.templepocforground.screens.widgetsfun

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.templepocforground.R
import com.example.templepocforground.models.AlertResponse
import com.example.templepocforground.utils.formatIsoToReadable


@Composable
fun MessageCard(message: AlertResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white)
        ),
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.alertcardiconcircle),
                    contentDescription = "Leading Image",
                    modifier = Modifier.size(32.dp)
                    // .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(

                    text = " ${message.data.Category}" + "\t" + message.data.Gender + "\t" + message.data.Injury + "\t" + message.data.Consideration + "\t" + message.data.PtNo,
                    style = MaterialTheme.typography.titleMedium,
                    // fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Text(
                text = formatIsoToReadable(message.createdDate),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End,
                color = colorResource(id = R.color.black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            )
        }
    }
}


