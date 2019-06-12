package fr.vmarchaud.mineweb.common.methods;

import fr.vmarchaud.mineweb.common.*;

@MethodHandler(inputs = 3, types = { String.class, String.class, Double.class })
public class CommonScheduledCommand implements IMethod
{
    @Override
    public Object execute(final ICore instance, final Object... inputs) {
        final String command = (String)inputs[0];
        final String player = (String)inputs[1];
        final Long time = (Long)inputs[2];
        final ScheduledCommand scheduled = new ScheduledCommand(command, player, time);
        instance.getCommandScheduler().getQueue().offer(scheduled);
        return true;
    }
}
