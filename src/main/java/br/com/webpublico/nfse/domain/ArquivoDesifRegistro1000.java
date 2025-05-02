package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.enums.TipoPartida;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class ArquivoDesifRegistro1000 extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private Long linha;
    @ManyToOne
    private ArquivoDesif arquivoDesif;
    private String cnpj;
    @ManyToOne
    private Cidade cidade;
    private String numeroLancamento;
    @Temporal(TemporalType.DATE)
    private Date dataLancamento;
    private BigDecimal valorPartidaLancamento;
    private String conta;
    @Enumerated(EnumType.STRING)
    private TipoPartida tipoPartida;
    @ManyToOne
    private EventoContabilDesif eventoContabil;
    @ManyToOne
    private Cidade cidadeVinculo;
    private String historico;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLinha() {
        return linha;
    }

    public void setLinha(Long linha) {
        this.linha = linha;
    }

    public ArquivoDesif getArquivoDesif() {
        return arquivoDesif;
    }

    public void setArquivoDesif(ArquivoDesif arquivoDesif) {
        this.arquivoDesif = arquivoDesif;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public String getNumeroLancamento() {
        return numeroLancamento;
    }

    public void setNumeroLancamento(String numeroLancamento) {
        this.numeroLancamento = numeroLancamento;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public BigDecimal getValorPartidaLancamento() {
        return valorPartidaLancamento;
    }

    public void setValorPartidaLancamento(BigDecimal valorPartidaLancamento) {
        this.valorPartidaLancamento = valorPartidaLancamento;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public TipoPartida getTipoPartida() {
        return tipoPartida;
    }

    public void setTipoPartida(TipoPartida tipoPartida) {
        this.tipoPartida = tipoPartida;
    }

    public EventoContabilDesif getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabilDesif eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public Cidade getCidadeVinculo() {
        return cidadeVinculo;
    }

    public void setCidadeVinculo(Cidade cidadeVinculo) {
        this.cidadeVinculo = cidadeVinculo;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }
}
