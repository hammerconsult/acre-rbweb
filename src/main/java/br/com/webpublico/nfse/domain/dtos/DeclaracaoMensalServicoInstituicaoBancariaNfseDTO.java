package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.SituacaoDeclaracaoMensalServicoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.TipoDeclaracaoMensalServicoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.TipoMovimentoMensalNfseDTO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by rodolfo on 09/04/18.
 */
public class DeclaracaoMensalServicoInstituicaoBancariaNfseDTO implements NfseDTO {

    private Long id;
    private Integer codigo;
    private Integer mes;
    private Integer exercicio;
    private Date competencia;
    private SituacaoDeclaracaoMensalServicoNfseDTO situacao;
    private TipoDeclaracaoMensalServicoNfseDTO tipo;
    private TipoMovimentoMensalNfseDTO tipoMovimento;
    private List<DeclaracaoContaBancariaNfseDTO> contas;
    private PrestadorServicoNfseDTO prestador;
    private String situacaoDebito;
    private Integer qtdNotas;
    private BigDecimal totalServicos;
    private BigDecimal totalIss;
    private BigDecimal totalJuros;
    private BigDecimal totalMulta;
    private BigDecimal totalCorrecao;
    private Date abertura;
    private Date encerramento;


    public DeclaracaoMensalServicoInstituicaoBancariaNfseDTO() {
        this.totalServicos = BigDecimal.ZERO;
        this.totalIss = BigDecimal.ZERO;
        this.totalJuros = BigDecimal.ZERO;
        this.totalMulta = BigDecimal.ZERO;
        this.totalCorrecao = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Date getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Date competencia) {
        this.competencia = competencia;
    }

    public TipoDeclaracaoMensalServicoNfseDTO getTipo() {
        return tipo;
    }

    public void setTipo(TipoDeclaracaoMensalServicoNfseDTO tipo) {
        this.tipo = tipo;
    }

    public List<DeclaracaoContaBancariaNfseDTO> getContas() {
        return contas;
    }

    public void setContas(List<DeclaracaoContaBancariaNfseDTO> contas) {
        this.contas = contas;
    }

    public PrestadorServicoNfseDTO getPrestador() {
        return prestador;
    }

    public void setPrestador(PrestadorServicoNfseDTO prestador) {
        this.prestador = prestador;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public SituacaoDeclaracaoMensalServicoNfseDTO getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoDeclaracaoMensalServicoNfseDTO situacao) {
        this.situacao = situacao;
    }

    public String getSituacaoDebito() {
        return situacaoDebito;
    }

    public void setSituacaoDebito(String situacaoDebito) {
        this.situacaoDebito = situacaoDebito;
    }

    public Integer getQtdNotas() {
        return qtdNotas;
    }

    public void setQtdNotas(Integer qtdNotas) {
        this.qtdNotas = qtdNotas;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos != null ? totalServicos : BigDecimal.ZERO;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getTotalIss() {
        return totalIss != null ? totalIss : BigDecimal.ZERO;
    }

    public void setTotalIss(BigDecimal totalIss) {
        this.totalIss = totalIss;
    }

    public BigDecimal getTotalJuros() {
        return totalJuros != null ? totalJuros : BigDecimal.ZERO;
    }

    public void setTotalJuros(BigDecimal totalJuros) {
        this.totalJuros = totalJuros;
    }

    public BigDecimal getTotalMulta() {
        return totalMulta != null ? totalMulta : BigDecimal.ZERO;
    }

    public void setTotalMulta(BigDecimal totalMulta) {
        this.totalMulta = totalMulta;
    }

    public BigDecimal getTotalCorrecao() {
        return totalCorrecao != null ? totalCorrecao : BigDecimal.ZERO;
    }

    public void setTotalCorrecao(BigDecimal totalCorrecao) {
        this.totalCorrecao = totalCorrecao;
    }

    public Date getAbertura() {
        return abertura;
    }

    public void setAbertura(Date abertura) {
        this.abertura = abertura;
    }

    public Date getEncerramento() {
        return encerramento;
    }

    public void setEncerramento(Date encerramento) {
        this.encerramento = encerramento;
    }

    public TipoMovimentoMensalNfseDTO getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimentoMensalNfseDTO tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }


}
