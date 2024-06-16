package com.yogadimas.tokoalattulissumberjaya.api

import android.provider.Telephony.Carriers.SERVER

class Api {

    companion object {

        // private val SERVER = "https://codimasproduction.nasihosting.com/aJyD7B/"
        // private val SERVER = "http://192.168.43.193/me/Kuliah/AplikasiMobile/RESTAPI/"
        private val SERVER = "http://192.168.106.106/me/Kuliah/AplikasiMobile/RESTAPI/"
        // private val SERVER = "http://10.0.2.2/me/Kuliah/AplikasiMobile/RESTAPI/"

        private val SERVER_BARANG = SERVER + "barang/"
        private val SERVER_PEMASOK =  SERVER + "pemasok/"
        private val SERVER_PEMESANAN =  SERVER + "pemesanan/"

        val CREATE_BARANG = SERVER_BARANG + "create_barang.php"
        val READ_BARANG = SERVER_BARANG + "read_barang.php"
        val UPDATE_BARANG = SERVER_BARANG + "update_barang.php"
        val DELETE_BARANG = SERVER_BARANG + "delete_barang.php"
        val ADD_STOK_BARANG = SERVER_BARANG + "add_stok_barang.php"

        val CREATE_PEMASOK = SERVER_PEMASOK + "create_pemasok.php"
        val READ_PEMASOK = SERVER_PEMASOK + "read_pemasok.php"
        val READ_PEMASOK_BARANG = SERVER_PEMASOK + "read_pemasok_barang.php"
        val UPDATE_PEMASOK = SERVER_PEMASOK + "update_pemasok.php"
        val DELETE_PEMASOK = SERVER_PEMASOK + "delete_pemasok.php"

        val CREATE_PEMESANAN = SERVER_PEMESANAN + "create_pemesanan.php"
        val READ_PEMESANAN = SERVER_PEMESANAN + "read_pemesanan.php"
        val CLEAR_PEMESANAN = SERVER_PEMESANAN + "clear_pemesanan.php"

    }
}
