package com.sparksys.commons.cloud.ribbon;

/**
 * description:
 *
 * @author: zhouxinlei
 * @date: 2020-07-12 16:16:40
 */
public class MetadataAwareRule extends DiscoveryEnabledRule {


    public MetadataAwareRule() {
        this(new MetadataAwarePredicate());
    }

    public MetadataAwareRule(DiscoveryEnabledPredicate predicate) {
        super(predicate);
    }

}
