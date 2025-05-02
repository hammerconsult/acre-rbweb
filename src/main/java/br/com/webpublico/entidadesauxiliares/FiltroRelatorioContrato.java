package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoAquisicaoContrato;
import br.com.webpublico.enums.TipoContrato;
import br.com.webpublico.enums.TipoObjetoCompra;

import java.util.Date;

public class FiltroRelatorioContrato {
    private HierarquiaOrganizacional unidadeAdministrativa;
    private TipoContrato tipoContrato;
    private TipoAquisicaoContrato tipoAquisicaoContrato;
    private TipoObjetoCompra tipoObjetoCompra;
    private String palavra;
    private Licitacao licitacao;
    private DispensaDeLicitacao dispensaLicitacao;
    private RegistroSolicitacaoMaterialExterno registroPrecoExterno;
    private Contrato contrato;
    private Date inicioVigencia;
    private Date fimVigencia;
    private Date inicioExecucao;
    private Date fimExecucao;
    private ContratoFP responsavelUnidade;
    private Boolean selecionarTodos;

    public HierarquiaOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(HierarquiaOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public TipoContrato getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(TipoContrato tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public TipoAquisicaoContrato getTipoAquisicaoContrato() {
        return tipoAquisicaoContrato;
    }

    public void setTipoAquisicaoContrato(TipoAquisicaoContrato tipoAquisicaoContrato) {
        this.tipoAquisicaoContrato = tipoAquisicaoContrato;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
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

    public Date getInicioExecucao() {
        return inicioExecucao;
    }

    public void setInicioExecucao(Date inicioExecucao) {
        this.inicioExecucao = inicioExecucao;
    }

    public Date getFimExecucao() {
        return fimExecucao;
    }

    public void setFimExecucao(Date fimExecucao) {
        this.fimExecucao = fimExecucao;
    }

    public ContratoFP getResponsavelUnidade() {
        return responsavelUnidade;
    }

    public void setResponsavelUnidade(ContratoFP responsavelUnidade) {
        this.responsavelUnidade = responsavelUnidade;
    }

    public DispensaDeLicitacao getDispensaLicitacao() {
        return dispensaLicitacao;
    }

    public void setDispensaLicitacao(DispensaDeLicitacao dispensaLicitacao) {
        this.dispensaLicitacao = dispensaLicitacao;
    }

    public RegistroSolicitacaoMaterialExterno getRegistroPrecoExterno() {
        return registroPrecoExterno;
    }

    public void setRegistroPrecoExterno(RegistroSolicitacaoMaterialExterno registroPrecoExterno) {
        this.registroPrecoExterno = registroPrecoExterno;
    }

    public Boolean getSelecionarTodos() {
        return selecionarTodos;
    }

    public void setSelecionarTodos(Boolean selecionarTodos) {
        this.selecionarTodos = selecionarTodos;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }
}
