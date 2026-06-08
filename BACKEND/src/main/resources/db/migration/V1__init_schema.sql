-- =============================================================
--  FLYWAY MIGRATION V1 — Creación de esquema y tablas
--  Archivo : V1__create_schema.sql
--  Autor   : LiteThinking Backend
--  Nota    : Flyway ejecuta este script una sola vez en orden.
-- =============================================================

-- -------------------------------------------------------------
-- 0. ESQUEMA
-- -------------------------------------------------------------
CREATE SCHEMA IF NOT EXISTS litethinking;
SET search_path TO litethinking;

-- -------------------------------------------------------------
-- 1. ROLES DE USUARIO
-- -------------------------------------------------------------
CREATE TABLE roles (
    id          BIGSERIAL       PRIMARY KEY,
    nombre      VARCHAR(50)     NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 2. USUARIOS (contraseña BCrypt hash)
-- -------------------------------------------------------------
CREATE TABLE usuarios (
    id          BIGSERIAL       PRIMARY KEY,
    nombre      VARCHAR(120)    NOT NULL,
    email       VARCHAR(120)    NOT NULL UNIQUE,
    password    VARCHAR(255)    NOT NULL,
    activo      BOOLEAN         NOT NULL DEFAULT TRUE,
    rol_id      BIGINT          NOT NULL,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES roles(id)
);

-- -------------------------------------------------------------
-- 3. EMPRESAS
-- -------------------------------------------------------------
CREATE TABLE empresas (
    nit         VARCHAR(20)     PRIMARY KEY,
    nombre      VARCHAR(200)    NOT NULL,
    direccion   VARCHAR(300)    NOT NULL,
    telefono    VARCHAR(30)     NOT NULL,
    activa      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 4. CATEGORÍAS DE PRODUCTO
-- -------------------------------------------------------------
CREATE TABLE categorias (
    id          BIGSERIAL       PRIMARY KEY,
    nombre      VARCHAR(100)    NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 5. PRODUCTOS
-- -------------------------------------------------------------
CREATE TABLE productos (
    codigo          VARCHAR(50)     PRIMARY KEY,
    nombre          VARCHAR(200)    NOT NULL,
    caracteristicas TEXT,
    precio_cop      NUMERIC(18,2)   NOT NULL DEFAULT 0,
    precio_usd      NUMERIC(18,2)   NOT NULL DEFAULT 0,
    precio_eur      NUMERIC(18,2)   NOT NULL DEFAULT 0,
    empresa_nit     VARCHAR(20)     NOT NULL,
    activo          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_producto_empresa FOREIGN KEY (empresa_nit) REFERENCES empresas(nit)
);

-- -------------------------------------------------------------
-- 6. TABLA INTERMEDIA: PRODUCTO <-> CATEGORÍA (Many-to-Many)
-- -------------------------------------------------------------
CREATE TABLE producto_categorias (
    producto_codigo VARCHAR(50) NOT NULL,
    categoria_id    BIGINT      NOT NULL,
    CONSTRAINT pk_producto_categoria PRIMARY KEY (producto_codigo, categoria_id),
    CONSTRAINT fk_pc_producto        FOREIGN KEY (producto_codigo) REFERENCES productos(codigo) ON DELETE CASCADE,
    CONSTRAINT fk_pc_categoria       FOREIGN KEY (categoria_id)    REFERENCES categorias(id)    ON DELETE CASCADE
);

-- -------------------------------------------------------------
-- 7. INVENTARIO
-- -------------------------------------------------------------
CREATE TABLE inventario (
    id              BIGSERIAL       PRIMARY KEY,
    empresa_nit     VARCHAR(20)     NOT NULL,
    producto_codigo VARCHAR(50)     NOT NULL,
    cantidad        INTEGER         NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_inventario_empresa_producto UNIQUE (empresa_nit, producto_codigo),
    CONSTRAINT fk_inv_empresa  FOREIGN KEY (empresa_nit)     REFERENCES empresas(nit),
    CONSTRAINT fk_inv_producto FOREIGN KEY (producto_codigo) REFERENCES productos(codigo)
);

-- -------------------------------------------------------------
-- 8. CLIENTES
-- -------------------------------------------------------------
CREATE TABLE clientes (
    id          BIGSERIAL       PRIMARY KEY,
    nombre      VARCHAR(120)    NOT NULL,
    email       VARCHAR(120)    NOT NULL UNIQUE,
    telefono    VARCHAR(30),
    direccion   VARCHAR(300),
    activo      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 9. ÓRDENES (One-to-Many con CLIENTES)
-- -------------------------------------------------------------
CREATE TABLE ordenes (
    id              BIGSERIAL       PRIMARY KEY,
    numero_orden    VARCHAR(50)     NOT NULL UNIQUE,
    cliente_id      BIGINT          NOT NULL,
    fecha_orden     TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    estado          VARCHAR(30)     NOT NULL DEFAULT 'PENDIENTE',
    total_cop       NUMERIC(18,2)   NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_orden_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

-- -------------------------------------------------------------
-- 10. TABLA INTERMEDIA: ORDEN <-> PRODUCTO (Many-to-Many)
-- -------------------------------------------------------------
CREATE TABLE orden_productos (
    orden_id        BIGINT          NOT NULL,
    producto_codigo VARCHAR(50)     NOT NULL,
    cantidad        INTEGER         NOT NULL DEFAULT 1,
    precio_unitario NUMERIC(18,2)   NOT NULL DEFAULT 0,
    CONSTRAINT pk_orden_producto PRIMARY KEY (orden_id, producto_codigo),
    CONSTRAINT fk_op_orden       FOREIGN KEY (orden_id)        REFERENCES ordenes(id)   ON DELETE CASCADE,
    CONSTRAINT fk_op_producto    FOREIGN KEY (producto_codigo) REFERENCES productos(codigo)
);

-- =============================================================
-- ÍNDICES DE RENDIMIENTO
-- =============================================================
CREATE INDEX idx_usuarios_email         ON usuarios(email);
CREATE INDEX idx_usuarios_rol           ON usuarios(rol_id);
CREATE INDEX idx_productos_empresa      ON productos(empresa_nit);
CREATE INDEX idx_inventario_empresa     ON inventario(empresa_nit);
CREATE INDEX idx_inventario_producto    ON inventario(producto_codigo);
CREATE INDEX idx_ordenes_cliente        ON ordenes(cliente_id);
CREATE INDEX idx_ordenes_estado         ON ordenes(estado);
CREATE INDEX idx_ordenes_fecha          ON ordenes(fecha_orden);
