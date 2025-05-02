package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.enums.TipoSolicitacao;
import br.com.webpublico.enums.TipoStatusSolicitacao;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * Created by tharlyson on 12/11/19.
 */
public class FiltroRelatorioAdministrativo {

    private Contrato contrato;
    private Long idObjeto;
    private Date dataReferencia;
    private Date dataInicial;
    private Date dataFinal;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private Integer numero;
    private String descricao;
    private TipoSolicitacao tipoSolicitacao;
    private ModalidadeLicitacao modalidadeLicitacao;
    private TipoStatusSolicitacao statusSolicitacao;
    private UnidadeGestora unidadeGestora;
    private Pessoa pessoa;
    private Empenho empenho;
    private String filtros;
    private Boolean quebraPagina;
    private List<HierarquiaOrganizacional> unidades;

    public FiltroRelatorioAdministrativo() {
        dataInicial = new Date();
        dataFinal = new Date();
        dataReferencia = new Date();
        unidades = Lists.newArrayList();
        quebraPagina = false;
        hierarquiaAdministrativa = null;
        hierarquiaOrcamentaria = null;
        pessoa = null;
        filtros = "";
        empenho = null;
        contrato = null;
    }

    public List<HierarquiaOrganizacional> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<HierarquiaOrganizacional> unidades) {
        this.unidades = unidades;
    }

    public Long getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(Long idObjeto) {
        this.idObjeto = idObjeto;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public UnidadeGestora getUnidadeGestora() {

        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoSolicitacao getTipoSolicitacao() {
        return tipoSolicitacao;
    }

    public void setTipoSolicitacao(TipoSolicitacao tipoSolicitacao) {
        this.tipoSolicitacao = tipoSolicitacao;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public ModalidadeLicitacao getModalidadeLicitacao() {
        return modalidadeLicitacao;
    }

    public void setModalidadeLicitacao(ModalidadeLicitacao modalidadeLicitacao) {
        this.modalidadeLicitacao = modalidadeLicitacao;
    }

    public TipoStatusSolicitacao getStatusSolicitacao() {
        return statusSolicitacao;
    }

    public void setStatusSolicitacao(TipoStatusSolicitacao statusSolicitacao) {
        this.statusSolicitacao = statusSolicitacao;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public Boolean getQuebraPagina() {
        return quebraPagina;
    }

    public void setQuebraPagina(Boolean quebraPagina) {
        this.quebraPagina = quebraPagina;
    }
}
