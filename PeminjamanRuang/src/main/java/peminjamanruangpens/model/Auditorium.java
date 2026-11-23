package peminjamanruangpens.model;

public class Auditorium extends Ruang {
    public Auditorium(String namaRuang, int kapasitas) {
        super(namaRuang, kapasitas, "Gedung Pascasarjana · Lantai 3", "/images/auditorium.png");
    }

    public Auditorium(String namaRuang, int kapasitas, String lokasi, String gambarPath) {
        super(namaRuang, kapasitas, lokasi, gambarPath);
    }

    @Override public String getJenis() { return "Auditorium"; }
    @Override public String getFasilitas() { return "Panggung, sound system, proyektor, 120 kursi"; }
}
