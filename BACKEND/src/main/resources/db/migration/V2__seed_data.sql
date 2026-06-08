-- =============================================================
--  FLYWAY MIGRATION V2 — Datos Semilla (Seed Data)
--  Archivo : V2__seed_data.sql
-- =============================================================

SET search_path TO litethinking;

-- Roles base del sistema
INSERT INTO roles (nombre, descripcion) VALUES
    ('ADMIN',   'Acceso completo: CRUD Empresas, Productos e Inventario'),
    ('EXTERNO', 'Solo lectura: visualización de empresas en modo visitante')
ON CONFLICT (nombre) DO NOTHING;

-- Usuario administrador por defecto
INSERT INTO usuarios (nombre, email, password, rol_id) VALUES
    (
        'Administrador Sistema',
        'admin@litethinking.com',
        '$2a$12$RnPKCEv5LSQQiIhyVMqkfexQzwn0dqGy1.sTUUUzV4JtXXfQV8dLa', -- Admin@123
        (SELECT id FROM roles WHERE nombre = 'ADMIN')
    )
ON CONFLICT (email) DO NOTHING;

-- Usuario visitante por defecto
INSERT INTO usuarios (nombre, email, password, rol_id) VALUES
    (
        'Visitante Sistema',
        'visitante@litethinking.com',
        '$2a$10$eTDjqXzMQUeEEuxH0s1PueXGc1HSQjjK/cK/rcEhadJ47hTsO0p2a', -- Visita@123
        (SELECT id FROM roles WHERE nombre = 'EXTERNO')
    )
ON CONFLICT (email) DO NOTHING;

-- Categorías de ejemplo
INSERT INTO categorias (nombre, descripcion) VALUES
    ('Tecnología',   'Productos de hardware y software'),
    ('Oficina',      'Artículos para oficina y papelería'),
    ('Electrónica',  'Dispositivos electrónicos y accesorios'),
    ('Mobiliario',   'Muebles y accesorios de escritorio')
ON CONFLICT (nombre) DO NOTHING;

-- Empresas
INSERT INTO empresas (nit, nombre, direccion, telefono) VALUES
    ('900123456-1', 'LiteThinking S.A.S.',   'Calle 100 # 15-20, Bogotá',    '601-5551234'),
    ('800987654-2', 'Tech Soluciones Ltda.', 'Carrera 50 # 26-40, Medellín', '604-5559876')
ON CONFLICT (nit) DO NOTHING;

-- Productos
INSERT INTO productos (codigo, nombre, precio_cop, precio_usd, precio_eur, empresa_nit) VALUES
    ('PRD-001', 'Portátil Pro 15', 5000000.00, 1250.00, 1150.00, '900123456-1'),
    ('PRD-002', 'Monitor 27K', 1200000.00, 300.00, 280.00, '900123456-1'),
    ('PRD-003', 'Silla Ergonómica', 800000.00, 200.00, 185.00, '800987654-2')
ON CONFLICT (codigo) DO NOTHING;

-- Inventario
INSERT INTO inventario (empresa_nit, producto_codigo, cantidad) VALUES
    ('900123456-1', 'PRD-001', 50),
    ('900123456-1', 'PRD-002', 120),
    ('800987654-2', 'PRD-003', 200)
ON CONFLICT (empresa_nit, producto_codigo) DO NOTHING;

-- Clientes
INSERT INTO clientes (nombre, email, telefono, direccion) VALUES
    ('Cliente Alpha', 'alpha@cliente.com', '3001234567', 'Av Principal 1'),
    ('Cliente Beta', 'beta@cliente.com', '3109876543', 'Av Secundaria 2'),
    ('Cliente Gamma', 'gamma@cliente.com', '3205556677', 'Plaza Central 3')
ON CONFLICT (email) DO NOTHING;

-- Ordenes
INSERT INTO ordenes (numero_orden, cliente_id, estado, total_cop) VALUES
    ('ORD-ABC-001', (SELECT id FROM clientes WHERE email = 'alpha@cliente.com'), 'PROCESADA', 6200000.00),
    ('ORD-DEF-002', (SELECT id FROM clientes WHERE email = 'beta@cliente.com'), 'PENDIENTE', 1600000.00)
ON CONFLICT (numero_orden) DO NOTHING;

-- Detalles de la Orden ORD-ABC-001
INSERT INTO orden_productos (orden_id, producto_codigo, cantidad, precio_unitario) VALUES
    ((SELECT id FROM ordenes WHERE numero_orden = 'ORD-ABC-001'), 'PRD-001', 1, 5000000.00),
    ((SELECT id FROM ordenes WHERE numero_orden = 'ORD-ABC-001'), 'PRD-002', 1, 1200000.00)
ON CONFLICT (orden_id, producto_codigo) DO NOTHING;

-- Detalles de la Orden ORD-DEF-002
INSERT INTO orden_productos (orden_id, producto_codigo, cantidad, precio_unitario) VALUES
    ((SELECT id FROM ordenes WHERE numero_orden = 'ORD-DEF-002'), 'PRD-003', 2, 800000.00)
ON CONFLICT (orden_id, producto_codigo) DO NOTHING;
