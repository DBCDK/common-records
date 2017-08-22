/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a field in a marc record.
 */
public class MarcField {
    private String name;
    private String indicator;
    private List<MarcSubField> subfields;

    public MarcField() {
        this("", "");
    }

    /**
     * Constructs a marc field.
     * <p>
     * The subFields attribute is initialized to null.
     * @param name      The name of the field.
     * @param indicator Its indicator.
     */
    public MarcField(String name, String indicator) {
        this(name, indicator, new ArrayList<>());
    }

    /**
     * Constructs a marc field.
     * @param name      The name of the field.
     * @param indicator Its indicator.
     * @param subFields List of sub fields.
     */
    public MarcField(String name, String indicator, List<MarcSubField> subFields) {
        this.name = name;
        this.indicator = indicator;
        this.subfields = subFields;
    }

    /**
     * Copy constructor.
     * <p>
     * Each subfield is deep copied to the new instance.
     * </p>
     */
    public MarcField(MarcField other) {
        this.name = other.name;
        this.indicator = other.indicator;

        this.subfields = new ArrayList<>();
        for (MarcSubField sf : other.subfields) {
            this.subfields.add(new MarcSubField(sf));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public List<MarcSubField> getSubfields() {
        return subfields;
    }

    public void setSubfields(List<MarcSubField> subfields) {
        this.subfields = subfields;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 71 * hash + (this.indicator != null ? this.indicator.hashCode() : 0);
        hash = 71 * hash + (this.subfields != null ? this.subfields.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MarcField other = (MarcField) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.indicator == null) ? (other.indicator != null) : !this.indicator.equals(other.indicator)) {
            return false;
        }
        if (this.subfields != other.subfields && (this.subfields == null || !this.subfields.equals(other.subfields))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(String.format("%s %s", this.name, this.indicator));
        for (MarcSubField sf : this.subfields) {
            if (sf != null && StringUtils.isNotEmpty(sf.getName())) {
                buffer.append(" ").append(sf.toString());
            }
        }
        return buffer.toString();
    }
}
