package peminjamanruangpens.interfaces;

import peminjamanruangpens.model.Member;

/**
 * INTERFACE: kontrak untuk apa pun yang bisa dipinjam.
 * Dipakai oleh Sesi, dan nanti bisa dipakai kelas lain (mis. Peralatan).
 */
public interface Bookable {
    boolean pinjam(Member peminjam);
    boolean batal();
    boolean isTersedia();
    String getStatus();
}
