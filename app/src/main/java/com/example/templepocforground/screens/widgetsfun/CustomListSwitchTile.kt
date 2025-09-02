package com.example.templepocforground.screens.widgetsfun

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.templepocforground.R
import com.example.templepocforground.services.PubSubMessageStore

@Composable
fun CustomListSwitchTile(
    imageRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    startConnection: () -> Unit,
    stopConnection: () -> Unit,
    connectingVia: String,
    receivingAlert: String,
) {
    val connectionState = PubSubMessageStore.connection
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            // .height(100.dp)
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.temple_text)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Spacer(modifier = Modifier.height(26.dp))
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Leading Image",
                    modifier = Modifier.size(36.dp),
                    // .clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontSize = 16.sp,
                )
                Text(
                    text = connectingVia,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontSize = 16.sp,
                )
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = receivingAlert,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontSize = 16.sp,
                    )
                }

            }
            val isConnected = connectionState.firstOrNull() == "CONNECTED"
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isConnected) "On Call" else "OFF Call",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isConnected, onCheckedChange = { checked ->
                            if (checked) startConnection() else stopConnection()
                        }, colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color.Green,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.LightGray
                        )
                    )
                }
            }

        }
    }
}
