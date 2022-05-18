package models;

import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class JPATareaRepository implements TareaRepository {
    private JPAApi jpaApi;

    @Inject
    public JPATareaRepository(JPAApi api) {
        this.jpaApi = api;
    }

    @Override
    public Tarea add(Tarea tarea) {
        return jpaApi.withTransaction(entityManager -> {
            entityManager.persist(tarea);
            entityManager.flush();
            entityManager.refresh(tarea);
            return tarea;
        });
    }

    @Override
    public Tarea update(Tarea tarea) {
        return jpaApi.withTransaction(entityManager -> entityManager.merge(tarea));
    }

    @Override
    public void delete(Long idTarea) {
        jpaApi.withTransaction(() -> {
            EntityManager entityManager = jpaApi.em();
            Tarea tareaBD = entityManager.getReference(Tarea.class, idTarea);
            entityManager.remove(tareaBD);
            //entityManager.flush();
        });
    }

    @Override
    public Tarea findById(Long idTarea) {
        return jpaApi.withTransaction(entityManager -> entityManager.find(Tarea.class, idTarea));
    }

    @Override
    public List<Tarea> findTareasTerminadas(Long idUsuario) {
        return getTareasTerminadasOPendientes(idUsuario, true);
    }

    @Override
    public List<Tarea> findTareasPendientes(Long idUsuario) {
        return getTareasTerminadasOPendientes(idUsuario, false);
    }

    private List<Tarea> getTareasTerminadasOPendientes(Long idUsuario, Object terminada) {
        return jpaApi.withTransaction(entityManager -> {
            final TypedQuery<Tarea> typedQuery = entityManager.createQuery("select t " +
                "from Tarea t " +
                "where t.usuario.id = :idUsuario " +
                "and t.terminada = :terminada " +
                "and t.tablero.id is null", Tarea.class);
            typedQuery.setParameter("idUsuario", idUsuario);
            typedQuery.setParameter("terminada", terminada);
            return typedQuery.getResultList();
        });
    }
}
