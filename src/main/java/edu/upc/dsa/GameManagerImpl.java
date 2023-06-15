package edu.upc.dsa;

import edu.upc.dsa.models.Mapa;
import edu.upc.dsa.models.Objeto;
import edu.upc.dsa.models.Usuario;
import edu.upc.dsa.models.Mensaje;
import edu.upc.dsa.models.dto.TablaCompra;
import edu.upc.dsa.models.dto.UsuarioTO;
import edu.upc.eetac.dsa.FactorySession;
import edu.upc.eetac.dsa.Session;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameManagerImpl implements GameManager {

    HashMap<String, Usuario> Usuarios;
    protected List<Usuario> listaUsuarios;
    HashMap<String, Objeto> Objetos;
    protected List<Objeto> listaObjetos;
    protected List<Mensaje> listaMensaje;
    private static GameManager instance;
    final static Logger logger = Logger.getLogger(GameManagerImpl.class);

    public GameManagerImpl(){
        this.listaUsuarios = new ArrayList<>();
        this.Usuarios = new HashMap<>();
        this.listaObjetos = new ArrayList<>();
        this.Objetos = new HashMap<>();
    }

    public static GameManager getInstance() {
        if (instance==null) instance = new GameManagerImpl();
        return instance;
    }

    @Override
    public void registrarUsuario(String nombre, String correo, String password) {

        if (Usuarios.get(correo) == null){

            this.listaUsuarios.add(new Usuario(nombre, correo, password));

            logger.info("Se ha realizado correctamente");
        }
        else
            logger.info("El correo ya existe con un usuario");
    }
    @Override
    public void añadirMensaje(String mensaje) {
        this.listaMensaje.add(new Mensaje(mensaje));
    }
    @Override
    public List<Mensaje> mostrarMensajes() {
        this.listaMensaje.add(new Mensaje("Funciona 1"));
        this.listaMensaje.add(new Mensaje("Funciona 2"));
        this.listaMensaje.add(new Mensaje("Funciona 3"));
        this.listaMensaje.add(new Mensaje("Funciona 4"));
        return this.listaMensaje;
    }

    @Override
    public int addUsuarioORM(String nombre, String correo, String password) {
        Session session = null;
        int userID = 0;
        try {
            session = FactorySession.openSession();
            UsuarioTO u = new UsuarioTO(nombre, correo, password);
            session.save(u);
        }
        catch (Exception e) {
            // LOG
        }
        finally {
            session.close();
        }

        return userID;

    }

    @Override
    public Usuario addUsuario2(Usuario u) {
        Session session = null;
        try{
            session = FactorySession.openSession();
            List<Usuario> listaUsuarios = session.findAll(Usuario.class);
            for (Usuario us : listaUsuarios) {
                if (us.getCorreo().equals(u.getCorreo())) {
                    return null;
                }
            }
            session.save(u);
            return u;
        }
        catch (Exception e){
                // LOG
        }
        finally {
            session.close();
        }
        return null;
    }

    @Override
    public void addObjeto(String nombre, String descripcion, double precio, String fotoImagen) {
        this.listaObjetos.add(new Objeto(nombre, descripcion, precio, fotoImagen));
        logger.info("Se ha añadido correctamente");
    }

    @Override
    public Objeto addObjetoORM(String nombre, String descripcion, double precio, String fotoImagen) {
        Session session = null;
        try{
            session = FactorySession.openSession();
            Objeto o = new Objeto(nombre, descripcion, precio, fotoImagen);
            session.save(o);
            return o;

        }
        catch (Exception e){
            // LOG
        }
        finally {
            session.close();
        }
        return null;
    }

    @Override
    public void login(String correo, String password) {

        Usuario usuario = null;
        for (Usuario u : listaUsuarios) {
            if (u.getCorreo().equals(correo)) {
                usuario = u;
                break;
            }
        }
        if (usuario != null && usuario.getPassword().equals(password)) {
            logger.info("Login con éxito");
        } else {
            logger.info("Contraseña incorrecta");
        }
    }

    @Override
    public boolean loginORM(String correo, String password) {
        Session session = null;
        Usuario usuario = null;
        try {
            session = FactorySession.openSession();
            usuario= getUserByEmailORM(correo);
            String p= session.getpassword(password);
            if (usuario.getCorreo().equals(correo)&(usuario.getPassword().equals(p))){
                logger.info("usuario loggeado");
                return true;

            }
            logger.info("usuario NO loggeado");
            return false;
            /*User u = new User(email, password);
            session.save(u);*/
        }
        catch (Exception e) {
            // LOG
        }
        finally {
            session.close();
        }

        return false;
        }


/*    @Override
    public Objeto hacerCompra(String Usuario, String nombreObjeto) {

        Usuario usuario = getUsuarioPorNombre(Usuario);
        if (usuario == null) {
            logger.info("Usuario " + Usuario + " no existe");
        }
        else {
            Objeto objeto = getObjetoPorNombre(nombreObjeto);
            if (usuario.getDsaCoins() < objeto.getPrecio()) {
                logger.info("No tienes money");
            }
            else{
                usuario.getListaObjetosComprados().add(objeto);
                double saldo = usuario.getDsaCoins() - objeto.getPrecio();
                usuario.setDsaCoins(saldo);
                logger.info("Objeto " + nombreObjeto + " comprado");
                logger.info(Usuario + " ahora tienes: " + saldo + " dsaCoins");
                return objeto;
            }
        }
        return null;
    }*/

    @Override
    public TablaCompra hacerCompraORM(String correo, String nombreObjeto) {
        Session session = null;
        try {
            session = FactorySession.openSession();
            Usuario usuario = getUserByEmailORM(correo);
            Objeto objeto = getObjectByNameORM(nombreObjeto);

            if(usuario.getDsacoins() < objeto.getPrecio()){
                return null;
            }
            else {
                double dinero = usuario.getDsacoins()-objeto.getPrecio();
                usuario.setDsacoins(dinero);
                session.update(usuario);
                TablaCompra tablacompra = new TablaCompra(correo, nombreObjeto);
                session.save(tablacompra);
                return tablacompra;
            }
        }
        catch (Exception e){

        }
        finally {
            session.close();
        }
        return null;
    }

    @Override
    public List<Objeto> listadeObjetos() {
        logger.info("Lista de objetos: " + listaObjetos.toString());
        return this.listaObjetos;
    }

    @Override
    public List<Objeto> listadeObjetosORM() {
        Session session = null;
        try{
            session = FactorySession.openSession();
            List<Objeto> listaObjetos = session.findAll(new Objeto().getClass());
            return listaObjetos;
        }
        catch (Exception e){
            // LOG
        }
        finally {
            session.close();
        }
        return null;
    }

    @Override
    public List<TablaCompra> listadeTablaCompraORM() {
        Session session = null;
        try{
            session = FactorySession.openSession();
            List<TablaCompra> listaTablaCompra = session.findAll(new TablaCompra().getClass());
            return listaTablaCompra;
        }
        catch (Exception e){
            // LOG
        }
        finally {
            session.close();
        }
        return null;
    }

    @Override
    public List<TablaCompra> listaObjetosCompradosPorUsuarioORM(String correo) {
        Session session = null;
        List<TablaCompra> inventario = new ArrayList<>();

        try {
            session = FactorySession.openSession();
            HashMap<String, String> usuario = new HashMap<>();
            usuario.put("correo", correo);
            List<TablaCompra> compras = session.findAll(TablaCompra.class, usuario);
            inventario.addAll(compras);
        }
        catch (Exception e){
            // LOG
        }
        finally{
            session.close();
        }
        return inventario;
    }

    @Override
    public void updateUsuario(String nombre, String correo, String password) {
        int id=0;
        Usuario us = new Usuario(nombre,correo,password);
        Usuario u = null;

        Session session = null;
        try {
            session = FactorySession.openSession();
            u=getUserByEmailORM(correo);
            if(u.getCorreo().equals(correo)) {
                us.setId(u.getId());
                session.update(us);
            }
        }
        catch (Exception e) {
            // LOG
        }
        finally {
            session.close();
        }
    }
/*    @Override
    public List<Objeto> listadeObjetosOrdenadosPorPrecio() {
        this.listaObjetos.sort(new Comparator<Objeto>() {
            public int compare(Objeto o1, Objeto o2) {
                return Double.compare(o2.getPrecio(), o1.getPrecio());
            }
        });
        logger.info("Lista ordenada por precio ascendente: " + listaObjetos.toString());
        return listaObjetos;

    } */


    //extras

    public Usuario getUsuarioPorNombre(String nombre){
        for (Usuario u: this.listaUsuarios) {
            if(u.getNombre().equals(nombre)){
                return u;
            }
        }
        return null;
    }

    public Objeto getObjetoPorNombre(String nombre){
        for (Objeto o: this.listaObjetos) {
            if(o.getNombre().equals(nombre)){
                return o;
            }
        }
        return null;
    }

    public Usuario getUsuarioPorCorreo(String correo){
        for (Usuario u: this.listaUsuarios) {
            if(u.getCorreo().equals(correo)){
                return u;
            }
        }
        return null;
    }

    @Override
    public Usuario getUserByEmailORM(String correo) {
        Session session = null;
        Usuario usuario = null;
        try {
            session = FactorySession.openSession();
            usuario = (Usuario) session.get(Usuario.class, "correo", correo);
        }
        catch (Exception e) {
            // LOG
        }
        finally {
            session.close();
        }

        return usuario;
    }

    @Override
    public Objeto getObjectByNameORM(String nombreObjeto) {
        Session session = null;
        Objeto objeto = null;
        try {
            session = FactorySession.openSession();
            objeto = (Objeto) session.get(Objeto.class, "nombre", nombreObjeto);
        }
        catch (Exception e) {
            // LOG
        }
        finally {
            session.close();
        }

        return objeto;
    }

    @Override
    public Usuario getUsuarioORM(String correo) {
        Session session = null;
        try {
            session = FactorySession.openSession();
            List<Usuario> listaUsuarios = session.findAll(new Usuario().getClass());
            for (Usuario usuario : listaUsuarios) {
                if (usuario.getCorreo() == correo) {
                    return usuario;
                }
            }
            return null;
        }
        catch (Exception e) {
            // LOG
        }
        finally {
            session.close();
        }

        return null;
    }

    @Override
    public Objeto getObjetoORM(String nombre){
        Session session = null;
        try {
            session = FactorySession.openSession();
            List<Objeto> listaObjetos = session.findAll(new Objeto().getClass());
            for (Objeto objeto : listaObjetos) {
                if (objeto.getNombre() == nombre) {
                    return objeto;
                }
            }
            return null;
        }
        catch (Exception e) {
            // LOG
        }
        finally {
            session.close();
        }

        return null;
    }

    @Override
    public void clear() {
        this.listaObjetos.clear();
    }

    @Override
    public int size() {
        return this.listaObjetos.size();
    }

    @Override
    public Mapa addMapaORM(String nombremapa, String mapatxt) {
        Session session = null;
        try{
            session = FactorySession.openSession();
            Mapa m = new Mapa(nombremapa, mapatxt);
            session.save(m);
            return m;

        }
        catch (Exception e){
            // LOG
        }
        finally {
            session.close();
        }
        return null;
    }

    @Override
    public Mapa getMapaPorNivelORM(String nombremapa) {
        Session session = null;
        Mapa mapa = null;
        try {
            session = FactorySession.openSession();
            mapa = (Mapa) session.get(Mapa.class, "nombremapa", nombremapa);
        }
        catch (Exception e) {
            // LOG
        }
        finally {
            session.close();
        }

        return mapa;
    }


    // public int size() {
    //   int ret = this.tracks.size();
    // logger.info("size " + ret);

    //return ret;
    //}

}