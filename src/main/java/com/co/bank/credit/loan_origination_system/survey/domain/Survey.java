package com.co.bank.credit.loan_origination_system.survey.domain;

import com.co.bank.credit.loan_origination_system.customer.domain.CustomerId;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplicationId;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplicationStatus;
import com.co.bank.credit.loan_origination_system.shared.domain.exception.BusinessRuleException;
import com.co.bank.credit.loan_origination_system.shared.domain.kernel.AggregateRoot;
import com.co.bank.credit.loan_origination_system.survey.domain.event.SurveyAnswered;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireLength;
import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonNull;

/**
 * Encuesta de satisfacción de una solicitud cerrada.
 * RN-13: solo para solicitudes en estado terminal y con calificación de 1 a 5.
 * Que se responda una sola vez por solicitud lo valida el caso de uso, porque necesita
 * consultar las encuestas existentes.
 */
public final class Survey extends AggregateRoot<SurveyId> {

    private static final int COMMENT_MAX_LENGTH = 500;

    private final LoanApplicationId loanApplicationId;
    private final CustomerId customerId;
    private final LoanApplicationStatus closingStatus;
    private final Rating rating;
    private final String comment;
    private final Instant answeredAt;

    private Survey(SurveyId id, LoanApplicationId loanApplicationId, CustomerId customerId,
                   LoanApplicationStatus closingStatus, Rating rating, String comment, Instant answeredAt) {
        super(id);
        this.loanApplicationId = requireNonNull(loanApplicationId, "Loan application id cannot be null");
        this.customerId = requireNonNull(customerId, "Customer id cannot be null");
        this.closingStatus = requireNonNull(closingStatus, "Closing status cannot be null");
        this.rating = requireNonNull(rating, "Rating cannot be null");
        this.comment = normalizeComment(comment);
        this.answeredAt = requireNonNull(answeredAt, "Answer date cannot be null");
    }

    public static Survey answer(SurveyId id, LoanApplicationId loanApplicationId, CustomerId customerId,
                                LoanApplicationStatus closingStatus, Rating rating, String comment, Instant now) {
        requireNonNull(closingStatus, "Closing status cannot be null");
        if (!closingStatus.isTerminal()) {
            throw new BusinessRuleException("RN-13",
                    "A survey can only be answered for a closed application, current status: " + closingStatus);
        }
        Survey survey = new Survey(id, loanApplicationId, customerId, closingStatus, rating, comment, now);
        survey.addEvent(new SurveyAnswered(UUID.randomUUID(), now, id.getValue(),
                loanApplicationId.getValue(), rating.getValue()));
        return survey;
    }

    public static Survey reconstitute(SurveyId id, LoanApplicationId loanApplicationId, CustomerId customerId,
                                      LoanApplicationStatus closingStatus, Rating rating, String comment,
                                      Instant answeredAt) {
        return new Survey(id, loanApplicationId, customerId, closingStatus, rating, comment, answeredAt);
    }

    private static String normalizeComment(String comment) {
        if (comment == null || comment.isBlank()) {
            return null;
        }
        return requireLength(comment.strip(), 1, COMMENT_MAX_LENGTH, "Survey comment");
    }

    public LoanApplicationId getLoanApplicationId() { return loanApplicationId; }
    public CustomerId getCustomerId() { return customerId; }
    public LoanApplicationStatus getClosingStatus() { return closingStatus; }
    public Rating getRating() { return rating; }
    public Optional<String> getComment() { return Optional.ofNullable(comment); }
    public Instant getAnsweredAt() { return answeredAt; }
}
