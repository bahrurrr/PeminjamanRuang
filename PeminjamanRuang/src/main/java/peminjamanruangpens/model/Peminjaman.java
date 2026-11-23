package peminjamanruangpens.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Peminjaman {
    private final Ruang ruang;
    private final LocalDate tanggal;
    private final Sesi sesi;
    private final Member member;
    private final LocalDateTime waktuTransaksi;
    private boolean aktif;

    public Peminjaman(Ruang ruang, LocalDate tanggal, Sesi sesi, Member member) {
        this.ruang = ruang;
        this.tanggal = tanggal;
        this.sesi = sesi;
        this.member = member;
        this.waktuTransaksi = LocalDateTime.now();
        this.aktif = true;
    }

    public Ruang getRuang() { return ruang; }
    public LocalDate getTanggal() { return tanggal; }
    public Sesi getSesi() { return sesi; }
    public Member getMember() { return member; }
    public LocalDateTime getWaktuTransaksi() { return waktuTransaksi; }
    public boolean isAktif() { return aktif; }

    public void tandaiBatal() { this.aktif = false; }
}
