package org.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "lineaspedido")
public class LineasPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idlineaspedido", nullable = false, columnDefinition = "INT AUTO_INCREMENT")
    private Integer idlinea;

    @Column(name = "idpedido")
    private Integer idpedido;

    @Column(name = "idarticuplo")
    private Integer idarticulo;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "enviado")
    @Convert(converter = org.example.converter.BooleanToIntegerAttributeConverter.class)
    private Boolean enviado;

    public LineasPedido() {
    }

    public Integer getIdlinea() {
        return idlinea;
    }

    public void setIdlinea(Integer idlinea) {
        this.idlinea = idlinea;
    }

    public Integer getIdpedido() {
        return idpedido;
    }

    public void setIdpedido(Integer idpedido) {
        this.idpedido = idpedido;
    }

    public Integer getIdarticulo() {
        return idarticulo;
    }

    public void setIdarticulo(Integer idarticulo) {
        this.idarticulo = idarticulo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEnviado() {
        return enviado;
    }

    public void setEnviado(Boolean enviado) {
        this.enviado = enviado;
    }
}
