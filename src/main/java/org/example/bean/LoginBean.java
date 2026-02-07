package org.example.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.entity.Cliente;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginBean implements Serializable {

    @PersistenceContext(unitName = "tiendaDebatesPU")
    private EntityManager em;

    private String nombre;
    private String password;
    private Cliente usuarioLogueado;

    public String login() {
        try {
            usuarioLogueado = em.createQuery(
                    "SELECT c FROM Cliente c WHERE c.nombre = :nombre AND c.password = :password", Cliente.class)
                    .setParameter("nombre", nombre)
                    .setParameter("password", password)
                    .getSingleResult();

            if (usuarioLogueado != null) {
                return "confirmarDireccion?faces-redirect=true";
            }
        } catch (NoResultException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario o contraseña erroneos", null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error interno al intentar iniciar sesión", null));
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public String actualizarDireccionYEntrar() {
        try {
            em.merge(usuarioLogueado);
            return "index?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al actualizar la dirección", null));
            e.printStackTrace();
            return null;
        }
    }

    public boolean isAdministrador() {
        return usuarioLogueado != null && usuarioLogueado.getAdministrador() != null && usuarioLogueado.getAdministrador() == 1;
    }

    public Cliente getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Cliente usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
