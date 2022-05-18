# Historias de usuario escogidas para el sprint
## SGT-5 Tablero contiene tareas
### Descripción
* Como usuario quiero crear tareas dentro de tableros para poder organizarlas por temas y/o tópicos.
### Condiciones de Satisfacción (COS)
* Cuando un usuario acceda a la página de detalles de un tablero, le aparecerá por defecto el listado de tareas pendientes de dicho tablero. En esta página, habrá un botón para que el usuario pueda crear una nueva tarea y también tendrá la opción de editarla y borrarla.
* Las tareas creadas pueden tener el mismo nombre.
* Además, desde la página de detalles de un tablero, un usuario podrá acceder también a la lista de tareas terminadas y a la lista de participantes.
* Para acceder a cada una de las páginas (listado de tareas pendientes de un tablero, listado de tareas terminadas de un tablero y lista de participantes de un tablero), el usuario hará uso de una barra de navegación situada debajo del título del tablero, el cual tendrá tres opciones: `Tareas pendientes`, `Tareas terminadas` y `Participantes`.
* En el caso de que el usuario no sea administrador del tablero o no participe en éste, sólo podrá ver el listado de participantes y el listado de tareas pendientes y terminadas (no pudiendo crear, ni editar, ni eliminar tareas).

## SGT-6 Añadir un calendario en donde irán las tareas
### Descripción
* Como usuario que administra muchas tareas quiero tener un calendario en donde poder visualizar la fecha de caducidad de cada tarea para poder organizar mejor mi vida de un simple vistazo.
### Condiciones de Satisfacción (COS)
* En el listado de tareas pondremos al principio un calendario en donde irán todas las tareas posicionadas por rango según su fecha de inicio (nuevo atributo) y su fecha límite.
* El calendario será "editable" para que puedas cambiar las fechas de inicio y fin de una tarea.
* Podrás hacer click para poder editar la tarea.
* Podrás configurar el tipo de vista del calendario (mes, semana, día) y poder asignar una favorita por usuario (nuevo atributo).

## SGT-7 No permitir editar la tarea cuando esté terminada
### Descripción
* Como usuario quiero poder restaurar una tarea y tener la opción de editarla de nuevo.
### Condiciones de Satisfacción (COS)
- Un usuario tiene la posibilidad de **restaurar tareas** que ya había dado por terminadas (_lista de tareas terminadas_), por lo que dichas tareas volverán a estar en la _lista de tareas pendientes_.
- Cuando un usuario da por terminada una tarea, no puede editarla. Es decir, la tarea sólo puede modificarse cuando está pendiente.
- Cuando un usuario lleve a cabo el borrado de una tarea, aparecerá una **advertencia** (para que no se pueda borrar directamente). Si confirma, se borrará la tarea de forma exitosa. En caso contrario, no se realizará ninguna acción.

## SGT-8 Añadir etiquetas
### Descripción
* Como usuario quiero poder asignar una etiqueta de un color a una tarea para poder agruparlas y distinguirlas visualmente (similares a los de GitHub y Trello).
### Condiciones de Satisfacción (COS)
* Un usuario puede **crear etiquetas** para su uso personal, a través de un botón habilitado para ello en la lista de tareas pendientes o terminadas (tanto si está trabajando con tareas en un tablero como si no).
* Las etiquetas tendrán un `título` y un `color`, para que el usuario pueda agruparlas de forma rápida. El usuario no tendrá que preocuparse de escribir el código en hexadecimal del color, ya que se utiliza un **selector de color** muy intuitivo.
* No puede haber dos etiquetas con el mismo título en ciertas ocasiones:
  * Cuando el usuario esté trabajando con sus tareas personales (sin tablero).
  * Cuando se trabaje en un tablero concreto.
* Pueden existir dos etiquetas con el mismo título en el caso que un usuario haya creado una etiqueta en sus tareas personales y otra con dicho título en el tablero; son independientes.
* En un tablero que participe un usuario (o sea administrador) tiene la opción de crear etiquetas a través de un botón habilitado para ello. Además, independientemente de si una etiqueta ha sido creada por dicho usuario, podrá editarla o borrarla.
* En la lista de tareas pendientes aparecerán las tareas acompañadas de la etiqueta que tengan asignada. Además, a la derecha de dicha etiqueta, aparecera una "X", la cual sirve para que un usuario pueda eliminar dicha etiqueta de la tarea. En el caso de que no tengan ninguna todavía, el usuario podrá asignar alguna de las ya existentes a dicha tarea a través de un botón que aparecerá a la derecha del nombre de la tarea.
* En la lista de tareas terminadas, si una tarea tiene alguna etiqueta asociada, aparecerá a su derecha (sin opción de poder editarla ni borrarla).
* Por último, si un usuario no participa ni es administrador de un tablero (tanto público como privado), no podrá crear, modificar, borrar ni asignar etiquetas.

