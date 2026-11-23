package peminjamanruangpens.model;

public class MemberMahasiswa extends Member {
    public static final int KUOTA_HARIAN = 1;

    public MemberMahasiswa(String nama, String nrp) {
        super(nama, nrp);
    }

    @Override public String getTipe() { return "Mahasiswa"; }
    @Override public int getKuotaPerHari() { return KUOTA_HARIAN; }
}
