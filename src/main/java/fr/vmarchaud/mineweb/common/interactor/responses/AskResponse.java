package fr.vmarchaud.mineweb.common.interactor.responses;

public class AskResponse
{
    private String signed;
    private String iv;
    
    public String getSigned() {
        return this.signed;
    }
    
    public void setSigned(final String signed) {
        this.signed = signed;
    }
    
    public String getIv() {
        return this.iv;
    }
    
    public void setIv(final String iv) {
        this.iv = iv;
    }
}
