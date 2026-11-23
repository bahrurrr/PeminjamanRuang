package peminjamanruangpens.ui.pages;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import peminjamanruangpens.model.Ruang;
import peminjamanruangpens.service.SistemPeminjaman;
import peminjamanruangpens.ui.components.BookingProcessCard;
import peminjamanruangpens.ui.components.Notifikasi;
import peminjamanruangpens.ui.components.RoomCard;
import peminjamanruangpens.ui.dialogs.BookingDialog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
    private final BookingDialog bookingDialog;

    public BookingPage(SistemPeminjaman sistem) {
        super(sistem);
        bookingDialog = new BookingDialog(sistem);
    }

    @Override public String getNamaMenu() { return "Cari & Booking Ruang"; }
    @Override public String getJudul() { return "Booking Ruangan"; }
    @Override public String getSubjudul() { return "Pilih ruang kampus, tentukan sesi yang tersedia, dan ajukan peminjaman."; }
    @Override protected boolean hasStandardHeader() { return false; }

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

        HBox hero = new HBox(36, intro, new BookingProcessCard());
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.getStyleClass().add("hero-section");
        return hero;
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
            roomGrid.getChildren().add(new RoomCard(r, tgl, this::bukaDialogPeminjaman));
        }
    }

    private void bukaDialogPeminjaman(Ruang ruang, LocalDate tanggal) {
        bookingDialog.show(ruang, tanggal).ifPresent(hasil -> {
            if (hasil.sukses()) {
                notif.sukses(hasil.pesan());
                muatJadwal();
            } else {
                notif.error(hasil.pesan());
            }
        });
    }
}
