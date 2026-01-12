package org.example.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.entity.Articulo;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

@Named
@SessionScoped
public class ArticuloBean implements Serializable {

    @PersistenceContext(unitName = "tiendaDebatesPU")
    private EntityManager em;

    private Session mailSession;

    private List<Articulo> articulos = new java.util.ArrayList<>();
    private List<Articulo> articulosSeleccionados = new java.util.ArrayList<>();
    private List<Articulo> cesta = new java.util.ArrayList<>();
    private java.util.Map<Integer, Boolean> seleccionMap = new java.util.HashMap<>();
    private java.util.Map<Integer, Integer> cantidadesMap = new java.util.HashMap<>();

    @PostConstruct
    public void init() {
        try {
            System.out.println("[DEBUG_LOG] ArticuloBean: Iniciando carga de artículos. File encoding: " + System.getProperty("file.encoding") + ", Default Charset: " + java.nio.charset.Charset.defaultCharset());
            System.out.println("[DEBUG_LOG] ArticuloBean: Iniciando carga de artículos desde la base de datos (JTA/DataSource)...");
            
            if (em == null) {
                System.err.println("[DEBUG_LOG] ArticuloBean: ERROR - EntityManager (em) es NULL. La inyección falló.");
                return;
            }

            articulos = em.createQuery("SELECT a FROM Articulo a ORDER BY a.descripcion ASC", Articulo.class).getResultList();
            
            if (articulos == null) {
                System.out.println("[DEBUG_LOG] ArticuloBean: La consulta devolvió NULL.");
                articulos = new java.util.ArrayList<>();
            } else {
                System.out.println("[DEBUG_LOG] ArticuloBean: Carga finalizada. Se encontraron " + articulos.size() + " artículos.");
                for (Articulo a : articulos) {
                    seleccionMap.put(a.getCodigo(), false);
                }
            }
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] ArticuloBean: EXCEPCIÓN durante la carga: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Articulo> getArticulos() {
        return articulos;
    }

    public void setArticulos(List<Articulo> articulos) {
        this.articulos = articulos;
    }

    public List<Articulo> getArticulosSeleccionados() {
        return articulosSeleccionados;
    }

    public void setArticulosSeleccionados(List<Articulo> articulosSeleccionados) {
        this.articulosSeleccionados = articulosSeleccionados;
    }

    public List<Articulo> getCesta() {
        return cesta;
    }

    public void setCesta(List<Articulo> cesta) {
        this.cesta = cesta;
    }

    public java.util.Map<Integer, Boolean> getSeleccionMap() {
        return seleccionMap;
    }

    public void setSeleccionMap(java.util.Map<Integer, Boolean> seleccionMap) {
        this.seleccionMap = seleccionMap;
    }

    public java.util.Map<Integer, Integer> getCantidadesMap() {
        return cantidadesMap;
    }

    public void setCantidadesMap(java.util.Map<Integer, Integer> cantidadesMap) {
        this.cantidadesMap = cantidadesMap;
    }

    public void toggleCesta(Articulo articulo) {
        if (!cesta.contains(articulo)) {
            cesta.add(articulo);
            seleccionMap.put(articulo.getCodigo(), true);
            cantidadesMap.put(articulo.getCodigo(), 1);
            System.out.println("[DEBUG_LOG] Articulo añadido a Cesta: " + articulo.getDescripcion());
        } else {
            cesta.remove(articulo);
            seleccionMap.put(articulo.getCodigo(), false);
            cantidadesMap.remove(articulo.getCodigo());
            System.out.println("[DEBUG_LOG] Articulo eliminado de Cesta: " + articulo.getDescripcion());
        }
        mostrarCestaEnMsgBox();
    }

    private void mostrarCestaEnMsgBox() {
        String summary = getCestaSummary();
        jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_INFO, "Cesta Actualizada", summary));
    }

    public String getCestaSummary() {
        StringBuilder sb = new StringBuilder("Contenido de la Cesta: ");
        if (cesta.isEmpty()) {
            sb.append("Vacía");
        } else {
            for (int i = 0; i < cesta.size(); i++) {
                Articulo a = cesta.get(i);
                Integer qty = getCantidadSafely(a.getCodigo());
                sb.append(a.getDescripcion()).append(" (").append(qty).append(")");
                if (i < cesta.size() - 1) {
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }

    private Integer getCantidadSafely(Integer codigo) {
        Object val = cantidadesMap.get(codigo);
        if (val == null) return 1;
        if (val instanceof Integer) return (Integer) val;
        if (val instanceof Number) return ((Number) val).intValue();
        try {
            return Integer.valueOf(val.toString());
        } catch (Exception e) {
            return 1;
        }
    }

    public boolean isInCesta(Articulo articulo) {
        return cesta.contains(articulo);
    }

    public void tramitarPedido() {
        System.out.println("[DEBUG_LOG] Tramitando pedido con " + cesta.size() + " artículos.");
        
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("<h1>Nuevo Pedido</h1>");
        emailBody.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        emailBody.append("<tr><th>Código</th><th>Descripción</th><th>Cantidad</th></tr>");
        
        for (Articulo a : cesta) {
            Integer qty = getCantidadSafely(a.getCodigo());
            emailBody.append("<tr>");
            emailBody.append("<td>").append(a.getCodigo()).append("</td>");
            emailBody.append("<td>").append(a.getDescripcion()).append("</td>");
            emailBody.append("<td>").append(qty).append("</td>");
            emailBody.append("</tr>");
            System.out.println("[DEBUG_LOG] -> " + a.getDescripcion() + " x " + qty);
        }
        emailBody.append("</table>");
        emailBody.append("<p>Total artículos: ").append(cesta.size()).append("</p>");

        try {
            enviarCorreo("administracion@debatesysolidaridad.org", "Nuevo Pedido - Tienda Debates", emailBody.toString());
            
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                    new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_INFO, 
                            "Pedido Tramitado", "El pedido ha sido enviado por correo correctamente."));
            
            // Opcional: Limpiar cesta tras el pedido
            // cesta.clear();
            // seleccionMap.clear();
            // cantidadesMap.clear();
            
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] Error al enviar correo: " + e.getMessage());
            // Loguear el contenido para no perder la información del pedido
            System.out.println("[DEBUG_LOG] === CONTENIDO DEL PEDIDO NO ENVIADO ===\n" + emailBody.toString() + "\n=========================================");
            
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                    new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_ERROR, 
                            "Error de Envío", "No se pudo conectar con el servidor de correo. Verifique la configuración en DataSourceConfig.java o los recursos del servidor. Detalle: " + e.getMessage()));
        }
    }

    private void enviarCorreo(String destinatario, String asunto, String contenido) throws Exception {
        if (mailSession == null) {
            String[] jndiNames = {
                "java:app/mail/tiendaMailSession",
                "java:comp/DefaultMailSession",
                "java:comp/mail/Default",
                "mail/Session",
                "java:jboss/mail/Default"
            };
            
            javax.naming.InitialContext ctx = new javax.naming.InitialContext();
            for (String name : jndiNames) {
                try {
                    mailSession = (Session) ctx.lookup(name);
                    if (mailSession != null) {
                        System.out.println("[DEBUG_LOG] Sesión de correo encontrada en JNDI: " + name);
                        break;
                    }
                } catch (Exception e) {
                    // Continuar
                }
            }
        }

        if (mailSession == null) {
            System.out.println("[DEBUG_LOG] No se encontró MailSession en JNDI. Usando fallback (localhost:25).");
            Properties props = new Properties();
            props.put("mail.smtp.host", "localhost");
            props.put("mail.smtp.port", "25");
            props.put("mail.smtp.auth", "false");
            mailSession = Session.getInstance(props);
        } else {
            Properties props = mailSession.getProperties();
            String host = props.getProperty("mail.smtp.host");
            String port = props.getProperty("mail.smtp.port");
            String user = props.getProperty("mail.smtp.user");
            String auth = props.getProperty("mail.smtp.auth");
            
            System.out.println("[DEBUG_LOG] Sesión de correo configurada:");
            System.out.println("[DEBUG_LOG]  - Host: " + (host != null ? host : "No definido"));
            System.out.println("[DEBUG_LOG]  - Puerto: " + (port != null ? port : "Default"));
            System.out.println("[DEBUG_LOG]  - Usuario: " + (user != null ? user : "No definido"));
            System.out.println("[DEBUG_LOG]  - Auth: " + (auth != null ? auth : "false"));
        }

        System.out.println("[DEBUG_LOG] Intentando enviar correo a: " + destinatario);
        MimeMessage message = new MimeMessage(mailSession);
        
        // El remitente DEBE ser una dirección válida autorizada por su servidor SMTP
        String remitenteEmail = mailSession.getProperties().getProperty("mail.smtp.from");
        if (remitenteEmail == null) {
            remitenteEmail = mailSession.getProperties().getProperty("mail.user");
        }
        if (remitenteEmail == null) {
            remitenteEmail = "tienda@debatesysolidaridad.org"; // Fallback
        }

        message.setFrom(new InternetAddress(remitenteEmail, "Tienda Debates", "UTF-8"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        message.setSubject(asunto, "UTF-8");
        message.setContent(contenido, "text/html; charset=utf-8");
        message.setHeader("Content-Type", "text/html; charset=utf-8");

        // Si el servidor requiere autenticación y los datos están en la sesión, Transport.send los usará
        // si la sesión fue creada con un Authenticator o si el proveedor SMTP los extrae de las propiedades.
        Transport.send(message);
        System.out.println("[DEBUG_LOG] Correo enviado exitosamente a " + destinatario);
    }
}
