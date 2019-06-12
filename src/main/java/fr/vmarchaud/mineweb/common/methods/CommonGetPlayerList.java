package fr.vmarchaud.mineweb.common.methods;

import fr.vmarchaud.mineweb.common.*;

@MethodHandler
public class CommonGetPlayerList implements IMethod
{
    @Override
    public Object execute(final ICore instance, final Object... inputs) {
        return instance.getPlayers();
    }
}
