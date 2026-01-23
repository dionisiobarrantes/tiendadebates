package org.example.bean;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import org.example.entity.LineasPedido;
import org.example.entity.Pedido;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class PedidoBean implements Serializable {

    @PersistenceContext(unitName = "tiendaDebatesPU")
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    private List<Pedido> pedidos;
    private Pedido selectedPedido;
    private List<LineasPedido> detalles;

    @PostConstruct
    public void init() {
        cargarPedidos();
    }

    public void cargarPedidos() {
        try {
            pedidos = em.createQuery("SELECT p FROM Pedido p ORDER BY p.fechapedido DESC, p.idpedido DESC", Pedido.class).getResultList();
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] Error al cargar pedidos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void verDetalles(Pedido pedido) {
        this.selectedPedido = pedido;
        try {
            // Limpiar caché para asegurar datos frescos de la BD
            em.getEntityManagerFactory().getCache().evict(LineasPedido.class);
            
            detalles = em.createQuery("SELECT l FROM LineasPedido l WHERE l.idpedido = :id", LineasPedido.class)
                    .setParameter("id", pedido.getIdpedido())
                    .setHint("javax.persistence.cache.retrieveMode", "BYPASS")
                    .getResultList();
            System.out.println("[DEBUG_LOG] Cargados " + (detalles != null ? detalles.size() : 0) + " detalles para pedido " + pedido.getIdpedido());
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] Error al cargar detalles del pedido: " + e.getMessage());
            e.printStackTrace();
            detalles = new ArrayList<>();
        }
    }

    public void guardarCambiosLineas() {
        try {
            utx.begin();
            for (LineasPedido linea : detalles) {
                System.out.println("[DEBUG_LOG] Actualizando línea " + linea.getIdlinea() + ": enviado=" + linea.getEnviado());
                em.merge(linea);
            }
            em.flush();
            utx.commit();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Estado de envío actualizado correctamente."));
        } catch (Exception e) {
            try {
                if (utx != null) utx.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.err.println("[DEBUG_LOG] Error al guardar cambios en líneas: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron guardar los cambios: " + e.getMessage()));
        }
    }

    public boolean isEnviado(LineasPedido linea) {
        return Boolean.TRUE.equals(linea.getEnviado());
    }

    public void setEnviado(LineasPedido linea, boolean enviado) {
        linea.setEnviado(enviado);
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public Pedido getSelectedPedido() {
        return selectedPedido;
    }

    public void setSelectedPedido(Pedido selectedPedido) {
        this.selectedPedido = selectedPedido;
    }

    public List<LineasPedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<LineasPedido> detalles) {
        this.detalles = detalles;
    }
}
