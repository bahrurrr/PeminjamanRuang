package peminjamanruangpens;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import peminjamanruangpens.service.SistemPeminjaman;
import peminjamanruangpens.ui.*;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private SistemPeminjaman sistem;
    private ScrollPane contentScroll;
    private final List<Button> navButtons = new ArrayList<>();
    private final List<Halaman> daftarHalaman = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        sistem = new SistemPeminjaman();

        BookingPage bookingPage = new BookingPage(sistem);
        daftarHalaman.add(bookingPage);
        daftarHalaman.add(new RiwayatPage(sistem));
        daftarHalaman.add(new MemberPage(sistem));
        daftarHalaman.add(new RuangPage(sistem));

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(createTopBar());

        contentScroll = new ScrollPane();
        contentScroll.setFitToWidth(true);
        contentScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        contentScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        contentScroll.setPannable(true);
        contentScroll.getStyleClass().add("content-scroll");
        root.setCenter(contentScroll);

        // Halaman default
        bukaHalaman(0);

        Scene scene = new Scene(root, 1260, 840);
        String css = getClass().getResource("/peminjamanruangpens/style.css") != null
                ? getClass().getResource("/peminjamanruangpens/style.css").toExternalForm() : null;
        if (css != null) scene.getStylesheets().add(css);

        stage.setTitle("PENSpace — Sistem Peminjaman Ruang Kampus PENS");
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.setScene(scene);
        stage.show();
    }

    private HBox createTopBar() {
        Label mark = new Label("P");
        mark.getStyleClass().add("brand-mark");
        Label brandName = new Label("PENSpace");
        brandName.getStyleClass().add("brand-name");
        Label campus = new Label("CAMPUS PENS");
        campus.getStyleClass().add("brand-caption");
        VBox brandText = new VBox(-3, brandName, campus);
        HBox brand = new HBox(10, mark, brandText);
        brand.setAlignment(Pos.CENTER_LEFT);

        HBox navigation = new HBox(12);
        navigation.setAlignment(Pos.CENTER);

        for (int i = 0; i < daftarHalaman.size(); i++) {
            final int index = i;
            Halaman hal = daftarHalaman.get(i);
            Button btn = new Button(hal.getNamaMenu());
            btn.getStyleClass().add("nav-link");
            btn.setOnAction(e -> bukaHalaman(index));
            navButtons.add(btn);
            navigation.getChildren().add(btn);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label avatar = new Label("PENS");
        avatar.getStyleClass().add("avatar");
        HBox account = new HBox(12, avatar);
        account.setAlignment(Pos.CENTER_RIGHT);

        HBox topBar = new HBox(24, brand, spacer, navigation, account);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getStyleClass().add("topbar");
        return topBar;
    }

    private void bukaHalaman(int index) {
        for (int i = 0; i < navButtons.size(); i++) {
            if (i == index) {
                if (!navButtons.get(i).getStyleClass().contains("nav-link-active")) {
                    navButtons.get(i).getStyleClass().add("nav-link-active");
                }
            } else {
                navButtons.get(i).getStyleClass().remove("nav-link-active");
            }
        }

        Halaman hal = daftarHalaman.get(index);
        contentScroll.setContent(hal.getTampilan());
        contentScroll.setVvalue(0);

        if (hal instanceof BookingPage bp) {
            bp.muatJadwal();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
