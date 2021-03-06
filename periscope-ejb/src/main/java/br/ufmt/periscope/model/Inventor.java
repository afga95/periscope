package br.ufmt.periscope.model;

import com.github.jmkgreen.morphia.annotations.Embedded;
import java.io.Serializable;

@Embedded
public class Inventor implements Serializable {

    private String name;
    @Embedded
    private Country country;
    private Boolean harmonized = false;
    
    private Integer documentCount = 0;

    public Inventor() {
    }

    public Inventor(String name) {
        this.name = name;
    }
    
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Boolean getHarmonized() {
        return harmonized;
    }

    public void setHarmonized(Boolean harmonized) {
        this.harmonized = harmonized;
    }

    public Integer getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(Integer documentCount) {
        this.documentCount = documentCount;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
