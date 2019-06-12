package fr.vmarchaud.mineweb.common.interactor.requests;

import java.util.*;

public class HandshakeRequest
{
    private String domain;
    private String secretKey;
    private transient String id;
    
    public HandshakeRequest() {
        this.id = UUID.randomUUID().toString().substring(0, 8);
    }
    
    public boolean isValid() {
        return this.getDomain() != null && this.getSecretKey() != null;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getSecretKey() {
        return this.secretKey;
    }
    
    public void setSecretKey(final String secretKey) {
        this.secretKey = secretKey;
    }
    
    public String getDomain() {
        return this.domain;
    }
    
    public void setDomain(final String domain) {
        this.domain = domain;
    }
}
