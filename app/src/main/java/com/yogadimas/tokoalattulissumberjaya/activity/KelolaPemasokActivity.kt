package com.yogadimas.tokoalattulissumberjaya.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.yogadimas.tokoalattulissumberjaya.R
import com.yogadimas.tokoalattulissumberjaya.api.Api
import org.json.JSONObject
import java.util.ArrayList

class KelolaPemasokActivity : AppCompatActivity() {

    private lateinit var i: Intent
    private lateinit var loading: ProgressBar

    private lateinit var edtKodePemasok: EditText
    private lateinit var edtNamaPemasok: EditText
    private lateinit var rgJenisKelamin: RadioGroup
    private lateinit var rPria: RadioButton
    private lateinit var rWanita: RadioButton
    private lateinit var edtAlamat: EditText
    private lateinit var edtKodePos: EditText
    private lateinit var edtKota: EditText

    private lateinit var btnCreate: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    private var gender = "Pria"

    private lateinit var layoutkodePemasokValidation: LinearLayout

    private val kodePemasokList: MutableList<String?> = ArrayList()

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_kelola_pemasok)
        AndroidNetworking.initialize(applicationContext.applicationContext)

        loading = findViewById(R.id.progress_bar)
        loading.visibility = View.GONE

        layoutkodePemasokValidation = findViewById(R.id.layoutkodePemasokValidation)
        layoutkodePemasokValidation.visibility = View.GONE

        edtKodePemasok = findViewById(R.id.edtKodePemasok)
        edtNamaPemasok = findViewById(R.id.edtNamaPemasok)
        rgJenisKelamin = findViewById(R.id.rgJenisKelamin)
        rPria = findViewById(R.id.rPria)
        rWanita = findViewById(R.id.rWanita)
        edtAlamat = findViewById(R.id.edtAlamat)
        edtKodePos = findViewById(R.id.edtKodePos)
        edtKota = findViewById(R.id.edtKota)
        btnCreate = findViewById(R.id.btnCreate)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)
        i = intent


        if (i.hasExtra("editmode")) {

            if (i.getStringExtra("editmode").equals("1")) {
                onEditMode()
            }
        }

        rgJenisKelamin.setOnCheckedChangeListener { _, i ->

            when (i) {

                R.id.rPria -> {
                    gender = "Pria"
                    textRadioButton(gender)

                }

                R.id.rWanita -> {
                    gender = "Wanita"
                    textRadioButton(gender)
                }

            }

        }
        textRadioButton(gender)
        btnCreate.setOnClickListener {create()}
        btnUpdate.setOnClickListener {update()}
        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Hapus pemasok ini ?")
                .setPositiveButton(
                    "HAPUS",
                    DialogInterface.OnClickListener { dialogInterface, i -> delete() })
                .setNegativeButton(
                    "BATAL",
                    DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                .show()

        }

        viewsTextColor()
        viewsTextStyleBold()

        edtKodePemasok.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkKodePemasok()
            }

            override fun afterTextChanged(s: Editable?) {
                checkKodePemasok()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        tampilKodePemasok()
    }

    private fun create() {
        loading.visibility = View.VISIBLE
        AndroidNetworking.post(Api.CREATE_PEMASOK)
            .addBodyParameter("kode_pemasok", edtKodePemasok.text.toString().trim())
            .addBodyParameter("nama_pemasok", edtNamaPemasok.text.toString().trim())
            .addBodyParameter("jenis_kelamin", gender)
            .addBodyParameter("alamat", edtAlamat.text.toString().trim())
            .addBodyParameter("kode_pos", edtKodePos.text.toString().trim())
            .addBodyParameter("kota", edtKota.text.toString().trim())
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
                        this@KelolaPemasokActivity.finish()
                    }
                }

                override fun onError(anError: ANError?) {
                    loading.visibility = View.GONE
                    Log.d("ONERROR", anError?.errorDetail.toString())
                    Toast.makeText(applicationContext, "Tambah Pemasok Gagal", Toast.LENGTH_SHORT)
                        .show()
                }

            })

    }

    private fun onEditMode() {
        edtKodePemasok.setText(i.getStringExtra("kode_pemasok"))
        edtNamaPemasok.setText(i.getStringExtra("nama_pemasok"))
        gender = i.getStringExtra("jenis_kelamin").toString()

        if (gender == "Pria") {
            rgJenisKelamin.check(R.id.rPria)
        } else {
            rgJenisKelamin.check(R.id.rWanita)
        }

        textRadioButton(gender)

        edtAlamat.setText(i.getStringExtra("alamat"))
        edtKodePos.setText(i.getStringExtra("kode_pos"))
        edtKota.setText(i.getStringExtra("kota"))
        edtKodePemasok.isEnabled = false


        btnCreate.visibility = View.GONE
        btnUpdate.visibility = View.VISIBLE
        btnDelete.visibility = View.VISIBLE

        viewsTextColorBackgroundDisabled()


    }

    private fun update() {
        loading.visibility = View.VISIBLE

        AndroidNetworking.post(Api.UPDATE_PEMASOK)
            .addBodyParameter("kode_pemasok", edtKodePemasok.text.toString().trim())
            .addBodyParameter("nama_pemasok", edtNamaPemasok.text.toString().trim())
            .addBodyParameter("jenis_kelamin", gender)
            .addBodyParameter("alamat", edtAlamat.text.toString().trim())
            .addBodyParameter("kode_pos", edtKodePos.text.toString().trim())
            .addBodyParameter("kota", edtKota.text.toString().trim())
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
                        this@KelolaPemasokActivity.finish()
                    }
                }

                override fun onError(anError: ANError?) {
                    loading.visibility = View.GONE
                    Log.d("ONERROR", anError?.errorDetail.toString())
                    Toast.makeText(applicationContext, "Edit Pemasok Gagal", Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }

    private fun delete() {
        loading.visibility = View.VISIBLE

        AndroidNetworking.get(Api.DELETE_PEMASOK + "?kode_pemasok=" + edtKodePemasok.text.toString())
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
                        this@KelolaPemasokActivity.finish()
                    }
                }

                override fun onError(anError: ANError?) {
                    loading.visibility = View.GONE
                    Log.d("ONERROR", anError?.errorDetail.toString())
                    Toast.makeText(applicationContext, "Hapus Pemasok Gagal", Toast.LENGTH_SHORT)
                        .show()
                }

            })

    }

    private fun viewsTextStyleBold() {
        textBold(edtKodePemasok)
        textBold(edtNamaPemasok)
        textBold(edtAlamat)
        textBold(edtKodePos)
        textBold(edtKota)
    }

    private fun viewsTextColor() {
        textColorBlack(edtKodePemasok)
        textColorBlack(edtNamaPemasok)
        textColorBlack(edtAlamat)
        textColorBlack(edtKodePos)
        textColorBlack(edtKota)
    }

    private fun viewsTextColorBackgroundDisabled() {
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

    private fun textRadioButton(s: String) {
        if (s == "Pria") {
            rPria.setTypeface(rPria.typeface, Typeface.BOLD)
            rWanita.setTypeface(null, Typeface.NORMAL)
        } else {
            rPria.setTypeface(null, Typeface.NORMAL)
            rWanita.setTypeface(rWanita.typeface, Typeface.BOLD)
        }

    }

    private fun tampilKodePemasok() {
        loading.visibility = View.VISIBLE

        AndroidNetworking.get(Api.READ_PEMASOK).setPriority(Priority.MEDIUM).build()
            .getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {
                    kodePemasokList?.clear()
                    val jsonArray = response?.optJSONArray("result")

                    if (jsonArray?.length() == 0) {
                        loading.visibility = View.GONE
                        Toast.makeText(applicationContext,
                            "Kode Pemasok Belum Ada Data",
                            Toast.LENGTH_SHORT).show()
                    }

                    for (i in 0 until jsonArray?.length()!!) {
                        loading.visibility = View.GONE
                        val jsonObject = jsonArray?.optJSONObject(i)
                        kodePemasokList.add(jsonObject?.getString("kode_pemasok"))
                    }

                }

                override fun onError(anError: ANError?) {
                    loading.visibility = View.GONE
                    Log.d("ONERROR", anError?.errorDetail.toString())
                    Toast.makeText(applicationContext,
                        "Koneksi Kode Pemasok Gagal",
                        Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun checkKodePemasok() {
        if (kodePemasokList.contains(edtKodePemasok.text.toString())) {
            layoutkodePemasokValidation.visibility = View.VISIBLE
            btnCreate.isEnabled = false
            btnCreate.isClickable = false
        } else {
            layoutkodePemasokValidation.visibility = View.GONE
            btnCreate.isEnabled = true
            btnCreate.isClickable = true
        }
    }
}