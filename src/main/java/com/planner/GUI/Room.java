package com.planner.GUI;

import javafx.beans.property.*;

public class Room {

    private IntegerProperty roomNo = new SimpleIntegerProperty();
    private IntegerProperty capacity = new SimpleIntegerProperty();
    private IntegerProperty rows = new SimpleIntegerProperty();
    private IntegerProperty columns = new SimpleIntegerProperty();
    private BooleanProperty selected = new SimpleBooleanProperty(false); //  checkbox

    public Room(int roomNo, int capacity, int rows, int columns) {
        this.roomNo.set(roomNo);
        this.capacity.set(capacity);
        this.rows.set(rows);
        this.columns.set(columns);
    }

    public int getRoomNo() { return roomNo.get(); }
    public int getCapacity() { return capacity.get(); }
    public int getRows() { return rows.get(); }
    public int getColumns() { return columns.get(); }

    public boolean isSelected() { return selected.get(); }
    public BooleanProperty selectedProperty() { return selected; }
}