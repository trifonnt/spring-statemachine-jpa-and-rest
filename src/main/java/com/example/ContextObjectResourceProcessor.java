package com.example;

import java.io.Serializable;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.transition.Transition;

import com.example.order.Order;
import com.example.order.OrderEvent;
import com.example.order.OrderState;

public class ContextObjectResourceProcessor<S, E, T extends ContextEntity<S, E, ? extends Serializable>> implements RepresentationModelProcessor<EntityModel<T>> {

    private static final KebabCaseStrategy TO_KEBAB = new KebabCaseStrategy();

    final EntityLinks entityLinks;

    final DefaultStateMachineAdapter<S, E, ContextEntity<S, E, ? extends Serializable>> stateMachineAdapter;

    private final Logger log = LoggerFactory.getLogger(ContextObjectResourceProcessor.class);


    public ContextObjectResourceProcessor(EntityLinks entityLinks,
			DefaultStateMachineAdapter<S, E, ContextEntity<S, E, ? extends Serializable>> stateMachineAdapter) {
		super();
		this.entityLinks = entityLinks;
		this.stateMachineAdapter = stateMachineAdapter;
	}

	@Override
    public EntityModel<T> process(EntityModel<T> resource) {
        ContextEntity<S, E, ? extends Serializable> contextObject = resource.getContent();
        StateMachine<S, E> stateMachine;
		try {
			stateMachine = stateMachineAdapter.restore(contextObject);

			for (Transition<S, E> transition : stateMachine.getTransitions()) {
	            if (stateMachine.getState().getId().equals(transition.getSource().getId())) {
	                E event = transition.getTrigger().getEvent();
	                log.info("Found transition triggered by event: {}", event);
	                resource.add(eventLink((Order)contextObject, event, TO_KEBAB.translate(event.toString()))); //@Trifon - TODO: MUST not be Hardcoded Order!!!
	            }
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}

        return resource;
    }

//    private Link eventLink(ContextEntity<S, E, ? extends Serializable> contextObject, E event, String rel) {
//    private Link eventLink(AbstractPersistable<Long> contextObject, E event, String rel) {
	private Link eventLink(Order contextObject, E event, String rel) {
    	// @Trifon - old: ContextEntity
        return entityLinks.linkForItemResource(Order.class, contextObject.getId()).slash("receive").slash(event).withRel(rel);
    }

    static class PaidPredicate implements Predicate<StateMachine<OrderState, OrderEvent>> {
        @Override
        public boolean test(StateMachine<OrderState, OrderEvent> stateMachine) {
            return stateMachine.getExtendedState().get("paid", Boolean.class);
        }
    }

    // copied from Jackson.PropertyNamingStrategy
    static class KebabCaseStrategy
    {
        public String translate(String input)
        {
            if (input == null) return input; // garbage in, garbage out
            int length = input.length();
            if (length == 0) {
                return input;
            }

            StringBuilder result = new StringBuilder(length + (length >> 1));

            int upperCount = 0;

            for (int i = 0; i < length; ++i) {
                char ch = input.charAt(i);
                char lc = Character.toLowerCase(ch);
                
                if (lc == ch) { // lower-case letter means we can get new word
                    // but need to check for multi-letter upper-case (acronym), where assumption
                    // is that the last upper-case char is start of a new word
                    if (upperCount > 1) {
                        // so insert hyphen before the last character now
                        result.insert(result.length() - 1, '-');
                    }
                    upperCount = 0;
                } else {
                    // Otherwise starts new word, unless beginning of string
                    if ((upperCount == 0) && (i > 0)) {
                        result.append('-');
                    }
                    ++upperCount;
                }
                result.append(lc);
            }
            return result.toString();
        }
    }

}
