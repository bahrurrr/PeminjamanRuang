package peminjamanruangpens.ui.components;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class Notifikasi extends Label {
    private final PauseTransition timer = new PauseTransition(Duration.seconds(4));

    public Notifikasi() {
        setVisible(false);
        setManaged(false);
        timer.setOnFinished(e -> sembunyikan());
    }

    public void sukses(String pesan) {
        tampilkan(pesan, "notif-sukses", "notif-error");
    }

    public void error(String pesan) {
        tampilkan(pesan, "notif-error", "notif-sukses");
    }

    private void tampilkan(String pesan, String tambah, String hapus) {
        setText(pesan);
        getStyleClass().removeAll(hapus);
        if (!getStyleClass().contains("notif")) getStyleClass().add("notif");
        if (!getStyleClass().contains(tambah)) getStyleClass().add(tambah);
        setVisible(true);
        setManaged(true);
        timer.playFromStart();
    }

    public void sembunyikan() {
        setVisible(false);
        setManaged(false);
        setText("");
    }
}
