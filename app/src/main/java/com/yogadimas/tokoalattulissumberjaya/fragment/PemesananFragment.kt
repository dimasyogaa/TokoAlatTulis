package com.yogadimas.tokoalattulissumberjaya.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yogadimas.tokoalattulissumberjaya.R
import com.yogadimas.tokoalattulissumberjaya.activity.KelolaPemesananActivity
import com.yogadimas.tokoalattulissumberjaya.adapter.PemesananAdapter
import com.yogadimas.tokoalattulissumberjaya.api.Api
import com.yogadimas.tokoalattulissumberjaya.model.Pemesanan
import org.json.JSONObject


class PemesananFragment : Fragment() {

    private var arrayList = ArrayList<Pemesanan>()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var loading: ProgressBar

    private lateinit var fabTransaksi: ExtendedFloatingActionButton

    private lateinit var fabTambahPesanan: FloatingActionButton
    private lateinit var fabTambahStok: FloatingActionButton

    private lateinit var addAlarmActionText: TextView
    private lateinit var addPersonActionText: TextView

    private var isAllFabsVisible: Boolean = false

    private lateinit var emptyState: View

    private lateinit var swipeRefresh: SwipeRefreshLayout

    private lateinit var btnClear: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pemesanan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AndroidNetworking.initialize(requireContext().applicationContext)

        swipeRefresh = view.findViewById(R.id.swipeRefresh)

        loading = view.findViewById(R.id.progress_bar)
        emptyState = view.findViewById(R.id.emptyState)

        mRecyclerView = view.findViewById(R.id.mRecyclerViewBarang)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        fabTransaksi = view.findViewById(R.id.fabTransaksi)
        fabTambahPesanan = view.findViewById(R.id.fabTambahPesanan)
        fabTambahStok = view.findViewById(R.id.fabTambahStok)

        addAlarmActionText = view.findViewById(R.id.tvTambahPesanan)
        addPersonActionText = view.findViewById(R.id.tvTambahStok)

        fabTambahPesanan.visibility = View.GONE
        fabTambahStok.visibility = View.GONE
        addAlarmActionText.visibility = View.GONE
        addPersonActionText.visibility = View.GONE

        fabTransaksi.shrink()
        fabTransaksi.setOnClickListener { expand() }
        fabTambahStok.setOnClickListener { goToIntent("1") }
        fabTambahPesanan.setOnClickListener { goToIntent("2") }

        swipeRefresh.setColorSchemeResources(R.color.blue_500)
        swipeRefresh.setOnRefreshListener {
            tampilPemesanan()
            swipeRefresh.isRefreshing = false
        }

        btnClear = view.findViewById(R.id.btnClear)
        btnClear.visibility = View.GONE
        btnClear.setOnClickListener { clear()}
    }


    private fun tampilPemesanan() {
        tampilDaftarPemesanan()
        fabTambahPesanan.hide()
        fabTambahStok.hide()
        addAlarmActionText.setVisibility(View.GONE)
        addPersonActionText.setVisibility(View.GONE)

        fabTransaksi.shrink()

        isAllFabsVisible = false
    }

    private fun expand() {

        if (!isAllFabsVisible) {

            fabTambahPesanan.show()
            fabTambahStok.show()
            addAlarmActionText.visibility = View.VISIBLE
            addPersonActionText.visibility = View.VISIBLE

            fabTransaksi.extend()

            isAllFabsVisible = true

        } else {

            fabTambahPesanan.hide()
            fabTambahStok.hide()
            addAlarmActionText.setVisibility(View.GONE)
            addPersonActionText.setVisibility(View.GONE)

            fabTransaksi.shrink()

            isAllFabsVisible = false

        }
    }

    override fun onResume() {
        super.onResume()
        tampilPemesanan()
    }


    private fun tampilDaftarPemesanan() {

        loading.visibility = View.VISIBLE

        AndroidNetworking.get(Api.READ_PEMESANAN)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {

                    arrayList.clear()
                    val jsonArray = response?.optJSONArray("result")
                    if (jsonArray?.length() == 0) {
                        loading.visibility = View.GONE
                        emptyState.visibility = View.VISIBLE
                        mRecyclerView.visibility = View.GONE
                        btnClear.visibility = View.GONE
                    }

                    for (i in 0 until jsonArray?.length()!!) {

                        val jsonObject = jsonArray.optJSONObject(i)
                        arrayList.add(
                            Pemesanan(
                                jsonObject?.getInt("kode_pesanan"),
                                jsonObject?.getString("kode_pemasok"),
                                jsonObject?.getString("nama_pemasok"),
                                jsonObject?.getString("kode_barang"),
                                jsonObject?.getString("nama_barang"),
                                jsonObject?.getString("merk"),
                                jsonObject?.getInt("stok"),
                                jsonObject?.getString("satuan"),
                                jsonObject?.getInt("jumlah"),
                                jsonObject?.getInt("harga"),
                                jsonObject?.getInt("total_harga"),
                                jsonObject?.getString("tgl_pesan")
                            )
                        )


                        if (jsonArray.length()!! - 1 == i) {

                            loading.visibility = View.GONE
                            emptyState.visibility = View.GONE
                            mRecyclerView.visibility = View.VISIBLE
                            btnClear.visibility = View.GONE
                            val adapter =
                                PemesananAdapter(requireContext().applicationContext, arrayList)
                            adapter.notifyDataSetChanged()
                            mRecyclerView.adapter = adapter

                        }

                    }

                }

                override fun onError(anError: ANError?) {
                    loading.visibility = View.GONE
                    emptyState.visibility = View.VISIBLE
                    Log.d("ONERROR", anError?.errorDetail.toString())
                    Toast.makeText(requireContext().applicationContext,
                        "Gagal Memuat Data Pemesanan",
                        Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }


    private fun goToIntent(value: String) {
        val i = Intent(requireContext().applicationContext, KelolaPemesananActivity::class.java)
        i.putExtra("kelolaPemesanan", value)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    private fun clear() {
        AlertDialog.Builder(this@PemesananFragment.requireContext())
            .setTitle("Konfirmasi")
            .setMessage("Kosongkan semua data pemesanan ?")
            .setPositiveButton(
                "KOSONGKAN",
                DialogInterface.OnClickListener { dialogInterface, i -> clearAll() })
            .setNegativeButton(
                "BATAL",
                DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
            .show()

    }

    private fun clearAll() {
        loading.visibility = View.VISIBLE
        AndroidNetworking.post(Api.CLEAR_PEMESANAN)
            .addBodyParameter("clear", "clear")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    loading.visibility = View.GONE
                    Toast.makeText(
                        requireContext().applicationContext,
                        response?.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()


                    if (response?.getString("message")?.contains("Berhasil")!!) {
                       tampilPemesanan()
                    }

                }

                override fun onError(anError: ANError?) {
                    loading.visibility = View.GONE
                    Log.d("ONERROR", anError?.errorDetail.toString())
                    Toast.makeText(requireContext().applicationContext, "Gagal Dikosongkan", Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }
}