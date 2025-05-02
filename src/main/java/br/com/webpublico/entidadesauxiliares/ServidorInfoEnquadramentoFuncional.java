package br.com.webpublico.entidadesauxiliares;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 02/06/2017.
 */
public class ServidorInfoEnquadramentoFuncional {

    private Integer item;
    private Date inicioVigencia;
    private Date fimVigencia;
    private Date dataCadastro;
    private String descricaoValor;
    private String categoria;
    private String progressao;
    private String inicioVigenciaEnq;
    private String fimVigenciaEnq;
    private String vencimentoBase;
    private List<ServidorInfoEnquadramentoFuncional> valores;

    public ServidorInfoEnquadramentoFuncional() {
        valores = new ArrayList<>();
    }

    public String getDescricaoValor() {
        return descricaoValor;
    }

    public void setDescricaoValor(String descricaoValor) {
        this.descricaoValor = descricaoValor;
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

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getProgressao() {
        return progressao;
    }

    public void setProgressao(String progressao) {
        this.progressao = progressao;
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

    public List<ServidorInfoEnquadramentoFuncional> getValores() {
        return valores;
    }

    public void setValores(List<ServidorInfoEnquadramentoFuncional> valores) {
        this.valores = valores;
    }
}
