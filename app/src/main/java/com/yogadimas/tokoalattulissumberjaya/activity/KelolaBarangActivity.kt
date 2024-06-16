package com.yogadimas.tokoalattulissumberjaya.activity

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.yogadimas.tokoalattulissumberjaya.R
import com.yogadimas.tokoalattulissumberjaya.api.Api
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*


class KelolaBarangActivity : AppCompatActivity() {
    private val locale = Locale("in", "ID")
    lateinit var i: Intent
    private var namaPemasok: MutableList<String?> = ArrayList()
    private var kodePemasok: MutableList<String?> = ArrayList()
    private lateinit var loading: ProgressBar

    private lateinit var layoutStokValidation: LinearLayout

    private lateinit var edtKodePemasok: EditText
    private lateinit var edtKodeBarang: EditText
    private lateinit var edtNamaBarang: EditText
    private lateinit var edtMerk: EditText
    private lateinit var edtStok: EditText
    private lateinit var edtSatuan: EditText
    private lateinit var edtHarga: EditText
    private lateinit var edtHargaFormat: EditText
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    private var oldStok: Int = 0
    private var stok: Int = 0
    private var harga: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_kelola_barang)
        AndroidNetworking.initialize(applicationContext.applicationContext)


        loading = findViewById(R.id.progress_bar)

        layoutStokValidation = findViewById(R.id.layoutStokBarangValidation)
        layoutStokValidation.visibility = View.GONE

        edtKodePemasok = findViewById(R.id.edtKodePemasok)
        edtKodeBarang = findViewById(R.id.edtKodeBarang)
        edtNamaBarang = findViewById(R.id.edtNamaBarang)
        edtMerk = findViewById(R.id.edtMerk)
        edtStok = findViewById(R.id.edtStok)
        edtSatuan = findViewById(R.id.edtSatuan)
        edtHarga = findViewById(R.id.edtHarga)
        edtHargaFormat = findViewById(R.id.edtHargaFormat)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)
        i = intent

        if (i.hasExtra("editmode")) {

            // jika ada maka di cek lagi apakah “editmode” mempunyai nilai “1”?
            if (i.getStringExtra("editmode").equals("1")) {
                onEditMode()
            }
        }



        btnUpdate.setOnClickListener { update() }
        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Hapus data ini ?")
                .setPositiveButton(
                    "HAPUS",
                    DialogInterface.OnClickListener { dialogInterface, i -> delete() })
                .setNegativeButton(
                    "BATAL",
                    DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                .show()

        }

    }


    private fun onEditMode() {
        loading.visibility = View.GONE
        edtKodePemasok.setText(i.getStringExtra("kode_pemasok"))
        edtKodeBarang.setText(i.getStringExtra("kode_barang"))
        edtNamaBarang.setText(i.getStringExtra("nama_barang"))
        edtMerk.setText(i.getStringExtra("merk"))
        edtStok.setText(i.getIntExtra("stok", 0).toString())
        edtSatuan.setText(i.getStringExtra("satuan"))
        edtHarga.setText(i.getIntExtra("harga", 0).toString())
        edtHargaFormat.setText(i.getIntExtra("harga", 0).toString().formatRupiah())

        edtStok.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                stokBarang()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                stokBarang()
            }

            override fun afterTextChanged(s: Editable?) {
                stokBarang()
            }

        })

        edtHarga.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                edtHargaFormat.setText(hargaBarang().toString().formatRupiah())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                edtHargaFormat.setText(hargaBarang().toString().formatRupiah())
            }

            override fun afterTextChanged(s: Editable?) {
                edtHargaFormat.setText(hargaBarang().toString().formatRupiah())
            }

        })
        detailView(true)


    }

    private fun hargaBarang(): Int {
        val xEdtHarga = edtHarga.text.toString()
        harga = if (xEdtHarga.isNotEmpty()) {
            xEdtHarga.toInt()
        } else {
            0
        }
        return harga
    }

    private fun stokBarang(): Int {
        oldStok = i.getIntExtra("stok", 0).toString().toInt()
        val xEdtStok = edtStok.text.toString()
        stok = if (xEdtStok.isNotEmpty()) {
            xEdtStok.toInt()
        } else {
            0
        }
        if (oldStok < stok) {
            layoutStokValidation.visibility = View.VISIBLE
            btnUpdate.isEnabled = false
            btnUpdate.isClickable = false
        } else {
            layoutStokValidation.visibility = View.GONE
            btnUpdate.isEnabled = true
            btnUpdate.isClickable = true
        }
        return stok
    }

    private fun update() {
        loading.visibility = View.VISIBLE

        AndroidNetworking.post(Api.UPDATE_BARANG)
            .addBodyParameter("kode_barang", edtKodeBarang.text.toString())
            .addBodyParameter("nama_barang", edtNamaBarang.text.toString())
            .addBodyParameter("merk", edtMerk.text.toString())
            .addBodyParameter("stok", stokBarang().toString())
            .addBodyParameter("satuan", edtSatuan.text.toString())
            .addBodyParameter("harga", hargaBarang().toString())
            .addBodyParameter("kode_pemasok", edtKodePemasok.text.toString())
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    loading.visibility = View.GONE
                    Toast.makeText(
                        applicationContext,
                        response?.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()

                    if (response?.getString("message")?.contains("Berhasil")!!) {
                        this@KelolaBarangActivity.finish()
                    }
                }

                override fun onError(anError: ANError?) {
                    loading.visibility = View.GONE
                    Log.d("ONERROR", anError?.errorDetail.toString())
                    Toast.makeText(applicationContext, "Edit Barang Gagal", Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }

    private fun delete() {
        loading.visibility = View.VISIBLE

        AndroidNetworking.get(Api.DELETE_BARANG + "?kode_barang=" + edtKodeBarang.text.toString())
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    loading.visibility = View.GONE
                    Toast.makeText(
                        applicationContext,
                        response?.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                    if (response?.getString("message")?.contains("Berhasil")!!) {
                        this@KelolaBarangActivity.finish()
                    }
                }

                override fun onError(anError: ANError?) {
                    loading.visibility = View.GONE
                    Log.d("ONERROR", anError?.errorDetail.toString())
                    Toast.makeText(applicationContext, "Hapus Barang Gagal", Toast.LENGTH_SHORT)
                        .show()
                }

            })

    }

    private fun detailView(c: Boolean) {

        val b = !c

        edtKodeBarang.isEnabled = b
        edtNamaBarang.isEnabled = !b
        edtMerk.isEnabled = !b
        edtStok.isEnabled = !b
        edtSatuan.isEnabled = !b
        edtHarga.isEnabled = !b
        edtHargaFormat.isEnabled = b

        edtKodePemasok.isEnabled = b
        viewsTextColorBackgroundDisabled()
        textBold(edtKodePemasok)
        textBold(edtKodeBarang)
        textBold(edtNamaBarang)
        textBold(edtMerk)
        textBold(edtStok)
        textBold(edtSatuan)
        textBold(edtHarga)
        textBold(edtHargaFormat)

        viewsTextColor()
    }

    private fun viewsTextColor() {
        textColorBlack(edtKodePemasok)
        textColorBlack(edtKodeBarang)
        textColorBlack(edtNamaBarang)
        textColorBlack(edtMerk)
        textColorBlack(edtStok)
        textColorBlack(edtSatuan)
        textColorBlack(edtHarga)
        textColorBlack(edtHargaFormat)
    }

    private fun viewsTextColorBackgroundDisabled() {
        textColorBackground(edtKodeBarang)
        textColorBackground(edtHargaFormat)
        textColorBackground(edtKodePemasok)
    }

    private fun textColorBlack(edt: EditText) {
        edt.setTextColor(Color.parseColor("#555555"))
    }

    private fun textColorBackground(edt: EditText) {
        edt.setBackgroundColor(Color.parseColor("#FFFFFF"))
    }

    private fun textBold(text: TextView) {
        text.setTypeface(text.typeface, Typeface.BOLD);
    }

    fun String.formatRupiah(): String {
        val uang = this.toDouble()
        val mCurrencyFormat = NumberFormat.getCurrencyInstance(locale)
        return mCurrencyFormat.format(uang)
    }




}