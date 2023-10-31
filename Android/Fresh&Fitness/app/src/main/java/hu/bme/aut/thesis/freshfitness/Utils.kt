package hu.bme.aut.thesis.freshfitness

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import hu.bme.aut.thesis.freshfitness.persistence.model.RunCheckpointEntity
import java.io.File
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.Date
import java.util.concurrent.TimeUnit

fun List<RunCheckpointEntity>.calculateDistanceInMeters() : Double {
    var distance = 0.0
    val points = this.map { LatLng(it.latitude, it.longitude) }
    for (i in 1 until points.size)
        distance += SphericalUtil.computeDistanceBetween(points[i - 1], points[i])
    return distance
}

fun calculateElapsedTime(start: Long, end: Long) : Long =
    TimeUnit.MILLISECONDS.toSeconds(end - start)

fun setCustomMapIcon(message: String): BitmapDescriptor {
    val height = 150f
    val widthPadding = 80.dp.value
    val width = paintTextWhite.measureText(message, 0, message.length) + widthPadding
    val roundStart = height/3
    val path = Path().apply {
        arcTo(0f, 0f,
            roundStart * 2, roundStart * 2,
            -90f, -180f, true)
        lineTo(width/2 - roundStart / 2, height * 2/3)
        lineTo(width/2, height)
        lineTo(width/2 + roundStart / 2, height * 2/3)
        lineTo(width - roundStart, height * 2/3)
        arcTo(width - roundStart * 2, 0f,
            width, height * 2/3,
            90f, -180f, true)
        lineTo(roundStart, 0f)
    }

    val bm = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bm)
    canvas.drawPath(path, paintBlackFill)
    canvas.drawPath(path, paintWhite)
    canvas.drawText(message, width/2, height * 2/3 * 2/3, paintTextWhite)

    return BitmapDescriptorFactory.fromBitmap(bm)
}

fun decodeJWT(accessToken: String): String {
    val decodedBytes = Base64.getDecoder().decode(accessToken)
    return String(decodedBytes, Charset.defaultCharset())
}

@SuppressLint("SimpleDateFormat")
fun parseDateToTimeSince(dateStr: String): String {
    val today = LocalDate.now()
    val yesterday = LocalDate.now().minusDays(1)
    val aWeekBefore = LocalDate.now().minusWeeks(1)
    val date = LocalDate.parse(dateStr.take(10))

    return if (date.equals(today))
        LocalDateTime.parse(dateStr.take(16)).format(DateTimeFormatter.ofPattern("HH:mm"))
    else if (date.equals(yesterday))
        "Yesterday, " + LocalDateTime.parse(dateStr.take(16)).format(DateTimeFormatter.ofPattern("HH:mm"))
    else if (date.isAfter(aWeekBefore))
        LocalDateTime.parse(dateStr.take(16)).format(DateTimeFormatter.ofPattern("EEEE HH:mm"))
    else
        LocalDateTime.parse(dateStr.take(16)).format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))
}

@SuppressLint("SimpleDateFormat")
fun parseDateToString(dateStr: String): String {
    return LocalDateTime.parse(dateStr).format(DateTimeFormatter.ofPattern("MMM dd"))
}

@SuppressLint("SimpleDateFormat")
fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
}

val paintBlackFill = Paint().apply {
    style = Paint.Style.STROKE
    strokeCap = Paint.Cap.ROUND
    strokeJoin = Paint.Join.ROUND
    isAntiAlias = true
    color = Color.DKGRAY
    style = Paint.Style.FILL
    textAlign = Paint.Align.CENTER
    textSize = 30.dp.value
}

val paintTextWhite = Paint().apply {
    strokeCap = Paint.Cap.ROUND
    strokeJoin = Paint.Join.ROUND
    isAntiAlias = true
    color = Color.WHITE
    textAlign = Paint.Align.CENTER
    strokeWidth = 6.dp.value
    textSize = 48.dp.value
}

val paintWhite = Paint().apply {
    style = Paint.Style.STROKE
    strokeCap = Paint.Cap.ROUND
    strokeJoin = Paint.Join.ROUND
    isAntiAlias = true
    color = Color.WHITE
    strokeWidth = 6.dp.value
}