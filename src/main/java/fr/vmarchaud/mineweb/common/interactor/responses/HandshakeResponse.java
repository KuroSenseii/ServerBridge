package fr.vmarchaud.mineweb.common.interactor.responses;

public class HandshakeResponse
{
    private boolean status;
    private String msg;
    
    public String getMsg() {
        return this.msg;
    }
    
    public void setMsg(final String msg) {
        this.msg = msg;
    }
    
    public boolean isStatus() {
        return this.status;
    }
    
    public void setStatus(final boolean status) {
        this.status = status;
    }
}
