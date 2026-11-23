package peminjamanruangpens.ui.components;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class CardPanel extends VBox {
    public CardPanel(Node... children) {
        this(12, children);
    }

    public CardPanel(double spacing, Node... children) {
        super(spacing, children);
        getStyleClass().add("card");
    }
}
