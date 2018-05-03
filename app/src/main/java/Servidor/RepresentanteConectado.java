package Servidor;

public class RepresentanteConectado {

    public String codigo_usu;
    public String nombre;
    public String username;
    public String password;
    private static RepresentanteConectado miUsuarioConectado;

    private RepresentanteConectado() {

    }


    public  static RepresentanteConectado getObjetoRepresentante() {

        if (miUsuarioConectado==null) {

            miUsuarioConectado=new RepresentanteConectado();
        }
        return miUsuarioConectado;
    }

    public void setAtributos( String codigo_usu, String nom, String username, String password){
            this.codigo_usu=codigo_usu;
            this.nombre=nom;
            this.username=username;
            this.password=password;
    }



}