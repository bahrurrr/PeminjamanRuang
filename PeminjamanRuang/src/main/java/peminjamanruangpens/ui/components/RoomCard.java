package peminjamanruangpens.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import peminjamanruangpens.model.Ruang;
import peminjamanruangpens.model.Sesi;

import java.time.LocalDate;
import java.util.function.BiConsumer;

public class RoomCard extends VBox {
    public RoomCard(Ruang room, LocalDate date, BiConsumer<Ruang, LocalDate> onBook) {
        super();
        StackPane photo = createPhoto(room);
        Label name = new Label(room.getNamaRuang());
        name.getStyleClass().add("room-name");
        Label location = new Label(room.getLokasi());
        location.getStyleClass().add("room-location");
        VBox title = new VBox(2, name, location);

        Label capacity = new Label("Kapasitas " + room.getKapasitas() + " orang");
        capacity.getStyleClass().add("room-meta");

        FlowPane amenities = new FlowPane(5, 5);
        amenities.getStyleClass().add("amenities");
        for (String facility : room.getFasilitas().split(",")) {
            Label chip = new Label(facility.trim());
            chip.getStyleClass().add("amenity-chip");
            amenities.getChildren().add(chip);
        }

        Sesi[] schedule = room.getJadwalHarian(date);
        HBox sessions = createSessionRow(schedule);
        long availableCount = java.util.Arrays.stream(schedule)
                .filter(Sesi::isTersedia)
                .count();
        Region statusDot = new Region();
        statusDot.getStyleClass().add("status-dot");
        Label availability = new Label(availableCount + " dari " + Ruang.JUMLAH_SESI + " sesi kosong");
        availability.getStyleClass().add("availability-text");
        HBox status = new HBox(6, statusDot, availability);
        status.setAlignment(Pos.CENTER_LEFT);

        Button bookButton = new Button("Pilih Sesi & Pinjam");
        bookButton.getStyleClass().add("book-button");
        bookButton.setOnAction(event -> onBook.accept(room, date));

        HBox footer = new HBox(status, bookButton);
        footer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(status, Priority.ALWAYS);

        VBox body = new VBox(10, title, capacity, amenities, sessions, footer);
        body.getStyleClass().add("room-card-body");
        getChildren().addAll(photo, body);
        setMinWidth(300);
        setPrefWidth(350);
        setMaxWidth(350);
        getStyleClass().add("room-card");
    }

    private StackPane createPhoto(Ruang room) {
        StackPane photo = new StackPane();
        photo.setPrefHeight(175);
        photo.setMinHeight(175);
        photo.setMaxHeight(175);
        photo.getStyleClass().add("room-photo");

        var imageUrl = getClass().getResource(room.getGambarPath());
        if (imageUrl != null) {
            ImageView image = new ImageView(new Image(imageUrl.toExternalForm(), 360, 175, false, true));
            Rectangle clip = new Rectangle(350, 175);
            clip.setArcWidth(28);
            clip.setArcHeight(28);
            image.setClip(clip);
            photo.getChildren().add(image);
        }

        Label badge = new Label(room.getJenis());
        badge.getStyleClass().add("room-badge");
        StackPane.setAlignment(badge, Pos.TOP_LEFT);
        StackPane.setMargin(badge, new Insets(12));
        photo.getChildren().add(badge);
        return photo;
    }

    private HBox createSessionRow(Sesi[] schedule) {
        HBox row = new HBox(6);
        row.getStyleClass().add("session-row");
        for (Sesi session : schedule) {
            Label chip = new Label("S" + session.getIdSesi() + " ("
                    + (session.isTersedia() ? "Kosong" : "Dipinjam") + ")");
            chip.getStyleClass().add(session.isTersedia() ? "session-chip-avail" : "session-chip-booked");
            row.getChildren().add(chip);
        }
        return row;
    }
}
