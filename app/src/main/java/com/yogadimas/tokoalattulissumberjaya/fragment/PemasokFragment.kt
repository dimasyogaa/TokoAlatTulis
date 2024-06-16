package com.yogadimas.tokoalattulissumberjaya.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yogadimas.tokoalattulissumberjaya.R
import com.yogadimas.tokoalattulissumberjaya.activity.KelolaBarangActivity
import com.yogadimas.tokoalattulissumberjaya.activity.KelolaPemasokActivity
import com.yogadimas.tokoalattulissumberjaya.adapter.BarangAdapter
import com.yogadimas.tokoalattulissumberjaya.adapter.PemasokAdapter
import com.yogadimas.tokoalattulissumberjaya.api.Api
import com.yogadimas.tokoalattulissumberjaya.model.Barang
import com.yogadimas.tokoalattulissumberjaya.model.Pemasok
import org.json.JSONObject


class PemasokFragment : Fragment() {

    private var arrayList = ArrayList<Pemasok>()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var loading: ProgressBar

    private lateinit var emptyState: View

    private lateinit var swipeRefresh: SwipeRefreshLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pemasok, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh = view.findViewById(R.id.swipeRefresh)

        loading = view.findViewById(R.id.progress_bar)
        emptyState = view.findViewById(R.id.emptyState)

        mRecyclerView = view.findViewById(R.id.mRecyclerViewPemasok)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val mFloatingActionButton: FloatingActionButton =
            view.findViewById(R.id.mFloatingActionButton)
        mFloatingActionButton.setOnClickListener {
            startActivity(Intent(requireContext().applicationContext, KelolaPemasokActivity::class.java))
        }

        swipeRefresh.setColorSchemeResources(R.color.blue_500)
        swipeRefresh.setOnRefreshListener {
            tampilDaftarPemasok()
            swipeRefresh.isRefreshing = false
        }

    }

    override fun onResume() {
        super.onResume()
        tampilDaftarPemasok()
    }



    private fun tampilDaftarPemasok() {

        loading.visibility = View.VISIBLE

        AndroidNetworking.get(Api.READ_PEMASOK)
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
                    }

                    for (i in 0 until jsonArray?.length()!!) {

                        val jsonObject = jsonArray?.optJSONObject(i)
                        arrayList.add(
                            Pemasok(
                                jsonObject?.getString("kode_pemasok"),
                                jsonObject?.getString("nama_pemasok"),
                                jsonObject?.getString("jenis_kelamin"),
                                jsonObject?.getString("alamat"),
                                jsonObject?.getString("kode_pos"),
                                jsonObject?.getString("kota")
                            )
                        )

                        if (jsonArray?.length()!! - 1 == i) {
                            loading.visibility = View.GONE
                            emptyState.visibility = View.GONE
                            mRecyclerView.visibility = View.VISIBLE
                            val adapter =
                                PemasokAdapter(requireContext().applicationContext, arrayList)
                            adapter.notifyDataSetChanged()
                            mRecyclerView.adapter = adapter

                        }

                    }

                }

                override fun onError(anError: ANError?) {
                    loading.visibility = View.GONE
                    emptyState.visibility = View.VISIBLE
                    Log.d("ONERROR", anError?.errorDetail.toString())
                    Toast.makeText(
                        requireContext().applicationContext,
                        "Gagal Memuat Data Pemasok",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            })
    }
}