package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 02/06/2017.
 */
public class ServidorInfoDependentePensaoAlimenticia {

    private Integer item;
    private String dependente;
    private Date inicioVigencia;
    private Date fimVigencia;
    private String tipoPensao;
    private String verba;
    private String descricaoValor;
    private List<ServidorInfoDependentePensaoAlimenticia> valores;

    public ServidorInfoDependentePensaoAlimenticia() {
        valores = new ArrayList<>();
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public String getDependente() {
        return dependente;
    }

    public void setDependente(String dependente) {
        this.dependente = dependente;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public String getTipoPensao() {
        return tipoPensao;
    }

    public void setTipoPensao(String tipoPensao) {
        this.tipoPensao = tipoPensao;
    }

    public String getVerba() {
        return verba;
    }

    public void setVerba(String verba) {
        this.verba = verba;
    }

    public String getDescricaoValor() {
        return descricaoValor;
    }

    public void setDescricaoValor(String descricaoValor) {
        this.descricaoValor = descricaoValor;
    }

    public List<ServidorInfoDependentePensaoAlimenticia> getValores() {
        return valores;
    }

    public void setValores(List<ServidorInfoDependentePensaoAlimenticia> valores) {
        this.valores = valores;
    }
}
