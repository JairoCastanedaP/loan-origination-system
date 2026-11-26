package com.co.bank.credit.loan_origination_system.loanapplication.domain;

import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProduct;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProductId;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.InterestRate;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.RequirementType;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.Term;
import com.co.bank.credit.loan_origination_system.customer.domain.CustomerId;
import com.co.bank.credit.loan_origination_system.shared.domain.exception.BusinessRuleException;
import com.co.bank.credit.loan_origination_system.shared.domain.kernel.AggregateRoot;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.amortization.AmortizationCalculator;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.amortization.PaymentPlan;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.event.LoanApplicationApproved;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.event.LoanApplicationDisbursed;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.event.LoanApplicationExpired;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.event.LoanApplicationObserved;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.event.LoanApplicationRejected;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.event.LoanApplicationRemediated;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.event.LoanApplicationStatusChanged;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.event.LoanApplicationSubmitted;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonBlank;
import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonNull;

/**
 * Solicitud de crédito: raíz del agregado principal.
 * <p>
 * Al crearse toma una copia de las condiciones del producto (tasa, tarifa de estudio y requisitos),
 * para que un cambio posterior del producto no altere una solicitud en curso.
 * Todas las fechas se reciben como parámetro ({@code now}) para que el dominio no dependa del reloj.
 */
public final class LoanApplication extends AggregateRoot<LoanApplicationId> {

    private final ApplicationNumber number;
    private final CustomerId customerId;
    private final CreditProductId productId;
    private final Money requestedAmount;
    private final Term term;
    private final InterestRate interestRate;
    private final Money studyFee;
    private final Set<RequirementType> requiredRequirements;
    private LoanApplicationStatus status;
    private final Map<RequirementType, SubmittedRequirement> requirements;
    private final List<Observation> observations;
    private StudyFeePayment studyFeePayment;
    private RiskAssessment riskAssessment;
    private Decision decision;
    private PaymentPlan paymentPlan;
    private Disbursement disbursement;
    private String cancellationReason;
    private final Instant createdAt;
    private Instant updatedAt;

    private LoanApplication(LoanApplicationId id, ApplicationNumber number, CustomerId customerId,
                            CreditProductId productId, Money requestedAmount, Term term,
                            InterestRate interestRate, Money studyFee, Set<RequirementType> requiredRequirements,
                            LoanApplicationStatus status, Collection<SubmittedRequirement> requirements,
                            List<Observation> observations, StudyFeePayment studyFeePayment,
                            RiskAssessment riskAssessment, Decision decision, PaymentPlan paymentPlan,
                            Disbursement disbursement, String cancellationReason,
                            Instant createdAt, Instant updatedAt) {
        super(id);
        this.number = requireNonNull(number, "Application number cannot be null");
        this.customerId = requireNonNull(customerId, "Customer id cannot be null");
        this.productId = requireNonNull(productId, "Product id cannot be null");
        this.requestedAmount = requireNonNull(requestedAmount, "Requested amount cannot be null");
        this.term = requireNonNull(term, "Term cannot be null");
        this.interestRate = requireNonNull(interestRate, "Interest rate cannot be null");
        this.studyFee = requireNonNull(studyFee, "Study fee cannot be null");
        this.requiredRequirements = Set.copyOf(requireNonNull(requiredRequirements, "Required requirements cannot be null"));
        this.status = requireNonNull(status, "Status cannot be null");
        this.requirements = new EnumMap<>(RequirementType.class);
        requireNonNull(requirements, "Requirements cannot be null").forEach(r -> this.requirements.put(r.type(), r));
        this.observations = new ArrayList<>(requireNonNull(observations, "Observations cannot be null"));
        this.studyFeePayment = studyFeePayment;
        this.riskAssessment = riskAssessment;
        this.decision = decision;
        this.paymentPlan = paymentPlan;
        this.disbursement = disbursement;
        this.cancellationReason = cancellationReason;
        this.createdAt = requireNonNull(createdAt, "Creation date cannot be null");
        this.updatedAt = requireNonNull(updatedAt, "Update date cannot be null");
    }

