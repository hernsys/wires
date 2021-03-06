package com.bayesian.network.api.events;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class BayesianEvent implements Serializable {

    private static final long serialVersionUID = -695192650020463533L;
    private String template;
    
    public BayesianEvent(){
        
    }

    public BayesianEvent(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

}
