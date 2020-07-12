package com.sparksys.commons.cloud.ribbon;

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.AvailabilityPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.PredicateBasedRule;
import org.springframework.util.Assert;

/**
 * description:
 *
 * @author: zhouxinlei
 * @date: 2020-07-12 16:16:19
*/
public abstract class DiscoveryEnabledRule extends PredicateBasedRule {

    private final CompositePredicate predicate;

    public DiscoveryEnabledRule(DiscoveryEnabledPredicate  discoveryEnabledPredicate) {
        Assert.notNull(discoveryEnabledPredicate, "Parameter 'discoveryEnabledPredicate' can't be null");
        this.predicate = createCompositePredicate(discoveryEnabledPredicate,new AvailabilityPredicate(this,null));
    }



    @Override
    public AbstractServerPredicate getPredicate() {
        return this.predicate;
    }

    private CompositePredicate createCompositePredicate(DiscoveryEnabledPredicate discoveryEnabledPredicate,
                                                        AvailabilityPredicate availabilityPredicate) {
        return CompositePredicate.withPredicates(discoveryEnabledPredicate, availabilityPredicate)
                .build();
    }

}
