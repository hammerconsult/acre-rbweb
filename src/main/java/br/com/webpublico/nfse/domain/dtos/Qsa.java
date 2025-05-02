package br.com.webpublico.nfse.domain.dtos;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "qual",
    "nome"
})
public class Qsa implements Serializable {

    @JsonProperty("qual")
    private String qual;
    @JsonProperty("nome")
    private String nome;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("qual")
    public String getQual() {
        return qual;
    }

    @JsonProperty("qual")
    public void setQual(String qual) {
        this.qual = qual;
    }

    @JsonProperty("nome")
    public String getNome() {
        return nome;
    }

    @JsonProperty("nome")
    public void setNome(String nome) {
        this.nome = nome;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder extra = new StringBuilder();

        extra.append(qual).append(" - ").append(nome).append("  ");

        for (String s : additionalProperties.keySet()) {
            extra.append(s).append(": ").append(additionalProperties.get(s)).append("; ");
        }

        return extra.toString();
    }

}
