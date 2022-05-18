package utilities;

import models.Tablero;
import models.Usuario;

public final class TableroUsuarioUtil {
    private TableroUsuarioUtil() {}

    public static boolean usuarioParticipaOEsAdministradorDeTablero(Usuario usuario, Tablero tablero){
        return tablero.getParticipantes().contains(usuario) || tablero.getAdministrador().equals(usuario);
    }

    public static boolean usuarioNoParticipaNiEsAdministradorDeTablero(Usuario usuario, Tablero tablero) {
        return !tablero.getParticipantes().contains(usuario) && !tablero.getAdministrador().equals(usuario);
    }

    public static boolean usuarioNoParticipaNiEsAdministradorDeTableroEnTableroPrivado(Usuario usuario, Tablero tablero) {
        return (!tablero.getParticipantes().contains(usuario) && !tablero.getAdministrador().equals(usuario)) && tablero.isPrivado();
    }
}
