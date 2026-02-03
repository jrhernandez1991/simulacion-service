-- src/main/resources/data.sql
-- Insertar usuarios de prueba
INSERT INTO usuarios (id, nombre, email, capital_disponible) VALUES
                                                                 ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Juan Pérez', 'juan.perez@email.com', 5000.00),
                                                                 ('b2c3d4e5-f6g7-8901-bcde-f23456789012', 'María García', 'maria.garcia@email.com', 8000.00),
                                                                 ('c3d4e5f6-g7h8-9012-cdef-345678901234', 'Carlos López', 'carlos.lopez@email.com', 3000.00),
                                                                 ('d4e5f6g7-h8i9-0123-defg-456789012345', 'Ana Martínez', 'ana.martinez@email.com', 10000.00),
                                                                 ('e5f6g7h8-i9j0-1234-efgh-567890123456', 'Pedro Sánchez', 'pedro.sanchez@email.com', 1500.00)
    ON CONFLICT (email) DO NOTHING;

-- Insertar productos financieros de prueba
INSERT INTO productos_financieros (id, nombre, descripcion, costo, porcentaje_retorno, activo, riesgo) VALUES
                                                                                                           ('c3d4e5f6-g7h8-9012-cdef-345678901234', 'Fondo Acciones Tech', 'Fondo de inversión en acciones tecnológicas', 1000.00, 8.50, true, 7),
                                                                                                           ('d4e5f6g7-h8i9-0123-defg-456789012345', 'Bonos Corporativos AAA', 'Bonos corporativos de alta calificación', 500.00, 5.25, true, 2),
                                                                                                           ('e6f7g8h9-i0j1-2345-fghi-678901234567', 'ETF Global', 'ETF diversificado a nivel mundial', 1500.00, 12.00, true, 6),
                                                                                                           ('f7g8h9i0-j1k2-3456-ghij-789012345678', 'Fondo de Dividendos', 'Foco en empresas con dividendos estables', 800.00, 6.75, true, 4),
                                                                                                           ('g8h9i0j1-k2l3-4567-hijk-890123456789', 'Bonos del Tesoro', 'Bonos gubernamentales de bajo riesgo', 1200.00, 4.50, true, 1),
                                                                                                           ('h9i0j1k2-l3m4-5678-ijkl-901234567890', 'Cuenta de Ahorro', 'Producto de ahorro tradicional', 0.00, 1.50, true, 1),
                                                                                                           ('i0j1k2l3-m4n5-6789-jklm-012345678901', 'Fondo Conservador', 'Inversión conservadora balanceada', 600.00, 3.25, true, 3),
                                                                                                           ('j1k2l3m4-n5o6-7890-klmn-123456789012', 'Fondo Premium', 'Producto exclusivo de alto rendimiento', 3000.00, 15.00, true, 9)
    ON CONFLICT (id) DO NOTHING;