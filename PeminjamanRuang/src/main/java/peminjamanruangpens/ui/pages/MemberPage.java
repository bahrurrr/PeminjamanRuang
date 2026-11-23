package peminjamanruangpens.ui.pages;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import peminjamanruangpens.model.Member;
import peminjamanruangpens.service.SistemPeminjaman;
import peminjamanruangpens.ui.components.CardPanel;
import peminjamanruangpens.ui.components.Notifikasi;

public class MemberPage extends Halaman {
    private TextField txtNama;
    private TextField txtNrp;
    private ComboBox<String> comboTipe;
    private Notifikasi notif;
    private TableView<Member> tabel;

    public MemberPage(SistemPeminjaman sistem) { super(sistem); }

    @Override public String getNamaMenu() { return "Data Member"; }
    @Override public String getJudul() { return "Data Member"; }
    @Override public String getSubjudul() { return "Kelola mahasiswa dan dosen yang berhak meminjam ruangan."; }

    @Override
    protected Node buatIsi() {
        notif = new Notifikasi();
        HBox utama = new HBox(20, buatForm(), buatTabel());
        return new VBox(16, notif, utama);
    }

    private Node buatForm() {
        txtNama = new TextField();
        txtNama.setPromptText("Nama lengkap");

        txtNrp = new TextField();
        txtNrp.setPromptText("3 digit angka, mis. 069");

        comboTipe = new ComboBox<>();
        comboTipe.getItems().addAll("Mahasiswa", "Dosen");
        comboTipe.getSelectionModel().selectFirst();
        comboTipe.setMaxWidth(Double.MAX_VALUE);

        Label ketKuota = new Label("Mahasiswa: kuota 1 sesi/hari • Dosen: kuota 3 sesi/hari");
        ketKuota.getStyleClass().add("muted");
        ketKuota.setWrapText(true);

        Button btnDaftar = new Button("Daftarkan Member");
        btnDaftar.getStyleClass().add("primary-button");
        btnDaftar.setMaxWidth(Double.MAX_VALUE);
        btnDaftar.setOnAction(e -> submit());

        VBox form = new CardPanel(12,
                buatLabelField("Nama Lengkap"), txtNama,
                buatLabelField("NRP / ID (3 digit)"), txtNrp,
                buatLabelField("Tipe Member"), comboTipe,
                ketKuota,
                btnDaftar);
        form.setPrefWidth(320);
        return form;
    }

    private Node buatTabel() {
        tabel = new TableView<>(sistem.getDaftarMember());
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Member, String> colNrp = new TableColumn<>("NRP");
        colNrp.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNrp()));
        colNrp.setPrefWidth(80);

        TableColumn<Member, String> colNama = new TableColumn<>("Nama");
        colNama.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNama()));
        colNama.setPrefWidth(200);

        TableColumn<Member, String> colTipe = new TableColumn<>("Tipe");
        colTipe.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipe()));
        colTipe.setPrefWidth(110);

        TableColumn<Member, String> colKuota = new TableColumn<>("Kuota/Hari");
        colKuota.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKuotaPerHari() + " sesi"));
        colKuota.setPrefWidth(100);

        tabel.getColumns().addAll(colNrp, colNama, colTipe, colKuota);
        tabel.setPlaceholder(new Label("Belum ada member terdaftar"));

        CardPanel wadah = new CardPanel(tabel);
        HBox.setHgrow(wadah, Priority.ALWAYS);
        return wadah;
    }

    private void submit() {
        var hasil = sistem.registrasiMember(txtNama.getText(), txtNrp.getText(), comboTipe.getValue());
        if (hasil.sukses()) {
            notif.sukses(hasil.pesan());
            txtNama.clear();
            txtNrp.clear();
        } else {
            notif.error(hasil.pesan());
        }
    }
}
