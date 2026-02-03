package com.andesfin.simulacion_service.service;

import com.andesfin.simulacion_service.dto.*;
import com.andesfin.simulacion_service.model.*;
import com.andesfin.simulacion_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class SimulacionService {

    private final SimulacionRepository simulacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoFinancieroRepository productoRepository;
    private final ProductoSimulacionRepository productoSimulacionRepository;

    // Eliminamos ObjectMapper y usaremos métodos manuales

    // Clase interna para manejar combinaciones (MANTENEMOS IGUAL)
    private static class ProductoInfo {
        String nombre;
        Double precio;
        Double porcentajeGanancia;
        Double gananciaEsperada;

        ProductoInfo(String nombre, Double precio, Double porcentajeGanancia) {
            this.nombre = nombre;
            this.precio = precio;
            this.porcentajeGanancia = porcentajeGanancia;
            this.gananciaEsperada = precio * (porcentajeGanancia / 100);
        }
    }

    // Clase para manejar combinaciones (MANTENEMOS IGUAL)
    private static class Combinacion {
        List<ProductoInfo> productos;
        Double costoTotal;
        Double gananciaTotal;

        Combinacion() {
            this.productos = new ArrayList<>();
            this.costoTotal = 0.0;
            this.gananciaTotal = 0.0;
        }

        void agregarProducto(ProductoInfo producto) {
            productos.add(producto);
            costoTotal += producto.precio;
            gananciaTotal += producto.gananciaEsperada;
        }

        Combinacion copiar() {
            Combinacion copia = new Combinacion();
            copia.productos = new ArrayList<>(this.productos);
            copia.costoTotal = this.costoTotal;
            copia.gananciaTotal = this.gananciaTotal;
            return copia;
        }
    }

    public SimulacionResponseDTO realizarSimulacion(SimulacionRequestDTO request) {
        // 1. Validar que el usuario existe
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Convertir productos candidatos a lista de ProductoInfo
        List<ProductoInfo> productosInfo = new ArrayList<>();
        for (ProductoCandidatoDTO producto : request.getProductos()) {
            productosInfo.add(new ProductoInfo(
                    producto.getNombre(),
                    producto.getPrecio(),
                    producto.getPorcentajeGanancia()
            ));
        }

        // 3. Filtrar productos viables (precio <= capital disponible)
        List<ProductoInfo> productosViables = productosInfo.stream()
                .filter(p -> p.precio <= request.getCapitalDisponible())
                .toList();

        // 4. Si no hay productos viables, lanzar excepción
        if (productosViables.isEmpty()) {
            Double productoMasBarato = productosInfo.stream()
                    .map(p -> p.precio)
                    .min(Double::compare)
                    .orElse(0.0);

            throw new RuntimeException(String.format(
                    "Fondos insuficientes. El capital disponible ($%.2f) es insuficiente para adquirir cualquier producto de la lista. Producto más barato: $%.2f",
                    request.getCapitalDisponible(),
                    productoMasBarato
            ));
        }

        // 5. Ordenar productos por relación ganancia/precio (descendente)
        productosViables.sort((p1, p2) -> {
            Double ratio1 = p1.gananciaEsperada / p1.precio;
            Double ratio2 = p2.gananciaEsperada / p2.precio;
            return ratio2.compareTo(ratio1);
        });

        // 6. Aplicar algoritmo de mochila 0/1 para optimización
        Combinacion mejorCombinacion = encontrarMejorCombinacion(
                productosViables,
                request.getCapitalDisponible()
        );

        // 7. Si no se pudo seleccionar ningún producto
        if (mejorCombinacion.productos.isEmpty()) {
            throw new RuntimeException(
                    "No se encontraron combinaciones viables con el capital disponible"
            );
        }

        // 8. Calcular métricas adicionales
        Double capitalRestante = request.getCapitalDisponible() - mejorCombinacion.costoTotal;
        Double retornoTotalPorcentaje = (mejorCombinacion.gananciaTotal / mejorCombinacion.costoTotal) * 100;
        Double eficienciaCapital = (mejorCombinacion.costoTotal / request.getCapitalDisponible()) * 100;

        // 9. Determinar mensaje según el resultado
        String mensaje = determinarMensaje(mejorCombinacion, capitalRestante, retornoTotalPorcentaje);

        // 10. Crear la entidad Simulacion
        Simulacion simulacion = Simulacion.builder()
                .usuario(usuario)
                .fechaSimulacion(LocalDateTime.now())
                .capitalDisponible(request.getCapitalDisponible())
                .costo_Total(redondear(mejorCombinacion.costo_Total))
                .gananciaTotal(redondear(mejorCombinacion.gananciaTotal))
                .capitalRestante(redondear(capitalRestante))
                .retornoTotalPorcentaje(redondear(retornoTotalPorcentaje))
                .eficienciaCapital(redondear(eficienciaCapital))
                .mensaje(mensaje)
                .build();

        // 11. Convertir productos seleccionados a JSON manualmente
        // (CAMBIO AQUÍ: Usamos método manual en lugar de Jackson)
        String productosJson = convertirProductosAJson(mejorCombinacion.productos);
        simulacion.setProductosSeleccionados(productosJson);

        // 12. Guardar la simulación
        Simulacion simulacionGuardada = simulacionRepository.save(simulacion);

        // 13. Guardar relación muchos a muchos con productos
        guardarProductosSimulacion(simulacionGuardada, mejorCombinacion.productos);

        // 14. Crear y retornar la respuesta
        return construirRespuesta(simulacionGuardada, mejorCombinacion.productos);
    }

    // MÉTODO NUEVO: Convertir productos a JSON manualmente
    private String convertirProductosAJson(List<ProductoInfo> productos) {
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < productos.size(); i++) {
            ProductoInfo p = productos.get(i);
            json.append("{")
                    .append("\"nombre\":\"").append(p.nombre).append("\",")
                    .append("\"precio\":").append(redondear(p.precio)).append(",")
                    .append("\"porcentajeGanancia\":").append(redondear(p.porcentajeGanancia)).append(",")
                    .append("\"gananciaEsperada\":").append(redondear(p.gananciaEsperada))
                    .append("}");

            if (i < productos.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");
        return json.toString();
    }

    // Método para convertir JSON a objetos (cuando leemos)
    private List<ProductoSeleccionadoDTO> convertirJsonAProductos(String json) {
        List<ProductoSeleccionadoDTO> productos = new ArrayList<>();

        if (json == null || json.trim().isEmpty()) {
            return productos;
        }

        // Simplificamos el parsing para este caso específico
        // En un proyecto real usaríamos Jackson, pero para aprendizaje hacemos manual
        String cleanJson = json.trim().replace("[", "").replace("]", "");
        if (cleanJson.isEmpty()) {
            return productos;
        }

        String[] objetos = cleanJson.split("\\},\\{");

        for (String obj : objetos) {
            String objClean = obj.replace("{", "").replace("}", "");
            String[] propiedades = objClean.split(",");

            String nombre = "";
            Double precio = 0.0;
            Double porcentajeGanancia = 0.0;
            Double gananciaEsperada = 0.0;

            for (String prop : propiedades) {
                String[] keyValue = prop.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");

                    switch (key) {
                        case "nombre":
                            nombre = value;
                            break;
                        case "precio":
                            precio = Double.parseDouble(value);
                            break;
                        case "porcentajeGanancia":
                            porcentajeGanancia = Double.parseDouble(value);
                            break;
                        case "gananciaEsperada":
                            gananciaEsperada = Double.parseDouble(value);
                            break;
                    }
                }
            }

            if (!nombre.isEmpty()) {
                productos.add(ProductoSeleccionadoDTO.builder()
                        .nombre(nombre)
                        .precio(precio)
                        .porcentajeGanancia(porcentajeGanancia)
                        .gananciaEsperada(gananciaEsperada)
                        .build());
            }
        }

        return productos;
    }

    private Combinacion encontrarMejorCombinacion(List<ProductoInfo> productos, Double capital) {
        int n = productos.size();
        // Crear matriz DP para algoritmo de mochila
        Double[][] dp = new Double[n + 1][capital.intValue() + 1];

        // Inicializar matriz
        for (int i = 0; i <= n; i++) {
            for (int w = 0; w <= capital.intValue(); w++) {
                dp[i][w] = 0.0;
            }
        }

        // Llenar matriz DP
        for (int i = 1; i <= n; i++) {
            ProductoInfo producto = productos.get(i - 1);
            int precioInt = producto.precio.intValue();

            for (int w = 1; w <= capital.intValue(); w++) {
                if (precioInt > w) {
                    dp[i][w] = dp[i - 1][w];
                } else {
                    dp[i][w] = Math.max(
                            dp[i - 1][w],
                            dp[i - 1][w - precioInt] + producto.gananciaEsperada
                    );
                }
            }
        }

        // Reconstruir combinación óptima
        Combinacion combinacion = new Combinacion();
        int w = capital.intValue();

        for (int i = n; i > 0 && w > 0; i--) {
            if (!dp[i][w].equals(dp[i - 1][w])) {
                ProductoInfo producto = productos.get(i - 1);
                combinacion.agregarProducto(producto);
                w -= producto.precio.intValue();
            }
        }

        return combinacion;
    }

    private String determinarMensaje(Combinacion combinacion, Double capitalRestante, Double retornoPorcentaje) {
        if (combinacion.gananciaTotal == 0) {
            return "No se encontraron productos con ganancias significativas";
        } else if (capitalRestante > combinacion.costoTotal * 0.5) {
            return "Simulación con baja eficiencia de capital. Considere aumentar capital para mejores opciones";
        } else if (retornoPorcentaje > 10) {
            return "Simulación exitosa con ganancias óptimas";
        } else if (retornoPorcentaje > 5) {
            return "Simulación con rendimiento moderado";
        } else {
            return "Simulación con ganancias mínimas. Considere productos con mayor rendimiento";
        }
    }

    private List<ProductoSeleccionadoDTO> convertirAProductosSeleccionados(List<ProductoInfo> productos) {
        return productos.stream()
                .map(p -> ProductoSeleccionadoDTO.builder()
                        .nombre(p.nombre)
                        .precio(redondear(p.precio))
                        .porcentajeGanancia(redondear(p.porcentajeGanancia))
                        .gananciaEsperada(redondear(p.gananciaEsperada))
                        .build())
                .toList();
    }

    private void guardarProductosSimulacion(Simulacion simulacion, List<ProductoInfo> productos) {
        for (ProductoInfo productoInfo : productos) {
            // Buscar producto en la base de datos por nombre
            ProductoFinanciero producto = productoRepository.findAll().stream()
                    .filter(p -> p.getNombre().equals(productoInfo.nombre))
                    .findFirst()
                    .orElse(null);

            if (producto != null) {
                ProductoSimulacion productoSimulacion = ProductoSimulacion.builder()
                        .simulacion(simulacion)
                        .producto(producto)
                        .costo(productoInfo.precio)
                        .porcentajeGanancia(productoInfo.porcentajeGanancia)
                        .gananciaEsperada(productoInfo.gananciaEsperada)
                        .build();

                productoSimulacionRepository.save(productoSimulacion);
            }
        }
    }

    private SimulacionResponseDTO construirRespuesta(Simulacion simulacion, List<ProductoInfo> productos) {
        List<ProductoSeleccionadoDTO> productosSeleccionados = convertirAProductosSeleccionados(productos);

        return SimulacionResponseDTO.builder()
                .id(simulacion.getId())
                .usuarioId(simulacion.getUsuario().getId())
                .fechaSimulacion(simulacion.getFechaSimulacion())
                .capitalDisponible(redondear(simulacion.getCapitalDisponible()))
                .productosSeleccionados(productosSeleccionados)
                .costoTotal(redondear(simulacion.getCostoTotal()))
                .capitalRestante(redondear(simulacion.getCapitalRestante()))
                .gananciaTotal(redondear(simulacion.getGananciaTotal()))
                .retornoTotalPorcentaje(redondear(simulacion.getRetornoTotalPorcentaje()))
                .eficienciaCapital(redondear(simulacion.getEficienciaCapital()))
                .mensaje(simulacion.getMensaje())
                .build();
    }

    private Double redondear(Double valor) {
        if (valor == null) return 0.0;
        return Math.round(valor * 100.0) / 100.0;
    }

    public List<SimulacionResumenDTO> getSimulacionesPorUsuario(UUID usuarioId) {
        return simulacionRepository.findSimulacionesPorUsuarioOrdenadas(usuarioId)
                .stream()
                .map(this::convertirAResumenDTO)
                .toList();
    }

    private SimulacionResumenDTO convertirAResumenDTO(Simulacion simulacion) {
        // Contar productos seleccionados usando nuestro método manual
        List<ProductoSeleccionadoDTO> productos = convertirJsonAProductos(simulacion.getProductosSeleccionados());
        int cantidadProductos = productos.size();

        return SimulacionResumenDTO.builder()
                .id(simulacion.getId())
                .usuarioId(simulacion.getUsuario().getId())
                .fechaSimulacion(simulacion.getFechaSimulacion())
                .capitalDisponible(redondear(simulacion.getCapitalDisponible()))
                .gananciaTotal(redondear(simulacion.getGananciaTotal()))
                .cantidadProductos(cantidadProductos)
                .retornoPorcentaje(redondear(simulacion.getRetornoTotalPorcentaje()))
                .build();
    }
    // En SimulacionService.java, modifica la parte final del archivo:

    public List<SimulacionResumenDTO> getSimulacionesPorUsuario(UUID usuarioId) {
        return simulacionRepository.findSimulacionesPorUsuarioOrdenadas(usuarioId)
                .stream()
                .map(this::convertirAResumenDTO)
                .toList();
    }

    private SimulacionResumenDTO convertirAResumenDTO(@NonNull Simulacion simulacion) {
        // Contar productos seleccionados usando nuestro método manual
        List<ProductoSeleccionadoDTO> productos = convertirJsonAProductos(simulacion.getProductosSeleccionados());
        int cantidadProductos = productos.size();

        return SimulacionResumenDTO.builder()
                .id(simulacion.getId())
                .usuarioId(simulacion.getUsuario().getId())
                .fechaSimulacion(simulacion.getFechaSimulacion())
                .capitalDisponible(redondear(simulacion.getCapitalDisponible()))
                .gananciaTotal(redondear(simulacion.getGananciaTotal()))
                .cantidadProductos(cantidadProductos)
                .retornoPorcentaje(redondear(simulacion.getRetornoTotalPorcentaje()))
                .build();
    }
}