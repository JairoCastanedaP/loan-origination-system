# loan-origination-system

Sistema de originación de créditos construido con **Arquitectura Hexagonal (Puertos y Adaptadores)** en Java y Spring Boot. Gestiona la solicitud de crédito desde la radicación hasta el desembolso: requisitos, evaluación de riesgo, observaciones, subsanación, pagos, seguimiento, encuestas y estadísticas.

> 🚧 Proyecto en construcción.

## Documentación

- [Análisis de requerimientos](docs/01-analisis-de-requerimientos.md)
- [Roadmap de entregas](docs/02-roadmap.md)

## Estructura

```text
src/main/java/com/co/bank/credit/loan_origination_system/
├── loanapplication/   Solicitud de crédito: domain, application, infrastructure
├── customer/          Cliente
├── creditproduct/     Producto de crédito
├── survey/            Encuesta de satisfacción
├── statistics/        Estadísticas y exportación CSV
└── shared/            Núcleo común (AggregateRoot, VOs, excepciones, puertos compartidos)
```

Cada módulo se divide en `domain` (Java puro), `application` (puertos y casos de uso) e `infrastructure` (adaptadores y configuración).

## Autor

Jairo Alejandro Castañeda Pedraza
