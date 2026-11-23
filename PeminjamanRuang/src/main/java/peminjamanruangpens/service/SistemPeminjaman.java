package peminjamanruangpens.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import peminjamanruangpens.model.*;

import java.time.LocalDate;

public class SistemPeminjaman {

    public record Hasil(boolean sukses, String pesan) {
        public static Hasil ok(String pesan) { return new Hasil(true, pesan); }
        public static Hasil gagal(String pesan) { return new Hasil(false, pesan); }
    }

    private final ObservableList<Ruang> daftarRuang = FXCollections.observableArrayList();
    private final ObservableList<Member> daftarMember = FXCollections.observableArrayList();
    private final ObservableList<Peminjaman> riwayat = FXCollections.observableArrayList();

    public SistemPeminjaman() {
        // Data awal: berbagai subclass Ruang (POLIMORFISME & Desain PENSpace)
        daftarRuang.add(new RuangKelas("Ruang Seminar D4.01", 30, "Gedung D4 · Lantai 1", "/images/ruang-seminar.png"));
        daftarRuang.add(new Auditorium("Auditorium Pascasarjana", 120, "Gedung Pascasarjana · Lantai 3", "/images/auditorium.png"));
        daftarRuang.add(new LabKomputer("Ruang Diskusi BAAK.02", 15, "Gedung BAAK · Lantai 2", "/images/ruang-diskusi.png"));
        daftarRuang.add(new LabKomputer("Lab Jaringan & Komputer", 35, "Gedung D3 · Lantai 2", "/images/ruang-diskusi.png"));
        daftarRuang.add(new RuangKelas("Ruang Kelas Teori B201", 60, "Gedung D4 · Lantai 2", "/images/ruang-seminar.png"));

        // Data awal: berbagai subclass Member (POLIMORFISME)
        daftarMember.add(new MemberMahasiswa("Luthfi Bahrur Rozaq", "069"));
        daftarMember.add(new MemberMahasiswa("Ahmad Fauzi", "012"));
        daftarMember.add(new MemberDosen("Dr. Ir. Budi Santoso, M.T.", "101"));
    }

    public ObservableList<Ruang> getDaftarRuang() { return daftarRuang; }
    public ObservableList<Member> getDaftarMember() { return daftarMember; }
    public ObservableList<Peminjaman> getRiwayat() { return riwayat; }

    public Member cariMember(String nrp) {
        for (Member m : daftarMember) {
            if (m.getNrp().equals(nrp)) return m;
        }
        return null;
    }

    public long getJumlahPeminjamanAktif() {
        return riwayat.stream().filter(Peminjaman::isAktif).count();
    }

    public Hasil registrasiMember(String nama, String nrp, String tipe) {
        if (nama == null || nama.isBlank() || nrp == null || nrp.isBlank()) {
            return Hasil.gagal("Nama dan NRP tidak boleh kosong!");
        }
        nama = nama.trim();
        nrp = nrp.trim();
        if (!nrp.matches("\\d{3}")) {
            return Hasil.gagal("NRP harus berupa 3 digit angka!");
        }
        if (tipe == null) {
            return Hasil.gagal("Pilih tipe member terlebih dahulu!");
        }
        if (cariMember(nrp) != null) {
            return Hasil.gagal("NRP " + nrp + " sudah terdaftar di sistem.");
        }

        // POLIMORFISME: referensi bertipe Member, objek aslinya MemberMahasiswa / MemberDosen
        Member baru = switch (tipe) {
            case "Dosen" -> new MemberDosen(nama, nrp);
            default -> new MemberMahasiswa(nama, nrp);
        };
        daftarMember.add(baru);
        return Hasil.ok("Member " + nama + " (" + baru.getTipe() + ") berhasil didaftarkan.");
    }

    public Hasil tambahRuangan(String nama, String kapasitasStr, String jenis) {
        if (nama == null || nama.isBlank() || kapasitasStr == null || kapasitasStr.isBlank()) {
            return Hasil.gagal("Nama ruangan dan kapasitas tidak boleh kosong!");
        }
        if (jenis == null) {
            return Hasil.gagal("Pilih jenis ruangan terlebih dahulu!");
        }
        nama = nama.trim();
        int kapasitas;
        try {
            kapasitas = Integer.parseInt(kapasitasStr.trim());
        } catch (NumberFormatException e) {
            return Hasil.gagal("Kapasitas harus berupa angka yang valid!");
        }
        if (kapasitas <= 0) {
            return Hasil.gagal("Kapasitas ruangan harus lebih besar dari 0!");
        }
        for (Ruang r : daftarRuang) {
            if (r.getNamaRuang().equalsIgnoreCase(nama)) {
                return Hasil.gagal("Ruangan dengan nama tersebut sudah terdaftar!");
            }
        }

        // POLIMORFISME: semua disimpan sebagai Ruang
        Ruang baru = switch (jenis) {
            case "Lab Komputer" -> new LabKomputer(nama, kapasitas);
            case "Auditorium" -> new Auditorium(nama, kapasitas);
            default -> new RuangKelas(nama, kapasitas);
        };
        daftarRuang.add(baru);
        return Hasil.ok("Ruangan " + nama + " (" + baru.getJenis() + ") berhasil ditambahkan.");
    }

    public Hasil pinjam(Ruang ruang, LocalDate tanggal, int idSesi, String nrp) {
        if (ruang == null || tanggal == null) {
            return Hasil.gagal("Ruangan dan tanggal harus dipilih!");
        }
        if (tanggal.isBefore(LocalDate.now())) {
            return Hasil.gagal("Tidak bisa meminjam untuk tanggal yang sudah lewat!");
        }
        if (idSesi < 1 || idSesi > Ruang.JUMLAH_SESI) {
            return Hasil.gagal("Nomor sesi tidak valid!");
        }
        if (nrp == null || !nrp.matches("\\d{3}")) {
            return Hasil.gagal("NRP harus berupa 3 digit angka!");
        }
        Member member = cariMember(nrp);
        if (member == null) {
            return Hasil.gagal("NRP " + nrp + " tidak terdaftar. Registrasi dulu di menu Data Member!");
        }

        // POLIMORFISME: kuota berbeda tergantung subclass Member
        long terpakai = riwayat.stream()
                .filter(p -> p.isAktif() && p.getMember() == member && p.getTanggal().equals(tanggal))
                .count();
        if (terpakai >= member.getKuotaPerHari()) {
            return Hasil.gagal(member.getNama() + " (" + member.getTipe() + ") sudah mencapai batas kuota "
                    + member.getKuotaPerHari() + " sesi/hari.");
        }

        Sesi sesi = ruang.getSesiSpesifik(tanggal, idSesi);
        if (!sesi.pinjam(member)) {
            return Hasil.gagal("Maaf, sesi " + idSesi + " tersebut sudah dipinjam orang lain.");
        }
        riwayat.add(0, new Peminjaman(ruang, tanggal, sesi, member));
        return Hasil.ok("Sukses! Peminjaman " + ruang.getNamaRuang() + " (Sesi " + idSesi + ") berhasil atas nama " + member.getNama() + ".");
    }

    public Hasil batalkan(Peminjaman p) {
        if (p == null || !p.isAktif()) {
            return Hasil.gagal("Peminjaman ini sudah dibatalkan.");
        }
        p.getSesi().batal();
        p.tandaiBatal();
        return Hasil.ok("Peminjaman " + p.getRuang().getNamaRuang() + " Sesi "
                + p.getSesi().getIdSesi() + " berhasil dibatalkan.");
    }
}
