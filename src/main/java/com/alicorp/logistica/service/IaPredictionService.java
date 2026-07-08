package com.alicorp.logistica.service;

import com.alicorp.logistica.model.Despacho;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class IaPredictionService {

    @Value("${ia.api.url}")
    private String pythonApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> consultarPrediccion(Despacho despacho) {
        // 1. Preparamos los datos (JSON) que le enviaremos a FastAPI
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("dia_semana", despacho.getDiaSemana());
        requestBody.put("hora_llegada", despacho.getHoraLlegada());
        requestBody.put("toneladas_carga", despacho.getToneladasCarga());
        requestBody.put("tipo_cliente", despacho.getTipoCliente());
        requestBody.put("indice_congestion", despacho.getIndiceCongestion());

        // 2. Configuramos las cabeceras HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // 3. Hacemos la llamada POST a la API de Python
            ResponseEntity<Map> response = restTemplate.postForEntity(pythonApiUrl, entity, Map.class);

            // 4. Retornamos la respuesta (que contiene los minutos predecidos)
            return response.getBody();

        } catch (Exception e) {
            System.err.println("Error conectando con la API de IA: " + e.getMessage());
            throw new RuntimeException("No se pudo obtener predicción de la IA.");
        }
    }
}