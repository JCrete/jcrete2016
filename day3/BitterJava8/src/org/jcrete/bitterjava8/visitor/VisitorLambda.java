package org.jcrete.bitterjava8.visitor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author ikost
 * @param <R>
 */
@org.jpatterns.gof.VisitorPattern.Visitor
public class VisitorLambda<R> {
    private final Map<Class<?>, Function<Object, R>> map = new HashMap<>();

    public <T> VisitorLambda<R> when(Class<T> type, Function<T,R> f) {
        map.put(type, (Function<Object, R>) f); // obj -> f.apply(type.cast(obj))
        return this;
    }
    
    public R visit(Object receiver) {
        return map.getOrDefault(receiver.getClass(), 
                r -> {throw new IllegalStateException();})
                .apply(receiver);
    }
    
}
