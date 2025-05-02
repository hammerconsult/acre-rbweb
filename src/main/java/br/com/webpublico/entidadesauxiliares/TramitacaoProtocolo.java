package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.controle.ProtocoloControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoProcessoTramite;
import org.primefaces.model.TreeNode;

import java.util.Date;
import java.util.List;

public class TramitacaoProtocolo {

    private Processo processo;
    private Tramite novoTramite;
    private Tramite selecionado;
    private SubAssunto subAssunto;
    private TreeNode root;
    private TreeNode noSelecionado;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private String conteudoEmail;
    private Boolean encaminhamentoMultiplo;
    private List<Tramite> tramites;
    private Integer indice;
    private TreeNode rootOrc;
    private TreeNode selectedNode;
    private List<UnidadeOrganizacional> unidadesEncaminhamentosMultiplos;
    private List<DocumentoProcesso> documentosProcesso;
    private TipoProcessoTramite tipoEncaminhamento;
    private String destinoExterno;
    private List<UnidadeOrganizacional> unidadeSemUsuarioCadastrado;
    private int quantidadeTotalDeTramitesRecebidos = 0;
    private List<VoTramite> tramitesUnidadesExtintas;
    private int inicioListaTramitesUnidadesExtintas = 0;
    private String numeroPesquisaProtocolo;
    private String anoPesquisaProtocolo;
    private String solicitantePesquisaProtocolo;
    private boolean movimentar = false;
    private List<VoTramite> tramitesSelecionados;
    private String motivoIncorporacao;
    private String motivoTramiteSelecionado;
    private String observacaoTramiteSelecionado;
    private SituacaoTramite situacaoAnteriorDoTramite;
    private ProtocoloControlador.ArvoreTramite arvoreTramite;
    private String responsavelAll;
    private Date dataAceite;
    private Date horaAceite;
    private ProcessoRota processoRota;

