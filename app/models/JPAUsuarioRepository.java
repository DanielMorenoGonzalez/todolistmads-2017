package models;

import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

public class JPAUsuarioRepository implements UsuarioRepository {
    // Objeto definido por Play para acceder al API de JPA
    // https://www.playframework.com/documentation/2.5.x/JavaJPA#Using-play.db.jpa.JPAApi
    private JPAApi jpaApi;

    // Para usar el JPAUsuarioRepository hay que proporcionar una JPAApi.
    // La anotación Inject hace que Play proporcione el JPAApi cuando se lance
    // la aplicación.
    @Inject
    public JPAUsuarioRepository(JPAApi api) {
        this.jpaApi = api;
    }

    @Override
    public Usuario add(Usuario usuario) {
        return jpaApi.withTransaction(entityManager -> {
            entityManager.persist(usuario);
            // Hacemos un flush y un refresh para asegurarnos de que se realiza
            // la creación en la BD y se devuelve el id inicializado
            entityManager.flush();
            entityManager.refresh(usuario);
            return usuario;
        });
    }

    @Override
    public Usuario update(Usuario usuario) {
        return jpaApi.withTransaction(entityManager -> entityManager.merge(usuario));
    }

    @Override
    public Usuario findById(Long idUsuario) {
        return jpaApi.withTransaction(entityManager -> {
            return entityManager.find(Usuario.class, idUsuario);
        });
    }

    @Override
    public List<Usuario> findUsuariosByLogin(String login){ // Método para obtener una lista de usuarios con un login similar al que se le pasa por parámetro
        return jpaApi.withTransaction(entityManager -> {
            TypedQuery<Usuario> typedQuery = entityManager.createQuery(
                    "select u from Usuario u where u.login like :login", Usuario.class);
            typedQuery.setParameter("login", "%"+login+"%");
            return typedQuery.getResultList();
        });
    }

    @Override
    public Usuario findByLogin(String login) {
        return jpaApi.withTransaction(entityManager -> {
            TypedQuery<Usuario> query = entityManager.createQuery(
                    "select u from Usuario u where u.login = :login", Usuario.class);
            try {
                Usuario usuario = query.setParameter("login", login).getSingleResult();
                return usuario;
            } catch (NoResultException ex) {
                return null;
            }
        });
    }

    @Override
    public Usuario findByEmail(String email) {
        return jpaApi.withTransaction(entityManager -> {
            TypedQuery<Usuario> query = entityManager.createQuery(
                    "select u from Usuario u where u.email = :email", Usuario.class);
            try {
                Usuario usuario = query.setParameter("email", email).getSingleResult();
                return usuario;
            } catch (NoResultException ex) {
                return null;
            }
        });
    }
}
