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
import com.yogadimas.tokoalattulissumberjaya.activity.KelolaPemasokActivity
import com.yogadimas.tokoalattulissumberjaya.model.Pemasok

class PemasokAdapter(private val context: Context, private val arrayList: ArrayList<Pemasok>) :
    RecyclerView.Adapter<PemasokAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.pemasok_list, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.lbKodePemasok.text = arrayList?.get(position)?.kodePemasok
        holder.lbNamaPemasok.text = arrayList?.get(position)?.namaPemasok
        holder.lbJenisKelamin.text = arrayList?.get(position)?.jenisKelamin
        holder.lbKota.text = arrayList?.get(position)?.kota
        holder.cvList.setOnClickListener {
            val i = Intent(context, KelolaPemasokActivity::class.java)
            i.putExtra("editmode", "1")
            i.putExtra("kode_pemasok", arrayList?.get(position)?.kodePemasok)
            i.putExtra("nama_pemasok", arrayList?.get(position)?.namaPemasok)
            i.putExtra("jenis_kelamin", arrayList?.get(position)?.jenisKelamin)
            i.putExtra("alamat", arrayList?.get(position)?.alamat)
            i.putExtra("kode_pos", arrayList?.get(position)?.kodePos)
            i.putExtra("kota", arrayList?.get(position)?.kota)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
        }
    }

    override fun getItemCount(): Int = arrayList!!.size

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        var lbKodePemasok: TextView = view.findViewById(R.id.lbKodePemasok)
        var lbNamaPemasok: TextView = view.findViewById(R.id.lbNamaPemasok)
        var lbJenisKelamin: TextView = view.findViewById(R.id.lbJenisKelamin)
        var lbKota: TextView = view.findViewById(R.id.lbKota)
        var cvList: CardView = view.findViewById(R.id.cvList)
    }

}