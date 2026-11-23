package peminjamanruangpens.model;

import peminjamanruangpens.interfaces.Describable;

/**
 * ABSTRACT CLASS (induk). Subclass wajib mengisi getTipe() dan getKuotaPerHari().
 */
public abstract class Member implements Describable {
    private final String nama;
    private final String nrp;

    protected Member(String nama, String nrp) {
        this.nama = nama;
        this.nrp = nrp;
    }

    public String getNama() { return nama; }
    public String getNrp() { return nrp; }

    /** Jenis member, diisi oleh subclass. */
    public abstract String getTipe();

    /** Batas jumlah sesi yang boleh dipinjam per hari, diisi oleh subclass. */
    public abstract int getKuotaPerHari();

    @Override
    public String getRingkasan() {
        return getTipe() + " - " + nama + " (" + nrp + ") [kuota: " + getKuotaPerHari() + " sesi/hari]";
    }

    @Override
    public String toString() {
        return nama + " (" + nrp + " - " + getTipe() + ")";
    }
}
