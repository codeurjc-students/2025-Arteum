![image](https://github.com/user-attachments/assets/7e63f64e-29f4-439b-8575-c19f2c4574ee)# 2025-Arteum

# Fase 0

## Nombre de la aplicación web
- **Arteum**

## Nombre del alumno y del tutor
- Enrique Tentor Martin
- Óscar Soto Sánchez

## Tema de la aplicación web
- Plataforma de Reseñas y Explicaciones de Museos y Obras de Arte

## Herramienta de coordinación
- Trello
- Miro

## Descripción del proyecto: 
### Entidades
- Usuario, Museo, Obra de arte, Reseña, Explicación
  - Un usuario registrado puede escribir varias reseñas y (si es experto) varias explicaciones o curiosidades.
  - Un museo tiene muchas obras de arte.
  - Una obra de arte puede recibir muchas reseñas de distintos usuarios.
  - Una obra de arte puede tener muchas explicaciones de distintos expertos.

### Funcionalidades principales
- Registro e inicio de sesión
- Búsqueda de museos y exposiciones por nombre, ubicación o popularidad.
- Publicación y visualización de reseñas con sistema de puntuación.
- Listas personalizadas (exposiciones visitadas, pendientes, favoritas).
- Sección destacada para "Curiosidades y contexto histórico" en cada obra.
- Recomendaciones personalizadas según historial de visitas y reseñas.

### Usuarios
- Anónimo
- Registrado
- Administrador

### Permisos de usuario
- Anónimo:
  - Buscar museos u obras y consultar su información

- Registrado:
  - Todos los permisos de Anónimo
  - Marcar como favoritos, tener listas de obras vistas o por ver
  - Añadir o eliminar sus reseñas
  - Modificar su área personal

- Administrador: 
  - Todos los permisos de Registrado
  - Crear, modificar y eliminar usuarios
  - Crear, modificar y eliminar reseñas
  - Crear, modificar y eliminar museos
  - Crear, modificar y eliminar obras de arte
  - Crear, modificar y eliminar explicaciones

### Imágenes
- Avatares de los usuarios
- Miniatura de los museos
- Miniatura de las obras de arte

### Gráficos
- Gráfico para visualizar las puntuaciones de las reseñas por obra de arte
- Gráfico con usuarios que han visto o dado favorito a la obra de arte

### Tecnología complementaria
- Generador de PDFs

### Algoritmos o consulta avanzada
Recomendaciones personalizadas según reseñas y obras favoritas: se calcula la afinidad al resto de usuarios según las obras que ha dado mejor puntuación el otro usuario y según las obras que le quedan por ver al usuario objetivo. Se calculan los posibles candidatos y en base a ellos se calcula una afinidad que nos permite sacar las obras de arte que más se adecuan al usuario.

# Fase 1
## Base
- Backend: API REST en Java y Spring Boot
- Frontend: Angular (SPA)
- Base de datos: MySQL
- Repositorio: GitHub
- CI/CD: GitHub Actions
- Pruebas automáticas: Java, Junit, Selenium y Rest Assured

## Ampliación
- Pruebas automáticas (2 puntos)
- Análisis estático de código (1 punto)

## Capturas de las pantallas
![image](https://github.com/user-attachments/assets/57b95596-634b-4ff8-9035-0793902e343f)
Página principal

![image](https://github.com/user-attachments/assets/1cd1c711-f318-4d50-8bf0-f005829d9c4b)
Inicio de sesión

![image](https://github.com/user-attachments/assets/658f2986-0c11-482c-9668-e86704715867)
Crear una nueva cuenta 

![image](https://github.com/user-attachments/assets/d5d2cf9e-e009-4e7f-9ab7-ee300a3ece31)
Página de obras de arte con filtros, ordenación, paginación y cambio de tamaño.

![image](https://github.com/user-attachments/assets/632e1f43-4256-46b7-9efb-b52b3b39fb95)
Detalle de una obra de arte, con sus datos y los datos de su artista, más abajo las reviews y obras similares.

![image](https://github.com/user-attachments/assets/de304e8c-3796-4d69-aa0e-bff79a549751)
Página de artistas con filtros, ordenación, paginación y cambio de tamaño.  

![image](https://github.com/user-attachments/assets/bd36478a-35b3-4ae6-acd7-c32813d8852b)
Detalle de un artista, con sus datos y sus obras de arte.

![image](https://github.com/user-attachments/assets/0990b733-bd26-49dc-a43b-655cd8cd0d65)
Página de museos con filtros, ordenación, paginación y cambio de tamaño. 

![image](https://github.com/user-attachments/assets/27c280ff-e2fc-425e-8659-fae87a829653)
Detalle de un museo, con sus datos y sus obras de arte.

![image](https://github.com/user-attachments/assets/4182cffa-cf8b-489b-b366-f7cdd7c7be3c)
Pagina sobre la aplicación web.

![image](https://github.com/user-attachments/assets/2bb9fcb7-8fd5-425d-a83f-e296c749d397)
Pagina de contacto.

![image](https://github.com/user-attachments/assets/9e5169eb-76a9-43c2-8ff5-a77936ce3bca)
Pagina de preguntas frecuentes. 

![image](https://github.com/user-attachments/assets/5b7f9919-c003-4f3e-a364-8543d884cfd6)
Pagina de términos y condiciones de uso.

![image](https://github.com/user-attachments/assets/f1010669-c2bf-47f0-a602-85255ad23db5)
Pagina de política de privacidad.

![image](https://github.com/user-attachments/assets/a4bc1bba-e36b-4f4c-b500-fea31dfa9455)
Pagina de gestión de cuenta de un usuario que ha iniciado sesión. 

![image](https://github.com/user-attachments/assets/b369481c-8b5b-47fa-8761-7dae21b77bcd)
Perfil público de un usuario en el que salen sus datos públicos y sus obras favoritas.

![image](https://github.com/user-attachments/assets/51437757-3ac2-4240-ab4c-845a17073079)
Pagina de recomendación de obras de arte en base al algoritmo desarrollado.

![image](https://github.com/user-attachments/assets/841f3fe8-e9b8-47d6-9f68-b76d5469eab7)
Pagina de reviews de un usuario.

![image](https://github.com/user-attachments/assets/c3014702-92ce-4cb4-91ef-1ed417570cb7)
Pagina de seguidos y seguidores de un usuario, el diseño es el mismo, pero son dos endpoints diferentes.

![image](https://github.com/user-attachments/assets/671a1fe7-e7c1-4bab-8758-82deb0ccc4ae)
Pagina de obras de arte favoritas.

![image](https://github.com/user-attachments/assets/6aa2ae7f-bdfd-4052-975d-635948455a9e)
Pagina de estadísticas de un usuario.

![image](https://github.com/user-attachments/assets/0292f07e-6549-4db4-9efc-0051894366cc)
Pagina de gestión de un administrador.

![image](https://github.com/user-attachments/assets/2e841d2c-2b46-4cc4-8e03-91a505d55537)
Pagina de creación o edición de un artista.

![image](https://github.com/user-attachments/assets/cc3d7bc5-f6bc-4177-aac4-174b69aee90a)
Pagina de creación o edición de una obra de arte.

![image](https://github.com/user-attachments/assets/9a7868da-ea9f-4514-9b12-aa163758c134)
Pagina de creación o edición de un museo.

![image](https://github.com/user-attachments/assets/0e9c78b9-b030-4565-aed4-d2a6f098c61a)
Pagina de creación de una obra de arte.

![image](https://github.com/user-attachments/assets/9cdacbba-c0d3-41e7-97f9-ef0054694745)
Página de estadísticas de la página web para un administrador.

## Diagrama de navegación
![diagramadenavegacion](https://github.com/user-attachments/assets/d054379b-39d2-42fb-b8c5-3bc32eee034c)

## Diagrama con las entidades de la base de datos
![diagramadebbdd1](https://github.com/user-attachments/assets/dc4b5b1e-8811-40dc-b068-ca7de28b778c)
![diagramadebbdd2](https://github.com/user-attachments/assets/d1eac0c3-3055-4c8c-85ea-3744e1e337be)

## Diagrama de clases del backend
![Diagrama de clases del backend](https://github.com/user-attachments/assets/eb573f6f-147c-413a-9cd4-71e8fe289d68)

## Diagrama de clases y templates de la SPA
![Diagrama de clases y templates de la SPA](https://github.com/user-attachments/assets/d7302194-6a00-49fc-9114-9c88a093874d)

## Documentación para construcción de la imagen docker
Para poder ejecutar la aplicación se puede hacer de la siguiente manera:
- Requisitos previos
  - Tener Docker instalado
  - Disponer de una cuenta en Docker Hub
  - Acceso al proyecto con los ficheros:
    - Dockerfile
    - docker-compose.yml
    - create-image.sh
  - El JAR generado (Arteum-0.0.1-SNAPSHOT.jar) ubicado correctamente en la carpeta backend/target 
- Ejecutar el create-image.sh con permisos

## Instrucciones de ejecución de la aplicación dockerizada
- Comprobar que se han ejecutado y desplegado las imágenes en tu repositorio de Docker
- Una vez levantado todo correctamente, puedes acceder desde: https://localhost/

## Vídeo


# Fase 2
## Qué tareas se realizan de forma automática
- Pruebas de API REST
  - Todos los permisos de Registrado
    - Usuario anónimo
    - Iniciar sesión
    - Registro de nuevo usuario
  - Artistas
    - Obtener lista paginada de artistas
    - Obtener detalles de un artista
    - Obtener imagen del artista o placeholder
    - Obtener los 10 artistas con mayor puntuación media en sus obras
  - Obras de arte
    - Obtener lista paginada de obras de arte
    - Obtener detalles de una obra de arte
    - Obtener imagen de la obra de arte o placeholder
    - Añadir obra a favoritos
    - Eliminar obra de favoritos
    - Obtener las 7 obras con mayor puntuación promedio
    - Obtener 7 obras seleccionadas aleatoriamente
  - Museos
    - Obtener lista paginada de museos
    - Obtener detalles de un museo
    - Obtener imagen del museo o placeholder
  - Review
    - Crear una nueva review
    - Editar una review existente
    - Eliminar una review
    - Obtener la review del usuario para una obra
  - Usuarios
    - Obtener lista paginada de usuarios
    - Obtener perfil público de un usuario
    - Obtener obras recomendadas
    - Generar PDF del perfil de usuario
    - Eliminar mi cuenta
    - Estadísticas del usuario autenticado
    - Reseñas del usuario autenticado paginadas
    - Lista paginada de seguidos
    - Lista paginada de seguidores
    - Seguir a un usuario
    - Dejar de seguir a un usuario
    - Listar obras de arte favoritas
    - Cambiar la contraseña del usuario autenticado
    - Actualizar datos del perfil del usuario autenticado
    - Obtener imagen de usuario
    - Actualizar imagen de usuario
    - Eliminar imagen de usuario
  - Administrador
    - Crear una obra de arte
    - Editar una obra de arte
    - Eliminar una obra de arte
    - Crear un artista
    - Editar un artista
    - Eliminar un artista
    - Crear un museo
    - Editar un museo
    - Eliminar un museo
    - Crear un usuario
    - Editar un usuario
    - Eliminar un usuario
    - Crear una review
    - Editar una review
    - Eliminar una review
    - Visualizar estadísticas sobre toda la web

- Pruebas de interfaz de usuario
  - Artistas
    - Navegar hasta la página de artistas
    - Introducir criterios de búsqueda
    - Introducir criterios de filtrado
    - Introducir criterios de ordenación
    - Navegar hasta la página de detalles de un artista
  - Obras de arte
    - Navegar hasta la página de obras de arte
    - Introducir criterios de búsqueda
    - Introducir criterios de filtrado
    - Introducir criterios de ordenación
    - Navegar hasta la página de detalles de una obra de arte
  - Museo
    - Navegar hasta la página de museos
    - Introducir criterios de búsqueda
    - Introducir criterios de filtrado
    - Introducir criterios de ordenación
    - Navegar hasta la página de detalles de un museo
  - General
    - Navegar hasta la página principal
    - Navegar hasta inicio de sesión
    - Navegar hasta registro de usuario
    - Navegar hasta la página de conta
    - Navegar hasta la página de preguntas frecuentes
    - Navegar hasta la página de sobre nosotros
    - Navegar hasta la página de términos y condiciones
    - Navegar hasta la página de política de privacidad
  - Usuario
    - Navegar hasta inicio de sesión
    - Introducir parámetros necesarios
    - Ejecutar el submit de datos
    - Navegar hasta registro de usuario
    - Introducir parámetros necesarios
    - Ejecutar el submit de datos 
## Dónde se albergan los artefactos generados
- Tareas automáticas
  - En Pull Requests: compilación del proyecto, ejecución de pruebas unitarias y de interfaz.
  - En commits a main: construcción y publicación de imagen Docker en Docker Hub. 
- Artefactos generados
  - Archivo .jar compilado del backend: target/Arteum-0.0.1-SNAPSHOT.jar
  - Imagen Docker: disponible en el repositorio Docker Hub del proyecto. 

# Fase 3
## Pruebas automáticas
Las pruebas se implementan utilizando JUnit 5 y Spring MockMvc, lo que permite simular peticiones HTTP a los endpoints y verificar tanto las vistas renderizadas como los datos enviados al modelo. Estas pruebas permiten comprobar no solo la lógica del controlador, sino también la correcta interacción con los servicios implicados (UserService, ReviewService, ArtworkService, etc.). 

- Los tests tienen los siguientes objetivos principales:
  - Verificar que los endpoints responden correctamente a peticiones válidas.
  - Asegurar la redirección adecuada en casos de acceso no autorizado o condiciones lógicas.
  - Validar la asignación de atributos en el modelo utilizados en las vistas.
  - Confirmar que se invocan los servicios necesarios y se comportan correctamente.
  - Detectar errores comunes como NullPointerException o errores de seguridad en los flujos del usuario. 

- Buenas Prácticas Aplicadas
  - Principio AAA (Arrange, Act, Assert): Cada prueba sigue una estructura clara que mejora su legibilidad y mantenimiento.
  - Uso de Mocks: Servicios como UserService, ReviewService, etc., se mockean para evitar dependencias externas o base de datos real.
  - Cobertura de Escenarios Positivos y Negativos: Se prueban tanto los casos esperados como aquellos que podrían dar lugar a errores (por ejemplo, errores en contraseñas, imágenes vacías, obras de arte nulas, etc.).
  - Simulación de Sesiones. 

- Beneficios Obtenidos
  - Mayor robustez y fiabilidad del sistema, al detectar errores antes del despliegue.
  - Prevención de regresiones, especialmente útil cuando se modifican controladores o servicios.
  - Validación de la lógica de negocio sin depender de la base de datos o servicios externos.
  - Mejora en la mantenibilidad del código, al documentar el comportamiento esperado de cada endpoint. 
![fase3_1](https://github.com/user-attachments/assets/d3846479-0754-4335-81b8-f6751b558be0)

## Análisis estático de código
### Configuración e instalación de SonarQube
Para facilitar el análisis del proyecto en un entorno controlado, se optó por una instalación local de SonarQube Developer Edition. El entorno fue desplegado en una máquina con sistema operativo Windows y JDK 17 (ajustado tras problemas de compatibilidad con versiones más modernas como Java 24). 
El proceso de instalación incluyó los siguientes pasos: 
- Descarga de SonarQube desde el sitio oficial.
- Configuración del archivo sonar.properties para habilitar la ejecución local.
- Arranque del servidor Sonar mediante el script StartSonar.bat.
- Acceso a la interfaz web en http://localhost:9000. 
En el proyecto Angular y Spring, se integró el análisis de código mediante SonarScanner, configurado para lanzar los análisis sobre ambos entornos. En el caso de Angular, se utilizó la cobertura generada por ng test --code-coverage, mientras que para Spring se empleó el plugin de JaCoCo (jacoco-maven-plugin) para generar informes XML compatibles con SonarQube. 
Se configuró un archivo sonar-project.properties. 
Esta configuración permitió centralizar el análisis del frontend y backend en una sola instancia de SonarQube. 

### Análisis y tratamiento de métricas clave
Una vez configurada la herramienta, se procedió a lanzar análisis periódicos del código. SonarQube ofrece una clasificación de problemas según diferentes categorías que permiten estructurar los esfuerzos de mejora: 
2.1. Reliability (Fiabilidad) 
- Esta categoría agrupa errores que podrían provocar fallos en tiempo de ejecución. Se detectaron principalmente los siguientes problemas:
- Uso incorrecto de estructuras condicionales.
- Posibles NullPointerException no gestionadas.
- Llamadas a métodos sin comprobación de resultados.
- Para cada incidencia marcada como bug, se procedió a revisar manualmente su veracidad, descartando falsos positivos y corrigiendo aquellas que podrían comprometer el comportamiento del sistema. Estas correcciones mejoraron significativamente el Reliability Rating hasta obtener una clasificación A (el más alto). 

2.2. Security Hotspots 
- Los Security Hotspots son puntos del código que podrían representar vulnerabilidades de seguridad si no se gestionan correctamente. Durante el análisis se detectaron prácticas como:
- Inyección directa de parámetros en consultas SQL sin validación.
- Uso de cookies sin atributos de seguridad (HttpOnly, Secure).
- Falta de validación en el backend de campos que también eran validados en el frontend.
- Estos puntos fueron marcados y revisados manualmente desde la interfaz de SonarQube, realizando correcciones progresivas. Se endureció la política de seguridad del servidor, mejorando la gestión de sesiones y el manejo de autenticación en el controlador de login. Como resultado, el número de hotspots sin revisar se redujo a cero, manteniendo una política de revisión continua. 

2.3. Maintainability (Mantenibilidad) 
- SonarQube clasifica los Code Smells como problemas que afectan a la mantenibilidad del código. Se identificaron malas prácticas como:
- Métodos con demasiadas líneas (funciones con más de 50 líneas).
- Uso de variables con nombres poco descriptivos.
- Lógica duplicada en varios controladores del backend.
- Se aplicaron refactorizaciones basadas en principios SOLID y DRY, dividiendo métodos en funciones más pequeñas, introduciendo clases de utilidad y generalizando componentes en Angular para evitar redundancias. Esto se tradujo en una reducción significativa de los Technical Debt Days (días estimados de trabajo para mejorar el código), pasando de un valor inicial de 5.3 días a tan solo 0.8 días. 

2.4. Duplications (Duplicaciones de código) 
- Otro aspecto clave fue la revisión de código duplicado. SonarQube proporciona un análisis de duplicación a nivel de línea y bloque. Se detectaron duplicaciones principalmente en: 
- Componentes de Angular con lógica repetida (por ejemplo, botones de logout). 
- Servicios REST con estructuras muy similares.
- Estas duplicaciones fueron reducidas mediante la creación de servicios reutilizables y componentes genéricos. En el backend, se introdujeron abstracciones comunes en el servicio de autenticación para evitar repetición de lógica de verificación.
Valor Inicial: 
![fase3_2_1](https://github.com/user-attachments/assets/b216e511-dbc6-4ed4-a266-af3eed16ec2c)
Valor Final:
![fase3_2_2](https://github.com/user-attachments/assets/b8fe100e-de12-453b-aa46-c367be57fe36)
