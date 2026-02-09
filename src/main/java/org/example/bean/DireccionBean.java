package org.example.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.entity.Cliente;
import org.example.entity.Direccion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@RequestScoped
public class DireccionBean implements Serializable {

    @PersistenceContext(unitName = "tiendaDebatesPU")
    private EntityManager em;

    private List<ClienteDireccionVO> clientesDirecciones;

    @PostConstruct
    public void init() {
        cargarClientesDirecciones();
    }

    public void cargarClientesDirecciones() {
        clientesDirecciones = new ArrayList<>();
        try {
            List<Cliente> clientes = em.createQuery("SELECT c FROM Cliente c", Cliente.class).getResultList();
            for (Cliente c : clientes) {
                Direccion d = null;
                try {
                    d = em.createQuery("SELECT d FROM Direccion d WHERE d.idcliente = :id", Direccion.class)
                            .setParameter("id", c.getIdclientes())
                            .getSingleResult();
                } catch (jakarta.persistence.NoResultException e) {
                    // No hay dirección para este cliente
                }
                clientesDirecciones.add(new ClienteDireccionVO(c, d));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ClienteDireccionVO> getClientesDirecciones() {
        return clientesDirecciones;
    }

    public static class ClienteDireccionVO {
        private Cliente cliente;
        private Direccion direccion;

        public ClienteDireccionVO(Cliente cliente, Direccion direccion) {
            this.cliente = cliente;
            this.direccion = direccion;
        }

        public Cliente getCliente() {
            return cliente;
        }

        public Direccion getDireccion() {
            return direccion;
        }
    }
}
