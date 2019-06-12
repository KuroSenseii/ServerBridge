package fr.vmarchaud.mineweb.utils.regex;

import java.util.*;
import java.util.regex.*;

public class NamedPattern
{
    private static final Pattern NAMED_GROUP_PATTERN;
    private Pattern pattern;
    private String namedPattern;
    private List<String> groupNames;
    
    static {
        NAMED_GROUP_PATTERN = Pattern.compile("\\(\\?<(\\w+)>|\\((?!\\?)");
    }
    
    public static NamedPattern compile(final String regex) {
        return new NamedPattern(regex, 0);
    }
    
    public static NamedPattern compile(final String regex, final int flags) {
        return new NamedPattern(regex, flags);
    }
    
    private NamedPattern(final String regex, final int i) {
        this.namedPattern = regex;
        this.pattern = buildStandardPattern(regex);
        this.groupNames = extractGroupNames(regex);
    }
    
    public int flags() {
        return this.pattern.flags();
    }
    
    public NamedMatcher matcher(final CharSequence input) {
        return new NamedMatcher(this, input);
    }
    
    Pattern pattern() {
        return this.pattern;
    }
    
    public String standardPattern() {
        return this.pattern.pattern();
    }
    
    public String namedPattern() {
        return this.namedPattern;
    }
    
    public List<String> groupNames() {
        return this.groupNames;
    }
    
    public String[] split(final CharSequence input, final int limit) {
        return this.pattern.split(input, limit);
    }
    
    public String[] split(final CharSequence input) {
        return this.pattern.split(input);
    }
    
    @Override
    public String toString() {
        return this.namedPattern;
    }
    
    static List<String> extractGroupNames(final String namedPattern) {
        final List<String> groupNames = new ArrayList<String>();
        final Matcher matcher = NamedPattern.NAMED_GROUP_PATTERN.matcher(namedPattern);
        while (matcher.find()) {
            groupNames.add(matcher.group(1));
        }
        return groupNames;
    }
    
    static Pattern buildStandardPattern(final String namedPattern) {
        return Pattern.compile(NamedPattern.NAMED_GROUP_PATTERN.matcher(namedPattern).replaceAll("("));
    }
}
