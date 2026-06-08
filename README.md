# LiteThinking - Reto Técnico Fullstack

Este proyecto es la solución al Reto Técnico propuesto por LiteThinking. Consiste en una aplicación web Fullstack (Java/Spring Boot + Vue.js/Quasar) estructurada bajo los lineamientos de Arquitectura Limpia (Hexagonal).

## 🚀 Arquitectura y Tecnologías

### Backend
- **Java 21**
- **Spring Boot 3.3.2** (Web, Data JPA, Security, Mail)
- **PostgreSQL 16**
- **Flyway** (Migraciones de base de datos)
- **Lombok**
- **iText / OpenPDF** (Generación de reportes PDF)
- **Arquitectura Hexagonal (Clean Architecture):** Separación estricta entre Dominio, Aplicación e Infraestructura.

### Frontend
- **Vue.js 3** (Composition API)
- **Quasar Framework** (Vite)
- **Pinia** (Manejo de estado)
- **Vue Router** (Guards para protección de rutas)
- **Axios** (Cliente HTTP)

---

## 🛠️ Requisitos Previos

Antes de levantar el proyecto, asegúrate de tener instalados:
- **Java JDK 21+**
- **Node.js 18+** y npm
- **PostgreSQL 16+** (Ejecutándose localmente)
- **Maven 3.9+**

---

## ⚙️ Configuración y Ejecución

### 1. Base de Datos
Crea una base de datos en PostgreSQL llamada `litethinking_db`:
```sql
CREATE DATABASE litethinking_db;
```
El usuario por defecto configurado en el backend es `postgres` con contraseña `admin`. Si tus credenciales son diferentes, actualízalas en el archivo:
`BACKEND/src/main/resources/application.yml`

### 2. Despliegue del Backend
Ve al directorio del backend y ejecuta la aplicación usando Maven Wrapper o tu Maven local:
```bash
cd BACKEND
mvn clean install
mvn spring-boot:run
```
Al arrancar, **Flyway** ejecutará automáticamente los scripts `V1`, `V2` y `V3` que crean el esquema de base de datos e insertan los usuarios y datos semilla.

### 3. Despliegue del Frontend
Abre otra terminal, ve al directorio del frontend e instala las dependencias:
```bash
cd FRONTEND
npm install
npm run dev
```
La aplicación Quasar se levantará en `http://localhost:9000/`.

---

## 🔐 Credenciales de Acceso

La base de datos se inicializa automáticamente con dos usuarios de prueba. Ambos usan contraseñas encriptadas con **BCrypt**.

| Perfil | Correo | Contraseña | Permisos |
| :--- | :--- | :--- | :--- |
| **Administrador** | admin@litethinking.com | Admin@123 | Acceso total (CRUD de Empresas, Productos e Inventario, descarga y envío de PDFs). |
| **Externo** | visitante@litethinking.com | Visita@123 | Modo lectura (Listado de Empresas e Inventario global). |

---

## 📝 Documentación Funcional

1. **Gestión de Empresas:** Se permite registrar, visualizar, editar y eliminar empresas. Validaciones estrictas por NIT.
2. **Gestión de Productos:** Formulario que captura Código, Nombre, Características y Precio (En COP, USD y EUR), así como la Empresa proveedora.
3. **Gestión de Inventario:** El inventario centraliza los productos agrupados por empresa.
4. **Reporte en PDF y Envío por Correo:** En la tabla de Inventario, al filtrar por una empresa, el Administrador tiene la opción de descargar el PDF o enviarlo automáticamente a un destinatario usando la API de JavaMailSender y OpenPDF.

> [!IMPORTANT]
> **Configuración del Servidor de Correo (SMTP):**
> El envío de correos está implementado usando JavaMailSender, pero requiere configurar las variables de entorno del servidor SMTP (`MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`) en el ambiente de producción para que funcione verdaderamente. 
> 
> Si deseas **probarlo en local**, puedes abrir el archivo `BACKEND/src/main/resources/application.yml` y reemplazar los valores por defecto por los de tu cuenta de Gmail (ej. `host: smtp.gmail.com`, `port: 587`, y usar una "Contraseña de Aplicación" en lugar de tu contraseña normal).

---

## 🏗️ Principios SOLID y Limpios Aplicados

- **Single Responsibility (SRP):** Cada *UseCase* en el backend tiene un único motivo para cambiar (Ej. `CrearEmpresaUseCase`, `GenerarPdfUseCase`).
- **Dependency Inversion (DIP):** Los casos de uso no dependen de bases de datos o frameworks. Dependen de *Puertos* (Interfaces) como `GuardarEmpresaPort`.
- **Manejo Centralizado de Excepciones:** Se implementó un `@ControllerAdvice` (`GlobalExceptionHandler`) para interceptar errores de validación, excepciones de dominio (404, 400) y errores inesperados (500), homologando la respuesta JSON para el frontend.
- **Seguridad Restringida:** Los endpoints críticos y el Frontend Router están estrictamente protegidos utilizando Role-Based Access Control (RBAC).
