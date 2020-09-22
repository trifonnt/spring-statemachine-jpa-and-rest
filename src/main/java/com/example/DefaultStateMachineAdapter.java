package com.example;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;

public class DefaultStateMachineAdapter<S, E, T> {

    final StateMachineFactory<S, E> stateMachineFactory;

    final StateMachinePersister<S, E, T> persister;

    public DefaultStateMachineAdapter(StateMachineFactory<S, E> stateMachineFactory, StateMachinePersister<S, E, T> persister) {
		this.stateMachineFactory = stateMachineFactory;
		this.persister = persister;
	}

    public StateMachine<S, E> restore(T contextObject) throws Exception {
        StateMachine<S, E> stateMachine = stateMachineFactory.getStateMachine();
        return persister.restore(stateMachine, contextObject);
    }

//    @SneakyThrows
    public void persist(StateMachine<S, E> stateMachine, T order) throws Exception {
        persister.persist(stateMachine, order);
    }

    public StateMachine<S, E> create() {
        StateMachine<S, E> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.start();
        return stateMachine;
    }

}
