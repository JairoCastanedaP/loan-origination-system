# Roadmap de entregas y evolución de arquitectura

## 1. Propósito

Este roadmap muestra cómo evoluciona el Loan Origination System, fase por fase. La aplicación crece de adentro hacia afuera: primero el dominio y los casos de uso, que no dependen de ninguna tecnología, y después los adaptadores que los conectan con el mundo exterior. Los requerimientos están en `01-analisis-de-requerimientos.md`.

## 2. Flujo de trabajo

- **Una rama por fase:** `architecture/NN-nombre`, creada desde `develop`.
- Al cerrar una fase: compila, pasan las pruebas (`./mvnw verify`) → merge a `develop`. Al final: merge a `main` y tag `mvp-final`.
- El dominio y la aplicación nunca importan clases de Spring, JPA ni de otra librería de infraestructura.
- Credenciales y datos de conexión siempre en variables de entorno (`.env`), nunca en el repositorio.

## 3. Fases

| N.º | Rama | Resultado | Estado |
|---|---|---|---|
| 00 | architecture/00-project-foundation | Proyecto Spring Boot base, paquetes hexagonales, requerimientos y roadmap | ✅ |
| 01 | architecture/01-domain-foundation | Base del dominio: `AggregateRoot`, `DomainEvent`, validaciones, excepciones y VOs compartidos, con sus pruebas | ✅ |
| 02 | architecture/02-credit-domain | `CreditProduct`, `Customer` y `LoanApplication` con estados, observaciones, recomendación, cuota y plan de pagos | ✅ |
| 03 | architecture/03-domain-testing | Pruebas unitarias del dominio y reglas de arquitectura | ⏳ |
| 04 | architecture/04-use-cases | Puertos de entrada y salida, servicios de casos de uso y adaptadores en memoria | ⏳ |
| 05 | architecture/05-rest-adapter | API REST v1, DTOs, manejo de errores y Swagger | ⏳ |
| 06 | architecture/06-persistence-adapters | PostgreSQL con Spring Data JPA y Cassandra para el seguimiento | ⏳ |
| 07 | architecture/07-integrations | Central de riesgo simulada (REST), RabbitMQ, job de vencimiento y CSV | ⏳ |
| 08 | architecture/08-packaging-deployment | Docker Compose, README final y colección Postman | ⏳ |

## 4. Detalle de las fases

### 00 — Proyecto base

- Proyecto generado con Spring Initializr: Maven, Java 25, Spring Boot 4.1.2, `groupId` `com.co.bank.credit`, `artifactId` `loan-origination-system`.
- Dependencias mínimas: Spring Web MVC y Validation. Cada dependencia de infraestructura entra en la fase que la usa.
- Paquete base `com.co.bank.credit.loan_origination_system` con las carpetas `domain`, `application` e `infrastructure`.
- Documentos `01-analisis-de-requerimientos.md` y `02-roadmap.md`.

### 01 — Base del dominio

- `AggregateRoot` con registro y extracción de eventos (`pullDomainEvents`).
- `DomainEvent` con identificador y fecha de ocurrencia.
- `Validate` con validaciones reutilizables (no nulo, no vacío, longitud, rango).
- Jerarquía de excepciones: `DomainException`, `ValueObjectException`, `BusinessRuleException`.
- VOs compartidos: `Money`, `Email`, `PhoneNumber`, `DocumentNumber` (con `DocumentType`: CC, CE, PA), `FirstName`, `LastName`.
- `AggregateRoot<ID>` compara por identidad: dos agregados con el mismo id son el mismo agregado.
- `Money` siempre tiene 2 decimales con redondeo `HALF_EVEN`, moneda COP por defecto y nunca es negativo.
- `PhoneNumber` acepta números colombianos de 10 dígitos (celular `3XX` o fijo `60X`), con o sin `+57`.
- Pruebas unitarias de cada VO, de `Validate` y de `AggregateRoot` (59 pruebas, sin Spring).

### 02 — Dominio de crédito

- `CreditProduct` con `InterestRate` (E.A. y su tasa mensual equivalente), rango de `Term` y de `Money`, tarifa de estudio, `RequirementType` exigidos y activación.
- `Customer` con documento, nombres, contacto e ingreso mensual.
- `LoanApplication` como raíz de agregado:
  - Al crearse copia las condiciones del producto (tasa, tarifa y requisitos), para que un cambio posterior del producto no altere solicitudes en curso.
  - `LoanApplicationStatus.transitionTo(...)` controla las 11 transiciones del diagrama.
  - Requisitos cargados (`SubmittedRequirement`), pago del estudio (`StudyFeePayment`), observaciones con fecha límite (`Observation`), evaluación (`RiskAssessment`), decisión (`Decision`), plan de pagos y desembolso.
  - Recibe la fecha (`now`) como parámetro: el dominio no lee el reloj del sistema.
- `CreditPolicy` concentra los parámetros configurables y las reglas RN-05 a RN-08; `CreditPolicy.standard()` trae los valores aprobados.
- `AmortizationCalculator` (servicio de dominio) calcula la cuota fija y el plan de pagos; la última cuota ajusta el redondeo para que el capital pagado sea exactamente el monto prestado.
- Eventos de dominio (`record`): `LoanApplicationStatusChanged` en cada transición (para el seguimiento) y `Submitted`, `Observed`, `Remediated`, `Approved`, `Rejected`, `Disbursed`, `Expired` (para notificaciones).
- RN-12 (una solicitud abierta por cliente y producto) necesita consultar otras solicitudes: la valida el caso de uso en la fase 04.
- Pruebas de las reglas principales y del cálculo de la cuota contra valores de referencia (79 pruebas en total).

### 03 — Pruebas del dominio

- Pruebas de los VOs de crédito (`InterestRate`, `Term`, `ApplicationNumber`, `CreditScore`) y de `CreditProduct` y `Customer`.
- Pruebas parametrizadas de todas las transiciones de estado, permitidas y rechazadas.
- Casos límite de las reglas (umbral exacto de score, de capacidad y de comité; vencimiento justo en la fecha límite).
- Reglas de arquitectura que fallan si el dominio o la aplicación dependen de Spring o de la infraestructura.

### 04 — Casos de uso

- Un puerto de entrada por caso de uso con su `Command` o `Query`.
- Puertos de salida pequeños por responsabilidad.
- Servicios que orquestan el dominio y publican los eventos.
- Adaptadores en memoria para ejecutar el flujo completo sin base de datos.
- Pruebas de los servicios.

### 05 a 08

Se detallan al iniciar cada fase.
