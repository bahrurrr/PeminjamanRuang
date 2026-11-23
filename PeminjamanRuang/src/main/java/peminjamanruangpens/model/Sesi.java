package peminjamanruangpens.model;

import peminjamanruangpens.interfaces.Bookable;

public class Sesi implements Bookable {
    private final int idSesi;
    private final String rentangWaktu;
    private boolean tersedia;
    private Member peminjam;

    public Sesi(int idSesi, String rentangWaktu) {
        this.idSesi = idSesi;
        this.rentangWaktu = rentangWaktu;
        this.tersedia = true;
        this.peminjam = null;
    }

    @Override
    public boolean pinjam(Member dataPeminjam) {
        if (this.tersedia) {
            this.tersedia = false;
            this.peminjam = dataPeminjam;
            return true;
        }
        return false;
    }

    @Override
    public boolean batal() {
        if (!this.tersedia) {
            this.tersedia = true;
            this.peminjam = null;
            return true;
        }
        return false;
    }

    @Override public boolean isTersedia() { return tersedia; }
    public int getIdSesi() { return idSesi; }
    public String getRentangWaktu() { return rentangWaktu; }
    public Member getPeminjam() { return peminjam; }

    @Override
    public String getStatus() {
        if (!tersedia && peminjam != null) {
            return "Sesi " + idSesi + " (" + rentangWaktu + ") - Dipinjam oleh: "
                    + peminjam.getNama() + " (" + peminjam.getTipe() + ", " + peminjam.getNrp() + ")";
        }
        return "Sesi " + idSesi + " (" + rentangWaktu + ") - Tersedia";
    }
}
