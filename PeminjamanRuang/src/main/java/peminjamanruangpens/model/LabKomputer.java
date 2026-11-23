package peminjamanruangpens.model;

public class LabKomputer extends Ruang {
    public LabKomputer(String namaRuang, int kapasitas) {
        super(namaRuang, kapasitas, "Gedung D4 · Lantai 2", "/images/ruang-diskusi.png");
    }

    public LabKomputer(String namaRuang, int kapasitas, String lokasi, String gambarPath) {
        super(namaRuang, kapasitas, lokasi, gambarPath);
    }

    @Override public String getJenis() { return "Lab Komputer"; }
    @Override public String getFasilitas() { return "PC praktikum, LAN gigabit, proyektor, AC"; }
}
