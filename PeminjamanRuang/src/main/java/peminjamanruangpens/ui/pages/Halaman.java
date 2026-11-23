package peminjamanruangpens.ui.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import peminjamanruangpens.service.SistemPeminjaman;
import peminjamanruangpens.ui.components.PageHeader;

/**
 * ABSTRACT CLASS: dasar semua halaman/tab dalam aplikasi.
 * Menerapkan Template Method Pattern untuk menyusun header + isi halaman.
 */
public abstract class Halaman {
    protected final SistemPeminjaman sistem;
    private Node tampilan;

    protected Halaman(SistemPeminjaman sistem) {
        this.sistem = sistem;
    }

    public abstract String getNamaMenu();
    public abstract String getJudul();
    public abstract String getSubjudul();

    protected abstract Node buatIsi();
    protected boolean hasStandardHeader() { return true; }

    /** Template Method: merakit header standar + isi spesifik subclass. */
    public Node getTampilan() {
        if (tampilan == null) {
            VBox wadah = new VBox(18);
            wadah.setMaxWidth(1260);

            if (hasStandardHeader()) {
                wadah.getChildren().add(new PageHeader(getJudul(), getSubjudul()));
            }

            wadah.getChildren().add(buatIsi());
            wadah.setPadding(new Insets(24, 38, 48, 38));

            StackPane wrapper = new StackPane(wadah);
            wrapper.setAlignment(Pos.TOP_CENTER);
            wrapper.getStyleClass().add("page-content");
            tampilan = wrapper;
        }
        return tampilan;
    }

    protected Label buatLabelField(String teks) {
        Label l = new Label(teks);
        l.getStyleClass().add("field-label");
        return l;
    }
}
