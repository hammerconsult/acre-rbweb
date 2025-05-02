package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.EnquadramentoFuncional;
import br.com.webpublico.enums.rh.InconsistenciaProgressao;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 17/09/13
 * Time: 15:05
 * To change this template use File | Settings | File Templates.
 */
public class ProgressaoAutomatica implements Serializable, Comparable {
    private Date inicioVigencia;
    private Date finalVigencia;
    private EnquadramentoFuncional antigoEnquadramentoFuncional;
    private EnquadramentoFuncional novoEnquadramentoFuncional;
    private EnquadramentoFuncional enquadramentoConsidProgAut;
    private Boolean chegouUltimaLetra;
    private Boolean temInconsistencia;
    private Boolean seraSalvo;
    private String observacao;
    private Integer diasDeTrabalhoEfetivo;
    private InconsistenciaProgressao inconsistenciaProgressao;
    private Integer quantidadeMesesProgressao;

    public ProgressaoAutomatica() {
        chegouUltimaLetra = false;
        temInconsistencia = false;
        seraSalvo = false;
    }

    public ProgressaoAutomatica(Date inicioVigencia, Date finalVigencia, EnquadramentoFuncional antigoEnquadramentoFuncional, EnquadramentoFuncional novoEnquadramentoFuncional, Boolean chegouUltimaLetra, Boolean temInconsistencia, Boolean seraSalvo, String observacao) {
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.antigoEnquadramentoFuncional = antigoEnquadramentoFuncional;
        this.novoEnquadramentoFuncional = novoEnquadramentoFuncional;
        this.chegouUltimaLetra = chegouUltimaLetra;
        this.temInconsistencia = temInconsistencia;
        this.seraSalvo = seraSalvo;
        this.observacao = observacao;
    }

    public ProgressaoAutomatica(Date inicioVigencia, Date finalVigencia, EnquadramentoFuncional antigoEnquadramentoFuncional, EnquadramentoFuncional novoEnquadramentoFuncional, EnquadramentoFuncional enquadramentoConsidProgAut, Boolean chegouUltimaLetra, Boolean temInconsistencia, Boolean seraSalvo, String observacao, Integer dias, InconsistenciaProgressao inconsistenciaProgressao, Integer quantidadeMesesProgressao) {
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.antigoEnquadramentoFuncional = antigoEnquadramentoFuncional;
        this.novoEnquadramentoFuncional = novoEnquadramentoFuncional;
        this.enquadramentoConsidProgAut = enquadramentoConsidProgAut;
        this.chegouUltimaLetra = chegouUltimaLetra;
        this.temInconsistencia = temInconsistencia;
        this.seraSalvo = seraSalvo;
        this.observacao = observacao;
        this.diasDeTrabalhoEfetivo = dias;
        this.inconsistenciaProgressao = inconsistenciaProgressao;
        this.quantidadeMesesProgressao = quantidadeMesesProgressao;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public EnquadramentoFuncional getAntigoEnquadramentoFuncional() {
        return antigoEnquadramentoFuncional;
    }

    public void setAntigoEnquadramentoFuncional(EnquadramentoFuncional antigoEnquadramentoFuncional) {
        this.antigoEnquadramentoFuncional = antigoEnquadramentoFuncional;
    }

    public EnquadramentoFuncional getNovoEnquadramentoFuncional() {
        return novoEnquadramentoFuncional;
    }

    public void setNovoEnquadramentoFuncional(EnquadramentoFuncional novoEnquadramentoFuncional) {
        this.novoEnquadramentoFuncional = novoEnquadramentoFuncional;
    }

    public Boolean getChegouUltimaLetra() {
        return chegouUltimaLetra == null ? false : chegouUltimaLetra;
    }

    public void setChegouUltimaLetra(Boolean chegouUltimaLetra) {
        this.chegouUltimaLetra = chegouUltimaLetra;
    }

    public Boolean getTemInconsistencia() {
        return temInconsistencia == null ? false : temInconsistencia;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void setTemInconsistencia(Boolean temInconsistencia) {
        this.temInconsistencia = temInconsistencia;
    }

    public Boolean getSeraSalvo() {
        return seraSalvo != null ? seraSalvo : false;
    }

    public void setSeraSalvo(Boolean seraSalvo) {
        this.seraSalvo = seraSalvo;
    }

    public InconsistenciaProgressao getInconsistenciaProgressao() {
        return inconsistenciaProgressao;
    }

    public void setInconsistenciaProgressao(InconsistenciaProgressao inconsistenciaProgressao) {
        this.inconsistenciaProgressao = inconsistenciaProgressao;
    }

    public EnquadramentoFuncional getEnquadramentoConsidProgAut() {
        return enquadramentoConsidProgAut;
    }

    public void setEnquadramentoConsidProgAut(EnquadramentoFuncional enquadramentoConsidProgAut) {
        this.enquadramentoConsidProgAut = enquadramentoConsidProgAut;
    }

    public Integer getQuantidadeMesesProgressao() {
        return quantidadeMesesProgressao;
    }

    public void setQuantidadeMesesProgressao(Integer quantidadeMesesProgressao) {
        this.quantidadeMesesProgressao = quantidadeMesesProgressao;
    }

    @Override
    public int compareTo(Object o) {
        return this.temInconsistencia.compareTo(((ProgressaoAutomatica) o).temInconsistencia);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
