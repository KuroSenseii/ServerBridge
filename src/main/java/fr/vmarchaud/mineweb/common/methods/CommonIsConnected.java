package fr.vmarchaud.mineweb.common.methods;

import fr.vmarchaud.mineweb.common.*;

@MethodHandler(inputs = 1, types = { String.class })
public class CommonIsConnected implements IMethod
{
    @Override
    public Object execute(final ICore instance, final Object... inputs) {
        final String name = (String)inputs[0];
        return instance.getPlayers().contains(name);
    }
}
