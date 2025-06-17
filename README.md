# 2025-Arteum

# Fase 0

## Nombre de la aplicación web
- **Arteum**

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
- Envío de correos
- Generador de PDFs

### Algoritmos o consulta avanzada
- Recomendaciones personalizadas según historial de visitas y reseñas: se calcula la afinidad al resto de obras segun las obras que ha dado mejor puntuacion, ha marcado como favorita o tiene en la lista de "por ver"

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
