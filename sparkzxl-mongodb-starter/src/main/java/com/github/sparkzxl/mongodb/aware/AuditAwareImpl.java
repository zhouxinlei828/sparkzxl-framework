package com.github.sparkzxl.mongodb.aware;

import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;

/**
 * description: 审计
 *
 * @author zhouxinlei
 */
public class AuditAwareImpl implements AuditorAware<String> {

    @NonNull
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(RequestLocalContextHolder.getUserId(String.class));
    }
}
