# eCommerce Backend - Spring Boot

Este proyecto es un backend para un eCommerce desarrollado con **Spring Boot**, **PostgreSQL** y **Docker**. Proporciona una API REST para la gestión de productos, usuarios y pedidos.

## 📌 Tecnologías Utilizadas
- **Java 17** (Compatible con Spring Boot 3.x)
- **Spring Boot** (Spring Web, Spring Data JPA, Spring Security, etc.)
- **PostgreSQL** 
- **Docker & Docker Compose**
- **Keycloak** (para autenticación y autorización)
- **Swagger** (para documentación de la API)
- **Lombok** (para reducir código repetitivo)
- **MapStruct** (para mapeo de DTOs)

## 🚀 Instalación y Configuración

### Prerrequisitos
Asegúrate de tener instalado:
- **Java 17**
- **Maven**
- **Docker y Docker Compose**

### 1️⃣ Clonar el repositorio
```bash
 git clone https://github.com/panchoarc/ecommerce-backend-springboot.git
 cd ecommerce-backend-springboot
```

### 2️⃣ Configurar PostgreSQL y Keycloak con Docker
```bash
docker-compose up -d
```
Esto levantará la base de datos PostgreSQL y el servicio de Keycloak, para los cuales existen volúmenes para persistencia de datos.

### 3️⃣ Configurar el archivo `application.yml`
Modifica el archivo `src/main/resources/application.yml` si es necesario, asegurándote de que las credenciales y la URL de la base de datos sean correctas.

### 4️⃣ Ejecutar la aplicación
```bash
mvn spring-boot:run
```
La aplicación estará disponible en `http://localhost:8080`.

## 🛠️ Características Principales

### 🔹 Gestión de Usuarios
- Registro y autenticación con Keycloak
- Perfiles de usuario: Administrador y Usuario

### 🔹 Gestión de Productos
- CRUD de productos
- Filtros por categoría, precio y disponibilidad


### 🔹 Gestión de Categorías
- CRUD de categorías
- Filtros de categorías

### 🔹 Gestión de Órdenes
- Creación, obtener las órdenes del usuario

### 🔹 Gestión de Reviews
- Creación, obtención y borrado de reviews para un producto

### 🔹 Gestión de Direcciones de Usuario
- Creación, obtención y borrado de direcciones asociadas al usuario

### 🔹 Gestión de Endpoints
- Sincronización de endpoints disponibles y catalogados
- Asociación de endpoints con roles

### 🔹 Gestión de Roles
- Sincronización de roles mediante keycloak.

### 🔹 Gestión de Backups
- Creación de backups mediante REST api
- Borrado mediante cron job

### 🔹 Seguridad y Autenticación
- Integración con Keycloak para autenticación OAuth2
- Roles y permisos para controlar el acceso a los endpoints

### 🔹 Documentación de la API
La API está documentada con Swagger. Puedes acceder a la documentación en:
```
http://localhost:8080/api/swagger-ui/index.html
```

## 📝 Notas Adicionales
- El proyecto sigue una arquitectura basada en capas (Controller-Service-Repository).
- Se recomienda usar **Postman** o **Insomnia** para probar los endpoints.

## 🏆 Contribución
Si deseas contribuir, haz un **fork** del repositorio, crea una rama y envía un **pull request** con tus mejoras.

## 📧 Contacto
Si tienes alguna duda o sugerencia, puedes abrir un issue en el repositorio o contactar al autor.

