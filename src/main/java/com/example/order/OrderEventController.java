package com.example.order;

import java.io.Serializable;

import javax.transaction.Transactional;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.ContextEntity;
import com.example.DefaultStateMachineAdapter;

@RepositoryRestController
public class OrderEventController {

    final DefaultStateMachineAdapter<OrderState, OrderEvent, ContextEntity<OrderState, OrderEvent, ? extends Serializable>> orderStateMachineAdapter;

    public OrderEventController(DefaultStateMachineAdapter<OrderState, OrderEvent, ContextEntity<OrderState, OrderEvent, ? extends Serializable>> orderStateMachineAdapter) {
		super();
		this.orderStateMachineAdapter = orderStateMachineAdapter;
	}

	@RequestMapping(path = "/orders/{id}/receive/{event}", method = RequestMethod.POST)
    @Transactional
    public HttpEntity<Void> receiveEvent(@PathVariable("id") Order order, @PathVariable("event") OrderEvent event) throws Exception {
        StateMachine<OrderState, OrderEvent> stateMachine = orderStateMachineAdapter.restore(order);
        if (stateMachine.sendEvent(event)) {
            orderStateMachineAdapter.persist(stateMachine, order);
            return ResponseEntity.accepted().build();
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }
    }
}
