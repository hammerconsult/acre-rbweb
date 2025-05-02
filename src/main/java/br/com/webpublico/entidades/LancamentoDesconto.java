package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoLancamentoDesconto;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Arrecadação")
@Entity

@Audited
@Etiqueta("Processo de Desconto")
public class LancamentoDesconto extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data do Lançamento")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataLancamento;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data Início")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicio;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data Fim")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fim;
    @Obrigatorio
    @Etiqueta("Motivo")
    private String motivo;
    @Obrigatorio
    @Etiqueta("Usuário")
    @ManyToOne
    private UsuarioSistema usuario;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Ato Legal")
    @ManyToOne
    private AtoLegal atoLegal;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoLancamentoDesconto situacao;
    @Etiqueta("Data de Estorno")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataCancelamento;
    @Etiqueta("Motivo do Estorno")
    private String motivoCancelamento;
    @OneToMany(mappedBy = "lancamentoDesconto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LancamentoDescontoParcela> parcelas;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Desconto")
    @Enumerated(EnumType.STRING)
    private TipoDesconto tipoDesconto;
    private BigDecimal valorDescontoImposto;
    private BigDecimal percentualDescontoImposto;
    private BigDecimal valorDescontoTaxa;
    private BigDecimal percentualDescontoTaxa;
    private BigDecimal valorDescontoJuros;
    private BigDecimal percentualDescontoJuros;
    private BigDecimal valorDescontoMulta;
    private BigDecimal percentualDescontoMulta;
    private BigDecimal valorDescontoCorrecao;
    private BigDecimal percentualDescontoCorrecao;
    private BigDecimal valorDescontoHonorarios;
    private BigDecimal percentualDescontoHonorarios;
    private Boolean lancouDescontos;


    public LancamentoDesconto() {
        super();
        lancouDescontos = Boolean.FALSE;
        parcelas = Lists.newLinkedList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public SituacaoLancamentoDesconto getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoLancamentoDesconto situacao) {
        this.situacao = situacao;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public List<LancamentoDescontoParcela> getParcelas() {
        return parcelas;
    }

    public List<LancamentoDescontoParcela> getParcelasComDescontos() {
        if (Boolean.TRUE.equals(lancouDescontos)) {
            return parcelas;
        }
        return Lists.newArrayList();
    }

    public void setParcelas(List<LancamentoDescontoParcela> parcelas) {
        this.parcelas = parcelas;
    }

    public LancamentoDesconto(TipoDesconto tipoDesconto) {
        this.tipoDesconto = tipoDesconto;
    }

    public TipoDesconto getTipoDesconto() {
        return tipoDesconto;
    }

    public void setTipoDesconto(TipoDesconto tipoDesconto) {
        this.tipoDesconto = tipoDesconto;
    }

    public BigDecimal getValorDescontoImposto() {
        return valorDescontoImposto != null ? valorDescontoImposto : BigDecimal.ZERO;
    }

    public void setValorDescontoImposto(BigDecimal valorDescontoImposto) {
        this.valorDescontoImposto = valorDescontoImposto;
    }

    public BigDecimal getPercentualDescontoImposto() {
        return percentualDescontoImposto != null ? percentualDescontoImposto : BigDecimal.ZERO;
    }

    public void setPercentualDescontoImposto(BigDecimal percentualDescontoImposto) {
        this.percentualDescontoImposto = percentualDescontoImposto;
    }

    public BigDecimal getValorDescontoTaxa() {
        return valorDescontoTaxa != null ? valorDescontoTaxa : BigDecimal.ZERO;
    }

    public void setValorDescontoTaxa(BigDecimal valorDescontoTaxa) {
        this.valorDescontoTaxa = valorDescontoTaxa;
    }

    public BigDecimal getPercentualDescontoTaxa() {
        return percentualDescontoTaxa != null ? percentualDescontoTaxa : BigDecimal.ZERO;
    }

    public void setPercentualDescontoTaxa(BigDecimal percentualDescontoTaxa) {
        this.percentualDescontoTaxa = percentualDescontoTaxa;
    }

    public BigDecimal getValorDescontoJuros() {
        return valorDescontoJuros != null ? valorDescontoJuros : BigDecimal.ZERO;
    }

    public void setValorDescontoJuros(BigDecimal valorDescontoJuros) {
        this.valorDescontoJuros = valorDescontoJuros;
    }

    public BigDecimal getPercentualDescontoJuros() {
        return percentualDescontoJuros != null ? percentualDescontoJuros : BigDecimal.ZERO;
    }

    public void setPercentualDescontoJuros(BigDecimal percentualDescontoJuros) {
        this.percentualDescontoJuros = percentualDescontoJuros;
    }

    public BigDecimal getValorDescontoMulta() {
        return valorDescontoMulta != null ? valorDescontoMulta : BigDecimal.ZERO;
    }

    public void setValorDescontoMulta(BigDecimal valorDescontoMulta) {
        this.valorDescontoMulta = valorDescontoMulta;
    }

    public BigDecimal getPercentualDescontoMulta() {
        return percentualDescontoMulta != null ? percentualDescontoMulta : BigDecimal.ZERO;
    }

    public void setPercentualDescontoMulta(BigDecimal percentualDescontoMulta) {
        this.percentualDescontoMulta = percentualDescontoMulta;
    }

    public BigDecimal getValorDescontoCorrecao() {
        return valorDescontoCorrecao != null ? valorDescontoCorrecao : BigDecimal.ZERO;
    }

    public void setValorDescontoCorrecao(BigDecimal valorDescontoCorrecao) {
        this.valorDescontoCorrecao = valorDescontoCorrecao;
    }

    public BigDecimal getPercentualDescontoCorrecao() {
        return percentualDescontoCorrecao != null ? percentualDescontoCorrecao : BigDecimal.ZERO;
    }

    public void setPercentualDescontoCorrecao(BigDecimal percentualDescontoCorrecao) {
        this.percentualDescontoCorrecao = percentualDescontoCorrecao;
    }

    public BigDecimal getValorDescontoHonorarios() {
        return valorDescontoHonorarios != null ? valorDescontoHonorarios : BigDecimal.ZERO;
    }

    public void setValorDescontoHonorarios(BigDecimal valorDescontoHonorarios) {
        this.valorDescontoHonorarios = valorDescontoHonorarios;
    }

    public BigDecimal getPercentualDescontoHonorarios() {
        return percentualDescontoHonorarios != null ? percentualDescontoHonorarios : BigDecimal.ZERO;
    }

    public void setPercentualDescontoHonorarios(BigDecimal percentualDescontoHonorarios) {
        this.percentualDescontoHonorarios = percentualDescontoHonorarios;
    }

    public BigDecimal getValorDesconto() {
        return getValorDescontoImposto().add(getValorDescontoTaxa()).add(getValorDescontoJuros()).add(getValorDescontoMulta()).add(getValorDescontoCorrecao()).add(getValorDescontoHonorarios());
    }

    public enum TipoDesconto implements EnumComDescricao {
        FIXO("Fixo"), PERCENTUAL("Percentual");

        private String descricao;

        TipoDesconto(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public boolean isTipoDescontoFixo() {
        return TipoDesconto.FIXO.equals(tipoDesconto);
    }

    public boolean isTipoDescontoPercentual() {
        return TipoDesconto.PERCENTUAL.equals(tipoDesconto);
    }

    public Boolean getLancouDescontos() {
        return lancouDescontos;
    }

    public void setLancouDescontos(Boolean lancouDescontos) {
        this.lancouDescontos = lancouDescontos;
    }
}
