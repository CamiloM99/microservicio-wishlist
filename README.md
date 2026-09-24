# Wishlist Service - Microservicio de Lista de Deseos e Historial

Microservicio encargado de gestionar los artículos de interés de cada usuario, registrar la auditoría histórica de cada movimiento y verificar el stock en tiempo real consumiendo `productos-service`.

---

## 1. Variables de Entorno y Credenciales

| Variable | Valor Local | Valor Docker | Descripción |
| :--- | :--- | :--- | :--- |
| `SERVER_PORT` | `8083` | `8083` | Puerto HTTP del servicio |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/carvajal_db?createDatabaseIfNotExist=true&useSSL=false` | `jdbc:mysql://mysql-db:3306/carvajal_db?createDatabaseIfNotExist=true&useSSL=false` | Cadena JDBC de conexión |
| `SPRING_DATASOURCE_USERNAME` | `root` | `root` | Usuario gestor de MySQL |
| `SPRING_DATASOURCE_PASSWORD` | *(vacío)* | *(vacío)* | Contraseña de acceso a MySQL |
| `JWT_SECRET` | `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970` | Clave secreta para validar tokens |
| `PRODUCT_SERVICE_URL` | `http://localhost:8082/api/v1/products/` | `http://productos-service:8082/api/v1/products/` | URL de conexión hacia productos |

---

## 2. Scripts de Base de Datos (MySQL)

Estructura de las tablas `wishlist` y `wishlist_history` en el esquema `carvajal_db`:

```sql
USE carvajal_db;

CREATE TABLE `wishlist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `product_id` bigint(20) NOT NULL,
  `quantity` int(11) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

## 3. Manual de Despliegue
Despliegue Local (Maven)

Bash
mvn clean install -DskipTests
mvn spring-boot:run

Despliegue con Docker

Bash
docker build -t wishlist-service .
docker run -d -p 8083:8083 --name wishlist-service --network carvajal-network wishlist-service

## 4. Endpoints Principales (Requieren Bearer Token)

POST /api/v1/wishlist/user/{userId}: Agrega un producto a la lista (registra acción AGREGADO).

GET /api/v1/wishlist/user/{userId}: Obtiene la lista con validación de stock en tiempo real.

PUT /api/v1/wishlist/user/{userId}/product/{productId}: Actualiza la cantidad (registra acción ACTUALIZADO).

DELETE /api/v1/wishlist/user/{userId}/product/{productId}: Elimina un producto (registra acción ELIMINADO).

GET /api/v1/wishlist/history/user/{userId}: Consulta la auditoría histórica.

## 5. Comentarios Adicionales para el Despliegue
Requiere que el bean RestTemplate esté configurado en WishlistApplication.java o en una clase @Configuration.

En entorno Docker, la variable PRODUCT_SERVICE_URL debe apuntar al contenedor productos-service y no a localhost.

