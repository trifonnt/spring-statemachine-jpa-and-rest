package com.example.order;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.statemachine.StateMachineContext;

import com.example.ContextEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Access(AccessType.FIELD)
@Table(name = "orders", indexes = @Index(columnList = "currentState"))
public class Order extends AbstractPersistable<Long> implements ContextEntity<OrderState, OrderEvent, Long> { // NOSONAR

    private static final long serialVersionUID = 8848887579564649636L;

    @JsonIgnore
    @Transient
    transient StateMachineContext<OrderState, OrderEvent> stateMachineContext; // NOSONAR

    @Enumerated(EnumType.STRING)
    OrderState currentState;

    public OrderState getCurrentState() {
		return currentState;
	}
	public void setCurrentState(OrderState currentState) {
		this.currentState = currentState;
	}

	@Override
	public StateMachineContext<OrderState, OrderEvent> getStateMachineContext() {
		return stateMachineContext;
	}
	@Override
    public void setStateMachineContext(/*@NotNull*/ StateMachineContext<OrderState, OrderEvent> stateMachineContext) {
        this.currentState = stateMachineContext.getState();
        this.stateMachineContext = stateMachineContext;
    }

    @JsonIgnore
    @Override
    public boolean isNew() {
        return super.isNew();
    }
}
