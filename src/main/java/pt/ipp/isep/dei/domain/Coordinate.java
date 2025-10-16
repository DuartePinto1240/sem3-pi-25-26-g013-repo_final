package main.java.pt.ipp.isep.dei.domain;

public class Coordinate {
    private int aisle;
    private int bay;

    //construtor
    public Coordinate(int aisle, int bay) {
        this.aisle = aisle;
        this.bay = bay;
    }

    //gets
    public int getBay() { return bay; }

    public int getAisle() { return aisle; }

    public int distanceTo (Coordinate other){
        if (this.aisle == other.aisle)
            return Math.abs(this.bay - other.bay);
        else
            return this.bay + Math.abs(this.aisle - other.aisle) * 3 + other.bay;
    }

    //to String
    @Override
    public String toString() {
        return "(" + aisle + "," + bay + ")";
    }
}
