package peminjamanruangpens.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import peminjamanruangpens.ui.pages.Halaman;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class TopBar extends HBox {
    private final List<Button> navigationButtons = new ArrayList<>();

    public TopBar(List<Halaman> pages, IntConsumer onPageSelected) {
        super(24);
        Label mark = new Label("P");
        mark.getStyleClass().add("brand-mark");

        Label brandName = new Label("PENSpace");
        brandName.getStyleClass().add("brand-name");
        Label campus = new Label("CAMPUS PENS");
        campus.getStyleClass().add("brand-caption");

        HBox brand = new HBox(10, mark, new VBox(-3, brandName, campus));
        brand.setAlignment(Pos.CENTER_LEFT);

        HBox navigation = new HBox(12);
        navigation.setAlignment(Pos.CENTER);
        for (int i = 0; i < pages.size(); i++) {
            int pageIndex = i;
            Button button = new Button(pages.get(i).getNamaMenu());
            button.getStyleClass().add("nav-link");
            button.setOnAction(event -> onPageSelected.accept(pageIndex));
            navigationButtons.add(button);
            navigation.getChildren().add(button);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label avatar = new Label("PENS");
        avatar.getStyleClass().add("avatar");
        HBox account = new HBox(12, avatar);
        account.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(brand, spacer, navigation, account);
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("topbar");
    }

    public void setActivePage(int activeIndex) {
        for (int i = 0; i < navigationButtons.size(); i++) {
            Button button = navigationButtons.get(i);
            button.getStyleClass().remove("nav-link-active");
            if (i == activeIndex) {
                button.getStyleClass().add("nav-link-active");
            }
        }
    }
}
