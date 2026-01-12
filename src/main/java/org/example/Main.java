package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.entity.Articulo;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting JPA Application Test...");
        
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("tiendaDebatesPU")) {
            System.out.println("EntityManagerFactory created successfully!");
            EntityManager em = emf.createEntityManager();
            
            List<Articulo> articulos = em.createQuery("SELECT a FROM Articulo a", Articulo.class).getResultList();
            System.out.println("Number of articles found: " + articulos.size());
            
            for (Articulo a : articulos) {
                System.out.println(" - " + a.getCodigo() + ": " + a.getDescripcion());
            }
            
            if (articulos.isEmpty()) {
                System.out.println("The table 'articulos' is empty. Inserting a sample record...");
                em.getTransaction().begin();
                Articulo nuevo = new Articulo();
                nuevo.setCodigo(1);
                nuevo.setDescripcion("Descripción de prueba");
                nuevo.setReferencia("REF-001");
                nuevo.setProv(100);
                nuevo.setCosto(500L);
                nuevo.setVenta("10.50");
                nuevo.setStock(50);
                nuevo.setMargen(20);
                nuevo.setRealVal(45);
                em.persist(nuevo);
                em.getTransaction().commit();
                System.out.println("Sample record inserted.");
            }
            
            em.close();
        } catch (Exception e) {
            System.err.println("Error during JPA test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}