package fr.vmarchaud.mineweb.bukkit.methods;

import fr.vmarchaud.mineweb.common.*;
import org.bukkit.*;
import com.annimon.stream.function.*;
import com.annimon.stream.*;
import java.util.*;

@MethodHandler
public class BukkitGetBannedPlayers implements IMethod
{
    @Override
    public Object execute(final ICore instance, final Object... inputs) {
        final Set<OfflinePlayer> wlp = (Set<OfflinePlayer>)((Server)instance.getGameServer()).getBannedPlayers();
        return Stream.of((Iterable<?>)wlp).map((Function<? super Object, ?>)OfflinePlayer::getName).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList());
    }
}
