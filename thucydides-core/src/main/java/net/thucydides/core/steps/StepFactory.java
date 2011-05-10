package net.thucydides.core.steps;

import net.sf.cglib.proxy.Enhancer;
import net.thucydides.core.pages.Pages;

import java.util.ArrayList;
import java.util.List;

/**
 * Produces an instance of a set of requirement steps for use in the acceptance tests.
 * Requirement steps navigate through pages using a WebDriver driver.
 *
 */
public class StepFactory {

    private final Pages pages;
    
    private final List<StepListener> listeners = new ArrayList<StepListener>();
    
    private final List<ScenarioSteps> managedSteps = new ArrayList<ScenarioSteps>();

    /**
     * Create a new step factory.
     * All step factories need a Pages object, which is passed to ScenarioSteps objects when they
     * are created.
     */
    public StepFactory(final Pages pages) {
        this.pages = pages;
    }
    
    public void addListener(final StepListener listener) {
        listeners.add(listener);
    }
    
    private static final Class<?>[] CONSTRUCTOR_ARG_TYPES = {Pages.class};
    
    /**
     * Returns a new ScenarioSteps instance, of the specified type.
     * This is actually a proxy that allows reporting and screenshots to
     * be performed at each step.
     */
    public ScenarioSteps newSteps(final Class<? extends ScenarioSteps> scenarioStepsClass,
                                  final boolean throwOriginalException) {
        Enhancer e = new Enhancer();
        e.setSuperclass(scenarioStepsClass);
        StepInterceptor stepInterceptor = new StepInterceptor(scenarioStepsClass, listeners, throwOriginalException);
        e.setCallback(stepInterceptor);
        
        Object[] arguments = new Object[1];
        arguments[0] = pages;
        ScenarioSteps steps = (ScenarioSteps) e.create(CONSTRUCTOR_ARG_TYPES, arguments);

        instanciateAnyNestedStepLibrariesIn(steps, scenarioStepsClass);

        managedSteps.add(steps);
        
        return steps;

    }

    public ScenarioSteps newSteps(final Class<? extends ScenarioSteps> scenarioStepsClass) {
        return newSteps(scenarioStepsClass, false);
    }

    private void instanciateAnyNestedStepLibrariesIn(final ScenarioSteps steps,
                                                     final Class<? extends ScenarioSteps> scenarioStepsClass){
        StepAnnotations.injectNestedScenarioStepsInto(steps, this, scenarioStepsClass);
    }

    public void notifyStepFinished() {
        System.out.println("FINISH STEPS");
        for(ScenarioSteps step : managedSteps) {
            step.done();
        }
    }


}