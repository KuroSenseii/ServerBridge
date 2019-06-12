package fr.vmarchaud.mineweb.utils.regex;

import java.util.regex.*;
import java.util.*;

public class NamedMatcher implements NamedMatchResult
{
    private Matcher matcher;
    private NamedPattern parentPattern;
    
    NamedMatcher() {
    }
    
    NamedMatcher(final NamedPattern parentPattern, final MatchResult matcher) {
        this.parentPattern = parentPattern;
        this.matcher = (Matcher)matcher;
    }
    
    NamedMatcher(final NamedPattern parentPattern, final CharSequence input) {
        this.parentPattern = parentPattern;
        this.matcher = parentPattern.pattern().matcher(input);
    }
    
    public Pattern standardPattern() {
        return this.matcher.pattern();
    }
    
    public NamedPattern namedPattern() {
        return this.parentPattern;
    }
    
    public NamedMatcher usePattern(final NamedPattern newPattern) {
        this.parentPattern = newPattern;
        this.matcher.usePattern(newPattern.pattern());
        return this;
    }
    
    public NamedMatcher reset() {
        this.matcher.reset();
        return this;
    }
    
    public NamedMatcher reset(final CharSequence input) {
        this.matcher.reset(input);
        return this;
    }
    
    public boolean matches() {
        return this.matcher.matches();
    }
    
    public NamedMatchResult toMatchResult() {
        return new NamedMatcher(this.parentPattern, this.matcher.toMatchResult());
    }
    
    public boolean find() {
        return this.matcher.find();
    }
    
    public boolean find(final int start) {
        return this.matcher.find(start);
    }
    
    public boolean lookingAt() {
        return this.matcher.lookingAt();
    }
    
    public NamedMatcher appendReplacement(final StringBuffer sb, final String replacement) {
        this.matcher.appendReplacement(sb, replacement);
        return this;
    }
    
    public StringBuffer appendTail(final StringBuffer sb) {
        return this.matcher.appendTail(sb);
    }
    
    @Override
    public String group() {
        return this.matcher.group();
    }
    
    @Override
    public String group(final int group) {
        return this.matcher.group(group);
    }
    
    @Override
    public int groupCount() {
        return this.matcher.groupCount();
    }
    
    @Override
    public List<String> orderedGroups() {
        final ArrayList<String> groups = new ArrayList<String>();
        for (int i = 1; i <= this.groupCount(); ++i) {
            groups.add(this.group(i));
        }
        return groups;
    }
    
    @Override
    public String group(final String groupName) {
        return this.group(this.groupIndex(groupName));
    }
    
    @Override
    public Map<String, String> namedGroups() {
        final Map<String, String> result = new LinkedHashMap<String, String>();
        for (int i = 1; i <= this.groupCount(); ++i) {
            final String groupName = this.parentPattern.groupNames().get(i - 1);
            final String groupValue = this.matcher.group(i);
            result.put(groupName, groupValue);
        }
        return result;
    }
    
    private int groupIndex(final String groupName) {
        return this.parentPattern.groupNames().indexOf(groupName) + 1;
    }
    
    @Override
    public int start() {
        return this.matcher.start();
    }
    
    @Override
    public int start(final int group) {
        return this.matcher.start(group);
    }
    
    @Override
    public int start(final String groupName) {
        return this.start(this.groupIndex(groupName));
    }
    
    @Override
    public int end() {
        return this.matcher.end();
    }
    
    @Override
    public int end(final int group) {
        return this.matcher.end(group);
    }
    
    @Override
    public int end(final String groupName) {
        return this.end(this.groupIndex(groupName));
    }
    
    public NamedMatcher region(final int start, final int end) {
        this.matcher.region(start, end);
        return this;
    }
    
    public int regionEnd() {
        return this.matcher.regionEnd();
    }
    
    public int regionStart() {
        return this.matcher.regionStart();
    }
    
    public boolean hitEnd() {
        return this.matcher.hitEnd();
    }
    
    public boolean requireEnd() {
        return this.matcher.requireEnd();
    }
    
    public boolean hasAnchoringBounds() {
        return this.matcher.hasAnchoringBounds();
    }
    
    public boolean hasTransparentBounds() {
        return this.matcher.hasTransparentBounds();
    }
    
    public String replaceAll(final String replacement) {
        return this.matcher.replaceAll(replacement);
    }
    
    public String replaceFirst(final String replacement) {
        return this.matcher.replaceFirst(replacement);
    }
    
    public NamedMatcher useAnchoringBounds(final boolean b) {
        this.matcher.useAnchoringBounds(b);
        return this;
    }
    
    public NamedMatcher useTransparentBounds(final boolean b) {
        this.matcher.useTransparentBounds(b);
        return this;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.matcher.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return this.matcher.hashCode();
    }
    
    @Override
    public String toString() {
        return this.matcher.toString();
    }
}
