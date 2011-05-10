package net.thucydides.core.model;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SUCCESS;

import java.util.List;

/**
 * A list of test results, used to determine the overall test result.
 *
 */
public class TestResultList {

    private final List<TestResult> testResults;
    
    public TestResultList(final List<TestResult> testResults) {
        this.testResults = testResults;
    }

    public boolean isEmpty() {
        return testResults.isEmpty();
    }

    public TestResult getOverallResult() {
        if (testResults.contains(FAILURE)) {
            return FAILURE;
        }

        if (testResults.contains(PENDING)) {
            return PENDING;
        }

        if (containsOnly(IGNORED)) {
            return IGNORED;
        }

        return SUCCESS;
    }

    private boolean containsOnly(final TestResult value) {
        if (testResults.isEmpty()) {
            return false;
        } else {
            return containsOnlyType(value);
        }
    }

    private boolean containsOnlyType(TestResult value) {
        for (TestResult result : testResults) {
            if (result != value) {
                return false;
            }
        }
        return true;
    }

}
