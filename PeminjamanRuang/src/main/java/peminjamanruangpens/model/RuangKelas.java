package peminjamanruangpens.model;

public class RuangKelas extends Ruang {
    public RuangKelas(String namaRuang, int kapasitas) {
        super(namaRuang, kapasitas, "Gedung D3 · Lantai 1", "/images/ruang-seminar.png");
    }

    public RuangKelas(String namaRuang, int kapasitas, String lokasi, String gambarPath) {
        super(namaRuang, kapasitas, lokasi, gambarPath);
    }

    @Override public String getJenis() { return "Ruang Kelas"; }
    @Override public String getFasilitas() { return "Papan tulis, proyektor, AC"; }
}
