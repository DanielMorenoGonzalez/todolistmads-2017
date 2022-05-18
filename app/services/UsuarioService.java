package services;

import models.Usuario;
import models.UsuarioRepository;
import models.VistaCalendario;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UsuarioService {
    UsuarioRepository repository;

    // Play proporcionará automáticamente el UsuarioRepository necesario
    // usando inyección de dependencias
    @Inject
    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario creaUsuario(String login, String email, String password) {
        if (repository.findByLogin(login) != null) {
            throw new UsuarioServiceException("Login ya existente");
        }
        Usuario usuario = new Usuario(login, email);
        usuario.setPassword(password);
        return repository.add(usuario);
    }

    public Usuario findUsuarioPorLogin(String login) {
        return repository.findByLogin(login);
    }

    public Usuario findUsuarioPorId(Long id) {
        return repository.findById(id);
    }

    public Usuario findUsuarioPorEmail(String email) {
        return repository.findByEmail(email);
    }

    public Usuario login(String login, String password) {
        // Puede que el usuario haya introducido el login
        Usuario usuario = repository.findByLogin(login);
        if (usuario != null && usuario.getPassword().equals(password)) return usuario;

        // Puede que el usuario haya introducido el email
        usuario = repository.findByEmail(login);
        if (usuario != null && usuario.getPassword().equals(password)) return usuario;
        else return null;
    }

    public Usuario actualizarPerfil(long idUsuario,
                                    String nuevoLogin,
                                    String nuevoEmail,
                                    String nuevaPassword,
                                    String nuevoNombre,
                                    String nuevosApellidos,
                                    Date nuevaFechaNacimiento,
                                    VistaCalendario nuevaVistaCalendario
    ) throws ParseException {
        Usuario usuario = findUsuarioPorId(idUsuario);
        if (usuario == null) {
            throw new UsuarioServiceException("No existe usuario");
        }

        // En el caso de que el usuario actualice su login al que tenía anteriormente
        // (por ejemplo, se ha equivocado), no se lanzará excepción
        Usuario usuarioRegistrado = findUsuarioPorLogin(nuevoLogin);
        if (usuarioRegistrado != null && !usuario.getId().equals(usuarioRegistrado.getId())) {
            throw new UsuarioServiceException("El login ya existe (no válido)");
        }

        // Resto de comprobaciones (max longitud) en los datos del usuario
        if (nuevoLogin.length() > 20) throw new UsuarioServiceException("El login no es válido");
        if (nuevoEmail.length() > 30) throw new UsuarioServiceException("El email no es válido");
        if (nuevaPassword.length() > 20) throw new UsuarioServiceException("La contraseña no es válida");
        if (nuevoNombre.length() > 50) throw new UsuarioServiceException("El nombre no es válido");
        if (nuevosApellidos.length() > 50) throw new UsuarioServiceException("Los apellidos no son válidos");

        // Comprobaciones en las que, si el campo está vacío, se dejará el valor
        // que tenía anteriormente el usuario en el campo correspondiente
        if (nuevoLogin.isEmpty()) nuevoLogin = usuario.getLogin();
        if (nuevoEmail.isEmpty()) nuevoEmail = usuario.getEmail();
        if (nuevaPassword.isEmpty()) nuevaPassword = usuario.getPassword();

        if (nuevaFechaNacimiento != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = sdf.format(nuevaFechaNacimiento);
            Date fecha = sdf.parse(dateString);

            Date fechaActual = new Date();
            if(fecha.after(fechaActual)) throw new UsuarioServiceException("La fecha no es válida");

            // Comprobamos si el año es menor a 1900 (en dicho caso se lanza excepción)
            Calendar cal = Calendar.getInstance();
            cal.setTime(fecha);
            if (cal.get(Calendar.YEAR) < 1900) throw new UsuarioServiceException("La fecha no es válida");
        }

        usuario.setLogin(nuevoLogin);
        usuario.setEmail(nuevoEmail);
        usuario.setPassword(nuevaPassword);
        usuario.setNombre(nuevoNombre);
        usuario.setApellidos(nuevosApellidos);
        usuario.setFechaNacimiento(nuevaFechaNacimiento);
        usuario.setVistaCalendario(nuevaVistaCalendario);
        usuario = repository.update(usuario);
        return usuario;
    }
}
