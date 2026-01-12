package org.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "articulos")
public class Articulo {

    @Id
    @Column(name = "Codigo")
    private Integer codigo;

    @Column(name = "Descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "Referencia", columnDefinition = "TEXT")
    private String referencia;

    @Column(name = "Prov")
    private Integer prov;

    @Column(name = "Costo")
    private Long costo;

    @Column(name = "Venta", columnDefinition = "TEXT")
    private String venta;

    @Column(name = "Stock")
    private Integer stock;

    @Column(name = "Margen")
    private Integer margen;

    @Column(name = "`Real`")
    private Integer realVal;

    public Articulo() {
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Integer getProv() {
        return prov;
    }

    public void setProv(Integer prov) {
        this.prov = prov;
    }

    public Long getCosto() {
        return costo;
    }

    public void setCosto(Long costo) {
        this.costo = costo;
    }

    public String getVenta() {
        return venta;
    }

    public void setVenta(String venta) {
        this.venta = venta;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getMargen() {
        return margen;
    }

    public void setMargen(Integer margen) {
        this.margen = margen;
    }

    public Integer getRealVal() {
        return realVal;
    }

    public void setRealVal(Integer realVal) {
        this.realVal = realVal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Articulo articulo = (Articulo) o;
        return Objects.equals(codigo, articulo.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
