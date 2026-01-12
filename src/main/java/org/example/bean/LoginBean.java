package org.example.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.example.entity.Cliente;

@Named
@RequestScoped
public class LoginBean {

    @PersistenceContext(unitName = "tiendaDebatesPU")
    private EntityManager em;

    private String nombre;
    private String password;

    public String login() {
        try {
            Cliente cliente = em.createQuery(
                    "SELECT c FROM Cliente c WHERE c.nombre = :nombre AND c.password = :password", Cliente.class)
                    .setParameter("nombre", nombre)
                    .setParameter("password", password)
                    .getSingleResult();

            if (cliente != null) {
                return "index?faces-redirect=true";
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
