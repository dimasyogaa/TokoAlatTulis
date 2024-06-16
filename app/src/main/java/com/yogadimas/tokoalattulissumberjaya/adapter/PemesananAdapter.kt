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
import com.yogadimas.tokoalattulissumberjaya.activity.CetakPemesananActivity
import com.yogadimas.tokoalattulissumberjaya.activity.KelolaBarangActivity
import com.yogadimas.tokoalattulissumberjaya.activity.KelolaPemesananActivity
import com.yogadimas.tokoalattulissumberjaya.helper.formatRupiah
import com.yogadimas.tokoalattulissumberjaya.model.Pemesanan

class PemesananAdapter(private val context: Context, private val arrayList: ArrayList<Pemesanan>) :
    RecyclerView.Adapter<PemesananAdapter.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view)  {
        var lbKodePesanan: TextView = view.findViewById(R.id.lbKodePesanan)
        var lbKodePemasok: TextView = view.findViewById(R.id.lbKodePemasok)
        var lbKodeBarang: TextView = view.findViewById(R.id.lbKodeBarang)
        var lbNamaBarang: TextView = view.findViewById(R.id.lbNamaBarang)
        var lbJumlah: TextView = view.findViewById(R.id.lbJumlah)
        var lbTotalHarga: TextView = view.findViewById(R.id.lbTotalHarga)

        var cvList: CardView = view.findViewById(R.id.cvList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.pemesanan_list, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.lbKodePesanan.text = arrayList?.get(position)?.kodePesanan.toString()
        holder.lbKodePemasok.text = arrayList?.get(position)?.kodePemasok
        holder.lbKodeBarang.text = arrayList?.get(position)?.kodeBarang
        holder.lbNamaBarang.text = arrayList?.get(position)?.namaBarang
        holder.lbJumlah.text = arrayList?.get(position)?.jumlah.toString()
        holder.lbTotalHarga.text = arrayList?.get(position)?.totalHarga.toString().formatRupiah()
        holder.cvList.setOnClickListener {
            val i = Intent(context, CetakPemesananActivity::class.java)
            i.putExtra("detail_transaksi", "1")
            i.putExtra("kode_pesanan", arrayList?.get(position)?.kodePesanan)
            i.putExtra("kode_pemasok", arrayList?.get(position)?.kodePemasok)
            i.putExtra("nama_pemasok", arrayList?.get(position)?.namaPemasok)
            i.putExtra("kode_barang", arrayList?.get(position)?.kodeBarang)
            i.putExtra("nama_barang", arrayList?.get(position)?.namaBarang)
            i.putExtra("merk", arrayList?.get(position)?.merk)
            i.putExtra("stok",  arrayList?.get(position)?.stok)
            i.putExtra("satuan",  arrayList?.get(position)?.satuan)
            i.putExtra("jumlah", arrayList?.get(position)?.jumlah)
            i.putExtra("harga", arrayList?.get(position)?.harga)
            i.putExtra("total_harga", arrayList?.get(position)?.totalHarga)
            i.putExtra("tgl_pesan", arrayList?.get(position)?.tglPesan)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
        }
    }
    override fun getItemCount():Int = arrayList!!.size
}
