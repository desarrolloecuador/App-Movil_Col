package Servidor;

public class NotificacionRecivida {

    public String materia;
    public String fecha;
    public String mensaje;
    private static NotificacionRecivida notificacionRecivida;

    private NotificacionRecivida() {

    }


    public  static NotificacionRecivida getNotificacionRecivida() {

        if (notificacionRecivida==null) {

            notificacionRecivida=new NotificacionRecivida();
        }
        return notificacionRecivida;
    }

    public void setAtributos( String materia, String fecha, String mensaje){
        this.materia=materia;
        this.fecha=fecha;
        this.mensaje=mensaje;
    }



}