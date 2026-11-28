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
| 02b | architecture/02b-feature-packages | Reorganización por módulo de negocio (`loanapplication`, `customer`, `creditproduct`, `shared`...) | ✅ |
| 03 | architecture/03-domain-testing | Pruebas unitarias del dominio y reglas de arquitectura | ✅ |
| 04a | architecture/04a-use-cases-intake | `Survey`, casos de uso de cliente y producto, y de la solicitud hasta el pago del estudio | ✅ |
| 04b | architecture/04b-use-cases-review | Casos de uso de evaluación, observaciones, decisión, desembolso, cancelación, consultas y encuesta | ⏳ |
| 05 | architecture/05-rest-adapter | API REST v1, DTOs, manejo de errores y Swagger | ⏳ |
| 06 | architecture/06-persistence-adapters | PostgreSQL con Spring Data JPA, Cassandra para el seguimiento y `XxxConfig` con los `@Bean` | ⏳ |
| 07 | architecture/07-integrations | Central de riesgo simulada (REST), RabbitMQ, job de vencimiento y CSV | ⏳ |
| 08 | architecture/08-packaging-deployment | Docker Compose, README final y colección Postman | ⏳ |

## 4. Detalle de las fases

### 00 — Proyecto base

- Proyecto generado con Spring Initializr: Maven, Java 25, Spring Boot 4.1.2, `groupId` `com.co.bank.credit`, `artifactId` `loan-origination-system`.
- Dependencias mínimas: Spring Web MVC y Validation. Cada dependencia de infraestructura entra en la fase que la usa.
- Paquete base `com.co.bank.credit.loan_origination_system` con las carpetas `domain`, `application` e `infrastructure` (reorganizado por módulo en la fase 02b).
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

### 02b — Paquetes por módulo de negocio

- Se alinea la estructura con la vista en clase: cada módulo (`loanapplication`, `customer`, `creditproduct`, `survey`, `statistics`) tiene su propio `domain`, `application` e `infrastructure`.
- Lo común pasa a `shared`: `domain/kernel` (`AggregateRoot`, `DomainEvent`), `domain/model` (VOs), `domain/utils` (`Validate`), `domain/exception`, `application/exception` y `application/port/out` (`PublishDomainEventsPort`).
- Solo se mueven paquetes; la lógica no cambia y las 79 pruebas siguen pasando.

### 03 — Pruebas del dominio

- Pruebas de los VOs de crédito: `InterestRate` (tasa mensual equivalente y su vuelta a E.A.), `Term`, `CreditProductId`, `ApplicationNumber` y `CreditScore`.
- Pruebas de `CreditProduct` (límites de RN-01, activación, rangos inconsistentes, copia defensiva de requisitos) y de `Customer`.
- `LoanApplicationStatusTest` recorre las 121 combinaciones de estados contra una tabla escrita a mano del diagrama: 14 transiciones permitidas y 107 rechazadas.
- Casos límite:
  - Score exactamente 500 y capacidad exactamente 40% aprueban; 499 o 40,01% no. El porcentaje se redondea a 4 decimales, así que 40,004% cuenta como 40%.
  - Un monto de exactamente 50.000.000 lo decide el analista; con un centavo más va al comité (RN-07).
  - Subsanar justo en la fecha límite se permite; un segundo después la solicitud solo puede vencer (RN-08).
- Reglas de arquitectura con ArchUnit (`HexagonalArchitectureTest`), que hacen fallar `./mvnw verify` si:
  - Una capa depende de otra en sentido contrario (infraestructura → aplicación → dominio).
  - El dominio usa algo distinto de `java.*` y del propio dominio.
  - La aplicación usa Spring, Jakarta, Hibernate o Lombok, o algo fuera de la infraestructura usa Spring.
  - `shared` depende de un módulo de negocio, o un módulo usa los adaptadores o servicios de otro.
  - Los eventos no son `record` que implementan `DomainEvent`, o los puertos no son interfaces.
- Las reglas de capas que aún están vacías se activan solas cuando aparecen sus primeras clases.
- 330 pruebas en total.

### 04a — Casos de uso: radicación

Se limita a lo visto en clase hasta la sesión 06.

- Dominio `Survey` con `Rating` (1 a 5) y el evento `SurveyAnswered`. Solo se responde con la solicitud en estado terminal (RN-13).
- `shared/application`:
  - `ApplicationException` y sus subclases: `InvalidCommandException`, `ResourceNotFoundException` y `ResourceAlreadyExistsException`.
  - `Require`, que valida la presencia de datos en los `Command` y `Query`.
  - `PublishDomainEventsPort`.
- Casos de uso:

| Módulo | Caso de uso | RF |
|---|---|---|
| customer | `RegisterCustomerUseCase` (documento único), `FindCustomerByIdUseCase`, `FindCustomerByDocumentUseCase` | RF-01, RF-02 |
| creditproduct | `CreateCreditProductUseCase`, `FindCreditProductByIdUseCase` | RF-03 |
| loanapplication | `CreateLoanApplicationUseCase` (RN-12 y número consecutivo), `AttachRequirementUseCase`, `SubmitLoanApplicationUseCase`, `RegisterStudyFeePaymentUseCase` | RF-04 a RF-08 |

- Puertos de salida: `XxxCommandPort` (agregado), `XxxQueryPort` (vistas `record` de solo lectura) y `ApplicationNumberGeneratorPort`.
- Los servicios no usan Spring y reciben un `java.time.Clock`. El año del número de solicitud se toma en la zona horaria del reloj: en Bogotá, el 31 de diciembre a las 23:30 todavía es el año anterior.
- Antes de tomar un número consecutivo se validan el cliente, el producto, RN-12 y RN-01. Así una solicitud rechazada no consume número.
- Las pruebas de los servicios usan dobles de prueba en memoria dentro de `src/test`; no hay adaptadores en `src/main` todavía.
- 367 pruebas en total.

### 04b — Casos de uso: estudio y cierre

- Evaluar el riesgo, con `RiskBureauPort` para el score.
- Observar, subsanar, reanudar, escalar al comité, aprobar, negar, desembolsar y cancelar.
- Consultar la solicitud.
- Responder la encuesta, una sola vez por solicitud.

### 05 a 08

Se detallan al iniciar cada fase. Los `XxxConfig` con `@Bean` entran en la fase 06, junto con los primeros adaptadores reales, porque sin adaptadores Spring no puede crear los servicios.
