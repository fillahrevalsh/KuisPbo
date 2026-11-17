package com.mycompany.maintoko;

import java.io.Serializable;

public class Produk implements Serializable {
    private String kode;
    private String nama;
    private int stok;

    public Produk(String kode, String nama, int stok) {
        this.kode = kode;
        this.nama = nama;
        this.stok = stok;
    }

    public String getKode() {
        return kode;
    }

    public String getNama() {
        return nama;
    }

    public int getStok() {
        return stok;
    }

    // Kurangi stok, kalau tidak cukup lempar exception
    public void kurangiStok(int jumlah) throws TransaksiException {
        if (jumlah > stok) {
            throw new TransaksiException("Stok " + nama + " tidak cukup (sisa " + stok
                    + ", diminta " + jumlah + ")");
        }
        stok = stok - jumlah;
    }
}