## SGT-9 Tableros privados sólo accesibles por invitación
### Descripción
* Como administrador quiero poder tener tableros privados que sólo sean accesibles por los usuarios que yo desee.
### Condiciones de Satisfacción (COS)
- Un usuario puede crear **tableros públicos y/o privados**. Por lo tanto, los tableros privados sólo serán visibles al administrador del tablero y a los usuarios que participen en éste. Los tableros públicos son accesibles por cualquier usuario.
- La **notificación** le llegará al usuario por **correo**, por lo que ahora será importante verificar el correo.
- Un administrador de un tablero podrá invitar a otros usuarios a participar en su tablero (a través del botón "**Añadir participante**" ya añadido en otra issue y ubicado justo antes de la lista de participantes).

## SGT-10 Añadir/Quitar participantes de un tablero
### Descripción
* Como administrador del tablero quiero poder añadir o eliminar participantes del mismo.
### Condiciones de Satisfacción (COS)
- Un administrador, tanto de un tablero público o privado, tiene la opción de **añadir** o **quitar** participantes a su antojo.
- Justo antes de la lista de participantes de un tablero aparecerá un botón, llamado "**Añadir participante**", con el cual un administrador tendrá la posibilidad de añadir a otros usuarios para que participen en el mismo.
  - Este botón será visible para el administrador del tablero actual.
- Se añadirá una columna "**Acción**" en la lista de participantes y en la cual aparecerá un botón (una "**X**") para que el administrador pueda eliminar de su tablero a los participantes que desee.
  - Un participante podrá desapuntarse a sí mismo de un tablero al que se haya apuntado, a través de un botón en la parte superior de la página del listado de participantes.

***

# Funcionalidades implementadas
## Tablero contiene tareas
Lo principal que nos faltaba por implementar una vez finalizada la práctica 3, era la primera candidata a entrar como nueva funcionalidad.
#### Descripción para el usuario
Ahora, además de tener tus propias tareas personales, también puedes crearlas dentro de cada tablero que tengas o participes. Las tareas del tablero no tendrán nada que ver con las tuyas, pero podrán ser accesibles (ver, editar, borrar) por todos los que tengan acceso al tablero.
#### Descripción técnica
Se ha añadido una nueva relación M:N entre tareas y tablero. Las restricciones que antes se aplicaban a las tareas de cada usuario ahora se aplican a nivel de tablero. Tienen permiso para visualizar y modificar (esto incluye el borrado) las tareas tanto los participantes del tablero como su administrador. Cuando quien creó la tarea en el tablero deja de ser participante, esta permanece en el mismo.

## Añadir un calendario en donde irán las tareas
Administrar las tareas a través de listados es muy de noviembre de 2017. Ahora la moda está en administrarlas desde un calendario.
#### Descripción para el usuario
Ahora, tanto desde la vista privada como desde la del tablero, podrás administrar las tareas a través de un calendario interactivo: mover una tarea de un día a otro (o de una hora a otra), cambiar la duración de la tarea... Todo en tiempo real y sin tener que recargar la página. Además, también dispones de dos nuevas vistas de agenda semanal y diaria por si te es más cómodo y sencillo trabajar así. La vista por defecto la podrás configurar desde tu perfil.
#### Descripción técnica
Se ha integrado el plugin de fullcalendar con nuestra aplicación. Para ello hemos tenido que añadir el atributo de fecha de inicio de la tarea, al igual que quitar las restricciones de fecha mínima límite para poder permitir un mejor trabajo con las tareas. Los eventos lanzados por el plugin son procesados mediante métodos AJAX, sin recarga de página. También se ha añadido un nuevo atributo de vista por defecto a la entidad de usuario para poder persistirla de algún modo.

