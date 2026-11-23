package peminjamanruangpens.ui.components;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PageHeader extends VBox {
    public PageHeader(String title, String subtitle) {
        super(4);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("page-title");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("results-count");

        getChildren().addAll(titleLabel, subtitleLabel);
    }
}
