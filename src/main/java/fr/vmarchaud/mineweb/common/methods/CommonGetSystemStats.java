package fr.vmarchaud.mineweb.common.methods;

import fr.vmarchaud.mineweb.common.*;
import com.google.gson.*;

@MethodHandler
public class CommonGetSystemStats implements IMethod
{
    @Override
    public Object execute(final ICore instance, final Object... inputs) {
        final JsonObject data = new JsonObject();
        data.addProperty("ram", String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000L));
        return data;
    }
}
