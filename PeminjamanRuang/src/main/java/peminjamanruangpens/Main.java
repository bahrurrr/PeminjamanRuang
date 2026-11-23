package peminjamanruangpens;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import peminjamanruangpens.service.SistemPeminjaman;
import peminjamanruangpens.ui.components.TopBar;
import peminjamanruangpens.ui.components.UiStyles;
import peminjamanruangpens.ui.pages.BookingPage;
import peminjamanruangpens.ui.pages.Halaman;
import peminjamanruangpens.ui.pages.MemberPage;
import peminjamanruangpens.ui.pages.RiwayatPage;
import peminjamanruangpens.ui.pages.RuangPage;

import java.util.List;

public class Main extends Application {
    private ScrollPane contentScroll;
    private List<Halaman> pages;
    private TopBar topBar;

    @Override
    public void start(Stage stage) {
        SistemPeminjaman sistem = new SistemPeminjaman();
        pages = List.of(
                new BookingPage(sistem),
                new RiwayatPage(sistem),
                new MemberPage(sistem),
                new RuangPage(sistem)
        );

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        topBar = new TopBar(pages, this::bukaHalaman);
        root.setTop(topBar);

        contentScroll = new ScrollPane();
        contentScroll.setFitToWidth(true);
        contentScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        contentScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        contentScroll.setPannable(true);
        contentScroll.getStyleClass().add("content-scroll");
        root.setCenter(contentScroll);

        bukaHalaman(0);

        Scene scene = new Scene(root, 1260, 840);
        UiStyles.applyTo(root);

        stage.setTitle("PENSpace — Sistem Peminjaman Ruang Kampus PENS");
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.setScene(scene);
        stage.show();
    }

    private void bukaHalaman(int index) {
        Halaman page = pages.get(index);
        topBar.setActivePage(index);
        contentScroll.setContent(page.getTampilan());
        contentScroll.setVvalue(0);

        if (page instanceof BookingPage bookingPage) {
            bookingPage.muatJadwal();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
