package peminjamanruangpens.ui.dialogs;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import peminjamanruangpens.model.Member;
import peminjamanruangpens.model.Ruang;
import peminjamanruangpens.model.Sesi;
import peminjamanruangpens.service.SistemPeminjaman;
import peminjamanruangpens.ui.components.UiStyles;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public class BookingDialog {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("d MMM yyyy", new Locale("id", "ID"));

    private final SistemPeminjaman sistem;

    public BookingDialog(SistemPeminjaman sistem) {
        this.sistem = sistem;
    }

    public Optional<SistemPeminjaman.Hasil> show(Ruang ruang, LocalDate tanggal) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Pengajuan Peminjaman Ruang");

        Label kicker = new Label("PERMOHONAN PEMINJAMAN");
        kicker.getStyleClass().add("dialog-kicker");
        Label title = new Label(ruang.getNamaRuang());
        title.getStyleClass().add("dialog-title");
        Label description = new Label("Pilih nomor sesi dan masukkan NRP member terdaftar.");
        description.getStyleClass().add("dialog-copy");
        VBox header = new VBox(4, kicker, title, description);

        GridPane summary = new GridPane();
        summary.setHgap(16);
        summary.setVgap(8);
        summary.getStyleClass().add("booking-summary");
        addSummaryRow(summary, 0, "Lokasi", ruang.getLokasi());
        addSummaryRow(summary, 1, "Tanggal", DATE_FORMAT.format(tanggal));
        addSummaryRow(summary, 2, "Kapasitas", ruang.getKapasitas() + " orang");
        addSummaryRow(summary, 3, "Fasilitas", ruang.getFasilitas());

        Label sessionLabel = new Label("Pilih Sesi yang Ingin Dipinjam:");
        sessionLabel.getStyleClass().add("dialog-field-label");
        Sesi[] schedule = ruang.getJadwalHarian(tanggal);
        ComboBox<String> sessionPicker = new ComboBox<>();
        for (Sesi session : schedule) {
            String status = session.isTersedia() ? " [Tersedia]" : " [Dipinjam]";
            sessionPicker.getItems().add("Sesi " + session.getIdSesi()
                    + " (" + session.getRentangWaktu() + ")" + status);
        }
        sessionPicker.getSelectionModel().selectFirst();
        sessionPicker.setMaxWidth(Double.MAX_VALUE);
        sessionPicker.getStyleClass().add("field-control");

        Label nrpLabel = new Label("NRP Peminjam (3 digit):");
        nrpLabel.getStyleClass().add("dialog-field-label");
        TextField nrpField = new TextField();
        nrpField.setPromptText("Contoh: 069 (Mahasiswa) atau 101 (Dosen)");
        nrpField.getStyleClass().add("field-control");

        Label memberInfo = new Label();
        memberInfo.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        nrpField.textProperty().addListener((observable, oldValue, value) -> {
            if (value != null && value.matches("\\d{3}")) {
                Member member = sistem.cariMember(value.trim());
                if (member == null) {
                    memberInfo.setText("✗ NRP tidak ditemukan! Silakan daftar di Data Member");
                    memberInfo.setStyle("-fx-text-fill: #9b1c1c;");
                } else {
                    memberInfo.setText("✓ " + member.getNama() + " (" + member.getTipe()
                            + " - Kuota: " + member.getKuotaPerHari() + "/hari)");
                    memberInfo.setStyle("-fx-text-fill: #1f7a42;");
                }
            } else {
                memberInfo.setText("");
            }
        });

        VBox content = new VBox(14,
                header,
                summary,
                new VBox(6, sessionLabel, sessionPicker),
                new VBox(6, nrpLabel, nrpField, memberInfo));
        content.setPrefWidth(420);
        content.getStyleClass().add("dialog-content");

        DialogPane pane = dialog.getDialogPane();
        UiStyles.applyTo(pane);
        pane.setContent(content);
        ButtonType cancel = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType submit = new ButtonType("Konfirmasi Peminjaman", ButtonBar.ButtonData.OK_DONE);
        pane.getButtonTypes().addAll(cancel, submit);
        ((Button) pane.lookupButton(submit)).getStyleClass().add("primary-button");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != submit) {
            return Optional.empty();
        }

        int sessionId = sessionPicker.getSelectionModel().getSelectedIndex() + 1;
        return Optional.of(sistem.pinjam(ruang, tanggal, sessionId, nrpField.getText()));
    }

    private void addSummaryRow(GridPane grid, int row, String labelText, String valueText) {
        Label label = new Label(labelText);
        label.getStyleClass().add("summary-label");
        Label value = new Label(valueText);
        value.getStyleClass().add("summary-value");
        value.setWrapText(true);
        grid.add(label, 0, row);
        grid.add(value, 1, row);
    }
}
