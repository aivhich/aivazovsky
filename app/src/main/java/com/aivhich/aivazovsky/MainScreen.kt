package com.aivhich.aivazovsky

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.aivhich.aivazovsky.states.SendingState
import kotlin.coroutines.coroutineContext

@Composable
fun MainScreen(vm:MainVM, connectStatus: MutableState<String>){
    var sendingDataState  by remember { mutableStateOf(SendingState.NOT_SENDING) }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImage = uri
    }

    val coroutineScope = rememberCoroutineScope()
    val painter = rememberImagePainter(
        ImageRequest.Builder(LocalContext.current)
        .data(selectedImage)
        .size(coil.size.OriginalSize)
        .build()
    )
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(20.dp, 10.dp)
                        .fillMaxWidth(0.7f)
                        .align(Alignment.TopCenter)
                        .background(
                            MaterialTheme.colorScheme.background,
                            RoundedCornerShape(25.dp)
                        )
                        .height(36.dp)
                ) {
                    Text(
                        text = connectStatus.value,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W800,
                        textAlign = TextAlign.Center,
                        lineHeight = 5.sp
                    )
                }
                Text(
                    text = "Добро пожаловать!",
                    fontSize = 30.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomStart),
                    fontWeight = FontWeight.W700
                )
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = "Что будем рисовать сегодня?",
                fontSize = 20.sp,
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.W600,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp, 2.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            if(selectedImage!=null) {
                Image(
                    painter = painter,
                    contentScale = ContentScale.FillWidth,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                        .fillMaxWidth()
                        .background(Color.Black)
                )
            }
            Button(
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                colors = ButtonColors(contentColor = MaterialTheme.colorScheme.background,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    disabledContentColor = MaterialTheme.colorScheme.secondary,
                    disabledContainerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .wrapContentSize()
                    .fillMaxWidth(0.95f)
                    .padding(10.dp)
            ) {
                Text(text = "Выбрать изображение")
            }
            if(selectedImage!=null) {

                Button(
                    onClick = {
                        vm.sendToPrint(selectedImage!!)
                    },
                    colors = ButtonColors(contentColor = MaterialTheme.colorScheme.background,
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .wrapContentSize()
                        .fillMaxWidth(0.95f)
                        .padding(10.dp)
                ) {
                    Text(text = "Отправить на печать")
                }
            }
            Spacer(Modifier.height(50.dp))
        }
    }
}