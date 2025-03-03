package cellsociety.controller;

/**
 * Represents the appearance of a cell state.
 * @author angelapredolac
 */
public class CellAppearance {
    private String color;
    private String imagePath;

    public CellAppearance() {
        this.color = null;
        this.imagePath = null;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Determines if this cell appearance uses an image.
     *
     * @return true if an image path is specified, false otherwise.
     */
    public boolean usesImage() {
        return imagePath != null && !imagePath.isEmpty();
    }
}
