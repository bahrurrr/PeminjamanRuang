package peminjamanruangpens.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class BookingProcessCard extends VBox {
    public BookingProcessCard() {
        super(12);
        Label kicker = new Label("ALUR PEMINJAMAN");
        kicker.getStyleClass().add("process-kicker");
        Label count = new Label("3 LANGKAH");
        count.getStyleClass().add("process-count");
        HBox top = new HBox(kicker, count);
        top.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(kicker, Priority.ALWAYS);

        Label title = new Label("Ruang pas, langsung ajukan.");
        title.getStyleClass().add("process-title");
        title.setWrapText(true);

        getChildren().addAll(
                top,
                title,
                new VBox(10,
                        createStep("01", "Pilih ruang & tanggal", "Cek ketersediaan sesi kosong"),
                        createStep("02", "Pilih nomor sesi", "Sesi 1, 2, atau sesi 3"),
                        createStep("03", "Masukkan NRP member", "Sistem memvalidasi kuota pinjam"))
        );
        setMinWidth(300);
        setPrefWidth(330);
        setMaxWidth(330);
        getStyleClass().add("process-card");
    }

    private HBox createStep(String number, String title, String detail) {
        Label stepNumber = new Label(number);
        stepNumber.getStyleClass().add("step-number");
        Label stepTitle = new Label(title);
        stepTitle.getStyleClass().add("step-title");
        Label stepDetail = new Label(detail);
        stepDetail.getStyleClass().add("step-detail");

        HBox row = new HBox(10, stepNumber, new VBox(2, stepTitle, stepDetail));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }
}