    public TramitacaoProtocolo() {

    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public Tramite getNovoTramite() {
        return novoTramite;
    }

    public void setNovoTramite(Tramite novoTramite) {
        this.novoTramite = novoTramite;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getNoSelecionado() {
        return noSelecionado;
    }

    public void setNoSelecionado(TreeNode noSelecionado) {
        this.noSelecionado = noSelecionado;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public String getConteudoEmail() {
        return conteudoEmail;
    }

    public void setConteudoEmail(String conteudoEmail) {
        this.conteudoEmail = conteudoEmail;
    }

    public Boolean getEncaminhamentoMultiplo() {
        return encaminhamentoMultiplo;
    }

    public void setEncaminhamentoMultiplo(Boolean encaminhamentoMultiplo) {
        this.encaminhamentoMultiplo = encaminhamentoMultiplo;
    }

    public List<Tramite> getTramites() {
        return tramites;
    }

    public void setTramites(List<Tramite> tramites) {
        this.tramites = tramites;
    }

    public Integer getIndice() {
        return indice;
    }

    public void setIndice(Integer indice) {
        this.indice = indice;
    }

    public TreeNode getRootOrc() {
        return rootOrc;
    }

    public void setRootOrc(TreeNode rootOrc) {
        this.rootOrc = rootOrc;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public List<UnidadeOrganizacional> getUnidadesEncaminhamentosMultiplos() {
        return unidadesEncaminhamentosMultiplos;
    }

    public void setUnidadesEncaminhamentosMultiplos(List<UnidadeOrganizacional> unidadesEncaminhamentosMultiplos) {
        this.unidadesEncaminhamentosMultiplos = unidadesEncaminhamentosMultiplos;
    }

    public List<DocumentoProcesso> getDocumentosProcesso() {
        return documentosProcesso;
    }

    public void setDocumentosProcesso(List<DocumentoProcesso> documentosProcesso) {
        this.documentosProcesso = documentosProcesso;
    }

    public TipoProcessoTramite getTipoEncaminhamento() {
        return tipoEncaminhamento;
    }

    public void setTipoEncaminhamento(TipoProcessoTramite tipoEncaminhamento) {
        this.tipoEncaminhamento = tipoEncaminhamento;
    }

    public String getDestinoExterno() {
        return destinoExterno;
    }

    public void setDestinoExterno(String destinoExterno) {
        this.destinoExterno = destinoExterno;
    }

    public List<UnidadeOrganizacional> getUnidadeSemUsuarioCadastrado() {
        return unidadeSemUsuarioCadastrado;
    }

    public void setUnidadeSemUsuarioCadastrado(List<UnidadeOrganizacional> unidadeSemUsuarioCadastrado) {
        this.unidadeSemUsuarioCadastrado = unidadeSemUsuarioCadastrado;
    }

    public int getQuantidadeTotalDeTramitesRecebidos() {
        return quantidadeTotalDeTramitesRecebidos;
    }

    public void setQuantidadeTotalDeTramitesRecebidos(int quantidadeTotalDeTramitesRecebidos) {
        this.quantidadeTotalDeTramitesRecebidos = quantidadeTotalDeTramitesRecebidos;
    }

    public List<VoTramite> getTramitesUnidadesExtintas() {
        return tramitesUnidadesExtintas;
    }

    public void setTramitesUnidadesExtintas(List<VoTramite> tramitesUnidadesExtintas) {
        this.tramitesUnidadesExtintas = tramitesUnidadesExtintas;
    }

    public int getInicioListaTramitesUnidadesExtintas() {
        return inicioListaTramitesUnidadesExtintas;
    }

    public void setInicioListaTramitesUnidadesExtintas(int inicioListaTramitesUnidadesExtintas) {
        this.inicioListaTramitesUnidadesExtintas = inicioListaTramitesUnidadesExtintas;
    }

    public String getNumeroPesquisaProtocolo() {
        return numeroPesquisaProtocolo;
    }

    public void setNumeroPesquisaProtocolo(String numeroPesquisaProtocolo) {
        this.numeroPesquisaProtocolo = numeroPesquisaProtocolo;
    }

    public String getAnoPesquisaProtocolo() {
        return anoPesquisaProtocolo;
    }

    public void setAnoPesquisaProtocolo(String anoPesquisaProtocolo) {
        this.anoPesquisaProtocolo = anoPesquisaProtocolo;
    }

    public String getSolicitantePesquisaProtocolo() {
        return solicitantePesquisaProtocolo;
    }

    public void setSolicitantePesquisaProtocolo(String solicitantePesquisaProtocolo) {
        this.solicitantePesquisaProtocolo = solicitantePesquisaProtocolo;
    }

    public boolean isMovimentar() {
        return movimentar;
    }

    public void setMovimentar(boolean movimentar) {
        this.movimentar = movimentar;
    }

    public List<VoTramite> getTramitesSelecionados() {
        return tramitesSelecionados;
    }

    public void setTramitesSelecionados(List<VoTramite> tramitesSelecionados) {
        this.tramitesSelecionados = tramitesSelecionados;
    }

    public String getMotivoIncorporacao() {
        return motivoIncorporacao;
    }

    public void setMotivoIncorporacao(String motivoIncorporacao) {
        this.motivoIncorporacao = motivoIncorporacao;
    }

    public String getMotivoTramiteSelecionado() {
        return motivoTramiteSelecionado;
    }

    public void setMotivoTramiteSelecionado(String motivoTramiteSelecionado) {
        this.motivoTramiteSelecionado = motivoTramiteSelecionado;
    }

    public String getObservacaoTramiteSelecionado() {
        return observacaoTramiteSelecionado;
    }

    public void setObservacaoTramiteSelecionado(String observacaoTramiteSelecionado) {
        this.observacaoTramiteSelecionado = observacaoTramiteSelecionado;
    }

    public SituacaoTramite getSituacaoAnteriorDoTramite() {
        return situacaoAnteriorDoTramite;
    }

    public void setSituacaoAnteriorDoTramite(SituacaoTramite situacaoAnteriorDoTramite) {
        this.situacaoAnteriorDoTramite = situacaoAnteriorDoTramite;
    }

    public ProtocoloControlador.ArvoreTramite getArvoreTramite() {
        return arvoreTramite;
    }

    public void setArvoreTramite(ProtocoloControlador.ArvoreTramite arvoreTramite) {
        this.arvoreTramite = arvoreTramite;
    }

    public String getResponsavelAll() {
        return responsavelAll;
    }

    public void setResponsavelAll(String responsavelAll) {
        this.responsavelAll = responsavelAll;
    }

    public Date getDataAceite() {
        return dataAceite;
    }

    public void setDataAceite(Date dataAceite) {
        this.dataAceite = dataAceite;
    }

    public Date getHoraAceite() {
        return horaAceite;
    }

    public void setHoraAceite(Date horaAceite) {
        this.horaAceite = horaAceite;
    }

    public Tramite getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Tramite selecionado) {
        this.selecionado = selecionado;
    }

    public SubAssunto getSubAssunto() {
        return subAssunto;
    }

    public void setSubAssunto(SubAssunto subAssunto) {
        this.subAssunto = subAssunto;
    }

    public ProcessoRota getProcessoRota() {
        return processoRota;
    }

    public void setProcessoRota(ProcessoRota processoRota) {
        this.processoRota = processoRota;
    }
}
