package com.genuino.crm.quoting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "proforma_sequence")
@IdClass(ProformaSequence.Pk.class)
public class ProformaSequence {

    @Id
    @Column(name = "year")
    public Integer year;

    @Id
    @Column(name = "series")
    public String series;

    @Column(name = "last_value")
    public Integer lastValue;

    @Column(name = "version")
    public Long version;

    public static class Pk implements Serializable {
        public Integer year;
        public String series;

        public Pk() {
        }

        public Pk(Integer year, String series) {
            this.year = year;
            this.series = series;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pk pk)) return false;
            return Objects.equals(year, pk.year) && Objects.equals(series, pk.series);
        }

        @Override
        public int hashCode() {
            return Objects.hash(year, series);
        }
    }
}