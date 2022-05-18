package models;

public enum VistaCalendario {
    Mes("month"),
    Semana("agendaWeek"),
    Dia("agendaDay");

    private String nombreVistaPlugin;

    VistaCalendario(final String nombreVistaPlugin) {
        this.nombreVistaPlugin = nombreVistaPlugin;
    }

    public String getNombreVistaPlugin() {
        return nombreVistaPlugin;
    }

    public void setNombreVistaPlugin(final String nombreVistaPlugin) {
        this.nombreVistaPlugin = nombreVistaPlugin;
    }
}
