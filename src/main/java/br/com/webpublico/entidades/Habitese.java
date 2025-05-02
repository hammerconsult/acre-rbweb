package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Tributario")
@Entity
@Audited
public class Habitese extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;
    @Etiqueta("Exercício")
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.DATE)
    private Date dataExpedicaoTermo;
    @Temporal(TemporalType.DATE)
    private Date dataVencimentoISS;
    @Temporal(TemporalType.DATE)
    private Date dataLancamento;
    @Etiqueta("Número do Protocolo")
    @Tabelavel
    @Pesquisavel
    private String numeroProtocolo;
    @Etiqueta("Ano do Protocolo")
    @Tabelavel
    @Pesquisavel
    private String anoProtocolo;
    @Etiqueta("Situação")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private Situacao situacao;
    @Tabelavel
    @ManyToOne
    @Etiqueta("Alvará de Construção")
    private AlvaraConstrucao alvaraConstrucao;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ProcessoCalculoAlvaraConstrucaoHabitese processoCalcAlvaConstHabi;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private CaracteristicaConstrucaoHabitese caracteristica;
    @OneToMany(mappedBy = "habitese", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeducaoHabitese> deducoes;
    private BigDecimal baseCalculoISSQN;
    private BigDecimal valorAliquota;
    private BigDecimal valorAliquotaCalculado;
    @ManyToOne
    private DocumentoOficial documentoOficial;
    @Etiqueta("Data de Vistoria")
    @Temporal(TemporalType.DATE)
    private Date dataVistoria;

    public Habitese() {
        situacao = Situacao.EM_ABERTO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataExpedicaoTermo() {
        return dataExpedicaoTermo;
    }

    public void setDataExpedicaoTermo(Date dataExpedicaoTermo) {
        this.dataExpedicaoTermo = dataExpedicaoTermo;
    }

    public Date getDataVencimentoISS() {
        return dataVencimentoISS;
    }

    public void setDataVencimentoISS(Date dataVencimentoISS) {
        this.dataVencimentoISS = dataVencimentoISS;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public AlvaraConstrucao getAlvaraConstrucao() {
        return alvaraConstrucao;
    }

    public void setAlvaraConstrucao(AlvaraConstrucao alvaraConstrucao) {
        this.alvaraConstrucao = alvaraConstrucao;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public ProcessoCalculoAlvaraConstrucaoHabitese getProcessoCalcAlvaConstHabi() {
        return processoCalcAlvaConstHabi;
    }

    public void setProcessoCalcAlvaConstHabi(ProcessoCalculoAlvaraConstrucaoHabitese processoCalcAlvaConstHabi) {
        this.processoCalcAlvaConstHabi = processoCalcAlvaConstHabi;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public CaracteristicaConstrucaoHabitese getCaracteristica() {
        return caracteristica;
    }

    public void setCaracteristica(CaracteristicaConstrucaoHabitese caracteristica) {
        this.caracteristica = caracteristica;
    }

    public List<DeducaoHabitese> getDeducoes() {
        if (deducoes == null) {
            deducoes = Lists.newArrayList();
        }
        return deducoes;
    }

    public void setDeducoes(List<DeducaoHabitese> deducoes) {
        this.deducoes = deducoes;
    }

    public BigDecimal getBaseCalculoISSQN() {
        return baseCalculoISSQN;
    }

    public void setBaseCalculoISSQN(BigDecimal baseCalculoISSQN) {
        this.baseCalculoISSQN = baseCalculoISSQN;
    }

    public BigDecimal getValorAliquota() {
        return valorAliquota;
    }

    public void setValorAliquota(BigDecimal valorAliquota) {
        this.valorAliquota = valorAliquota;
    }

    public BigDecimal getValorAliquotaCalculado() {
        return valorAliquotaCalculado;
    }

    public void setValorAliquotaCalculado(BigDecimal valorAliquotaCalculado) {
        this.valorAliquotaCalculado = valorAliquotaCalculado;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public Date getDataVistoria() {
        return dataVistoria;
    }

    public void setDataVistoria(Date dataVistoria) {
        this.dataVistoria = dataVistoria;
    }

    public enum Situacao implements EnumComDescricao {
        EM_ABERTO("Em Aberto"),
        EFETIVADO("Efetivado"),
        FINALIZADO("Finalizado"),
        CANCELADO("Cancelado");

        private final String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }

    @Override
    public String toString() {
        return codigo + "/" + exercicio.getAno();
    }
}
