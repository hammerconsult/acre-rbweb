package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 02/06/2017.
 */
public class ServidorInfoFuncaoGratificada {

    private Integer item;
    private Date inicioVigencia;
    private Date fimVigencia;
    private Date dataRegistro;
    private String descricao;
    private String enquadramento;
    private String inicioVigenciaEnq;
    private String fimVigenciaEnq;
    private String vencimentoBase;
    private List<ServidorInfoFuncaoGratificada> enquadramentoFGs;

    public ServidorInfoFuncaoGratificada() {
        enquadramentoFGs = new ArrayList<>();
    }

    public String getEnquadramento() {
        return enquadramento;
    }

    public void setEnquadramento(String enquadramento) {
        this.enquadramento = enquadramento;
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
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

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getInicioVigenciaEnq() {
        return inicioVigenciaEnq;
    }

    public void setInicioVigenciaEnq(String inicioVigenciaEnq) {
        this.inicioVigenciaEnq = inicioVigenciaEnq;
    }

    public String getFimVigenciaEnq() {
        return fimVigenciaEnq;
    }

    public void setFimVigenciaEnq(String fimVigenciaEnq) {
        this.fimVigenciaEnq = fimVigenciaEnq;
    }

    public String getVencimentoBase() {
        return vencimentoBase;
    }

    public void setVencimentoBase(String vencimentoBase) {
        this.vencimentoBase = vencimentoBase;
    }

    public List<ServidorInfoFuncaoGratificada> getEnquadramentoFGs() {
        return enquadramentoFGs;
    }

    public void setEnquadramentoFGs(List<ServidorInfoFuncaoGratificada> enquadramentoFGs) {
        this.enquadramentoFGs = enquadramentoFGs;
    }
}
