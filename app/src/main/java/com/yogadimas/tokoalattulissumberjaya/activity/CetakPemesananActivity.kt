package com.yogadimas.tokoalattulissumberjaya.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.yogadimas.tokoalattulissumberjaya.R
import com.yogadimas.tokoalattulissumberjaya.helper.formatRupiah
import com.yogadimas.tokoalattulissumberjaya.helper.locale
import com.yogadimas.tokoalattulissumberjaya.model.Pemesanan
import com.yogadimas.tokoalattulissumberjaya.pdf.PdfConverter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class CetakPemesananActivity : AppCompatActivity() {
    private lateinit var i: Intent

    private lateinit var tvKodePesanan: TextView
    private lateinit var tvKodePemasok: TextView
    private lateinit var tvNamaPemasok: TextView
    private lateinit var tvKodeBarang: TextView
    private lateinit var tvNamaBarang: TextView
    private lateinit var tvMerk: TextView
    private lateinit var tvStok: TextView
    private lateinit var tvSatuan: TextView
    private lateinit var tvJumlah: TextView
    private lateinit var tvHarga: TextView
    private lateinit var tvTotalHarga: TextView
    private lateinit var tvTglPesan: TextView

    private lateinit var btnCetak: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_cetak_pemesanan)

        tvKodePesanan = findViewById(R.id.tvKodePesanan)
        tvKodePemasok = findViewById(R.id.tvKodePemasok)
        tvNamaPemasok = findViewById(R.id.tvNamaPemasok)
        tvKodeBarang = findViewById(R.id.tvKodeBarang)
        tvNamaBarang = findViewById(R.id.tvNamaBarang)
        tvMerk = findViewById(R.id.tvMerk)
        tvStok = findViewById(R.id.tvStok)
        tvSatuan = findViewById(R.id.tvSatuan)
        tvJumlah = findViewById(R.id.tvJumlah)
        tvHarga = findViewById(R.id.tvHarga)
        tvTotalHarga = findViewById(R.id.tvTotalHarga)
        tvTglPesan = findViewById(R.id.tvTglPesan)

        btnCetak = findViewById(R.id.btnCetak)

        i = intent

        if (i.hasExtra("detail_transaksi")) {

            if (i.getStringExtra("detail_transaksi").equals("1")) {
                detailTransaksi()
            }

        }

    }

    private fun detailTransaksi() {
        tvKodePesanan.text = i.getIntExtra("kode_pesanan", 0).toString()
        tvKodePemasok.text = i.getStringExtra("kode_pemasok")
        tvNamaPemasok.text = i.getStringExtra("nama_pemasok") ?: "Pemasok Sudah Dihapus"
        tvKodeBarang.text = i.getStringExtra("kode_barang")


        tvNamaBarang.text = i.getStringExtra("nama_barang") ?: "Barang Sudah Dihapus"

        tvMerk.text = i.getStringExtra("merk")
        tvStok.text = i.getIntExtra("stok", 0).toString()
        tvSatuan.text = i.getStringExtra("satuan") ?: "Barang Sudah Dihapus"
        tvJumlah.text = i.getIntExtra("jumlah", 0).toString()

        val harga = i.getIntExtra("harga", 0).toString()
        tvHarga.text = harga.formatRupiah()

        val totalHarga = i.getIntExtra("total_harga", 0).toString()
        tvTotalHarga.text = totalHarga.formatRupiah()

        val tglPesan = i.getStringExtra("tgl_pesan").toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvTglPesan.text = nowDatetime(s = tglPesan)
        } else {
            tvTglPesan.text = tglPesan
        }

        btnCetak.setOnClickListener {
            val pdfDetails = Pemesanan(
                tvKodePesanan.text.toString().toInt(),
                tvKodePemasok.text.toString(),
                tvNamaPemasok.text.toString(),
                tvKodeBarang.text.toString(),
                tvNamaBarang.text.toString(),
                tvMerk.text.toString(),
                tvStok.text.toString().toInt(),
                tvSatuan.text.toString(),
                tvJumlah.text.toString().toInt(),
                harga.toInt(),
                totalHarga.toInt(),
                tvTglPesan.text.toString()
            )
            val pdfConverter = PdfConverter()
            pdfConverter.createPdf(this, pdfDetails, this)
        }


    }


    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat", "WeekBasedYear")
    fun nowDatetime(s: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentdate = sdf.parse(s)
        val sdf2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("EEEE, dd MMM YYYY HH:mm:ss z", locale)
        } else {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)
        }
        return sdf2.format(currentdate!!)
    }



}