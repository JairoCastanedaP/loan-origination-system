# loan-origination-system

Sistema de originación de créditos construido con **Arquitectura Hexagonal (Puertos y Adaptadores)** en Java y Spring Boot. Gestiona la solicitud de crédito desde la radicación hasta el desembolso: requisitos, evaluación de riesgo, observaciones, subsanación, pagos, seguimiento, encuestas y estadísticas.

> 🚧 Proyecto en construcción.

## Documentación

- [Análisis de requerimientos](docs/01-analisis-de-requerimientos.md)

## Estructura

```text
src/main/java/com/co/bank/credit/loan_origination_system/
├── domain/           Núcleo de negocio en Java puro
├── application/      Puertos (in/out) y servicios de casos de uso
└── infrastructure/   Adaptadores (REST, jobs, persistencia, mensajería) y configuración
```

## Autor

Jairo Alejandro Castañeda Pedraza
