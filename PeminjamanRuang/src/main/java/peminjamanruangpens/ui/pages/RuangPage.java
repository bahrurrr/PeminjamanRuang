package peminjamanruangpens.ui.pages;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import peminjamanruangpens.model.Ruang;
import peminjamanruangpens.service.SistemPeminjaman;
import peminjamanruangpens.ui.components.CardPanel;
import peminjamanruangpens.ui.components.Notifikasi;

public class RuangPage extends Halaman {
    private TextField txtNama;
    private TextField txtKapasitas;
    private ComboBox<String> comboJenis;
    private Notifikasi notif;
    private TableView<Ruang> tabel;

    public RuangPage(SistemPeminjaman sistem) { super(sistem); }

    @Override public String getNamaMenu() { return "Data Ruangan"; }
    @Override public String getJudul() { return "Data Ruangan"; }
    @Override public String getSubjudul() { return "Tambah dan lihat daftar ruangan yang dapat dipinjam."; }

    @Override
    protected Node buatIsi() {
        notif = new Notifikasi();
        HBox utama = new HBox(20, buatForm(), buatTabel());
        return new VBox(16, notif, utama);
    }

    private Node buatForm() {
        txtNama = new TextField();
        txtNama.setPromptText("Contoh: Lab Robotika");

        txtKapasitas = new TextField();
        txtKapasitas.setPromptText("Jumlah orang (angka)");

        comboJenis = new ComboBox<>();
        comboJenis.getItems().addAll("Lab Komputer", "Ruang Kelas", "Auditorium");
        comboJenis.getSelectionModel().selectFirst();
        comboJenis.setMaxWidth(Double.MAX_VALUE);

        Button btnTambah = new Button("Tambah Ruangan");
        btnTambah.getStyleClass().add("primary-button");
        btnTambah.setMaxWidth(Double.MAX_VALUE);
        btnTambah.setOnAction(e -> submit());

        VBox form = new CardPanel(12,
                buatLabelField("Nama Ruangan"), txtNama,
                buatLabelField("Kapasitas"), txtKapasitas,
                buatLabelField("Jenis Ruangan"), comboJenis,
                btnTambah);
        form.setPrefWidth(320);
        return form;
    }

    private Node buatTabel() {
        tabel = new TableView<>(sistem.getDaftarRuang());
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Ruang, String> colNama = new TableColumn<>("Nama Ruangan");
        colNama.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNamaRuang()));
        colNama.setPrefWidth(160);

        TableColumn<Ruang, String> colJenis = new TableColumn<>("Jenis");
        colJenis.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getJenis()));
        colJenis.setPrefWidth(120);

        TableColumn<Ruang, String> colKap = new TableColumn<>("Kapasitas");
        colKap.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKapasitas() + " orang"));
        colKap.setPrefWidth(100);

        TableColumn<Ruang, String> colFas = new TableColumn<>("Fasilitas");
        colFas.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFasilitas()));
        colFas.setPrefWidth(260);

        tabel.getColumns().addAll(colNama, colJenis, colKap, colFas);
        tabel.setPlaceholder(new Label("Belum ada ruangan terdaftar"));

        CardPanel wadah = new CardPanel(tabel);
        HBox.setHgrow(wadah, Priority.ALWAYS);
        return wadah;
    }

    private void submit() {
        var hasil = sistem.tambahRuangan(txtNama.getText(), txtKapasitas.getText(), comboJenis.getValue());
        if (hasil.sukses()) {
            notif.sukses(hasil.pesan());
            txtNama.clear();
            txtKapasitas.clear();
        } else {
            notif.error(hasil.pesan());
        }
    }
}