    /**
     * Crea una solicitud en borrador. El producto debe estar activo y el monto y el plazo
     * deben estar dentro de sus límites (RN-01).
     * La regla RN-12 (una sola solicitud abierta por cliente y producto) necesita consultar
     * otras solicitudes, por eso la valida el caso de uso antes de llamar a este método.
     */
    public static LoanApplication create(LoanApplicationId id, ApplicationNumber number, CustomerId customerId,
                                         CreditProduct product, Money requestedAmount, Term term, Instant now) {
        requireNonNull(product, "Credit product cannot be null");
        requireNonNull(now, "Creation date cannot be null");
        product.ensureActive();
        product.validateConditions(requestedAmount, term);
        return new LoanApplication(id, number, customerId, product.getId(), requestedAmount, term,
                product.getInterestRate(), product.getStudyFee(), product.getRequiredRequirements(),
                LoanApplicationStatus.DRAFT, List.of(), List.of(), null, null, null, null, null, null,
                now, now);
    }

    public static LoanApplication reconstitute(LoanApplicationId id, ApplicationNumber number, CustomerId customerId,
                                               CreditProductId productId, Money requestedAmount, Term term,
                                               InterestRate interestRate, Money studyFee,
                                               Set<RequirementType> requiredRequirements,
                                               LoanApplicationStatus status,
                                               Collection<SubmittedRequirement> requirements,
                                               List<Observation> observations, StudyFeePayment studyFeePayment,
                                               RiskAssessment riskAssessment, Decision decision,
                                               PaymentPlan paymentPlan, Disbursement disbursement,
                                               String cancellationReason, Instant createdAt, Instant updatedAt) {
        return new LoanApplication(id, number, customerId, productId, requestedAmount, term, interestRate,
                studyFee, requiredRequirements, status, requirements, observations, studyFeePayment,
                riskAssessment, decision, paymentPlan, disbursement, cancellationReason, createdAt, updatedAt);
    }

    // ------------------------------------------------------------------ requisitos y radicación

    /**
     * Carga o reemplaza el documento de un requisito. Solo en borrador o mientras se subsana.
     */
    public void attachRequirement(RequirementType type, String documentReference, Instant now) {
        requireNonNull(type, "Requirement type cannot be null");
        if (status != LoanApplicationStatus.DRAFT && status != LoanApplicationStatus.OBSERVED) {
            throw new BusinessRuleException("REQUIREMENTS_LOCKED",
                    "Requirements can only be changed in DRAFT or OBSERVED status, current: " + status);
        }
        if (!requiredRequirements.contains(type)) {
            throw new BusinessRuleException("RN-02", "Requirement " + type + " is not required by the product");
        }
        requirements.put(type, new SubmittedRequirement(type, documentReference, now));
        touch(now);
    }

    public Set<RequirementType> missingRequirements() {
        Set<RequirementType> missing = EnumSet.noneOf(RequirementType.class);
        missing.addAll(requiredRequirements);
        missing.removeAll(requirements.keySet());
        return missing;
    }

    /**
     * RN-02: solo se radica con todos los requisitos obligatorios cargados.
     */
    public void submit(Instant now) {
        Set<RequirementType> missing = missingRequirements();
        if (!missing.isEmpty()) {
            throw new BusinessRuleException("RN-02", "Missing required documents: " + missing);
        }
        changeStatus(LoanApplicationStatus.SUBMITTED, now);
        addEvent(new LoanApplicationSubmitted(UUID.randomUUID(), now, idValue(), number.getValue(),
                customerId.getValue()));
    }

    /**
     * RN-03: con el pago de la tarifa de estudio confirmado, la solicitud pasa a estudio.
     */
    public void registerStudyFeePayment(Money amount, String paymentReference, Instant now) {
        requireNonNull(amount, "Payment amount cannot be null");
        if (!amount.equals(studyFee)) {
            throw new BusinessRuleException("RN-03",
                    "Payment amount " + amount + " does not match the study fee " + studyFee);
        }
        StudyFeePayment payment = new StudyFeePayment(amount, paymentReference, now);
        changeStatus(LoanApplicationStatus.UNDER_REVIEW, now);
        this.studyFeePayment = payment;
    }

    // ------------------------------------------------------------------ evaluación

    /**
     * Calcula la cuota (RN-04) y emite la recomendación automática (RN-05, RN-06).
     * Se puede repetir mientras la solicitud esté en estudio.
     */
    public RiskAssessment assessRisk(CreditScore score, Money monthlyIncome, CreditPolicy policy, Instant now) {
        requireNonNull(policy, "Credit policy cannot be null");
        requireStatus(LoanApplicationStatus.UNDER_REVIEW, "assess risk");
        Money installment = AmortizationCalculator.monthlyInstallment(requestedAmount, interestRate, term);
        this.riskAssessment = policy.assess(score, installment, monthlyIncome, now);
        touch(now);
        return riskAssessment;
    }

