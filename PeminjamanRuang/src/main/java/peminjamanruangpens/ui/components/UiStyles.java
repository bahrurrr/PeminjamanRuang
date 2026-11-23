package peminjamanruangpens.ui.components;

import javafx.scene.Parent;

import java.net.URL;
import java.util.List;

public final class UiStyles {
    private static final List<String> STYLESHEETS = List.of(
            "/peminjamanruangpens/styles/base.css",
            "/peminjamanruangpens/styles/navigation.css",
            "/peminjamanruangpens/styles/booking.css",
            "/peminjamanruangpens/styles/data.css"
    );

    private UiStyles() {}

    public static void applyTo(Parent parent) {
        for (String path : STYLESHEETS) {
            URL stylesheet = UiStyles.class.getResource(path);
            if (stylesheet == null) {
                throw new IllegalStateException("Stylesheet tidak ditemukan: " + path);
            }
            String url = stylesheet.toExternalForm();
            if (!parent.getStylesheets().contains(url)) {
                parent.getStylesheets().add(url);
            }
        }
    }
}
