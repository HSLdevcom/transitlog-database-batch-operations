package fi.hsl.features.splitdatabasetables.batchfiles;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;

import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;

public class DuplicateViolationPolicy implements org.springframework.batch.core.step.skip.SkipPolicy {
    @Override
    public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
        if (t instanceof EntityExistsException) {
            return true;
        }
        return t instanceof PersistenceException &&
                t.getCause() instanceof ConstraintViolationException;

    }
}
