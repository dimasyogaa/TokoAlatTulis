package com.yogadimas.tokoalattulissumberjaya.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.InputType.*
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.yogadimas.tokoalattulissumberjaya.R
import com.yogadimas.tokoalattulissumberjaya.api.Api
import com.yogadimas.tokoalattulissumberjaya.helper.formatRupiah
import com.yogadimas.tokoalattulissumberjaya.helper.locale
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class KelolaPemesananActivity : AppCompatActivity() {
    private lateinit var mode: Intent

    private lateinit var layoutSpinnerPemasok: View
    private lateinit var spinnerPemasok: Spinner
    private lateinit var layoutSpinnerIdPemasok: View
    private lateinit var spinnerIdPemasok: Spinner
    private val namaPemasokList: MutableList<String?> = ArrayList()
    private val kodePemasokList: MutableList<String?> = ArrayList()
    private lateinit var adapterNamaPemasok: ArrayAdapter<String?>
    private lateinit var kodePemasok: String
    private lateinit var namaPemasok: String

    private lateinit var layoutSpinnerBarang: View
    private lateinit var spinnerBarang: Spinner
    private lateinit var layoutSpinnerIdBarang: View
    private lateinit var spinnerIdBarang: Spinner
    private lateinit var layoutSpinnerNamaBarang: View
    private lateinit var spinnerNamaBarang: Spinner
    private val namaBarangList: MutableList<String?> = ArrayList()
    private val kodeBarangList: MutableList<String?> = ArrayList()
    private lateinit var adapterNamaBarang: ArrayAdapter<String?>
    private lateinit var kodeBarang: String
    private lateinit var namaBarang: String
    private lateinit var merkBarang: String
    private lateinit var satuanBarang: String

    private var stokBarang: Int = 0
    private var hargaBarang: Int = 0
    private var jmlBarang: Int = 0
    private var totalHarga: Double = 0.0
    private var xHarga: Double = 0.0

    private val merkList: MutableList<String?> = ArrayList()
    private val satuanList: MutableList<String?> = ArrayList()
    private val stokList: MutableList<Int?> = ArrayList()
    private val hargaList: MutableList<Int?> = ArrayList()

    private lateinit var layoutkodeBarangValidation: View

    private lateinit var edtKodePemasok: EditText
    private lateinit var edtNamaPemasok: EditText
    private lateinit var edtKodeBarang: EditText
    private lateinit var edtNamaBarang: EditText
    private lateinit var edtMerk: EditText
    private lateinit var edtSatuan: EditText
    private lateinit var edtStok: EditText
    private lateinit var edtJumlah: EditText
    private lateinit var edtHarga: EditText
    private lateinit var edtTotalHarga: EditText
    private lateinit var edtTglPesan: EditText

    private lateinit var loading: ProgressBar

    private lateinit var tglPesan: String

    private lateinit var btnTambahStok: Button
    private lateinit var btnTambahPesanan: Button

    private lateinit var tvJumlah: TextView
    private lateinit var tvTotalHarga: TextView

    private lateinit var layoutStok: View

    private val kodePemasokBarangList: MutableList<String?> = ArrayList()
    private var namaPemasokBarang: String = ""

    private lateinit var emptyState: LinearLayout
    private lateinit var tvEmptyState: TextView
    private lateinit var content: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_kelola_pemesanan)

        AndroidNetworking.initialize(applicationContext.applicationContext)

        loading = findViewById(R.id.progress_bar)

        layoutkodeBarangValidation = findViewById(R.id.layoutkodeBarangValidation)
        layoutkodeBarangValidation.visibility = View.GONE

        edtKodePemasok = findViewById(R.id.edtKodePemasok)
        edtNamaPemasok = findViewById(R.id.edtNamaPemasok)
        edtKodeBarang = findViewById(R.id.edtKodeBarang)
        edtNamaBarang = findViewById(R.id.edtNamaBarang)

        edtMerk = findViewById(R.id.edtMerk)
        edtSatuan = findViewById(R.id.edtSatuan)
        edtStok = findViewById(R.id.edtStok)
        edtJumlah = findViewById(R.id.edtJumlah)

        edtHarga = findViewById(R.id.edtHarga)

        edtTotalHarga = findViewById(R.id.edtTotalHarga)
        edtTotalHarga.isEnabled = false

        edtTglPesan = findViewById(R.id.edtTglPesan)
        edtTglPesan.visibility = View.GONE

        btnTambahStok = findViewById(R.id.btnAddStock)
        btnTambahPesanan = findViewById(R.id.btnAddOrder)

        tvJumlah = findViewById(R.id.tvJumlah)
        tvTotalHarga = findViewById(R.id.tvTotalHarga)

        layoutStok = findViewById(R.id.layoutStok)

        emptyState = findViewById(R.id.emptyState)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        content = findViewById(R.id.content)
        emptyState.visibility = View.GONE
        content.visibility = View.GONE

        edtTglPesan.setText(nowDatetime("EEEE, dd MMM YYYY HH:mm:ss"))
        tglPesan = nowDatetime()
        spinnerBarang()
        spinnerPemasok()

        layoutSpinnerIdPemasok.visibility = View.GONE

        layoutSpinnerIdBarang.visibility = View.GONE
        layoutSpinnerNamaBarang.visibility = View.GONE
        mode = intent

        if (mode.hasExtra("kelolaPemesanan")) {
            if (mode.getStringExtra("kelolaPemesanan").equals("1")) {
                tvEmptyState.text = "Data Barang Kosong,\nBelum Bisa Menambahkan Stok"
                tambahStokMode()
            } else {
                tvEmptyState.text = "Data Pemasok Kosong,\nBelum Bisa Menambahkan Pesanan"
                tambahPesananMode()
            }
        }

        hitungTotalHarga()
        edtJumlah.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                hitungTotalHarga()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                hitungTotalHarga()
            }

            override fun afterTextChanged(s: Editable?) {
                hitungTotalHarga()
            }

        })

        btnTambahStok.setOnClickListener {
            tambahStokBarang()
        }

        btnTambahPesanan.setOnClickListener { tambahPemesananBarang() }


    }

    private fun hitungTotalHarga() {
        val sHarga = edtHarga.text.toString()
        val sJumlah = edtJumlah.text.toString()
        jmlBarang = if (sJumlah.isNotEmpty()) sJumlah.toInt() else 0

        xHarga = if (mode.getStringExtra("kelolaPemesanan").equals("2")) {
            if (sHarga.isNotEmpty()) sHarga.toDouble() else 0.0
        } else {
            if (sHarga.isNotEmpty()) hargaBarang.toDouble() else 0.0
        }


        totalHarga = xHarga * jmlBarang

        edtHarga.hint = xHarga.toString().formatRupiah()
        edtTotalHarga.setText(totalHarga.toString().formatRupiah())

    }

    @SuppressLint("SimpleDateFormat")
    fun nowDatetime(patternFormat: String = "yyyy-MM-dd HH:mm:ss"): String {

        val date = Date()
        val formatDate = SimpleDateFormat(patternFormat, locale)
        return formatDate.format(date)
    }

    override fun onResume() {
        super.onResume()
        if (mode.hasExtra("kelolaPemesanan")) {
            if (mode.getStringExtra("kelolaPemesanan").equals("1")) {
                tampilSpinnerDataBarang()
            } else {
                tampilSpinnerDataPemasok()
            }
        }
    }

    private fun spinnerPemasok() {
        layoutSpinnerPemasok = findViewById(R.id.layoutSpinnerPemasok)
        layoutSpinnerIdPemasok = findViewById(R.id.layoutIdPemasok)
        spinnerPemasok = findViewById(R.id.spinnerPemasok)
        spinnerIdPemasok = findViewById(R.id.spinnerIdPemasok)

        spinnerPemasok.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val selectedPemasok = parent?.getItemAtPosition(position).toString()

                val adapterKodePemasok = ArrayAdapter(this@KelolaPemesananActivity,
                    android.R.layout.simple_list_item_1,
                    kodePemasokList)

                adapterKodePemasok.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


                spinnerIdPemasok.isEnabled = false
                spinnerIdPemasok.setAdapter(adapterKodePemasok)
                if (selectedPemasok != null) {
                    val spinnerPosition = adapterNamaPemasok.getPosition(selectedPemasok)
                    spinnerIdPemasok.setSelection(spinnerPosition)
                    kodePemasok = kodePemasokList[spinnerPosition]!!
                    namaPemasok = namaPemasokList[spinnerPosition]!!
                    edtKodePemasok.setText(kodePemasok)
                    edtNamaPemasok.setText(namaPemasok)
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun spinnerBarang() {
        layoutSpinnerBarang = findViewById(R.id.layoutSpinnerBarang)
        layoutSpinnerIdBarang = findViewById(R.id.layoutIdBarang)
        layoutSpinnerNamaBarang = findViewById(R.id.layoutNamaBarang)
        spinnerBarang = findViewById(R.id.spinnerBarang)
        spinnerIdBarang = findViewById(R.id.spinnerIdBarang)
        spinnerNamaBarang = findViewById(R.id.spinnerNamaBarang)


        spinnerBarang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {


                val selectedBarang = parent?.getItemAtPosition(position).toString()

                val adapterKodeBarang = ArrayAdapter(this@KelolaPemesananActivity,
                    android.R.layout.simple_list_item_1,
                    kodeBarangList)

                adapterKodeBarang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spinnerIdBarang.isEnabled = false
                spinnerIdBarang.setAdapter(adapterKodeBarang)
                if (selectedBarang != null) {
                    val spinnerPosition = adapterNamaBarang.getPosition(selectedBarang)
                    spinnerIdBarang.setSelection(spinnerPosition)
                    kodeBarang = kodeBarangList[spinnerPosition]!!
                    edtKodeBarang.setText(kodeBarang)
                }

                val adapterNamaBarang2 = ArrayAdapter(this@KelolaPemesananActivity,
                    android.R.layout.simple_list_item_1,
                    namaBarangList)

                spinnerNamaBarang.isEnabled = false
                spinnerNamaBarang.setAdapter(adapterNamaBarang2)
                if (selectedBarang != null) {
                    val spinnerPosition = adapterNamaBarang.getPosition(selectedBarang)
                    spinnerNamaBarang.setSelection(spinnerPosition)
                    namaBarang = namaBarangList[spinnerPosition]!!
                    merkBarang = merkList[spinnerPosition]!!
                    satuanBarang = satuanList[spinnerPosition]!!
                    stokBarang = stokList[spinnerPosition]!!
                    hargaBarang = hargaList[spinnerPosition]!!

                    kodePemasok = kodePemasokBarangList[spinnerPosition]!!


                    edtNamaBarang.setText(namaBarang)
                    edtMerk.setText(merkBarang)
                    edtSatuan.setText(satuanBarang)
                    edtStok.setText(stokBarang.toString())
                    edtHarga.setText(hargaBarang.toString().formatRupiah())
                    edtKodePemasok.setText(kodePemasok)
                    tampilKodePemasokBarang()


                }

                hitungTotalHarga()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun tampilSpinnerDataPemasok() {
        loading.visibility = View.VISIBLE

        AndroidNetworking.get(Api.READ_PEMASOK).setPriority(Priority.MEDIUM).build()
            .getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {
                    kodePemasokList?.clear()
                    namaPemasokList?.clear()
                    val jsonArray = response?.optJSONArray("result")

                    if (jsonArray?.length() == 0) {
                        loading.visibility = View.GONE
                        emptyState.visibility = View.VISIBLE
                        content.visibility = View.GONE
                        Toast.makeText(applicationContext,
                            "Belum Ada Data Pemasok",
                            Toast.LENGTH_SHORT).show()
                    }

                    for (i in 0 until jsonArray?.length()!!) {

                        val jsonObject = jsonArray?.optJSONObject(i)
                        kodePemasokList.add(jsonObject?.getString("kode_pemasok"))

                        namaPemasokList.add(jsonObject?.getString("nama_pemasok"))

                        if (jsonArray?.length()!! - 1 == i) {
                            loading.visibility = View.GONE
                            emptyState.visibility = View.GONE
                            content.visibility = View.VISIBLE
                            val kodeNamaPemasok =
                                kodePemasokList.zip(namaPemasokList) { a: String?, b: String? -> "$a - $b" }

                            adapterNamaPemasok = ArrayAdapter(this@KelolaPemesananActivity,
                                android.R.layout.simple_list_item_1,
                                kodeNamaPemasok)

                            adapterNamaPemasok.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                            spinnerPemasok.adapter = adapterNamaPemasok
                        }

                    }

                }

                override fun onError(anError: ANError?) {
                    loading.visibility = View.GONE
                    emptyState.visibility = View.GONE
                    content.visibility = View.VISIBLE
                    Log.d("ONERROR", anError?.errorDetail.toString())
                    Toast.makeText(applicationContext,
                        "Gagal Memuat Data Pemasok",
                        Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun tampilSpinnerDataBarang() {
        loading.visibility = View.VISIBLE

        AndroidNetworking.get(Api.READ_BARANG).setPriority(Priority.MEDIUM).build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    kodeBarangList?.clear()
                    namaBarangList?.clear()
                    merkList?.clear()
                    satuanList?.clear()
                    stokList?.clear()
                    hargaList?.clear()
                    kodePemasokBarangList?.clear()
                    val jsonArray = response?.optJSONArray("result")

                    if (jsonArray?.length() == 0) {
                        loading.visibility = View.GONE
                        if (mode.hasExtra("kelolaPemesanan")) {
                            if (mode.getStringExtra("kelolaPemesanan").equals("1")) {
                                emptyState.visibility = View.VISIBLE
                                content.visibility = View.GONE
                                Toast.makeText(applicationContext,
                                    "Belum Ada Data Barang",
                                    Toast.LENGTH_SHORT).show()

                            }
                        }

                    }

                    for (i in 0 until jsonArray?.length()!!) {
                        val jsonObject = jsonArray?.optJSONObject(i)

                        kodeBarangList.add(jsonObject?.getString("kode_barang"))

                        namaBarangList.add(jsonObject?.getString("nama_barang"))

                        merkList.add(jsonObject?.getString("merk"))

                        satuanList.add(jsonObject?.getString("satuan"))

                        stokList.add(jsonObject?.getInt("stok"))

                        hargaList.add(jsonObject?.getInt("harga"))

                        kodePemasokBarangList.add(jsonObject?.getString("kode_pemasok"))

                        if (jsonArray?.length()!! - 1 == i) {
                            loading.visibility = View.GONE
                            content.visibility = View.VISIBLE
                            val kodeNamaBarang =
                                kodeBarangList.zip(namaBarangList) { a: String?, b: String? -> "$a - $b" }

                            adapterNamaBarang = ArrayAdapter(this@KelolaPemesananActivity,
                                android.R.layout.simple_list_item_1,
                                kodeNamaBarang)

                            adapterNamaBarang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                            spinnerBarang.adapter = adapterNamaBarang
                        }

                    }

                }

                override fun onError(anError: ANError?) {
                    loading.visibility = View.GONE
                    Log.d("ONERROR", anError?.errorDetail.toString())
                    Toast.makeText(applicationContext,
                        "Gagal Memuat Data Barang",
                        Toast.LENGTH_SHORT).show()
                }

            })


    }

    private fun tampilKodePemasokBarang() {
        loading.visibility = View.VISIBLE

        AndroidNetworking.get(Api.READ_PEMASOK_BARANG + "?kode_pemasok=" + edtKodePemasok.text.toString())
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    val jsonArray = response?.optJSONArray("result")
                    if (jsonArray?.length() == 0) {
                        loading.visibility = View.GONE
                        if (mode.hasExtra("kelolaPemesanan")) {
                            if (mode.getStringExtra("kelolaPemesanan").equals("1")) {
                                Toast.makeText(applicationContext,
                                    "Pemasok Telah Dihapus",
                                    Toast.LENGTH_SHORT).show()
                                edtNamaPemasok.setText("Pemasok telah dihapus")
                            }
                        }

                    }

                    for (i in 0 until jsonArray?.length()!!) {
                        val jsonObject = jsonArray?.optJSONObject(i)
                        if (jsonArray?.length()!! - 1 == i) {
                            loading.visibility = View.GONE
                            namaPemasokBarang = jsonObject?.getString("nama_pemasok").toString()
                            namaPemasok = namaPemasokBarang
                            edtNamaPemasok.setText(namaPemasok)
                        }
                    }

                }

                override fun onError(anError: ANError?) {
                    loading.visibility = View.GONE
                    Log.d("ONERROR", anError?.errorDetail.toString())
                    Toast.makeText(applicationContext,
                        "Gagal Memuat Data Kode Pemasok",
                        Toast.LENGTH_SHORT).show()
                }

            })

    }

    private fun tambahStokMode() {
        readOnly(true)
    }

    private fun tambahPesananMode() {
        readOnly(false)
        edtKodeBarang.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                tampilSpinnerDataBarang()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tampilSpinnerDataBarang()
                checkKodeBarang()
            }

            override fun afterTextChanged(s: Editable?) {
                tampilSpinnerDataBarang()
                checkKodeBarang()
            }

        })
        edtHarga.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                hitungTotalHarga()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                hitungTotalHarga()
            }

            override fun afterTextChanged(s: Editable?) {
                hitungTotalHarga()
            }

        })

    }

    private fun checkKodeBarang() {
        if (kodeBarangList.contains(edtKodeBarang.text.toString())) {
            layoutkodeBarangValidation.visibility = View.VISIBLE
            btnTambahPesanan.isEnabled = false
            btnTambahPesanan.isClickable = false
        } else {
            layoutkodeBarangValidation.visibility = View.GONE
            btnTambahPesanan.isEnabled = true
            btnTambahPesanan.isClickable = true
        }
    }

    private fun readOnly(c: Boolean) {
        val b = !c

        edtKodeBarang.isEnabled = b
        edtNamaBarang.isEnabled = b
        edtMerk.isEnabled = b
        edtStok.isEnabled = b
        edtSatuan.isEnabled = b
        edtHarga.isEnabled = b
        if (!b) {
            edtKodePemasok.isEnabled = b
            edtNamaPemasok.isEnabled = b
            layoutSpinnerPemasok.visibility = View.GONE
            layoutSpinnerBarang.visibility = View.VISIBLE
            btnTambahStok.visibility = View.VISIBLE
            btnTambahPesanan.visibility = View.GONE
            layoutStok.visibility = View.VISIBLE
            viewsTextColorBackgroundDisabled(!b)
            textBold(tvJumlah)
            textBold(tvTotalHarga)
        } else {
            edtKodePemasok.isEnabled = !b
            edtNamaPemasok.isEnabled = !b
            layoutSpinnerPemasok.visibility = View.VISIBLE
            layoutSpinnerBarang.visibility = View.GONE
            btnTambahStok.visibility = View.GONE
            btnTambahPesanan.visibility = View.VISIBLE
            layoutStok.visibility = View.GONE
            viewsTextColorBackgroundDisabled()
            textBold(edtKodePemasok)
            textBold(edtNamaPemasok)
            textBold(edtKodeBarang)
            textBold(edtNamaBarang)
            textBold(edtMerk)
            textBold(edtSatuan)
            textBold(edtHarga)

        }
        textBold(edtJumlah)
        textBold(edtTotalHarga)
        viewsTextColor()
    }

    private fun viewsTextColor() {
        textColorBlack(edtKodePemasok)
        textColorBlack(edtNamaPemasok)
        textColorBlack(edtKodeBarang)
        textColorBlack(edtNamaBarang)
        textColorBlack(edtMerk)
        textColorBlack(edtStok)
        textColorBlack(edtSatuan)
        textColorBlack(edtJumlah)
        textColorBlack(edtHarga)
        textColorBlack(edtTotalHarga)
    }

    private fun viewsTextColorBackgroundDisabled(tambahStok: Boolean = false) {
        if (tambahStok) {
            textColorBackground(edtKodeBarang)
            textColorBackground(edtNamaBarang)
            textColorBackground(edtMerk)
            textColorBackground(edtStok)
            textColorBackground(edtSatuan)
            textColorBackground(edtHarga)
        }
        textColorBackground(edtKodePemasok)
        textColorBackground(edtNamaPemasok)
        textColorBackground(edtTotalHarga)
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


    private fun newStok(): String {
        var oldStok = 0
        if (mode.hasExtra("kelolaPemesanan")) {
            if (mode.getStringExtra("kelolaPemesanan").equals("1")) {
                oldStok = edtStok.text.toString().trim().toInt()
            }
        }
        val newStok = oldStok + jmlBarang
        return newStok.toString()
    }

    private fun tambahStokBarang() {
        loading.visibility = View.VISIBLE

        if (newStok().isNotEmpty() && newStok() != "0") {
            AndroidNetworking.post(Api.ADD_STOK_BARANG)
                .addBodyParameter("kode_barang", edtKodeBarang.text.toString().trim())
                .addBodyParameter("stok", newStok()).setPriority(Priority.MEDIUM).build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        loading.visibility = View.GONE
                        if (response?.getString("message")?.contains("Berhasil")!!) {
                            tambahPemesanan()
                        }
                    }

                    override fun onError(anError: ANError?) {
                        loading.visibility = View.GONE
                        Log.d("ONERROR", anError?.errorDetail.toString())
                        Toast.makeText(applicationContext,
                            "Tambah Stok Gagal",
                            Toast.LENGTH_SHORT)
                            .show()
                    }

                })
        } else {
            edtJumlah.isFocusableInTouchMode = true
            edtJumlah.requestFocus()
            val showKeyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            showKeyboard.showSoftInput(edtJumlah, InputMethodManager.SHOW_IMPLICIT)
            Toast.makeText(applicationContext, "Data Jumlah Harus Diisi", Toast.LENGTH_SHORT).show()
            loading.visibility = View.GONE
        }

    }

    private fun tambahPemesanan() {
        loading.visibility = View.VISIBLE

        AndroidNetworking.post(Api.CREATE_PEMESANAN)
            .addBodyParameter("kode_barang", edtKodeBarang.text.toString())
            .addBodyParameter("kode_pemasok", edtKodePemasok.text.toString())
            .addBodyParameter("nama_barang", edtNamaBarang.text.toString())
            .addBodyParameter("nama_pemasok", edtNamaPemasok.text.toString())
            .addBodyParameter("merk", edtMerk.text.toString())
            .addBodyParameter("stok", newStok())
            .addBodyParameter("satuan", edtSatuan.text.toString())
            .addBodyParameter("jumlah", jmlBarang.toString())
            .addBodyParameter("harga", xHarga.toInt().toString())
            .addBodyParameter("total_harga", totalHarga.toInt().toString())
            .addBodyParameter("tgl_pesan", tglPesan).setPriority(Priority.MEDIUM).build()
            .getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {

                    loading.visibility = View.GONE

                    Toast.makeText(applicationContext,
                        response?.getString("message"),
                        Toast.LENGTH_SHORT).show()

                    if (response?.getString("message")?.contains("Berhasil")!!) {
                        this@KelolaPemesananActivity.finish()
                    }

                }

                override fun onError(anError: ANError?) {
                    loading.visibility = View.GONE
                    Log.d("ONERROR", anError?.errorDetail.toString())
                    Toast.makeText(applicationContext,
                        "Tambah Pesanan Gagal",
                        Toast.LENGTH_SHORT).show()
                }


            })

    }

    private fun tambahPemesananBarang() {
        loading.visibility = View.VISIBLE

        AndroidNetworking.post(Api.CREATE_BARANG)
            .addBodyParameter("kode_barang", edtKodeBarang.text.toString().trim())
            .addBodyParameter("nama_barang", edtNamaBarang.text.toString().trim())
            .addBodyParameter("merk", edtMerk.text.toString().trim())
            .addBodyParameter("stok", jmlBarang.toString())
            .addBodyParameter("satuan", edtSatuan.text.toString().trim())
            .addBodyParameter("harga", xHarga.toInt().toString())
            .addBodyParameter("kode_pemasok", edtKodePemasok.text.toString().trim())
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {
                    loading.visibility = View.GONE
                    if (response?.getString("message")?.contains("Berhasil")!!) {
                        tambahPemesanan()
                    }
                }

                override fun onError(anError: ANError?) {
                    loading.visibility = View.GONE
                    Log.d("TAG", anError?.errorDetail.toString())
                    Toast.makeText(applicationContext, "Tambah Barang Gagal", Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }


}

