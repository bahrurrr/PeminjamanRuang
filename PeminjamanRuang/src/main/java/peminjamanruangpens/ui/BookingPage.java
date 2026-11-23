package peminjamanruangpens.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import peminjamanruangpens.model.Member;
import peminjamanruangpens.model.Ruang;
import peminjamanruangpens.model.Sesi;
import peminjamanruangpens.service.SistemPeminjaman;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BookingPage extends Halaman {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("d MMM yyyy", new Locale("id", "ID"));

    private TextField locationField;
    private DatePicker datePicker;
    private ComboBox<Integer> capacityPicker;
    private ComboBox<String> sortPicker;
    private ToggleGroup categoryGroup;
    private FlowPane roomGrid;
    private Label resultsHeading;
    private Label resultsCount;
    private Notifikasi notif;

    public BookingPage(SistemPeminjaman sistem) {
        super(sistem);
    }

    @Override public String getNamaMenu() { return "Cari & Booking Ruang"; }
    @Override public String getJudul() { return "Booking Ruangan"; }
    @Override public String getSubjudul() { return "Pilih ruang kampus, tentukan sesi yang tersedia, dan ajukan peminjaman."; }

    @Override
    protected Node buatIsi() {
        notif = new Notifikasi();

        VBox page = new VBox(0);
        page.getStyleClass().add("page-content");
        page.setMaxWidth(1260);

        roomGrid = new FlowPane();
        roomGrid.setHgap(18);
        roomGrid.setVgap(20);
        roomGrid.setPrefWrapLength(1120);
        roomGrid.getStyleClass().add("room-grid");

        page.getChildren().addAll(
                notif,
                buatHero(),
                buatSearchBar(),
                buatResultsHeading(),
                buatFiltersAndSort(),
                roomGrid
        );

        muatJadwal();
        return page;
    }

    // ---------- HERO SECTION (PENSpace) ----------
    private HBox buatHero() {
        Region accentLine = new Region();
        accentLine.getStyleClass().add("eyebrow-line");
        Label eyebrowText = new Label("SATU KAMPUS PENS, BANYAK KEMUNGKINAN");
        eyebrowText.getStyleClass().add("eyebrow-text");
        HBox eyebrow = new HBox(8, accentLine, eyebrowText);
        eyebrow.setAlignment(Pos.CENTER_LEFT);

        Label firstLine = new Label("Temukan ruang.");
        firstLine.getStyleClass().add("hero-title");
        Label secondLine = new Label("Wujudkan kegiatan.");
        secondLine.getStyleClass().addAll("hero-title", "hero-title-blue");
        VBox headline = new VBox(0, firstLine, secondLine);
        VBox intro = new VBox(12, eyebrow, headline);
        HBox.setHgrow(intro, Priority.ALWAYS);

        HBox hero = new HBox(36, intro, buatProcessCard());
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.getStyleClass().add("hero-section");
        return hero;
    }

    private VBox buatProcessCard() {
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

        VBox steps = new VBox(10,
                buatProcessStep("01", "Pilih ruang & tanggal", "Cek ketersediaan sesi kosong"),
                buatProcessStep("02", "Pilih nomor sesi", "Sesi 1, 2, atau sesi 3"),
                buatProcessStep("03", "Masukkan NRP member", "Sistem memvalidasi kuota pinjam")
        );

        VBox card = new VBox(12, top, title, steps);
        card.setMinWidth(300);
        card.setPrefWidth(330);
        card.setMaxWidth(330);
        card.getStyleClass().add("process-card");
        return card;
    }

    private HBox buatProcessStep(String number, String title, String detail) {
        Label stepNumber = new Label(number);
        stepNumber.getStyleClass().add("step-number");
        Label stepTitle = new Label(title);
        stepTitle.getStyleClass().add("step-title");
        Label stepDetail = new Label(detail);
        stepDetail.getStyleClass().add("step-detail");
        VBox copy = new VBox(2, stepTitle, stepDetail);
        HBox row = new HBox(10, stepNumber, copy);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // ---------- SEARCH PANEL ----------
    private HBox buatSearchBar() {
        locationField = new TextField();
        locationField.setPromptText("Cari nama ruang atau gedung...");
        locationField.getStyleClass().add("field-control");
        locationField.textProperty().addListener((obs, old, val) -> muatJadwal());

        datePicker = new DatePicker(LocalDate.now());
        datePicker.getStyleClass().add("field-control");
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        datePicker.valueProperty().addListener((obs, old, val) -> muatJadwal());

        capacityPicker = new ComboBox<>(FXCollections.observableArrayList(0, 10, 25, 50, 100));
        capacityPicker.setValue(0);
        capacityPicker.setMaxWidth(Double.MAX_VALUE);
        capacityPicker.getStyleClass().add("field-control");
        capacityPicker.valueProperty().addListener((obs, old, val) -> muatJadwal());

        VBox locationBox = buatSearchField("NAMA RUANGAN / GEDUNG", locationField);
        VBox dateBox = buatSearchField("TANGGAL PENGGUNAAN", datePicker);
        VBox capacityBox = buatSearchField("MINIMAL KAPASITAS", capacityPicker);

        Button searchButton = new Button("Refresh Jadwal");
        searchButton.getStyleClass().add("primary-button");
        searchButton.setOnAction(e -> muatJadwal());

        HBox searchBar = new HBox(10,
                locationBox, buatDivider(),
                dateBox, buatDivider(),
                capacityBox, searchButton
        );
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.getStyleClass().add("search-panel");
        return searchBar;
    }

    private VBox buatSearchField(String labelText, Node control) {
        Label label = new Label(labelText);
        label.getStyleClass().add("field-label");
        VBox field = new VBox(5, label, control);
        field.getStyleClass().add("search-field");
        HBox.setHgrow(field, Priority.ALWAYS);
        return field;
    }

    private Region buatDivider() {
        Region divider = new Region();
        divider.getStyleClass().add("search-divider");
        return divider;
    }

    private HBox buatResultsHeading() {
        resultsHeading = new Label("Ruang tersedia di kampus");
        resultsHeading.getStyleClass().add("results-heading");
        resultsCount = new Label();
        resultsCount.getStyleClass().add("results-count");
        HBox titleGroup = new HBox(10, resultsHeading, resultsCount);
        titleGroup.setAlignment(Pos.BASELINE_LEFT);

        HBox row = new HBox(titleGroup);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("results-header");
        return row;
    }

    private HBox buatFiltersAndSort() {
        categoryGroup = new ToggleGroup();
        HBox filters = new HBox(8);
        filters.getStyleClass().add("filter-bar");
        tambahFilter(filters, "Semua ruang", "all", true);
        tambahFilter(filters, "Lab Komputer", "Lab Komputer", false);
        tambahFilter(filters, "Ruang Kelas", "Ruang Kelas", false);
        tambahFilter(filters, "Auditorium", "Auditorium", false);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        sortPicker = new ComboBox<>(FXCollections.observableArrayList("Semua", "Kapasitas terbesar"));
        sortPicker.setValue("Semua");
        sortPicker.getStyleClass().add("sort-picker");
        sortPicker.setOnAction(e -> muatJadwal());

        HBox row = new HBox(12, filters, spacer, sortPicker);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("filter-sort-row");
        return row;
    }

    private void tambahFilter(HBox filters, String label, String category, boolean selected) {
        ToggleButton button = new ToggleButton(label);
        button.setUserData(category);
        button.setToggleGroup(categoryGroup);
        button.getStyleClass().add("filter-button");
        button.setSelected(selected);
        button.setOnAction(e -> muatJadwal());
        filters.getChildren().add(button);
    }

    // ---------- RENDER ROOM CARDS ----------
    public void muatJadwal() {
        if (roomGrid == null) return;
        LocalDate tgl = datePicker != null && datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now();
        String query = locationField != null && locationField.getText() != null ? locationField.getText().trim().toLowerCase() : "";
        int minKap = capacityPicker != null && capacityPicker.getValue() != null ? capacityPicker.getValue() : 0;

        Toggle selectedToggle = categoryGroup != null ? categoryGroup.getSelectedToggle() : null;
        String cat = selectedToggle == null ? "all" : String.valueOf(selectedToggle.getUserData());

        List<Ruang> list = new ArrayList<>(sistem.getDaftarRuang().stream()
                .filter(r -> cat.equals("all") || r.getJenis().equalsIgnoreCase(cat))
                .filter(r -> r.getKapasitas() >= minKap)
                .filter(r -> query.isEmpty() || (r.getNamaRuang() + " " + r.getLokasi()).toLowerCase().contains(query))
                .toList());

        if (sortPicker != null && "Kapasitas terbesar".equals(sortPicker.getValue())) {
            list.sort(Comparator.comparingInt(Ruang::getKapasitas).reversed());
        }

        resultsCount.setText(list.size() + " ruang terdaftar");
        resultsHeading.setText(query.isEmpty() ? "Ruang Kampus PENS (" + DATE_FMT.format(tgl) + ")"
                : "Hasil pencarian: \"" + locationField.getText().trim() + "\"");

        roomGrid.getChildren().clear();
        for (Ruang r : list) {
            roomGrid.getChildren().add(buatRoomCard(r, tgl));
        }
    }

    private Node buatRoomCard(Ruang r, LocalDate tgl) {
        StackPane photoContainer = new StackPane();
        photoContainer.setPrefHeight(175);
        photoContainer.setMinHeight(175);
        photoContainer.setMaxHeight(175);
        photoContainer.getStyleClass().add("room-photo");

        try {
            var url = getClass().getResource(r.getGambarPath());
            if (url != null) {
                ImageView iv = new ImageView(new Image(url.toExternalForm(), 360, 175, false, true));
                Rectangle clip = new Rectangle(350, 175);
                clip.setArcWidth(28);
                clip.setArcHeight(28);
                iv.setClip(clip);
                photoContainer.getChildren().add(iv);
            }
        } catch (Exception ignored) {}

        Label badge = new Label(r.getJenis());
        badge.getStyleClass().add("room-badge");
        StackPane.setAlignment(badge, Pos.TOP_LEFT);
        StackPane.setMargin(badge, new Insets(12));
        photoContainer.getChildren().add(badge);

        Label name = new Label(r.getNamaRuang());
        name.getStyleClass().add("room-name");
        Label loc = new Label(r.getLokasi());
        loc.getStyleClass().add("room-location");
        VBox titleBox = new VBox(2, name, loc);

        Label cap = new Label("Kapasitas " + r.getKapasitas() + " orang");
        cap.getStyleClass().add("room-meta");

        FlowPane amenities = new FlowPane(5, 5);
        amenities.getStyleClass().add("amenities");
        for (String f : r.getFasilitas().split(",")) {
            Label chip = new Label(f.trim());
            chip.getStyleClass().add("amenity-chip");
            amenities.getChildren().add(chip);
        }

        // Tampilkan status sesi hari itu
        Sesi[] jadwal = r.getJadwalHarian(tgl);
        HBox sessionRow = new HBox(6);
        sessionRow.getStyleClass().add("session-row");
        int availCount = 0;
        for (Sesi s : jadwal) {
            Label chip = new Label("S" + s.getIdSesi() + " (" + (s.isTersedia() ? "Kosong" : "Dipinjam") + ")");
            chip.getStyleClass().add(s.isTersedia() ? "session-chip-avail" : "session-chip-booked");
            sessionRow.getChildren().add(chip);
            if (s.isTersedia()) availCount++;
        }

        Region statusDot = new Region();
        statusDot.getStyleClass().add("status-dot");
        Label availText = new Label(availCount + " dari 3 sesi kosong");
        availText.getStyleClass().add("availability-text");
        HBox statusBox = new HBox(6, statusDot, availText);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        Button bookButton = new Button("Pilih Sesi & Pinjam");
        bookButton.getStyleClass().add("book-button");
        bookButton.setOnAction(e -> bukaDialogPeminjaman(r, tgl));

        HBox footer = new HBox(statusBox, bookButton);
        footer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(statusBox, Priority.ALWAYS);

        VBox body = new VBox(10, titleBox, cap, amenities, sessionRow, footer);
        body.getStyleClass().add("room-card-body");

        VBox card = new VBox(photoContainer, body);
        card.setMinWidth(300);
        card.setPrefWidth(350);
        card.setMaxWidth(350);
        card.getStyleClass().add("room-card");
        return card;
    }

    // ---------- DIALOG BOOKING (PENSpace Style) ----------
    private void bukaDialogPeminjaman(Ruang ruang, LocalDate tanggal) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Pengajuan Peminjaman Ruang");

        Label kicker = new Label("PERMOHONAN PEMINJAMAN");
        kicker.getStyleClass().add("dialog-kicker");
        Label title = new Label(ruang.getNamaRuang());
        title.getStyleClass().add("dialog-title");
        Label desc = new Label("Pilih nomor sesi dan masukkan NRP member terdaftar.");
        desc.getStyleClass().add("dialog-copy");
        VBox header = new VBox(4, kicker, title, desc);

        GridPane summary = new GridPane();
        summary.setHgap(16);
        summary.setVgap(8);
        summary.getStyleClass().add("booking-summary");
        tambahSummaryRow(summary, 0, "Lokasi", ruang.getLokasi());
        tambahSummaryRow(summary, 1, "Tanggal", DATE_FMT.format(tanggal));
        tambahSummaryRow(summary, 2, "Kapasitas", ruang.getKapasitas() + " orang");
        tambahSummaryRow(summary, 3, "Fasilitas", ruang.getFasilitas());

        // Pilihan Sesi
        Label lblSesi = new Label("Pilih Sesi yang Ingin Dipinjam:");
        lblSesi.getStyleClass().add("dialog-field-label");

        Sesi[] jadwal = ruang.getJadwalHarian(tanggal);
        ComboBox<String> comboSesi = new ComboBox<>();
        for (Sesi s : jadwal) {
            String ket = s.isTersedia() ? " [Tersedia]" : " [Dipinjam]";
            comboSesi.getItems().add("Sesi " + s.getIdSesi() + " (" + s.getRentangWaktu() + ")" + ket);
        }
        comboSesi.getSelectionModel().selectFirst();
        comboSesi.setMaxWidth(Double.MAX_VALUE);
        comboSesi.getStyleClass().add("field-control");

        // Input NRP
        Label lblNrp = new Label("NRP Peminjam (3 digit):");
        lblNrp.getStyleClass().add("dialog-field-label");
        TextField txtNrp = new TextField();
        txtNrp.setPromptText("Contoh: 069 (Mahasiswa) atau 101 (Dosen)");
        txtNrp.getStyleClass().add("field-control");

        Label lblMemberInfo = new Label();
        lblMemberInfo.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");

        txtNrp.textProperty().addListener((obs, old, val) -> {
            if (val != null && val.matches("\\d{3}")) {
                Member m = sistem.cariMember(val.trim());
                if (m != null) {
                    lblMemberInfo.setText("✓ " + m.getNama() + " (" + m.getTipe() + " - Kuota: " + m.getKuotaPerHari() + "/hari)");
                    lblMemberInfo.setStyle("-fx-text-fill: #1f7a42;");
                } else {
                    lblMemberInfo.setText("✗ NRP tidak ditemukan! Silakan daftar di Data Member");
                    lblMemberInfo.setStyle("-fx-text-fill: #9b1c1c;");
                }
            } else {
                lblMemberInfo.setText("");
            }
        });

        VBox content = new VBox(14, header, summary,
                new VBox(6, lblSesi, comboSesi),
                new VBox(6, lblNrp, txtNrp, lblMemberInfo)
        );
        content.setPrefWidth(420);
        content.getStyleClass().add("dialog-content");

        DialogPane pane = dialog.getDialogPane();
        String css = getClass().getResource("/peminjamanruangpens/style.css") != null
                ? getClass().getResource("/peminjamanruangpens/style.css").toExternalForm() : null;
        if (css != null) pane.getStylesheets().add(css);

        pane.setContent(content);
        ButtonType btnBatal = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btnKirim = new ButtonType("Konfirmasi Peminjaman", ButtonBar.ButtonData.OK_DONE);
        pane.getButtonTypes().addAll(btnBatal, btnKirim);

        Button submitBtn = (Button) pane.lookupButton(btnKirim);
        submitBtn.getStyleClass().add("primary-button");

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == btnKirim) {
            int sesiIdx = comboSesi.getSelectionModel().getSelectedIndex() + 1;
            String nrp = txtNrp.getText();
            var hasil = sistem.pinjam(ruang, tanggal, sesiIdx, nrp);
            if (hasil.sukses()) {
                notif.sukses(hasil.pesan());
                muatJadwal();
            } else {
                notif.error(hasil.pesan());
            }
        }
    }

    private void tambahSummaryRow(GridPane grid, int row, String label, String value) {
        Label l = new Label(label);
        l.getStyleClass().add("summary-label");
        Label v = new Label(value);
        v.getStyleClass().add("summary-value");
        v.setWrapText(true);
        grid.add(l, 0, row);
        grid.add(v, 1, row);
    }
}
