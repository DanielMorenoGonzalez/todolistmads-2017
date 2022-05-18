package models;

import com.google.inject.ImplementedBy;

import java.util.List;

@ImplementedBy(JPATareaRepository.class)
public interface TareaRepository {
    Tarea add(Tarea tarea);

    Tarea update(Tarea tarea);

    void delete(Long idTarea);

    Tarea findById(Long idTarea);

    List<Tarea> findTareasTerminadas(Long idUsuario);

    List<Tarea> findTareasPendientes(Long idUsuario);
}
