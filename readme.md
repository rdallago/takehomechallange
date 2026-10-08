# API de Notificaciones - Desafío Técnico Full Stack

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/rdallago/takehomechallange/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/rdallago/takehomechallange/tree/main)
[![Coverage Status](https://coveralls.io/repos/github/rdallago/takehomechallange/badge.svg?branch=main)](https://coveralls.io/github/rdallago/takehomechallange?branch=main)

API REST para la gestión de notificaciones de usuarios autenticados. Cada notificación se envía (de forma simulada) por el canal elegido: Email, SMS o Push.

## Características

- Registro de usuarios con email y contraseña (la contraseña se guarda hasheada con BCrypt).
- Inicio de sesión que devuelve un token JWT con una hora de validez.
- Todos los endpoints de notificaciones requieren un token válido.
- CRUD de notificaciones: crear, listar las propias, modificar y eliminar.
- Envío automático al crear una notificación, con lógica propia por canal:
  - **Email:** valida el formato del destinatario, genera un template HTML y registra el envío.
  - **SMS:** valida el número, limita el contenido a 160 caracteres y registra número y fecha.
  - **Push:** valida el token del dispositivo, arma el payload JSON y registra el estado.
- Cada envío ejecutado queda registrado en la tabla `notification_deliveries` con su estado y su detalle.
- Si los datos no cumplen las reglas del canal (por ejemplo, un token Push de menos de 20 caracteres), la API responde 400 y no se guarda nada: ni la notificación ni un registro de envío.
- Un usuario no puede ver ni modificar las notificaciones de otro.
- Documentación interactiva con Swagger.

## Prerrequisitos

- Java 17
- Docker Desktop (con Docker Compose)
- Git

No hace falta instalar Maven: el proyecto incluye el wrapper (`mvnw`).

## Cómo ejecutar la aplicación

**Opción 1: todo con Docker (recomendada)**

```bash
git clone https://github.com/rdallago/takehomechallange.git
cd takehomechallange
docker compose up -d --build
```

Este comando levanta PostgreSQL y la aplicación. Flyway crea las tablas automáticamente al iniciar. La primera vez tarda unos minutos porque compila el proyecto dentro de la imagen.

Para ver los logs: `docker compose logs -f app`.
Para apagar todo: `docker compose down` (con `-v` también se borran los datos).

**Opción 2: base en Docker y aplicación local**

```bash
docker compose up -d postgres-db
./mvnw spring-boot:run
```

En Windows se usa `.\mvnw spring-boot:run`. En este caso la base queda expuesta en el puerto `5433` del equipo.

**Probar la API desde Swagger**

1. Abrir la ruta de Swagger (ver sección Rutas).
2. Ejecutar `POST /api/auth/register` y luego `POST /api/auth/login`.
3. Copiar el `accessToken`, presionar **Authorize** y pegar solo el token, sin la palabra `Bearer`.
4. Probar los endpoints de notificaciones.

Ejemplos de `recipient` según el canal:

| Canal | Ejemplo de `recipient` |
|---|---|
| EMAIL | `alguien@mail.com` |
| SMS | `+5493794123456` |
| PUSH | `abcdefghij1234567890xyz` (mínimo 20 caracteres alfanuméricos) |

## Cómo ejecutar los tests

Los tests de integración usan una base separada, `takehome_test_db`, que vive en el mismo contenedor de PostgreSQL pero es independiente de la base de la aplicación. Antes de cada test se vacían las tablas, y los tests corren de a uno, nunca en paralelo.

```bash
docker compose up -d postgres-db
./mvnw test
```

Si la base de test no existe (por ejemplo, porque el volumen de Docker se creó antes de agregar el script de inicialización), se crea una sola vez con:

```bash
docker exec -it takehome_postgres psql -U postgres -c "CREATE DATABASE takehome_test_db;"
```

El reporte de cobertura de JaCoCo queda en `target/site/jacoco/index.html`.

En CircleCI los mismos tests corren contra un contenedor de PostgreSQL efímero, y el reporte de cobertura se envía a Coveralls.

## Áreas de mejora

- Registrar también los intentos rechazados por validación (hoy solo se registran los envíos ejecutados), para tener un historial de auditoría completo.
- Agregar `GET /api/notifications/{id}` y paginación o filtros en el listado, que hoy no existen.
- Hacer más cómodo el uso de PUT y DELETE: hoy requieren el ID de la notificación, y en Swagger hay que obtenerlo antes con un GET. Un frontend con botones "Editar" y "Eliminar" por fila lo resolvería.
- Reemplazar los envíos simulados por proveedores reales (por ejemplo SendGrid, Twilio o Firebase).
- Agregar reintentos y envío asíncrono para los envíos fallidos.
- Agregar refresh tokens.

## Errores a corregir

No hay errores funcionales conocidos. Sí hay dos limitaciones del plan gratuito de Render:

- **Arranque en frío:** el servicio se duerme tras un rato sin tráfico, y la primera petición puede tardar cerca de un minuto o fallar con "Failed to fetch" en Swagger. Alcanza con reintentar.
- **Base de datos gratuita con vencimiento:** Render suspende o elimina las bases gratuitas pasado un tiempo, por lo que la demo online podría dejar de funcionar.

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java (Temurin) | 17 |
| Spring Boot | 3.3.4 |
| Spring Web, Spring Data JPA (Hibernate), Spring Security, Bean Validation | gestionadas por Spring Boot 3.3.4 |
| PostgreSQL | 15 |
| Flyway (migraciones) | gestionada por Spring Boot 3.3.4 |
| JJWT (JWT) | 0.12.6 |
| springdoc-openapi (Swagger) | 2.6.0 |
| JUnit 5 y Mockito | gestionadas por Spring Boot 3.3.4 |
| JaCoCo (cobertura) | 0.8.12 |
| Maven (wrapper) | incluido en el proyecto |
| Docker y Docker Compose | - |
| CircleCI y Coveralls | - |
| Render (despliegue) | - |

## Decisiones tomadas

- **Arquitectura hexagonal (clean architecture):** el dominio y los casos de uso no dependen de JPA, JWT ni del framework web. Todo lo externo se accede mediante interfaces (puertos) que implementan los adaptadores de infraestructura. Esto vuelve el proyecto más mantenible y fácil de testear. El costo es una mayor cantidad de archivos que en una arquitectura en capas simple (puertos y mapeo entre dominio y entidades JPA), que se consideró razonable por la extensibilidad que pide el enunciado.
- **Patrón Strategy para los canales:** cada canal implementa la interfaz `NotificationSenderPort`, y un resolver elige la estrategia según el canal. Agregar un canal nuevo implica una clase nueva, un valor en el enum `Channel` y una migración que amplíe el `CHECK` de la columna, sin modificar la lógica existente (principio Open/Closed).
- **Validación antes de guardar:** cada canal valida sus reglas antes de persistir. Una notificación inválida se rechaza con un 400 y no deja rastro en la base. Se priorizó que la base contenga solo datos válidos por sobre registrar los intentos rechazados.
- **Dos tablas, `notifications` y `notification_deliveries`:** la primera guarda lo que el usuario quiere comunicar y la segunda el registro del envío ejecutado (estado, fecha y detalle propio del canal, como el template, el payload o el número). Se separaron porque tienen responsabilidades distintas, y esto permite que un futuro historial de envíos, por ejemplo con reintentos, crezca sin modificar la tabla de notificaciones.
- **Flyway para las migraciones:** el esquema se versiona y Hibernate solo lo valida (`ddl-auto=validate`).
- **Spring Data JPA con Hibernate** para la persistencia, con entidades separadas del modelo de dominio.
- **Autenticación stateless con JWT y BCrypt:** no hay sesión en el servidor. El identificador del usuario se obtiene siempre del token, nunca del cuerpo de la petición.
- **Respuesta 404 y no 403** cuando se intenta acceder a una notificación ajena, para no revelar que existe. El login devuelve el mismo error si falla el email o la clave.
- **Docker:** build multi-stage y ejecución con usuario sin privilegios, para que el proyecto sea portable y se ejecute igual en local, CI y Render.
- **Configuración por variables de entorno** (`SPRING_DATASOURCE_*`, `JWT_SECRET`): los secretos de producción no están en el repositorio.
- **Testing:** tests unitarios con JUnit 5 y Mockito, y un test de integración con Spring Boot Test y MockMvc contra una base PostgreSQL real. Cuando un endpoint responde con el código esperado y los datos quedan bien en la base, se asume que toda la cadena (controlador, caso de uso, sender, persistencia y seguridad) funcionó correctamente. JaCoCo mide la cobertura.
- **CI:** CircleCI ejecuta los tests en cada push y Coveralls publica la cobertura.

## Rutas

Swagger (local): http://localhost:8080/swagger-ui/index.html#/

| Método | Ruta | Autenticación |
|---|---|---|
| POST | `/api/auth/register` | No |
| POST | `/api/auth/login` | No |
| POST | `/api/notifications` | Sí |
| GET | `/api/notifications` | Sí |
| PUT | `/api/notifications/{id}` | Sí |
| DELETE | `/api/notifications/{id}` | Sí |

## Despliegue

La aplicación está desplegada en Render (servicio web con Docker y base PostgreSQL en Render):

https://takehomechallange.onrender.com/swagger-ui/index.html#/

Por el arranque en frío del plan gratuito, la primera petición puede demorar. Si falla, alcanza con reintentar luego de unos segundos.
