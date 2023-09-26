package com.hashone.commons.languages

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hashone.commons.utils.checkClickTime
import com.hashone.commons.utils.dpToPx
import kotlinx.coroutines.launch

@Composable
fun AnimatedLanguageSelection(
    languageBuilder: Language.Builder,
    languageList: java.util.ArrayList<LanguageItem>,
    initialSelectedIndex: Int,
    activity: Activity,
    onLanguageChange: (item: LanguageItem) -> Unit
) {
    var currentIndex by remember { mutableStateOf(initialSelectedIndex) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    BoxWithConstraints {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = languageBuilder.languageItemBuilder.bgColor)),
            state = listState,
        ) {
            itemsIndexed(languageList) { index: Int, item: LanguageItem ->
                Layout(
                    content = {
                        LanguageSelectionCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorResource(id = languageBuilder.languageItemBuilder.bgColor))
                                .clickable(
                                    indication = rememberRipple(),
                                    interactionSource = remember {
                                        MutableInteractionSource()
                                    }
                                ) {
                                    if (checkClickTime()) {
                                        if (currentIndex != index) {
                                            currentIndex = index
                                            onLanguageChange(item)
                                        }
                                    }
                                },
                            itemColor = colorResource(id = if (currentIndex == index) languageBuilder.languageItemBuilder.selectedColor else languageBuilder.languageItemBuilder.defaultColor),
                            languageData = item,
                            fontSize = languageBuilder.languageItemBuilder.titleSize,
                            font = FontFamily(Font(languageBuilder.languageItemBuilder.titleFont)),
                            selectedIcon = if (currentIndex == index) languageBuilder.languageItemBuilder.selectedIcon else null,
                            dividerColor = colorResource(languageBuilder.languageItemBuilder.dividerColor),
                            dividerWidth = languageBuilder.languageItemBuilder.dividerThickness,
                            languageItemBuilder = languageBuilder.languageItemBuilder
                        )
                    },
                    measurePolicy = { measures, constraints ->
                        // so it's measuring just the Box
                        val placeable = measures.first().measure(constraints)
                        layout(placeable.width, placeable.height) {
                            // Placing the Box in the right X position
                            placeable.place(dpToPx(0f).toInt(), 0)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LanguageSelectionCard(
    modifier: Modifier,
    itemColor: Color,
    languageData: LanguageItem,
    fontSize: Float,
    font: FontFamily,
    selectedIcon: Int?,
    dividerColor: Color,
    dividerWidth: Int,
    languageItemBuilder: Language.LanguageItemBuilder
) {

    Column(
        modifier = modifier,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (languageData.languageName.isNotEmpty()) {
                val mModifier = if (languageItemBuilder.paddingAll != -1) Modifier.padding(
                    languageItemBuilder.paddingAll.dp
                ) else Modifier.padding(
                    start = languageItemBuilder.paddingStart.dp,
                    top = languageItemBuilder.paddingTop.dp + if (languageData.languageOriginalName.isNullOrEmpty()) 8.dp else 0.dp,
                    end = languageItemBuilder.paddingEnd.dp,
                    bottom = languageItemBuilder.paddingBottom.dp + if (languageData.languageOriginalName.isNullOrEmpty()) 8.dp else 0.dp
                )
                Column(modifier = mModifier) {
                    Text(
                        text = languageData.languageName,
                        color = itemColor,
                        fontSize = fontSize.sp,
                        fontFamily = font,
                    )
                    if (languageData.languageOriginalName.isNotEmpty())
                        Text(
                            text = languageData.languageOriginalName,
                            color = itemColor,
                            fontSize = languageItemBuilder.originalNameSize.sp,
                            fontFamily = FontFamily(Font(languageItemBuilder.originalNameFont)),
                        )
                }

            }
            if (selectedIcon != null) {
                val mModifier = if (languageItemBuilder.iconPaddingAll != -1) Modifier.padding(
                    languageItemBuilder.iconPaddingAll.dp
                ) else Modifier.padding(
                    start = languageItemBuilder.iconPaddingStart.dp,
                    top = languageItemBuilder.iconPaddingTop.dp,
                    end = languageItemBuilder.iconPaddingEnd.dp,
                    bottom = languageItemBuilder.iconPaddingBottom.dp
                )
                Box(
                    modifier = mModifier.align(Alignment.CenterVertically)
                ) {
                    Image(
                        painter = painterResource(id = selectedIcon),
                        //TODO: Language translation require
                        contentDescription = "Icon",
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .background(dividerColor)
                .height(dividerWidth.dp)
                .fillMaxWidth()
        )
    }

}
