package com.alicorp.logistica.config;

import com.alicorp.logistica.model.Despacho;
import com.alicorp.logistica.repository.DespachoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private DespachoRepository despachoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Solo ejecutamos el script si hay menos de 5 registros en la base de datos
        if (despachoRepository.count() < 5) {
            System.out.println("======================================================");
            System.out.println("Iniciando Data Seeding: Generando historial de prueba...");

            List<Despacho> despachosMock = new ArrayList<>();
            Random random = new Random();

            // Vamos a generar 50 despachos aleatorios
            for (int i = 0; i < 50; i++) {
                Despacho d = new Despacho();

                // Datos de entrada aleatorios pero realistas
                d.setDiaSemana(random.nextInt(7) + 1); // Días del 1 al 7
                d.setHoraLlegada(random.nextInt(16) + 6); // Horas de 6 AM a 21 PM

                // Toneladas entre 2.0 y 15.0 redondeado a un decimal
                double toneladas = 2.0 + (15.0 - 2.0) * random.nextDouble();
                d.setToneladasCarga(Math.round(toneladas * 10.0) / 10.0);

                d.setTipoCliente(random.nextInt(3) + 1); // Clientes 1, 2 o 3

                // Congestión entre 0.1 y 1.0 redondeado a dos decimales
                double congestion = 0.1 + (1.0 - 0.1) * random.nextDouble();
                d.setIndiceCongestion(Math.round(congestion * 100.0) / 100.0);

                // Simulamos la fórmula matemática del modelo para que los resultados tengan sentido lógico
                int baseTiempo = (int) ((d.getToneladasCarga() * 2.5) + (d.getIndiceCongestion() * 45));
                int penalizacionCliente = (d.getTipoCliente() == 1) ? 30 : 10;
                int variacionRandom = random.nextInt(20) - 5; // Ruido aleatorio

                int deathtime = baseTiempo + penalizacionCliente + variacionRandom;
                if (deathtime < 15) deathtime = 15; // Mínimo 15 minutos

                d.setDeathtimePredicho(deathtime);

                // Clasificamos si es riesgo o ruta óptima
                if (deathtime > 60) {
                    d.setRecomendacionSistema("⚠️ Peligro de Retraso: Sugerencia reprogramar.");
                } else {
                    d.setRecomendacionSistema("✅ Ruta Óptima");
                }

                // Generamos fechas escalonadas hacia atrás (para que el historial se vea de los últimos días)
                LocalDateTime fecha = LocalDateTime.now()
                        .minusHours(i * 4L)
                        .minusMinutes(random.nextInt(60));
                d.setFechaRegistro(fecha);

                despachosMock.add(d);
            }

            // Guardamos los 50 registros de golpe en Neon PostgreSQL
            despachoRepository.saveAll(despachosMock);

            System.out.println("¡Se insertaron " + despachosMock.size() + " registros exitosamente en Neon!");
            System.out.println("======================================================");
        } else {
            System.out.println("Data Seeding omitido: La base de datos ya contiene información.");
        }
    }
}