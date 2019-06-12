package fr.vmarchaud.mineweb.common.methods;

import fr.vmarchaud.mineweb.common.*;

@MethodHandler(inputs = 1, types = { String.class })
public class CommonRunCommand implements IMethod
{
    @Override
    public Object execute(final ICore instance, final Object... inputs) {
        final String command = (String)inputs[0];
        instance.runCommand(command);
        return true;
    }
}
