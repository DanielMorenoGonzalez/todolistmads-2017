package models;

import com.google.inject.ImplementedBy;
import java.util.List;

// Interfaz que define los métodos del UsuarioRepository
// La anotación ImplementedBy hace que Play para resolver la
// inyección de dependencias escoja como objeto que implementa
// esta interfaz un objeto JPAUsuarioRepository
@ImplementedBy(JPAUsuarioRepository.class)
public interface UsuarioRepository {
    Usuario add(Usuario usuario);

    Usuario update(Usuario usuario);

    Usuario findById(Long idUsuario);

    Usuario findByLogin(String login);

    Usuario findByEmail(String email);

    List<Usuario> findUsuariosByLogin(String login);

}
