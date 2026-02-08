package model;

public enum TypeOfData {
    BOZZE("bozze"),
    IN_ARRIVO(""),
    INVIATI("");

    public final String filename;
    TypeOfData(String s) {
        this.filename = s;
    }
}
