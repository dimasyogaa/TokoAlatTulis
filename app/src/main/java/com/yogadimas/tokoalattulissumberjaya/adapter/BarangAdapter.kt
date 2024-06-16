package com.yogadimas.tokoalattulissumberjaya.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.yogadimas.tokoalattulissumberjaya.R
import com.yogadimas.tokoalattulissumberjaya.activity.KelolaBarangActivity
import com.yogadimas.tokoalattulissumberjaya.helper.formatRupiah
import com.yogadimas.tokoalattulissumberjaya.model.Barang
import java.util.*
import kotlin.collections.ArrayList

class BarangAdapter(private val context: Context, private val arrayList: ArrayList<Barang>) :
    RecyclerView.Adapter<BarangAdapter.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.barang_list, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.lbKodeBarang.text = arrayList?.get(position)?.kodeBarang
        holder.lbNamaBarang.text = arrayList.get(position)?.namaBarang
        holder.lbMerkBarang.text = arrayList?.get(position)?.merk
        holder.lbStok.text = arrayList?.get(position)?.stok.toString()
        holder.lbSatuan.text = arrayList?.get(position)?.satuan.toString().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        holder.lbHarga.text = arrayList?.get(position)?.harga.toString().formatRupiah()
        holder.lbKodePemasok.text = arrayList?.get(position)?.kodePemasok
        holder.cvList.setOnClickListener {
            val i = Intent(context, KelolaBarangActivity::class.java)
            i.putExtra("editmode", "1")
            i.putExtra("kode_barang", arrayList?.get(position)?.kodeBarang)
            i.putExtra("nama_barang", arrayList?.get(position)?.namaBarang)
            i.putExtra("merk", arrayList?.get(position)?.merk)
            i.putExtra("stok", arrayList?.get(position)?.stok)
            i.putExtra("satuan", arrayList?.get(position)?.satuan)
            i.putExtra("harga", arrayList?.get(position)?.harga)
            i.putExtra("kode_pemasok", arrayList?.get(position)?.kodePemasok)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
        }
    }

    override fun getItemCount(): Int = arrayList!!.size

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        var lbKodeBarang: TextView = view.findViewById(R.id.lbKodeBarang)
        var lbNamaBarang: TextView = view.findViewById(R.id.lbNamaBarang)
        var lbMerkBarang: TextView = view.findViewById(R.id.lbMerkBarang)
        var lbStok: TextView = view.findViewById(R.id.lbStok)
        var lbSatuan: TextView = view.findViewById(R.id.lbSatuan)
        var lbHarga: TextView = view.findViewById(R.id.lbHarga)
        var lbKodePemasok: TextView = view.findViewById(R.id.lbKodePemasok)
        var cvList: CardView = view.findViewById(R.id.cvList)


    }



}