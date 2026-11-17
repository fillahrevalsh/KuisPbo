package com.mycompany.maintoko;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SistemToko implements Serializable {
    private String namaToko;
    private HashMap<String, Pelanggan> daftarPelanggan = new HashMap<>();
    private HashMap<String, Produk> daftarProduk = new HashMap<>();
    private ArrayList<String> daftarTransaksi = new ArrayList<>();

    public SistemToko(String namaToko) {
        this.namaToko = namaToko;
    }

    public void tambahPelanggan(String id, String nama) {
        daftarPelanggan.put(id, new Pelanggan(id, nama));
    }

    public void tambahProduk(String kode, String nama, int stok) {
        daftarProduk.put(kode, new Produk(kode, nama, stok));
    }

    // Baca file, format tiap baris: ID-ACT-01/KD-OBJ-A/JML-3/
    public void jalankanTransaksiDariFile(String namaFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(namaFile));
        try {
            String baris;
            while ((baris = reader.readLine()) != null) {
                baris = baris.trim();
                if (baris.isEmpty()) {
                    continue;
                }
                try {
                    prosesBaris(baris);
                } catch (TransaksiException e) {
                    System.out.println("[GAGAL] " + e.getMessage());
                }
            }
        } finally {
            reader.close();
        }
    }

    private void prosesBaris(String baris) throws TransaksiException {
        // split("/") -> ["ID-ACT-01", "KD-OBJ-A", "JML-3"]
        String[] bagian = baris.split("/");
        if (bagian.length != 3) {
            throw new TransaksiException("Format baris salah: " + baris);
        }

        // buang awalan "ID-" dan "KD-" (3 huruf), "JML-" (4 huruf)
        String idPelanggan = bagian[0].substring(3);
        String kodeProduk = bagian[1].substring(3);
        int jumlah;
        try {
            jumlah = Integer.parseInt(bagian[2].substring(4));
        } catch (NumberFormatException e) {
            throw new TransaksiException("JML bukan angka: " + baris);
        }

        Pelanggan pelanggan = daftarPelanggan.get(idPelanggan);
        if (pelanggan == null) {
            throw new TransaksiException("Pelanggan " + idPelanggan + " tidak terdaftar");
        }
        Produk produk = daftarProduk.get(kodeProduk);
        if (produk == null) {
            throw new TransaksiException("Produk " + kodeProduk + " tidak terdaftar");
        }

        produk.kurangiStok(jumlah);
        String catatan = pelanggan.getNama() + " membeli " + jumlah + " x " + produk.getNama();
        daftarTransaksi.add(catatan);
        System.out.println("[BERHASIL] " + catatan);
    }

    public void simpanLaporanAkhir() {
        System.out.println("--- MENYIMPAN LAPORAN AKHIR ---");

        // 1. Serialisasi objek ke snapshot.obj
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("snapshot.obj"));
            out.writeObject(this);
            out.close();
            System.out.println("[OK] Serialisasi objek tersimpan di snapshot.obj");
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal serialisasi: " + e.getMessage());
        }

        // 2. Laporan Daftar Kunci (ID saja) pakai keySet() ke laporan.txt
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("laporan.txt"));
            writer.println("=== DAFTAR KUNCI (ID SAJA) - " + namaToko + " ===");
            writer.println();
            writer.println("ID Pelanggan:");
            for (String id : daftarPelanggan.keySet()) {
                writer.println(id);
            }
            writer.println();
            writer.println("Kode Produk:");
            for (String kode : daftarProduk.keySet()) {
                writer.println(kode);
            }
            writer.close();
            System.out.println("[OK] Laporan teks tersimpan di laporan.txt");
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menulis laporan.txt: " + e.getMessage());
        }
    }
}
