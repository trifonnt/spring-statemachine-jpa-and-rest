package com.example;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

//import org.springframework.hateoas.Identifiable;
import org.springframework.statemachine.StateMachineContext;

public interface ContextEntity<S, E, ID extends Serializable> /*extends Identifiable<ID>*/ {

	StateMachineContext<S, E> getStateMachineContext();

	void setStateMachineContext(/* @NotNull */ StateMachineContext<S, E> context);

//	Long getId();
}