## No permitir editar la tarea cuando esté terminada
Presuponemos a partir de la historia de _Tablero contiene tareas_ que todo el tema de tareas se aplica en ambos ámbitos, a no ser que se diga lo contrario.
#### Descripción para el usuario
Ahora, en lugar de poder editar directamente una tarea que ya hayas finalizado, tendrás que restaurarla antes de poder editarla de nuevo. Además, si intentas borrar alguna tarea (ya sea pendiente o terminada), tendrás que confirmar que deseas eliminarla definitivamente de tus tareas, para así evitar equivocaciones o acciones no deseadas.
#### Descripción técnica
Se ha añadido una nueva funcionalidad que permite restaurar una tarea terminada, ya que éstas ahora no podrán ser editadas una vez estén en terminadas. Por último, se ha cambiado la confirmación de la operación de pasar a terminadas una tarea a la operación de borrarla.

## Añadir etiquetas
Presuponemos a partir de la historia de _Tablero contiene tareas_ que todo el tema de tareas se aplica en ambos ámbitos, a no ser que se diga lo contrario.
#### Descripción para el usuario
Ahora, tendrás la opción de crear nuevas etiquetas con un título y un color y poder asociarlas a tus tareas con el objetivo de organizarlas de forma rápida y cómoda. Las etiquetas que crees para tus tareas personales serán distintas de las que se creen en los tableros en los que participes. La única restricción es que no podrás tener dos etiquetas con un mismo título.
#### Descripción técnica
Se ha creado un nuevo modelo llamado Etiqueta y se han añadido 3 relaciones: M:N entre etiquetas y usuario, M:N entre tareas y etiqueta y M:N entre etiquetas y tablero. Se deben diferenciar entre las etiquetas de un usuario (tareas privadas de éste) y las de un tablero, ya que son independientes. Además, los usuarios que sean participantes o administrador de un tablero tendrán permiso para crear, modificar y borrar etiquetas. Si una etiqueta ha sido creada por un usuario en un tablero, ésta puede ser modificada o borrada por otro usuario que pertenezca a dicho tablero. Las etiquetas se pueden tanto asociar como desligar de una tarea. Por último, se ha integrado un color picker en nuestra aplicación.

## Tableros privados sólo accesibles por invitación
#### Descripción para el usuario
Ahora, podrás crear tableros privados e invitar a los usuarios que desees. Por otro lado, si un usuario te agrega a su tablero privado, recibirás un correo notificándote que has empezado a formar parte de él. Además, en este correo, se incluirá un enlace para acceder a dicho tablero.
#### Descripción técnica
Se ha añadido un atributo al modelo Tablero que permite distinguir entre tableros públicos y privados. En relación a esto, a la hora de crear un tablero, se ha integrado un bootstrap-toggle para elegir entre público o privado. Ahora, los tableros privados estarán ocultos a los usuarios que no sean administradores ni participen en éstos. También se ha integrado el plugin Play-Mailer en nuestra aplicación, el cual permite enviar un correo al participante que ha sido añadido a un tablero privado.

## Añadir/Quitar participantes de un tablero
#### Descripción para el usuario
Ahora, como administrador de un tablero podrás añadir y eliminar participantes. Si eres participante de un tablero, y ya no te interesa seguir en éste, podrás desapuntarte teniendo en cuenta que si el tablero es privado sólo podrás apuntarte de nuevo si el administrador te vuelve a invitar.
#### Descripción técnica
Tanto para los tableros públicos como para los tableros privados, se ha añadido un modal a la hora de añadir un participante en un tablero. Si el usuario no existe o se intenta añadir a uno ya existente o a sí mismo, no se llevará a cabo la acción, mostrando un mensaje de error. Por otro lado, cuando se borra un participante de un tablero, se realiza una petición AJAX, la cual elimina a dicho usuario del tablero y posteriormente recarga la página.

***

# Informe sobre la metodología seguida
## Métricas de desarrollo
### Gráfica de burndown
* Tenemos 12 puntos de historia por hacer en 28 días aproximadamente:
  * __SGT-5 Tablero contiene tareas__: Tamaño M (2 puntos).
  * __SGT-6 Añadir un calendario en donde irán las tareas__: Tamaño L (4 puntos).
  * __SGT-7 No permitir editar la tarea cuando esté terminada__: Tamaño S (1 punto).
  * __SGT-8 Añadir etiquetas__: Tamaño M (2 puntos).
  * __SGT-9 Tableros privados sólo accesibles por invitación__: Tamaño M (2 puntos).
  * __SGT-10 Añadir/Quitar participantes de un tablero__: Tamaño S (1 punto).
