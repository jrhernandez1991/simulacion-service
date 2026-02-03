-- src/main/resources/schema.sql
CREATE TABLE IF NOT EXISTS usuarios (
                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    capital_disponible DECIMAL(10,2) NOT NULL CHECK (capital_disponible >= 0)
    );

CREATE TABLE IF NOT EXISTS productos_financieros (
                                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    costo DECIMAL(10,2) NOT NULL CHECK (costo >= 0),
    porcentaje_retorno DECIMAL(5,2) NOT NULL CHECK (porcentaje_retorno >= 0 AND porcentaje_retorno <= 100),
    activo BOOLEAN DEFAULT TRUE,
    riesgo INTEGER CHECK (riesgo >= 1 AND riesgo <= 10)
    );

CREATE TABLE IF NOT EXISTS simulaciones (
                                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    fecha_simulacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    capital_disponible DECIMAL(10,2) NOT NULL CHECK (capital_disponible >= 0),
    costo_total DECIMAL(10,2) NOT NULL CHECK (costo_total >= 0),
    ganancia_total DECIMAL(10,2) NOT NULL,
    capital_restante DECIMAL(10,2) NOT NULL CHECK (capital_restante >= 0),
    retorno_total_porcentaje DECIMAL(5,2),
    eficiencia_capital DECIMAL(5,2),
    mensaje TEXT,
    productos_seleccionados JSON
    );

CREATE TABLE IF NOT EXISTS productos_simulacion (
                                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    simulacion_id UUID NOT NULL REFERENCES simulaciones(id) ON DELETE CASCADE,
    producto_id UUID NOT NULL REFERENCES productos_financieros(id) ON DELETE CASCADE,
    costo DECIMAL(10,2),
    porcentaje_ganancia DECIMAL(5,2),
    ganancia_esperada DECIMAL(10,2),
    UNIQUE(simulacion_id, producto_id)
    );

-- Índices para mejorar rendimiento
CREATE INDEX idx_simulaciones_usuario ON simulaciones(usuario_id);
CREATE INDEX idx_simulaciones_fecha ON simulaciones(fecha_simulacion DESC);
CREATE INDEX idx_productos_activos ON productos_financieros(activo) WHERE activo = true;
CREATE INDEX idx_productos_costo ON productos_financieros(costo);