    // ------------------------------------------------------------------ observaciones y subsanación

    /**
     * Registra una observación del analista. El cliente tiene el plazo de RN-08 para subsanar.
     */
    public void observe(String description, CreditPolicy policy, Instant now) {
        requireNonNull(policy, "Credit policy cannot be null");
        requireNonNull(now, "Observation date cannot be null");
        Observation observation = Observation.create(description, now, policy.remediationDeadline(now));
        changeStatus(LoanApplicationStatus.OBSERVED, now);
        observations.add(observation);
        addEvent(new LoanApplicationObserved(UUID.randomUUID(), now, idValue(), number.getValue(),
                observation.getDescription(), observation.getDueAt()));
    }

    /**
     * El cliente subsana dentro del plazo (RN-08); las observaciones pendientes quedan resueltas.
     */
    public void remediate(Instant now) {
        requireStatus(LoanApplicationStatus.OBSERVED, "remediate");
        if (isRemediationOverdue(now)) {
            throw new BusinessRuleException("RN-08", "The remediation deadline has passed");
        }
        changeStatus(LoanApplicationStatus.REMEDIATED, now);
        pendingObservations().forEach(o -> o.resolve(now));
        addEvent(new LoanApplicationRemediated(UUID.randomUUID(), now, idValue(), number.getValue()));
    }

    /**
     * El analista retoma el estudio. La evaluación anterior se descarta porque los documentos cambiaron.
     */
    public void resumeReview(Instant now) {
        changeStatus(LoanApplicationStatus.UNDER_REVIEW, now);
        this.riskAssessment = null;
    }

    /**
     * RN-08: vence la solicitud si alguna observación superó su fecha límite sin subsanar.
     */
    public void expire(Instant now) {
        requireStatus(LoanApplicationStatus.OBSERVED, "expire");
        if (!isRemediationOverdue(now)) {
            throw new BusinessRuleException("RN-08", "The remediation deadline has not passed yet");
        }
        changeStatus(LoanApplicationStatus.EXPIRED, now);
        addEvent(new LoanApplicationExpired(UUID.randomUUID(), now, idValue(), number.getValue()));
    }

    public boolean isRemediationOverdue(Instant now) {
        requireNonNull(now, "Date cannot be null");
        return observations.stream().anyMatch(o -> o.isOverdue(now));
    }

    // ------------------------------------------------------------------ decisión

    /**
     * RN-07: los montos que superan el umbral pasan al comité.
     */
    public void escalateToCommittee(CreditPolicy policy, Instant now) {
        requireNonNull(policy, "Credit policy cannot be null");
        requireStatus(LoanApplicationStatus.UNDER_REVIEW, "escalate to committee");
        requireRiskAssessment();
        if (!policy.requiresCommittee(requestedAmount)) {
            throw new BusinessRuleException("RN-07",
                    "Amount " + requestedAmount + " does not require committee approval");
        }
        changeStatus(LoanApplicationStatus.IN_COMMITTEE, now);
    }

    /**
     * Aprueba la solicitud y genera el plan de pagos (RF-16).
     * RN-09a: si la evaluación recomendó negar, la justificación es obligatoria.
     */
    public void approve(String decidedBy, String justification, CreditPolicy policy, Instant now) {
        ensureCanBeDecided(policy);
        boolean followsRecommendation = riskAssessment.recommendsApproval();
        if (!followsRecommendation && (justification == null || justification.isBlank())) {
            throw new BusinessRuleException("RN-09a",
                    "Approving against the automatic recommendation requires a justification");
        }
        Decision newDecision = new Decision(true, decidedBy, isInCommittee(), followsRecommendation,
                justification, now);
        PaymentPlan plan = AmortizationCalculator.paymentPlan(requestedAmount, interestRate, term,
                LocalDate.ofInstant(now, ZoneOffset.UTC));

        boolean byCommittee = isInCommittee();
        changeStatus(LoanApplicationStatus.APPROVED, now);
        this.decision = newDecision;
        this.paymentPlan = plan;
        addEvent(new LoanApplicationApproved(UUID.randomUUID(), now, idValue(), number.getValue(),
                requestedAmount, plan.monthlyInstallment(), byCommittee));
    }