* Como se puede observar en la siguiente gráfica burndown, el desarrollo **real** se aleja un poco del **ideal** (en teoría está calculado que cada 5 días se realicen 2 puntos). Esto es debido, entre otras razones, a que:
  * Dani tuvo que cambiar en la última semana su historia de usuario de **Registro y login con Facebook** por la de **Añadir etiquetas**, por lo que los puntos de historia que se tenían previstos para una semana antes se hicieran la última.
  * La historia de usuario de Pavel eran 4 puntos y, a pesar de que ha hecho un desarrollo continuo, no se terminó hasta la última semana, por lo que no se pudo contar ningún punto hasta que se finalizó.
  * En general, cada uno de los miembros del equipo no ha estado disponible alguna semana para llevar a cabo alguna de sus historias de usuario (por trabajo, otras asignaturas, ...). Y aunque no se haya progresado en este aspecto, hay que destacar que se ha revisado el código del correspondiente compañero que solicitaba mezclar su rama con master (a través del Pull Request).
[![grafica-burndown.png](https://s31.postimg.org/v6jfrmobf/grafica-burndown.png)](https://postimg.org/image/pid50qjyv/)
### Pull Requests por semana
* Esta es la gráfica que se ha obtenido de los Pull Requests realizados en cada semana del sprint.
* Las 2 primeras semanas se llevaron a cabo 2 PR, la tercera semana 3 PR y la última 11 PR. Esto último es debido a que:
  * Dani tuvo que cambiar en la última semana su historia de usuario de **Registro y login con Facebook** por la de **Añadir etiquetas**. Dicha historia de usuario se divide en 5 issues (prácticamente casi la mitad de dicha semana).
  * En general, cada uno de los miembros del equipo no ha estado disponible alguna semana para llevar a cabo el desarrollo de código y añadir PR (por trabajo, otras asignaturas, ...). Y aunque no se haya progresado en este aspecto, hay que destacar que se ha revisado el código del correspondiente compañero que solicitaba mezclar su rama con master (a través del Pull Request).
[![prporsemana.png](https://s31.postimg.org/4mvg325e3/prporsemana.png)](https://postimg.org/image/yesii8s7b/)

## Evolución del tablero Trello
* A continuación se va a exponer la evolución del **tablero _Trello_**
* Como se puede observar, hasta el momento hay 2 historias de usuario en la columna "_Sprint_": **SGT-9 Tableros privados sólo accesibles por invitación** y **SGT-10 Añadir/Quitar participantes de un tablero**; 3 historias en la columna "_En marcha_": **SGT-8 Registro y login con Facebook**, **SGT-6 Añadir calendario de tareas** y **SGT-7 No permitir editar la tarea cuando esté terminada**; ninguna en la columna "_QA_"; y 1 en la columna "_Terminadas_": **SGT-5 Tablero contiene tareas** (hasta este momento tendríamos 1 historia de usuario terminada, es decir, 2 puntos).
[![tablerotrello.jpg](https://s31.postimg.org/uif6mouhn/tablerotrello.jpg)](https://postimg.org/image/5p5mm1bh3/)
* Unos días más tarde, la historia de usuario **SGT-9 Tableros privados sólo accesibles por invitación** se ha movido a la columna "_En marcha_" y la historia **SGT-7 No permitir editar la tarea cuando esté terminada** se ha pasado a la columna "_QA_" (a la que se deberán aplicar pruebas funcionales y de rendimiento). El resto de historias de usuario se mantienen en sus respectivas columnas.
[![tablerotrello2.png](https://s31.postimg.org/9mswb4t23/tablerotrello2.png)](https://postimg.org/image/xds9t8t93/)
* Unos días más tarde, la historia de usuario **SGT-10 Añadir/Quitar participantes de un tablero** se ha movido a la columna "_En marcha_", por lo que ya no hay ninguna historia de usuario en la columna "_Sprint_". Además, la historia de usuario **SGT-7 No permitir editar la tarea cuando esté terminada** se ha probado de forma exitosa por el equipo (dando el visto bueno) y por lo tanto se ha pasado a la columna "_Terminadas_" (hasta este momento tendríamos 2 historias de usuario terminadas, es decir, 3 puntos).
[![tablerotrello3.png](https://s31.postimg.org/pz2y0wx3v/tablerotrello3.png)](https://postimg.org/image/mfh0b3udz/)
* Unos días más tarde, la historia de usuario **SGT-10 Añadir/Quitar participantes de un tablero** ha pasado por la columna "_QA_" (a la que se realizaron pruebas funcionales y de rendimiento) y por último a la columna "_Terminadas_" (el equipo ha dado su visto bueno). Hasta este momento tendríamos 3 historias de usuario terminadas, es decir, 4 puntos. Además, se ha movido la historia **SGT-9 Tableros privados sólo accesibles por invitación** a la columna "_QA_". Cabe destacar que aquí se ha cambiado la historia **SGT-8 Registro y login con Facebook** por **SGT-8 Añadir etiquetas** (ya que se tuvieron bastantes complicaciones a la hora de desarrollarla).
[![tablerotrello4.png](https://s31.postimg.org/ij3ofa9gb/tablerotrello4.png)](https://postimg.org/image/526pwez4n/)
* El último día, la historia de usuario **SGT-9 Tableros privados sólo accesibles por invitación** se ha pasado a la columna "_Terminadas_" (el equipo ha dado el visto bueno). Además, las historias **SGT-6 Añadir calendario de tareas** y **SGT-8 Añadir etiquetas** han pasado por la columna "_QA_" y finalmente se movieron a la columna "_Terminadas_".
* Para finalizar se puede comprobar que todas las historias de usuario escogidas para el sprint se han terminado (6 historias de usuario, es decir, 12 puntos).
[![tablerotrello5.png](https://s31.postimg.org/nuil03y4r/tablerotrello5.png)](https://postimg.org/image/ms8ehkfbb/)

***

# Informes sobre las reuniones de Scrum
## Semana 1 - Planificación del Sprint _(22-11-2017)_
* Esta semana hemos elegido, de la lista de historias de usuario que teníamos en el Backlog, cuáles íbamos a implementar en este Sprint de 4 semanas de duración:
  * SGT-5 Tablero contiene tareas _(Tamaño: M)_
  * SGT-6 Añadir calendario de tareas _(Tamaño: L)_
  * SGT-7 No permitir editar la tarea cuando esté terminada _(Tamaño: S)_
  * SGT-8 Registro y login con Facebook _(Tamaño: M)_
  * SGT-9 Tableros privados sólo accesibles por invitación _(Tamaño: M)_
  * SGT-10 Añadir/Quitar participantes de un tablero _(Tamaño: S)_
* Así, en total tenemos 12 puntos para este Sprint (tocando a 4 puntos por persona). Además, también se acordó quién realizaría cada historia de usuario:
  * __Dani__
    * SGT-5 Tablero contiene tareas
    * SGT-8 Registro y login con Facebook
  * __Pavel__
    * SGT-6 Añadir calendario de tareas
  * __Espe__
    * SGT-7 No permitir editar la tarea cuando esté terminada
    * SGT-9 Tableros privados sólo accesibles por invitación
    * SGT-10 Añadir/Quitar participantes de un tablero
* También debatimos cómo implementar algunas de las historias de usuario. Por ejemplo, en el caso de **SGT-9 Tableros privados sólo accesibles por invitación**, pensamos que los usuarios deberían recibir la invitación por correo y que, para ello, podríamos usar [Play-Mailer](https://github.com/playframework/play-mailer).
* Por último, se habló que durante esta primera semana se iban a realizar las páginas en la Wiki de GitHub para cada historia de usuario, y que cada miembro del equipo (si no tuviera demasiada carga de trabajo con otras asignaturas) empezaría a desarrollar alguna de sus historias de usuario.

## Semana 2 - Reunión semanal (Daily) _(29/11/2017)_
* Revisando el Trello (de esta reunión no disponemos de foto del tablero), sólo se ha movido la historia de usuario **SGT-5 Tablero contiene tareas** a lo largo de todo el tablero (ahora se encuentra en la columna "Terminadas").
* Para cada uno de los integrantes del equipo, este es el resumen:
  * __Dani__
    * __¿Qué he hecho desde la última reunión?__: He hecho la historia de usuario **SGT-5 Tablero contiene tareas**. Ahora puede haber tareas que estén asociadas a un tablero y otras que no.
    * __¿Qué voy a estar haciendo para la próxima semana?__: Voy a empezar a mirarme la documentación del SDK de Facebook para JavaScript, además de revisar código de mis compañeros.
    * __¿Qué problemas he tenido?__: Uno de los problemas que tuve fue al actualizar la relación entre Tablero y Tarea, ya que sólo me guardaba una tarea en un tablero (aunque añadiera más de una). Esto se solucionó haciendo un merge de la tarea, en el método update correspondiente al repositorio de la Tarea (al igual que se hizo con Tablero en la práctica 2). Tuve que pedirle una tutoría a Domingo para resolver el problema cuanto antes, ya que estuve bloqueado dos días probando cosas sin éxito. 
  * __Espe__
    * __¿Qué he hecho desde la última reunión?__: He revisado código de mi compañero Daniel y he investigado acerca de la documentación del plugin ([Play-Mailer](https://github.com/playframework/play-mailer)) que se va a utilizar para la historia de usuario **SGT-9 Tableros privados sólo accesibles por invitación**.
    * __¿Qué voy a estar haciendo para la próxima semana?__: Hacer la historia de usuario **SGT-7 No permitir editar la tarea cuando esté terminada**.
    * __¿Qué problemas he tenido?__: Ninguno, ya que sólo he estado revisando código y mirando documentación.
  * __Pavel__
    * __¿Qué he hecho desde la última reunión?__: Nada, debido al problema que he tenido (que explico a continuación).
    * __¿Qué voy a estar haciendo para la próxima semana?__: Empezar la historia de usuario **SGT-6 Añadir calendario de tareas**. Es decir, añadir el calendario y colocarle las tareas.
    * __¿Qué problemas he tenido?__: Se me rompió el portatil y no he podido avanzar en el desarrollo del proyecto.

## Semana 3 - Reunión semanal (Daily) _(8/12/2017)_
* Revisando el Trello, hemos observado que tenemos el WIP de la columna "En marcha" al máximo, con 3 historias en curso. Eso quiere decir que todos y cada uno de nosotros está realizando una historia de usuario.
[![tablerotrello-semana2.jpg](https://s18.postimg.org/64algdjrt/tablerotrello-semana2.jpg)](https://postimg.org/image/dk9v267h1/)
* Para cada uno de los integrantes del equipo, este es el resumen:
  * __Dani__
    * __¿Qué he hecho desde la última reunión?__: He estado mirando la documentación del SDK de Facebook para JavaScript para llevar a cabo la historia de usuario **SGT-8 Registro y login con Facebook**. De momento lo que tengo hecho es el botón de iniciar sesión en la página de registro.
    * __¿Qué voy a estar haciendo para la próxima semana?__: Voy a intentar terminar los issues _Registro de usuarios con Facebook_ y _Login de usuarios con Facebook_ y me dejaría para la última semana el issue _Actualización del perfil de un usuario logeado con Facebook_ para separar los datos a editar entre usuarios registrados con la aplicación y registrados por Facebook.
    * __¿Qué problemas he tenido?__: He tenido problemas al intentar guardar un usuario que ya ha iniciado sesión en Facebook en la BD. Más concretamente, llamar desde JavaScript a un método del Controller (sería como un POST) para llevar a cabo dicho registro.
  * __Espe__
    * __¿Qué he hecho desde la última reunión?__: He hecho la historia de usuario **SGT-7 No permitir editar la tarea cuando esté terminada**, la cual incluye también la opción de restaurar la tarea.
    * __¿Qué voy a estar haciendo para la próxima semana?__: Empezaré la historia de usuario **SGT-9 Tableros privados sólo accesibles por invitación**.
    * __¿Qué problemas he tenido?__: El problema que he tenido es que no me dejaba trasladar el mismo formato de confirmación que utilizábamos para terminar una tarea al de borrarla, ya que en un form sólo tienes la opción de poner un GET o un POST, no un DELETE.
  * __Pavel__
    * __¿Qué he hecho desde la última reunión?__: He encontrado el full calendar (me lo dijo mi compañero Joaquín) y lo he incluido en el proyecto. Me puse a trastear con él y ya que estaba implementé también el colocar las tareas en el calendario.
    * __¿Qué voy a estar haciendo para la próxima semana?__: Revisaré el último PR de Espe para comprobar si su solución con la confirmación de borrado de tarea es buena o no; en caso de que no, escribiré otra propuesta. Respecto a lo mío, espero poder terminar el Drag&Drop de tareas o por lo menos el añadir la fecha de inicio a la tarea.
    * __¿Qué problemas he tenido?__: Como no tengo la fecha de inicio de una tarea, de momento no puedo colocar de forma correcta las tareas. Provisionalmente he hecho que la fecha de inicio y la de finalización sea la misma: la fecha límite. También estoy preocupado por el tema de mover tareas por el calendario (cambiarlas de fecha), ya que puede ser bastante más complicado de lo que me han dicho que es.

## Semana 4 - Reunión semanal (Daily) _(13/12/2017)_
* En general, esta semana Dani ha estado un poco liado con otras cosas y no ha podido hacer mucho, Esperanza tiene dos historias de usuario en desarrollo simultáneamente y Pavel ya tiene el Drag&Drop (se suponía la parte complicada de su historia) terminada.
[![6b4f78c9-a133-4cc7-886a-2c2f5eae3d03.png](https://s18.postimg.org/6ip91vgt5/6b4f78c9-a133-4cc7-886a-2c2f5eae3d03.png)](https://postimg.org/image/voq78pi39/)
* Para cada uno de los integrantes del equipo, este es el resumen:
  * __Dani__
    * __¿Qué he hecho desde la última reunión?__: Seguir mirando documentación, no mucho más, ya que he estado liado con otras asignaturas, pero he estado revisando todo el código y aprobando cambios de mis otros dos compañeros.
    * __¿Qué voy a estar haciendo para la próxima semana?__: Terminar la historia de usuario **SGT-8 Registro y login con Facebook**, revisar código de mis compañeros y además ayudar a realizar la presentación.
    * __¿Qué problemas he tenido?__: Ninguno, ya que sólo he estado revisando código y mirando documentación.
  * __Espe__
    * __¿Qué he hecho desde la última reunión?__: He terminado la issue _Poder tener tableros públicos y privados_ y restringir el acceso a los que no participan en un tablero privado. He tenido que revisar algunas historias de usuario para cambiar los requisitos, ahora que ya tenemos una mejor información sobre cómo queremos que se comporte la aplicación para finales de este Sprint.
    * __¿Qué voy a estar haciendo para la próxima semana?__: Lo que queda: terminar las dos historias de usuario y revisar código de mis compañeros.
    * __¿Qué problemas he tenido?__: Ha estado complicado configurar el plugin de Bootstrap del checkbox-toggle, en especial el hecho de integrarlo con los helpers que proporciona el motor de plantillas de Twirl.
  * __Pavel__
    * __¿Qué he hecho desde la última reunión?__: He añadido el atributo de fecha de inicio a la tarea (además de ir corrigiendo varios errores que me detectó Dani) y además he empezado con la parte de hacer el calendario interactivo, por lo que he recuperado ese tiempo atrasado que llevaba.
    * __¿Qué voy a estar haciendo para la próxima semana?__: Tengo que probar el resize a ver si me gusta cómo funciona y decidir si incluirlo o no, además de hacer que cuando hagas click en la tarea te lleve a su formulario de edición. Una vez terminado esto, terminaré por añadir diferentes vistas del calendario y, si procede, poder establecer una vista de calendario por defecto. Por otro lado, y si me da tiempo, tengo planeado añadir una funcionalidad extra de cara a la presentación del martes (no nos olvidemos de que también tendré que ayudar en la propia presentación y demás).
    * __¿Qué problemas he tenido?__: Ninguno, pero la verdad es que me veo muy justo de tiempo para poder terminar todo lo que tengo pendiente.

***

# Resultado de la retrospectiva
## ¿Qué ha ido bien?
* La **comunicación** entre los miembros del equipo y el reparto de trabajo.
* Las **reuniones semanales** nos han ayudado a estar al tanto de los avances y problemas del proyecto.
* **Forzar a que un PR no pueda ser aceptado** hasta que uno de los otros miembros lo revise y te dé el visto bueno ha sido una medida que ha ayudado en la detección temprana de bugs y también ha servido para mejorar la calidad del desarrollo (mejores interfaces, código más conciso...).
* 138 **tests** y sumando...
## ¿Qué se podría mejorar?
* Para el próximo Sprint se debería hacer una **mejor estimación de las historias de usuario**, del esfuerzo que supone cada historia.
* Consideramos que estaría genial que la UA pagase una **licencia de Travis**, ya que en ocasiones otros grupos pueden estar usándolo al mismo tiempo y no disponemos de trabajos concurrentes, o en cambio probar a usar Jenkins, una alternativa muy parecida a ésta.
* Tener que estar coordinándose entre el tablero de Trello y el de Github ha sido un poco lioso; pensamos que hubiese sido mejor elegir o uno u otro, para así tener **todo integrado en un único sitio**.
