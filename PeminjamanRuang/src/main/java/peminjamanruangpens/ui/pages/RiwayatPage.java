package peminjamanruangpens.ui.pages;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import peminjamanruangpens.model.Peminjaman;
import peminjamanruangpens.service.SistemPeminjaman;
import peminjamanruangpens.ui.components.CardPanel;
import peminjamanruangpens.ui.components.Notifikasi;

import java.time.format.DateTimeFormatter;

public class RiwayatPage extends Halaman {
    private static final DateTimeFormatter TGL_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter WKT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private TableView<Peminjaman> tabel;
    private Notifikasi notif;

    public RiwayatPage(SistemPeminjaman sistem) { super(sistem); }

    @Override public String getNamaMenu() { return "Riwayat Peminjaman"; }
    @Override public String getJudul() { return "Riwayat Peminjaman"; }
    @Override public String getSubjudul() { return "Daftar seluruh transaksi peminjaman dan pembatalan."; }

    @Override
    protected Node buatIsi() {
        notif = new Notifikasi();

        tabel = new TableView<>(sistem.getRiwayat());
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Peminjaman, String> colWaktu = new TableColumn<>("Waktu Transaksi");
        colWaktu.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getWaktuTransaksi().format(WKT_FMT)));
        colWaktu.setPrefWidth(130);

        TableColumn<Peminjaman, String> colTgl = new TableColumn<>("Tgl Pakai");
        colTgl.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTanggal().format(TGL_FMT)));
        colTgl.setPrefWidth(95);

        TableColumn<Peminjaman, String> colRuang = new TableColumn<>("Ruangan");
        colRuang.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRuang().getNamaRuang()));
        colRuang.setPrefWidth(130);

        TableColumn<Peminjaman, String> colSesi = new TableColumn<>("Sesi");
        colSesi.setCellValueFactory(c -> new SimpleStringProperty(
                "Sesi " + c.getValue().getSesi().getIdSesi() + " (" + c.getValue().getSesi().getRentangWaktu() + ")"));
        colSesi.setPrefWidth(140);

        TableColumn<Peminjaman, String> colMember = new TableColumn<>("Peminjam");
        colMember.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getMember().getNama() + " (" + c.getValue().getMember().getTipe() + ")"));
        colMember.setPrefWidth(180);

        TableColumn<Peminjaman, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Peminjaman p = getTableRow().getItem();
                    Label b = new Label(p.isAktif() ? "Aktif" : "Dibatalkan");
                    b.getStyleClass().setAll("badge", p.isAktif() ? "badge-available" : "badge-past");
                    setGraphic(b);
                    setText(null);
                }
            }
        });
        colStatus.setPrefWidth(100);

        TableColumn<Peminjaman, Void> colAksi = new TableColumn<>("Aksi");
        colAksi.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Batalkan");
            {
                btn.getStyleClass().add("btn-danger-sm");
                btn.setOnAction(e -> {
                    Peminjaman p = getTableRow().getItem();
                    if (p != null) {
                        var hasil = sistem.batalkan(p);
                        if (hasil.sukses()) {
                            notif.sukses(hasil.pesan());
                            tabel.refresh();
                        } else {
                            notif.error(hasil.pesan());
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Peminjaman p = getTableRow().getItem();
                    btn.setDisable(!p.isAktif());
                    setGraphic(btn);
                }
            }
        });
        colAksi.setPrefWidth(100);

        tabel.getColumns().addAll(colWaktu, colTgl, colRuang, colSesi, colMember, colStatus, colAksi);
        tabel.setPlaceholder(new Label("Belum ada riwayat transaksi peminjaman"));

        CardPanel wadah = new CardPanel(tabel);
        VBox.setVgrow(wadah, Priority.ALWAYS);

        return new VBox(16, notif, wadah);
    }
}
