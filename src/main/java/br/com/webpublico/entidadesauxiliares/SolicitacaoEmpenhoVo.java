package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OrigemSolicitacaoEmpenho;
import br.com.webpublico.enums.TipoEmpenho;

import java.math.BigDecimal;
import java.util.Date;

public class SolicitacaoEmpenhoVo {

    private ClasseCredor classeCredor;
    private BigDecimal valor;
    private UnidadeOrganizacional unidadeOrganizacional;
    private TipoEmpenho tipoEmpenho;
    private HistoricoContabil historicoContabil;
    private FonteDespesaORC fonteDespesaOrc;
    private DespesaORC despesaOrc;
    private Date dataSolicitacao;
    private Conta contaDespesaDesdobrada;
    private String complementoHistorico;
    private Pessoa fornecedor;
    private UsuarioSistema usuarioSistema;
    private Boolean gerarReserva;
    private Contrato contrato;
    private OrigemSolicitacaoEmpenho origemSolicitacaoEmpenho;
    private ReconhecimentoDivida reconhecimentoDivida;
    private OrigemSolicitacaoEmpenho origemSocilicitacao;

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public OrigemSolicitacaoEmpenho getOrigemSolicitacaoEmpenho() {
        return origemSolicitacaoEmpenho;
    }

    public void setOrigemSolicitacaoEmpenho(OrigemSolicitacaoEmpenho origemSolicitacaoEmpenho) {
        this.origemSolicitacaoEmpenho = origemSolicitacaoEmpenho;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public TipoEmpenho getTipoEmpenho() {
        return tipoEmpenho;
    }

    public void setTipoEmpenho(TipoEmpenho tipoEmpenho) {
        this.tipoEmpenho = tipoEmpenho;
    }

    public HistoricoContabil getHistoricoContabil() {
        return historicoContabil;
    }

    public void setHistoricoContabil(HistoricoContabil historicoContabil) {
        this.historicoContabil = historicoContabil;
    }

    public FonteDespesaORC getFonteDespesaOrc() {
        return fonteDespesaOrc;
    }

    public void setFonteDespesaOrc(FonteDespesaORC fonteDespesaOrc) {
        this.fonteDespesaOrc = fonteDespesaOrc;
    }

    public DespesaORC getDespesaOrc() {
        return despesaOrc;
    }

    public void setDespesaOrc(DespesaORC despesaOrc) {
        this.despesaOrc = despesaOrc;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public Conta getContaDespesaDesdobrada() {
        return contaDespesaDesdobrada;
    }

    public void setContaDespesaDesdobrada(Conta contaDespesaDesdobrada) {
        this.contaDespesaDesdobrada = contaDespesaDesdobrada;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Boolean getGerarReserva() {
        return gerarReserva;
    }

    public void setGerarReserva(Boolean gerarReserva) {
        this.gerarReserva = gerarReserva;
    }

    public OrigemSolicitacaoEmpenho getOrigemSocilicitacao() {
        return origemSocilicitacao;
    }

    public void setOrigemSocilicitacao(OrigemSolicitacaoEmpenho origemSocilicitacao) {
        this.origemSocilicitacao = origemSocilicitacao;
    }

    public ReconhecimentoDivida getReconhecimentoDivida() {
        return reconhecimentoDivida;
    }

    public void setReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        this.reconhecimentoDivida = reconhecimentoDivida;
    }
}


