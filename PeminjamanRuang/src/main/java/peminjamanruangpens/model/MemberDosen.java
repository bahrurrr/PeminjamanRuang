package peminjamanruangpens.model;

public class MemberDosen extends Member {
    public static final int KUOTA_HARIAN = 3;

    public MemberDosen(String nama, String nrp) {
        super(nama, nrp);
    }

    @Override public String getTipe() { return "Dosen"; }
    @Override public int getKuotaPerHari() { return KUOTA_HARIAN; }
}
