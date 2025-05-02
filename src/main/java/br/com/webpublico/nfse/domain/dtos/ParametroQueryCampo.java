package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParametroQueryCampo implements Serializable {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private String campo;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Operador operador;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String valorString;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Integer valorInteger;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Date valorDate;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String valorDateString;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Long valorLong;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Boolean valorBoolean;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private List<String> valorListString;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Object valor;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String livre;

    public ParametroQueryCampo() {
    }

    public ParametroQueryCampo(String campo, Operador operador, String valorString) {
        this.campo = campo;
        this.operador = operador;
        this.valorString = valorString;
    }

    public ParametroQueryCampo(String campo, Operador operador, Integer valorInteger) {
        this.campo = campo;
        this.operador = operador;
        this.valorInteger = valorInteger;
    }

    public ParametroQueryCampo(String campo, Operador operador, Date valorDate) {
        this.campo = campo;
        this.operador = operador;
        this.valorDate = valorDate;
    }

    public ParametroQueryCampo(String campo, Operador operador, Long valorLong) {
        this.campo = campo;
        this.operador = operador;
        this.valorLong = valorLong;
    }

    public ParametroQueryCampo(String campo, Operador operador, List<String> valorListString) {
        this.campo = campo;
        this.operador = operador;
        this.valorListString = valorListString;
    }

    public ParametroQueryCampo(String campo, Operador operador, Boolean valorBoolean) {
        this.campo = campo;
        this.operador = operador;
        this.valorBoolean = valorBoolean;
    }

    public ParametroQueryCampo(String livre) {
        this.livre = livre;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public Operador getOperador() {
        return operador;
    }

    public void setOperador(Operador operador) {
        this.operador = operador;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public String getValorString() {
        return valorString;
    }

    public void setValorString(String valorString) {
        this.valorString = valorString;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public Integer getValorInteger() {
        return valorInteger;
    }

    public void setValorInteger(Integer valorInteger) {
        this.valorInteger = valorInteger;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public Date getValorDate() {
        return valorDate;
    }

    public void setValorDate(Date valorDate) {
        this.valorDate = valorDate;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public String getValorDateString() {
        return valorDateString;
    }

    public void setValorDateString(String valorDateString) {
        this.valorDateString = valorDateString;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public Long getValorLong() {
        return valorLong;
    }

    public void setValorLong(Long valorLong) {
        this.valorLong = valorLong;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public List<String> getValorListString() {
        return valorListString;
    }

    public void setValorListString(List<String> valorListString) {
        this.valorListString = valorListString;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public Boolean getValorBoolean() {
        return valorBoolean;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public void setValorBoolean(Boolean valorBoolean) {
        this.valorBoolean = valorBoolean;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public String getLivre() {
        return livre;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public void setLivre(String livre) {
        this.livre = livre;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public Object getValor() {
        return valorString != null ? valorString :
            valorDateString != null ? DateUtils.getData(valorDateString, "dd/MM/yyyy") :
                valorInteger != null ? valorInteger :
                    valorDate != null ? valorDate :
                        valorLong != null ? valorLong :
                            valorBoolean != null ? valorBoolean :
                                valorListString != null ? valorListString : null;
    }
}
