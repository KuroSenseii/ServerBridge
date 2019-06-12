package fr.vmarchaud.mineweb.common;

public class Command
{
    private String name;
    private Object[] args;
    
    public Object[] getArgs() {
        return this.args;
    }
    
    public void setArgs(final Object[] args) {
        this.args = args;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}
