package com.alicorp.logistica.controller;

import com.alicorp.logistica.model.Despacho;
import com.alicorp.logistica.repository.DespachoRepository;
import com.alicorp.logistica.service.IaPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/despachos")
@CrossOrigin(origins = "*") // Permite que el frontend en React se conecte sin bloqueos CORS
public class DespachoController {

    @Autowired
    private DespachoRepository despachoRepository;

    @Autowired
    private IaPredictionService iaPredictionService;

    // 1. Endpoint para planificar un nuevo despacho (Consume IA y guarda en BD)
    @PostMapping("/planificar")
    public ResponseEntity<Despacho> planificarDespacho(@RequestBody Despacho nuevoDespacho) {

        // Llamamos a nuestro servicio que consulta al modelo en Python
        Map<String, Object> respuestaIA = iaPredictionService.consultarPrediccion(nuevoDespacho);

        // Extraemos los resultados del JSON que devolvió Python
        Map<String, Object> resultados = (Map<String, Object>) respuestaIA.get("resultados");

        // SOLUCIÓN AL ERROR: Convertimos primero a 'Number' genérico y luego extraemos el Integer
        // Esto evita que Java se confunda si Python le manda "78" o "78.0"
        Number tiempoEstimadoNum = (Number) resultados.get("deathtime_estimado_minutos");
        Integer tiempoEstimado = tiempoEstimadoNum.intValue();

        String sugerencia = (String) resultados.get("recomendacion_sistema");

        // Actualizamos nuestra entidad con los resultados de la IA
        nuevoDespacho.setDeathtimePredicho(tiempoEstimado);
        nuevoDespacho.setRecomendacionSistema(sugerencia);

        // Guardamos el viaje completo en la base de datos Neon (PostgreSQL)
        Despacho despachoGuardado = despachoRepository.save(nuevoDespacho);

        return ResponseEntity.ok(despachoGuardado);
    }

    // 2. Endpoint para obtener el historial (Para llenar la tabla en React)
    @GetMapping("/historial")
    public ResponseEntity<List<Despacho>> obtenerHistorial() {
        return ResponseEntity.ok(despachoRepository.findAllByOrderByFechaRegistroDesc());
    }
}