package com.example.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object IosIcons {

    val Warning: ImageVector = ImageVector.Builder(
        name = "IosWarning",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(12f, 3f)
        lineTo(21f, 19f)
        curveTo(21.5f, 19.8f, 21.2f, 21f, 20f, 21f)
        lineTo(4f, 21f)
        curveTo(2.8f, 21f, 2.5f, 19.8f, 3f, 19f)
        close()
    }.path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round
    ) {
        moveTo(12f, 9f)
        lineTo(12f, 14f)
    }.path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round
    ) {
        moveTo(12f, 17.5f)
        lineTo(12f, 17.51f)
    }.build()

    val Sun: ImageVector = ImageVector.Builder(
        name = "IosSun",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round
    ) {
        moveTo(12f, 7f)
        curveTo(14.76f, 7f, 17f, 9.24f, 17f, 12f)
        curveTo(17f, 14.76f, 14.76f, 17f, 12f, 17f)
        curveTo(9.24f, 17f, 7f, 14.76f, 7f, 12f)
        curveTo(7f, 9.24f, 9.24f, 7f, 12f, 7f)
        close()
        moveTo(12f, 1f).lineTo(12f, 3f)
        moveTo(12f, 21f).lineTo(12f, 23f)
        moveTo(4.22f, 4.22f).lineTo(5.64f, 5.64f)
        moveTo(18.36f, 18.36f).lineTo(19.78f, 19.78f)
        moveTo(1f, 12f).lineTo(3f, 12f)
        moveTo(21f, 12f).lineTo(23f, 12f)
        moveTo(4.22f, 19.78f).lineTo(5.64f, 18.36f)
        moveTo(18.36f, 5.64f).lineTo(19.78f, 4.22f)
    }.build()

    val Moon: ImageVector = ImageVector.Builder(
        name = "IosMoon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(21f, 12.79f)
        curveTo(20.85f, 12.8f, 20.7f, 12.8f, 20.55f, 12.8f)
        curveTo(14.34f, 12.8f, 11.21f, 9.67f, 11.21f, 3.45f)
        curveTo(11.21f, 3.3f, 11.21f, 3.15f, 11.22f, 3f)
        curveTo(6.75f, 3.65f, 3f, 7.35f, 3f, 12f)
        curveTo(3f, 16.97f, 7.03f, 21f, 12f, 21f)
        curveTo(16.65f, 21f, 20.35f, 17.25f, 21f, 12.79f)
        close()
    }.build()

    val Menu: ImageVector = ImageVector.Builder(
        name = "IosMenu",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(4f, 6f)
        lineTo(20f, 6f)
        moveTo(4f, 12f)
        lineTo(20f, 12f)
        moveTo(4f, 18f)
        lineTo(20f, 18f)
    }.build()

    val ChevronLeft: ImageVector = ImageVector.Builder(
        name = "IosChevronLeft",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(16f, 4f)
        lineTo(8f, 12f)
        lineTo(16f, 20f)
    }.build()

    val ChevronRight: ImageVector = ImageVector.Builder(
        name = "IosChevronRight",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(8f, 4f)
        lineTo(16f, 12f)
        lineTo(8f, 20f)
    }.build()

    val ChevronUp: ImageVector = ImageVector.Builder(
        name = "IosChevronUp",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(4f, 16f)
        lineTo(12f, 8f)
        lineTo(20f, 16f)
    }.build()

    val ChevronDown: ImageVector = ImageVector.Builder(
        name = "IosChevronDown",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(4f, 8f)
        lineTo(12f, 16f)
        lineTo(20f, 8f)
    }.build()

    val Refresh: ImageVector = ImageVector.Builder(
        name = "IosRefresh",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(18.5f, 9f)
        curveTo(17.3f, 6.2f, 14.5f, 4.5f, 11f, 4.5f)
        curveTo(6.3f, 4.5f, 2.5f, 8.3f, 2.5f, 13f)
        curveTo(2.5f, 17.7f, 6.3f, 21.5f, 11f, 21.5f)
        curveTo(15f, 21.5f, 18.2f, 18.7f, 19.2f, 15f)
    }.path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(15f, 9f)
        lineTo(19f, 9f)
        lineTo(19f, 5f)
    }.build()

    val Search: ImageVector = ImageVector.Builder(
        name = "IosSearch",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(10.5f, 4.5f)
        curveTo(13.8f, 4.5f, 16.5f, 7.2f, 16.5f, 10.5f)
        curveTo(16.5f, 13.8f, 13.8f, 16.5f, 10.5f, 16.5f)
        curveTo(7.2f, 16.5f, 4.5f, 13.8f, 4.5f, 10.5f)
        curveTo(4.5f, 7.2f, 7.2f, 4.5f, 10.5f, 4.5f)
        close()
    }.path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round
    ) {
        moveTo(15f, 15f)
        lineTo(20f, 20f)
    }.build()

    val Close: ImageVector = ImageVector.Builder(
        name = "IosClose",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(6f, 6f)
        lineTo(18f, 18f)
        moveTo(18f, 6f)
        lineTo(6f, 18f)
    }.build()

    val List: ImageVector = ImageVector.Builder(
        name = "IosList",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(8f, 6f)
        lineTo(20f, 6f)
        moveTo(8f, 12f)
        lineTo(20f, 12f)
        moveTo(8f, 18f)
        lineTo(20f, 18f)
    }.path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round
    ) {
        moveTo(4.5f, 6f)
        lineTo(4.51f, 6f)
        moveTo(4.5f, 12f)
        lineTo(4.51f, 12f)
        moveTo(4.5f, 18f)
        lineTo(4.51f, 18f)
    }.build()

    val Bookmark: ImageVector = ImageVector.Builder(
        name = "IosBookmark",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(5f, 4f)
        curveTo(5f, 3f, 6f, 2f, 7f, 2f)
        lineTo(17f, 2f)
        curveTo(18f, 2f, 19f, 3f, 19f, 4f)
        lineTo(19f, 21f)
        lineTo(12f, 17f)
        lineTo(5f, 21f)
        close()
    }.build()

    val BookmarkFilled: ImageVector = ImageVector.Builder(
        name = "IosBookmarkFilled",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        fill = SolidColor(Color.Black)
    ) {
        moveTo(5f, 4f)
        curveTo(5f, 3f, 6f, 2f, 7f, 2f)
        lineTo(17f, 2f)
        curveTo(18f, 2f, 19f, 3f, 19f, 4f)
        lineTo(19f, 21f)
        lineTo(12f, 17f)
        lineTo(5f, 21f)
        close()
    }.build()

    val Info: ImageVector = ImageVector.Builder(
        name = "IosInfo",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        // Outer Circle
        moveTo(12f, 3f)
        curveTo(7f, 3f, 3f, 7f, 3f, 12f)
        curveTo(3f, 17f, 7f, 21f, 12f, 21f)
        curveTo(17f, 21f, 21f, 17f, 21f, 12f)
        curveTo(21f, 7f, 17f, 3f, 12f, 3f)
        close()
    }.path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round
    ) {
        moveTo(12f, 11f)
        lineTo(12f, 17f)
    }.path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round
    ) {
        moveTo(12f, 7.5f)
        lineTo(12f, 7.51f)
    }.build()

    val Star: ImageVector = ImageVector.Builder(
        name = "IosStar",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(12f, 2f)
        lineTo(15f, 9f)
        lineTo(22f, 9.5f)
        lineTo(17f, 14f)
        lineTo(18.5f, 21.5f)
        lineTo(12f, 17.5f)
        lineTo(5.5f, 21.5f)
        lineTo(7f, 14f)
        lineTo(2f, 9.5f)
        lineTo(9f, 9f)
        close()
    }.build()

    val StarFilled: ImageVector = ImageVector.Builder(
        name = "IosStarFilled",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        fill = SolidColor(Color.Black)
    ) {
        moveTo(12f, 2f)
        lineTo(15f, 9f)
        lineTo(22f, 9.5f)
        lineTo(17f, 14f)
        lineTo(18.5f, 21.5f)
        lineTo(12f, 17.5f)
        lineTo(5.5f, 21.5f)
        lineTo(7f, 14f)
        lineTo(2f, 9.5f)
        lineTo(9f, 9f)
        close()
    }.build()

    val Edit: ImageVector = ImageVector.Builder(
        name = "IosEdit",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(13.5f, 5.5f)
        lineTo(18.5f, 10.5f)
        moveTo(16f, 3f)
        lineTo(21f, 8f)
        lineTo(8f, 21f)
        lineTo(3f, 21f)
        lineTo(3f, 16f)
        close()
    }.build()

    val Delete: ImageVector = ImageVector.Builder(
        name = "IosDelete",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(5f, 6f)
        lineTo(19f, 6f)
        moveTo(9f, 6f)
        lineTo(9f, 4f)
        curveTo(9f, 3.5f, 9.5f, 3f, 10f, 3f)
        lineTo(14f, 3f)
        curveTo(14.5f, 3f, 15f, 3.5f, 15f, 4f)
        lineTo(15f, 6f)
        moveTo(7f, 6f)
        lineTo(8f, 20f)
        curveTo(8.1f, 20.5f, 8.5f, 21f, 9f, 21f)
        lineTo(15f, 21f)
        curveTo(15.5f, 21f, 15.9f, 20.5f, 16f, 20f)
        lineTo(17f, 6f)
    }.build()

    val ArrowForward: ImageVector = ImageVector.Builder(
        name = "IosArrowForward",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(4f, 12f)
        lineTo(20f, 12f)
        moveTo(14f, 6f)
        lineTo(20f, 12f)
        lineTo(14f, 18f)
    }.build()

    val Send: ImageVector = ImageVector.Builder(
        name = "IosSend",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(21f, 3f)
        lineTo(3f, 10.5f)
        lineTo(10.5f, 13.5f)
        lineTo(13.5f, 21f)
        close()
        moveTo(10.5f, 13.5f)
        lineTo(21f, 3f)
    }.build()

    val CheckCircle: ImageVector = ImageVector.Builder(
        name = "IosCheckCircle",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(12f, 3f)
        curveTo(7f, 3f, 3f, 7f, 3f, 12f)
        curveTo(3f, 17f, 7f, 21f, 12f, 21f)
        curveTo(17f, 21f, 21f, 17f, 21f, 12f)
        curveTo(21f, 7f, 17f, 3f, 12f, 3f)
        close()
    }.path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(8.5f, 12.5f)
        lineTo(11f, 15f)
        lineTo(16f, 9.5f)
    }.build()

    val FavoriteBorder: ImageVector = ImageVector.Builder(
        name = "IosFavoriteBorder",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(12f, 5.5f)
        curveTo(10.5f, -0.5f, 3f, 0f, 3f, 6.5f)
        curveTo(3f, 12.5f, 11f, 18.5f, 12f, 21f)
        curveTo(13f, 18.5f, 21f, 12.5f, 21f, 6.5f)
        curveTo(21f, 0f, 13.5f, -0.5f, 12f, 5.5f)
        close()
    }.build()

    val Check: ImageVector = ImageVector.Builder(
        name = "IosCheck",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(4f, 12.5f)
        lineTo(9f, 17.5f)
        lineTo(20f, 6f)
    }.build()

    val Home: ImageVector = ImageVector.Builder(
        name = "IosHome",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(3f, 10f)
        lineTo(12f, 3f)
        lineTo(21f, 10f)
        moveTo(5f, 9.5f)
        lineTo(5f, 20.5f)
        curveTo(5f, 21f, 5.5f, 21.5f, 6f, 21.5f)
        lineTo(18f, 21.5f)
        curveTo(18.5f, 21.5f, 19f, 21f, 19f, 20.5f)
        lineTo(19f, 9.5f)
        moveTo(10f, 21.5f)
        lineTo(10f, 14.5f)
        lineTo(14f, 14.5f)
        lineTo(14f, 21.5f)
    }.build()

    val HomeFilled: ImageVector = ImageVector.Builder(
        name = "IosHomeFilled",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        fill = SolidColor(Color.Black)
    ) {
        moveTo(12f, 2f)
        lineTo(2f, 10f)
        lineTo(4f, 10f)
        lineTo(4f, 21f)
        lineTo(10f, 21f)
        lineTo(10f, 14f)
        lineTo(14f, 14f)
        lineTo(14f, 21f)
        lineTo(20f, 21f)
        lineTo(20f, 10f)
        lineTo(22f, 10f)
        close()
    }.build()

    val Book: ImageVector = ImageVector.Builder(
        name = "IosBook",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(12f, 21f)
        curveTo(9f, 17.5f, 4.5f, 17.5f, 4.5f, 17.5f)
        lineTo(4.5f, 5f)
        curveTo(4.5f, 5f, 9f, 5f, 12f, 7f)
        curveTo(15f, 5f, 19.5f, 5f, 19.5f, 5f)
        lineTo(19.5f, 17.5f)
        curveTo(19.5f, 17.5f, 15f, 17.5f, 12f, 21f)
        close()
        moveTo(12f, 7f)
        lineTo(12f, 21f)
    }.build()

    val BookFilled: ImageVector = ImageVector.Builder(
        name = "IosBookFilled",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        fill = SolidColor(Color.Black)
    ) {
        moveTo(12f, 21f)
        curveTo(9f, 17.5f, 4.5f, 17.5f, 4.5f, 17.5f)
        lineTo(4.5f, 5f)
        curveTo(4.5f, 5f, 9f, 5f, 12f, 7f)
        curveTo(15f, 5f, 19.5f, 5f, 19.5f, 5f)
        lineTo(19.5f, 17.5f)
        curveTo(19.5f, 17.5f, 15f, 17.5f, 12f, 21f)
        close()
    }.build()

    val Sparkles: ImageVector = ImageVector.Builder(
        name = "IosSparkles",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        // Star 1 (Main sparkle)
        moveTo(11f, 2f)
        lineTo(12f, 5f)
        lineTo(15f, 6f)
        lineTo(12f, 7f)
        lineTo(11f, 10f)
        lineTo(10f, 7f)
        lineTo(7f, 6f)
        lineTo(10f, 5f)
        close()
    }.path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        // Star 2 (Top right)
        moveTo(18f, 11f)
        lineTo(18.5f, 12.5f)
        lineTo(20f, 13f)
        lineTo(18.5f, 13.5f)
        lineTo(18f, 15f)
        lineTo(17.5f, 13.5f)
        lineTo(16f, 13f)
        lineTo(17.5f, 12.5f)
        close()
    }.path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        // Star 3 (Bottom left)
        moveTo(5f, 13f)
        lineTo(5.5f, 14.5f)
        lineTo(7f, 15f)
        lineTo(5.5f, 15.5f)
        lineTo(5f, 17f)
        lineTo(4.5f, 15.5f)
        lineTo(3f, 15f)
        lineTo(4.5f, 14.5f)
        close()
    }.build()

    val Person: ImageVector = ImageVector.Builder(
        name = "IosPerson",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        // Head circle
        moveTo(12f, 11f)
        curveTo(14.2f, 11f, 16f, 9.2f, 16f, 7f)
        curveTo(16f, 4.8f, 14.2f, 3f, 12f, 3f)
        curveTo(9.8f, 3f, 8f, 4.8f, 8f, 7f)
        curveTo(8f, 9.2f, 9.8f, 11f, 12f, 11f)
        close()
    }.path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        // Shoulders arc
        moveTo(4f, 21f)
        curveTo(4f, 17f, 7.5f, 14.5f, 12f, 14.5f)
        curveTo(16.5f, 14.5f, 20f, 17f, 20f, 21f)
    }.build()

    val PersonFilled: ImageVector = ImageVector.Builder(
        name = "IosPersonFilled",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        fill = SolidColor(Color.Black)
    ) {
        moveTo(12f, 12f)
        curveTo(14.76f, 12f, 17f, 9.76f, 17f, 7f)
        curveTo(17f, 4.24f, 14.76f, 2f, 12f, 2f)
        curveTo(9.24f, 2f, 7f, 4.24f, 7f, 7f)
        curveTo(7f, 9.76f, 9.24f, 12f, 12f, 12f)
        close()
        moveTo(12f, 14f)
        curveTo(7.58f, 14f, 4f, 17.58f, 4f, 22f)
        lineTo(20f, 22f)
        curveTo(20f, 17.58f, 16.42f, 14f, 12f, 14f)
        close()
    }.build()

    val Bell: ImageVector = ImageVector.Builder(
        name = "IosBell",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(12f, 2.5f)
        curveTo(10f, 2.5f, 7f, 4.5f, 7f, 9.5f)
        lineTo(7f, 14.5f)
        lineTo(5f, 16.5f)
        lineTo(19f, 16.5f)
        lineTo(17f, 14.5f)
        lineTo(17f, 9.5f)
        curveTo(17f, 4.5f, 14f, 2.5f, 12f, 2.5f)
        close()
    }.path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(10f, 17f)
        curveTo(10f, 18.5f, 11f, 20.5f, 12f, 20.5f)
        curveTo(13f, 20.5f, 14f, 18.5f, 14f, 17f)
    }.build()

    val ScalesOfJustice: ImageVector = ImageVector.Builder(
        name = "IosScalesOfJustice",
        defaultWidth = 48.dp,
        defaultHeight = 48.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(12f, 4f)
        lineTo(12f, 19f)
        moveTo(7f, 19f)
        lineTo(17f, 19f)
        moveTo(8f, 20f)
        lineTo(16f, 20f)
        moveTo(12f, 3.5f)
        curveTo(12.28f, 3.5f, 12.5f, 3.72f, 12.5f, 4f)
        curveTo(12.5f, 4.28f, 12.28f, 4.5f, 12f, 4.5f)
        curveTo(11.72f, 4.5f, 11.5f, 4.28f, 11.5f, 4f)
        curveTo(11.5f, 3.72f, 11.72f, 3.5f, 12f, 3.5f)
        close()
        moveTo(4f, 7f)
        curveTo(8f, 5.5f, 16f, 5.5f, 20f, 7f)
        moveTo(4f, 7f)
        lineTo(2f, 13f)
        moveTo(4f, 7f)
        lineTo(6f, 13f)
        moveTo(2f, 13f)
        curveTo(2f, 15f, 6f, 15f, 6f, 13f)
        close()
        moveTo(20f, 7f)
        lineTo(18f, 13f)
        moveTo(20f, 7f)
        lineTo(22f, 13f)
        moveTo(18f, 13f)
        curveTo(18f, 15f, 22f, 15f, 22f, 13f)
        close()
    }.build()

    val Flame: ImageVector = ImageVector.Builder(
        name = "LucideFlame",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(8.5f, 14.5f)
        curveTo(8.5f, 16.5f, 10f, 18f, 12f, 18f)
        curveTo(14f, 18f, 15.5f, 16.5f, 15.5f, 14.5f)
        curveTo(15.5f, 12.5f, 14f, 10.5f, 12f, 9f)
        curveTo(10f, 10.5f, 8.5f, 12.5f, 8.5f, 14.5f)
        close()
        moveTo(12f, 2f)
        curveTo(12f, 2f, 6f, 7f, 6f, 13f)
        curveTo(6f, 16.31f, 8.69f, 19f, 12f, 19f)
        curveTo(15.31f, 19f, 18f, 16.31f, 18f, 13f)
        curveTo(18f, 7f, 12f, 2f, 12f, 2f)
        close()
    }.build()

    val Zap: ImageVector = ImageVector.Builder(
        name = "LucideZap",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(13f, 2f)
        lineTo(3f, 14f)
        lineTo(12f, 14f)
        lineTo(11f, 22f)
        lineTo(21f, 10f)
        lineTo(12f, 10f)
        close()
    }.build()

    val TrendingUp: ImageVector = ImageVector.Builder(
        name = "LucideTrendingUp",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(23f, 6f)
        lineTo(13.5f, 15.5f)
        lineTo(8.5f, 10.5f)
        lineTo(1f, 18f)
        moveTo(17f, 6f)
        lineTo(23f, 6f)
        lineTo(23f, 12f)
    }.build()

    val Award: ImageVector = ImageVector.Builder(
        name = "LucideAward",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(12f, 15f)
        curveTo(13.66f, 15f, 15f, 13.66f, 15f, 12f)
        curveTo(15f, 10.34f, 13.66f, 9f, 12f, 9f)
        curveTo(10.34f, 9f, 9f, 10.34f, 9f, 12f)
        curveTo(9f, 13.66f, 10.34f, 15f, 12f, 15f)
        close()
        moveTo(8.21f, 13.89f)
        lineTo(7f, 23f)
        lineTo(12f, 20f)
        lineTo(17f, 23f)
        lineTo(15.79f, 13.88f)
    }.build()

    val BookOpen: ImageVector get() = Book

    val Shield: ImageVector = ImageVector.Builder(
        name = "LucideShield",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(12f, 22f)
        curveTo(12f, 22f, 20f, 18f, 20f, 12f)
        lineTo(20f, 5f)
        lineTo(12f, 2f)
        lineTo(4f, 5f)
        lineTo(4f, 12f)
        curveTo(4f, 18f, 12f, 22f, 12f, 22f)
        close()
    }.build()

    val Scale: ImageVector get() = ScalesOfJustice

    val PlayCircle: ImageVector = ImageVector.Builder(
        name = "LucidePlayCircle",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(12f, 22f)
        curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
        curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f)
        curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
        curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f)
        close()
        moveTo(10f, 8f)
        lineTo(16f, 12f)
        lineTo(10f, 16f)
        close()
    }.build()

    val BookMarked: ImageVector = ImageVector.Builder(
        name = "LucideBookMarked",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(4f, 19.5f)
        curveTo(4f, 18.12f, 5.12f, 17f, 6.5f, 17f)
        lineTo(20f, 17f)
        moveTo(4f, 19.5f)
        curveTo(4f, 20.88f, 5.12f, 22f, 6.5f, 22f)
        lineTo(20f, 22f)
        moveTo(4f, 19.5f)
        lineTo(4f, 4.5f)
        curveTo(4f, 3.12f, 5.12f, 2f, 6.5f, 2f)
        lineTo(20f, 2f)
        lineTo(20f, 22f)
        moveTo(10f, 2f)
        lineTo(10f, 10f)
        lineTo(13f, 7.5f)
        lineTo(16f, 10f)
        lineTo(16f, 2f)
    }.build()

    val MessageSquare: ImageVector = ImageVector.Builder(
        name = "LucideMessageSquare",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(21f, 15f)
        curveTo(21f, 16.1f, 20.1f, 17f, 19f, 17f)
        lineTo(7f, 17f)
        lineTo(3f, 21f)
        lineTo(3f, 5f)
        curveTo(3f, 3.9f, 3.9f, 3f, 5f, 3f)
        lineTo(19f, 3f)
        curveTo(20.1f, 3f, 21f, 3.9f, 21f, 5f)
        close()
    }.build()

    val User: ImageVector get() = Person

    val Lightbulb: ImageVector = ImageVector.Builder(
        name = "LucideLightbulb",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(15f, 14f)
        curveTo(15.2f, 13f, 15.7f, 12.3f, 16.5f, 11.5f)
        curveTo(17.5f, 10.6f, 18f, 9.3f, 18f, 8f)
        curveTo(18f, 4.7f, 15.3f, 2f, 12f, 2f)
        curveTo(8.7f, 2f, 6f, 4.7f, 6f, 8f)
        curveTo(6f, 9.3f, 6.5f, 10.6f, 7.5f, 11.5f)
        curveTo(8.3f, 12.3f, 8.8f, 13f, 9f, 14f)
    }.path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round
    ) {
        moveTo(9f, 18f)
        lineTo(15f, 18f)
        moveTo(10f, 22f)
        lineTo(14f, 22f)
    }.build()

    val Library: ImageVector get() = Book
    val Checkmark: ImageVector get() = Check
    val Download: ImageVector = ImageVector.Builder(
        name = "IosDownload",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(21f, 15f)
        lineTo(21f, 19f)
        curveTo(21f, 20.1f, 20.1f, 21f, 19f, 21f)
        lineTo(5f, 21f)
        curveTo(3.9f, 21f, 3f, 20.1f, 3f, 19f)
        lineTo(3f, 15f)
        moveTo(7f, 10f)
        lineTo(12f, 15f)
        lineTo(17f, 10f)
        moveTo(12f, 15f)
        lineTo(12f, 3f)
    }.build()

    val Share: ImageVector = ImageVector.Builder(
        name = "IosShare",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(4f, 12f)
        verticalLineTo(20f)
        curveTo(4f, 20.6f, 4.4f, 21f, 5f, 21f)
        horizontalLineTo(19f)
        curveTo(19.6f, 21f, 20f, 20.6f, 20f, 20f)
        verticalLineTo(12f)
        moveTo(12f, 15f)
        verticalLineTo(3f)
        moveTo(12f, 3f)
        lineTo(7f, 8f)
        moveTo(12f, 3f)
        lineTo(17f, 8f)
    }.build()

    val Document: ImageVector = ImageVector.Builder(
        name = "IosDocument",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(14f, 2f)
        lineTo(4f, 2f)
        curveTo(2.9f, 2f, 2f, 2.9f, 2f, 4f)
        lineTo(2f, 20f)
        curveTo(2f, 21.1f, 2.9f, 22f, 4f, 22f)
        lineTo(20f, 22f)
        curveTo(21.1f, 22f, 22f, 21.1f, 22f, 20f)
        lineTo(22f, 10f)
        close()
        moveTo(14f, 2f)
        lineTo(14f, 10f)
        lineTo(22f, 10f)
    }.path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round
    ) {
        moveTo(6f, 14f)
        lineTo(18f, 14f)
        moveTo(6f, 18f)
        lineTo(14f, 18f)
    }.build()
}

