package fr.vmarchaud.mineweb.utils.regex;

import java.util.regex.*;
import java.util.*;

public interface NamedMatchResult extends MatchResult
{
    List<String> orderedGroups();
    
    Map<String, String> namedGroups();
    
    String group(final String p0);
    
    int start(final String p0);
    
    int end(final String p0);
}
