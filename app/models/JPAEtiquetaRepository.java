package models;

import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;
import java.util.List;

public class JPAEtiquetaRepository implements EtiquetaRepository {
    private JPAApi jpaApi;

    @Inject
    public JPAEtiquetaRepository(JPAApi api) {
        this.jpaApi = api;
    }

    @Override
    public Etiqueta add(Etiqueta etiqueta) {
        return jpaApi.withTransaction(entityManager -> {
            entityManager.persist(etiqueta);
            entityManager.flush();
            entityManager.refresh(etiqueta);
            return etiqueta;
        });
    }

    @Override
    public Etiqueta update(Etiqueta etiqueta) {
        return jpaApi.withTransaction(entityManager -> entityManager.merge(etiqueta));
    }

    @Override
    public void delete(Long idEtiqueta) {
        jpaApi.withTransaction(() -> {
            EntityManager entityManager = jpaApi.em();
            Etiqueta etiquetaBD = entityManager.getReference(Etiqueta.class, idEtiqueta);
            entityManager.remove(etiquetaBD);
        });
    }

    @Override
    public Etiqueta findById(Long idEtiqueta) {
        return jpaApi.withTransaction(entityManager -> entityManager.find(Etiqueta.class, idEtiqueta));
    }

    @Override
    public Etiqueta findByTituloConUsuario(String tituloEtiqueta, Long idUsuario) {
        return jpaApi.withTransaction(entityManager -> {
            TypedQuery<Etiqueta> typedQuery = entityManager.createQuery(
            "select e from Etiqueta e where e.titulo = :tituloEtiqueta and e.usuario.id = :idUsuario " +
            "and e.tablero.id is null", Etiqueta.class);
            try {
                typedQuery.setParameter("tituloEtiqueta", tituloEtiqueta);
                typedQuery.setParameter("idUsuario", idUsuario);
                Etiqueta etiqueta = typedQuery.getSingleResult();
                return etiqueta;
            } catch (NoResultException ex) {
                return null;
            }
        });
    }

    @Override
    public Etiqueta findByTituloConTablero(String tituloEtiqueta, Long idTablero) {
        return jpaApi.withTransaction(entityManager -> {
            TypedQuery<Etiqueta> typedQuery = entityManager.createQuery(
            "select e from Etiqueta e where e.titulo = :tituloEtiqueta and e.tablero.id = :idTablero", Etiqueta.class);
            try {
                typedQuery.setParameter("tituloEtiqueta", tituloEtiqueta);
                typedQuery.setParameter("idTablero", idTablero);
                Etiqueta etiqueta = typedQuery.getSingleResult();
                return etiqueta;
            } catch (NoResultException ex) {
                return null;
            }
        });
    }

    @Override
    public List<Etiqueta> findAll(Long idUsuario) {
        return jpaApi.withTransaction(entityManager -> {
            final TypedQuery<Etiqueta> typedQuery = entityManager.createQuery("select e " +
                "from Etiqueta e " +
                "where e.usuario.id = :idUsuario", Etiqueta.class);
            typedQuery.setParameter("idUsuario", idUsuario);
            return typedQuery.getResultList();
        });
    }
}
