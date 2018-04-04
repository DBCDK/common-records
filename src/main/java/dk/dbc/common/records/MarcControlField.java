package dk.dbc.common.records;

import java.util.Objects;

public class MarcControlField {

    private String name;
    private String value;

    public MarcControlField(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public MarcControlField(MarcControlField other) {
        this.name = other.name;
        this.value = other.value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("*%s %s", this.name, this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarcControlField that = (MarcControlField) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
