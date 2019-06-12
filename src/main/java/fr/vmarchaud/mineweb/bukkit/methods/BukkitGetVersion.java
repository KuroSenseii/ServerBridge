package fr.vmarchaud.mineweb.bukkit.methods;

import fr.vmarchaud.mineweb.common.*;
import org.bukkit.*;

@MethodHandler
public class BukkitGetVersion implements IMethod
{
    @Override
    public Object execute(final ICore instance, final Object... inputs) {
        return ((Server)instance.getGameServer()).getBukkitVersion();
    }
}
