package com.yogadimas.tokoalattulissumberjaya.pdf

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.FileProvider
import com.yogadimas.tokoalattulissumberjaya.R
import com.yogadimas.tokoalattulissumberjaya.helper.formatRupiah
import com.yogadimas.tokoalattulissumberjaya.model.Pemesanan
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.*

class PdfConverter {
    fun createPdf(
        context: Context,
        pemesananDetail: Pemesanan,
        activity: Activity
    ) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.activity_cetak_pemesanan, null)

        // val adapter = MarksRecyclerAdapter(pdfDetails.subjectDetailsList)
        // val bitmap = createBitmapFromView(context, view, pemesananDetail, adapter, activity)
        val bitmap = createBitmapFromView(context, view, pemesananDetail, activity)
        convertBitmapToPdf(bitmap, activity, pemesananDetail)
    }

    private fun createBitmapFromView(
        context: Context,
        view: View,
        pemesananDetail: Pemesanan,
        // adapter: MarksRecyclerAdapter,
        activity: Activity
    ): Bitmap {
        val kodePesanan = view.findViewById<TextView>(R.id.tvKodePesanan)
        val kodePemasok = view.findViewById<TextView>(R.id.tvKodePemasok)
        val namaPemasok = view.findViewById<TextView>(R.id.tvNamaPemasok)
        val kodeBarang = view.findViewById<TextView>(R.id.tvKodeBarang)
        val namaBarang = view.findViewById<TextView>(R.id.tvNamaBarang)
        val merk = view.findViewById<TextView>(R.id.tvMerk)
        val stok = view.findViewById<TextView>(R.id.tvStok)
        val satuan = view.findViewById<TextView>(R.id.tvSatuan)
        val harga = view.findViewById<TextView>(R.id.tvHarga)
        val jumlah = view.findViewById<TextView>(R.id.tvJumlah)
        val totalHarga = view.findViewById<TextView>(R.id.tvTotalHarga)
        val tglPesan = view.findViewById<TextView>(R.id.tvTglPesan)
        val btnCetak = view.findViewById<TextView>(R.id.btnCetak)
        btnCetak.visibility = View.GONE
        // val recyclerView = view.findViewById<RecyclerView>(R.id.pdf_marks)
        kodePesanan.text = pemesananDetail.kodePesanan.toString()
        kodePemasok.text = pemesananDetail.kodePemasok
        namaPemasok.text = pemesananDetail.namaPemasok
        kodeBarang.text = pemesananDetail.kodeBarang
        namaBarang.text = pemesananDetail.namaBarang
        merk.text = pemesananDetail.merk
        stok.text = pemesananDetail.stok.toString()
        satuan.text = pemesananDetail.satuan
        harga.text = pemesananDetail.harga.toString().formatRupiah()
        jumlah.text = pemesananDetail.jumlah.toString()
        totalHarga.text = pemesananDetail.totalHarga.toString().formatRupiah()
        tglPesan.text = pemesananDetail.tglPesan

        return createBitmap(context, view, activity)
    }

    private fun createBitmap(
        context: Context,
        view: View,
        activity: Activity,
    ): Bitmap {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.getRealMetrics(displayMetrics)
            displayMetrics.densityDpi
        } else {
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        view.measure(
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.widthPixels, View.MeasureSpec.EXACTLY
            ),
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.heightPixels, View.MeasureSpec.EXACTLY
            )
        )
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return Bitmap.createScaledBitmap(bitmap, 1080, 1920, true)

    }

    private fun convertBitmapToPdf(bitmap: Bitmap, context: Context, pemesananDetail: Pemesanan) {
        val nameFile = "Bukti Pemesanan ${pemesananDetail.kodePesanan.toString()}.pdf"
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0F, 0F, null)
        pdfDocument.finishPage(page)
        val filePath = File(context.getExternalFilesDir(null), nameFile)
        pdfDocument.writeTo(FileOutputStream(filePath))
        pdfDocument.close()
        renderPdf(context, filePath)
    }



    private fun renderPdf(context: Context, filePath: File) {
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            filePath
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/pdf")

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {

        }
    }

}