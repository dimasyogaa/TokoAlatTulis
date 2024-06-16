package com.yogadimas.tokoalattulissumberjaya.model

data class Pemesanan(
    val kodePesanan: Int?,
    val kodePemasok: String?,
    val namaPemasok: String?,
    val kodeBarang: String?,
    val namaBarang: String?,
    val merk: String?,
    val stok: Int?,
    val satuan: String?,
    val jumlah: Int?,
    val harga: Int?,
    val totalHarga: Int?,
    val tglPesan: String?,
)