    /**
     * Niega la solicitud. RN-09: el motivo es obligatorio.
     */
    public void reject(String decidedBy, String reason, CreditPolicy policy, Instant now) {
        ensureCanBeDecided(policy);
        String validReason = requireNonBlank(reason, "A rejection reason is required (RN-09)");
        Decision newDecision = new Decision(false, decidedBy, isInCommittee(),
                !riskAssessment.recommendsApproval(), validReason, now);
        changeStatus(LoanApplicationStatus.REJECTED, now);
        this.decision = newDecision;
        addEvent(new LoanApplicationRejected(UUID.randomUUID(), now, idValue(), number.getValue(), validReason));
    }

    // ------------------------------------------------------------------ desembolso y cancelación

    /**
     * RN-10: solo se desembolsa una solicitud aprobada, por el monto aprobado.
     */
    public void disburse(String disbursedBy, Instant now) {
        Disbursement newDisbursement = new Disbursement(requestedAmount, disbursedBy, now);
        changeStatus(LoanApplicationStatus.DISBURSED, now);
        this.disbursement = newDisbursement;
        addEvent(new LoanApplicationDisbursed(UUID.randomUUID(), now, idValue(), number.getValue(),
                requestedAmount));
    }

    /**
     * RN-11: el cliente solo puede cancelar en DRAFT o SUBMITTED (lo garantizan las transiciones).
     */
    public void cancel(String reason, Instant now) {
        String validReason = requireNonBlank(reason, "A cancellation reason is required");
        changeStatus(LoanApplicationStatus.CANCELLED, now);
        this.cancellationReason = validReason;
    }

    // ------------------------------------------------------------------ apoyo

    private void ensureCanBeDecided(CreditPolicy policy) {
        requireNonNull(policy, "Credit policy cannot be null");
        if (status != LoanApplicationStatus.UNDER_REVIEW && status != LoanApplicationStatus.IN_COMMITTEE) {
            throw new BusinessRuleException("INVALID_TRANSITION",
                    "Only applications UNDER_REVIEW or IN_COMMITTEE can be decided, current: " + status);
        }
        requireRiskAssessment();
        if (policy.requiresCommittee(requestedAmount) && !isInCommittee()) {
            throw new BusinessRuleException("RN-07",
                    "Amount " + requestedAmount + " requires a committee decision");
        }
    }

    private void requireRiskAssessment() {
        if (riskAssessment == null) {
            throw new BusinessRuleException("RISK_NOT_ASSESSED", "The application has not been risk assessed");
        }
    }

    private void requireStatus(LoanApplicationStatus expected, String action) {
        if (status != expected) {
            throw new BusinessRuleException("INVALID_TRANSITION",
                    "Cannot " + action + " an application in status " + status);
        }
    }

    private void changeStatus(LoanApplicationStatus target, Instant now) {
        requireNonNull(now, "Date cannot be null");
        LoanApplicationStatus previous = status;
        this.status = status.transitionTo(target);
        touch(now);
        addEvent(new LoanApplicationStatusChanged(UUID.randomUUID(), now, idValue(), number.getValue(),
                previous, target));
    }

    private void touch(Instant now) {
        this.updatedAt = requireNonNull(now, "Date cannot be null");
    }

    private boolean isInCommittee() {
        return status == LoanApplicationStatus.IN_COMMITTEE;
    }

    private UUID idValue() {
        return getId().getValue();
    }

    private List<Observation> pendingObservations() {
        return observations.stream().filter(Observation::isPending).toList();
    }

    // ------------------------------------------------------------------ getters

    public ApplicationNumber getNumber() { return number; }
    public CustomerId getCustomerId() { return customerId; }
    public CreditProductId getProductId() { return productId; }
    public Money getRequestedAmount() { return requestedAmount; }
    public Term getTerm() { return term; }
    public InterestRate getInterestRate() { return interestRate; }
    public Money getStudyFee() { return studyFee; }
    public Set<RequirementType> getRequiredRequirements() { return requiredRequirements; }
    public LoanApplicationStatus getStatus() { return status; }
    public List<SubmittedRequirement> getRequirements() { return List.copyOf(requirements.values()); }
    public List<Observation> getObservations() { return List.copyOf(observations); }
    public Optional<StudyFeePayment> getStudyFeePayment() { return Optional.ofNullable(studyFeePayment); }
    public Optional<RiskAssessment> getRiskAssessment() { return Optional.ofNullable(riskAssessment); }
    public Optional<Decision> getDecision() { return Optional.ofNullable(decision); }
    public Optional<PaymentPlan> getPaymentPlan() { return Optional.ofNullable(paymentPlan); }
    public Optional<Disbursement> getDisbursement() { return Optional.ofNullable(disbursement); }
    public Optional<String> getCancellationReason() { return Optional.ofNullable(cancellationReason); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
