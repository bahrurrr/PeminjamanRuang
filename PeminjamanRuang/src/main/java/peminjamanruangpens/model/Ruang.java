package peminjamanruangpens.model;

import peminjamanruangpens.interfaces.Describable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * ABSTRACT CLASS (induk). Jenis dan fasilitas ditentukan oleh subclass.
 */
public abstract class Ruang implements Describable {
    public static final int JUMLAH_SESI = 3;

    private final String namaRuang;
    private final int kapasitas;
    private final String lokasi;
    private final String gambarPath;
    private final Map<LocalDate, Sesi[]> jadwalPerHari = new HashMap<>();

    protected Ruang(String namaRuang, int kapasitas, String lokasi, String gambarPath) {
        this.namaRuang = namaRuang;
        this.kapasitas = kapasitas;
        this.lokasi = lokasi != null ? lokasi : "Kampus PENS";
        this.gambarPath = gambarPath;
    }

    protected Ruang(String namaRuang, int kapasitas) {
        this(namaRuang, kapasitas, "Gedung Utama · Kampus PENS", "/images/ruang-seminar.png");
    }

    public String getNamaRuang() { return namaRuang; }
    public int getKapasitas() { return kapasitas; }
    public String getLokasi() { return lokasi; }
    public String getGambarPath() { return gambarPath; }

    public abstract String getJenis();
    public abstract String getFasilitas();

    private Sesi[] buatSesiBaru() {
        return new Sesi[] {
                new Sesi(1, "08:00 - 10:00"),
                new Sesi(2, "10:00 - 12:00"),
                new Sesi(3, "13:00 - 15:00")
        };
    }

    public Sesi[] getJadwalHarian(LocalDate tanggal) {
        return jadwalPerHari.computeIfAbsent(tanggal, t -> buatSesiBaru());
    }

    public Sesi getSesiSpesifik(LocalDate tanggal, int idSesi) {
        return getJadwalHarian(tanggal)[idSesi - 1];
    }

    @Override
    public String getRingkasan() {
        return "[" + getJenis() + "] " + namaRuang + " (Kapasitas: " + kapasitas + " org) - " + getFasilitas();
    }

    @Override
    public String toString() {
        return namaRuang + " (" + getJenis() + " - " + kapasitas + " org)";
    }
}
