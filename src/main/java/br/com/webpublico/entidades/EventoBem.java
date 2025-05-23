package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EventoBemIncorporavelComContabil;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 04/12/13
 * Time: 10:11
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta(value = "Evento do Bem", genero = "M")
public abstract class EventoBem extends SuperEntidade implements EventoBemIncorporavelComContabil, Comparable<EventoBem>, PossuidorArquivo {

    private static final Logger logger = LoggerFactory.getLogger(EventoBem.class);

    @Obrigatorio
    @Etiqueta("Valor do Lançamento")
    protected BigDecimal valorDoLancamento;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Lançamento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataLancamento;

    @Obrigatorio
    @Etiqueta("Data da Operação")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOperacao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Bem")
    @ManyToOne
    private Bem bem;

    @Obrigatorio
    @Etiqueta("Situação do Evento do Bem")
    @Enumerated(EnumType.STRING)
    private SituacaoEventoBem situacaoEventoBem;

    @Obrigatorio
    @Etiqueta("Estado Inicial")
    @ManyToOne
    private EstadoBem estadoInicial;

    @Obrigatorio
    @Etiqueta("Estado Resultante")
    @ManyToOne
    private EstadoBem estadoResultante;

    @Obrigatorio
    @Etiqueta("Tipo Evento Bem")
    @Enumerated(EnumType.STRING)
    private TipoEventoBem tipoEventoBem;

    @Obrigatorio
    @Etiqueta("Tipo Evento Bem")
    @Enumerated(EnumType.STRING)
    private TipoOperacao tipoOperacao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public EventoBem(TipoEventoBem tipoEventoBem, TipoOperacao tipoOperacao) {
        super();
        this.tipoEventoBem = tipoEventoBem;
        this.dataLancamento = new Date();
        this.dataOperacao = LocalDateTime.now().toDate();
        this.tipoOperacao = tipoOperacao;
        this.setValorDoLancamento(BigDecimal.ZERO);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
        }
    }

    public EventoBem(TipoEventoBem tipoEventoBem, TipoOperacao tipoOperacao, Date dataLancamento) {
        super();
        this.tipoEventoBem = tipoEventoBem;
        this.dataLancamento = DataUtil.getDataComHoraAtual(dataLancamento);
        this.dataOperacao = LocalDateTime.now().toDate();
        this.tipoOperacao = tipoOperacao;
        this.setValorDoLancamento(BigDecimal.ZERO);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            logger.error("new EventoBem {}", e);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EstadoBem getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(EstadoBem estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public EstadoBem getEstadoResultante() {
        return estadoResultante;
    }

    public void setEstadoResultante(EstadoBem estadoResultante) {
        this.estadoResultante = estadoResultante;
    }

    @Override
    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public SituacaoEventoBem getSituacaoEventoBem() {
        return situacaoEventoBem;
    }

    public void setSituacaoEventoBem(SituacaoEventoBem situacaoEventoBem) {
        this.situacaoEventoBem = situacaoEventoBem;
    }

    public TipoEventoBem getTipoEventoBem() {
        return this.tipoEventoBem;
    }

    public void setTipoEventoBem(TipoEventoBem tipoEventoBem) {
        this.tipoEventoBem = tipoEventoBem;
    }

    public TipoOperacao getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacao tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    @Override
    public String toString() {
        return estadoResultante.getIdentificacao() + " " + bem.getDescricao();
    }

    @Override
    public BigDecimal getValorDoLancamento() {
        return valorDoLancamento != null ? valorDoLancamento : BigDecimal.ZERO;
    }

    public void setValorDoLancamento(BigDecimal valorDoLancamento) {
        this.valorDoLancamento = valorDoLancamento;
    }

    @Override
    public BigDecimal getValorDepreciacao() {
        return getEstadoResultante().getValorAcumuladoDaDepreciacao();
    }

    @Override
    public BigDecimal getValorAmortizacao() {
        return getEstadoResultante().getValorAcumuladoDaAmortizacao();
    }

    @Override
    public BigDecimal getValorExaustao() {
        return getEstadoResultante().getValorAcumuladoDaExaustao();
    }

    @Override
    public BigDecimal getValorReducao() {
        return getEstadoResultante().getValorAcumuladoDeAjuste();
    }

    @Override
    public BigDecimal getValorOriginal() {
        return getEstadoResultante().getValorOriginal();
    }

    @Override
    public UnidadeOrganizacional getUnidadeOrcamentariaInicial() {
        return estadoInicial.getDetentoraOrcamentaria();
    }

    @Override
    public UnidadeOrganizacional getUnidadeOrcamentariaResultante() {
        return estadoResultante.getDetentoraOrcamentaria();
    }

    @Override
    public UnidadeOrganizacional getUnidadeAdministrativaInicial() {
        return estadoInicial.getDetentoraAdministrativa();
    }

    @Override
    public UnidadeOrganizacional getUnidadeAdministrativaResultante() {
        return estadoResultante.getDetentoraAdministrativa();
    }

    @Override
    public TipoGrupo getTipoGrupoInicial() {
        return estadoInicial.getTipoGrupo();
    }

    @Override
    public TipoGrupo getTipoGrupoResultante() {
        return estadoResultante.getTipoGrupo();
    }

    @Override
    public GrupoBem getGrupoBemInicial() {
        return estadoInicial.getGrupoBem();
    }

    @Override
    public GrupoBem getGrupoBemResultante() {
        return estadoResultante.getGrupoBem();
    }


    @Override
    public TipoBem getTipoBemResultante() {
        return estadoResultante.getGrupoBem().getTipoBem();
    }

    @Override
    public Date getDataDoLancamento() {
        return getDataLancamento();
    }

    @Override
    public Boolean ehEstorno() {
        return false;
    }

    @Override
    public int compareTo(EventoBem o) {
        return this.dataOperacao.compareTo(o.dataOperacao);
    }
}
