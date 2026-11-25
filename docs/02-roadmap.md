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
| 02 | architecture/02-credit-domain | `CreditProduct`, `Customer` y `LoanApplication` con estados, observaciones, recomendación, cuota y plan de pagos | ⏳ |
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

- `CreditProduct` con `InterestRate`, rango de `Term` y de `Money`, tarifa de estudio y requisitos.
- `Customer` con ingreso mensual.
- `LoanApplication` como raíz de agregado: `LoanApplicationStatus.transitionTo(...)`, requisitos cargados, observaciones con fecha límite, recomendación automática, decisión, plan de pagos y desembolso.
- Cálculo de cuota por amortización francesa con `BigDecimal`.
- Eventos de dominio de cada cambio de estado.

### 03 — Pruebas del dominio

- Pruebas de los VOs y agregados de crédito (los VOs compartidos ya se prueban en la fase 01).
- Pruebas de todas las transiciones de estado, permitidas y rechazadas.
- Pruebas de las reglas RN-01 a RN-13 y del plan de pagos.
- Reglas de arquitectura que fallan si el dominio o la aplicación dependen de Spring o de la infraestructura.

### 04 — Casos de uso

- Un puerto de entrada por caso de uso con su `Command` o `Query`.
- Puertos de salida pequeños por responsabilidad.
- Servicios que orquestan el dominio y publican los eventos.
- Adaptadores en memoria para ejecutar el flujo completo sin base de datos.
- Pruebas de los servicios.

### 05 a 08

Se detallan al iniciar cada fase.
