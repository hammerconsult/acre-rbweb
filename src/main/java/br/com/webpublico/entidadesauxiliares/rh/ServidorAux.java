package br.com.webpublico.entidadesauxiliares.rh;

import com.google.common.base.Objects;

import java.math.BigDecimal;

public class ServidorAux {
    private String matricula;
    private String nome;
    private String codigoOrgao;
    private String codigoEvento;
    private String tipoEventoFP;
    private BigDecimal valorItem;
    private Integer mes;

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public BigDecimal getValorItem() {
        return valorItem;
    }

    public void setValorItem(BigDecimal valorItem) {
        this.valorItem = valorItem;
    }

    public String getTipoEventoFP() {
        return tipoEventoFP;
    }

    public void setTipoEventoFP(String tipoEventoFP) {
        this.tipoEventoFP = tipoEventoFP;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(!(obj instanceof ServidorAux)) {
            return false;
        }
        ServidorAux servidor = (ServidorAux) obj;
        return Objects.equal(getMatricula(), servidor.getMatricula());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getMatricula());
    }

}
