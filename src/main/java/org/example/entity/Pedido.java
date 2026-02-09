package org.example.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpedido", nullable = false, columnDefinition = "INT AUTO_INCREMENT")
    private Integer idpedido;

    @Column(name = "cliente")
    private Integer cliente;

    @Column(name = "fechapedido")
    @Temporal(TemporalType.DATE)
    private Date fechapedido;

    @Column(name = "nombrecliente")
    private String nombrecliente;

    @Column(name = "observaciones")
    private String observaciones;

    @Transient
    private String estado;

    public Pedido() {
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getIdpedido() {
        return idpedido;
    }

    public void setIdpedido(Integer idpedido) {
        this.idpedido = idpedido;
    }

    public Integer getCliente() {
        return cliente;
    }

    public void setCliente(Integer cliente) {
        this.cliente = cliente;
    }

    public Date getFechapedido() {
        return fechapedido;
    }

    public void setFechapedido(Date fechapedido) {
        this.fechapedido = fechapedido;
    }

    public String getNombrecliente() {
        return nombrecliente;
    }

    public void setNombrecliente(String nombrecliente) {
        this.nombrecliente = nombrecliente;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
