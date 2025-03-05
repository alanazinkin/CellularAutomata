package cellsociety.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds styling configuration for a simulation.
 * @author angelapredolac
 */
// SimulationStyle.java

public class SimulationStyle {
    private Map<String, CellAppearance> cellAppearances;
    private EdgePolicy edgePolicy;
    private CellShape cellShape;
    private NeighborArrangement neighborArrangement;
    private boolean showGridOutline;
    private ColorTheme colorTheme;
    private double animationSpeed;

    public SimulationStyle() {
        this.cellAppearances = new HashMap<>();
        this.edgePolicy = EdgePolicy.BOUNDED;
        this.cellShape = CellShape.Rectangle;
        this.neighborArrangement = NeighborArrangement.MOORE;
        this.showGridOutline = true;
        this.colorTheme = ColorTheme.LIGHT;
        this.animationSpeed = 1.0;
    }

    public EdgePolicy getEdgePolicy() {
        return edgePolicy;
    }

    public void setEdgePolicy(EdgePolicy edgePolicy) {
        this.edgePolicy = edgePolicy;
    }

    public CellShape getCellShape() {
        return cellShape;
    }

    public void setCellShape(CellShape cellShape) {
        this.cellShape = cellShape;
    }

    public NeighborArrangement getNeighborArrangement() {
        return neighborArrangement;
    }

    public void setNeighborArrangement(NeighborArrangement neighborArrangement) {
        this.neighborArrangement = neighborArrangement;
    }

    public boolean isShowGridOutline() {
        return showGridOutline;
    }

    public void setShowGridOutline(boolean showGridOutline) {
        this.showGridOutline = showGridOutline;
    }

    public ColorTheme getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(ColorTheme colorTheme) {
        this.colorTheme = colorTheme;
    }

    public double getAnimationSpeed() {
        return animationSpeed;
    }

    public void setAnimationSpeed(double animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public Map<String, CellAppearance> getCellAppearances() {
        return cellAppearances;
    }

    public void setCellAppearances(Map<String, CellAppearance> appearances) {
        this.cellAppearances = appearances;
    }
}
