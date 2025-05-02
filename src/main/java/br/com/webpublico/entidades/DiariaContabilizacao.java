/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.enums.OperacaoDiariaContabilizacao;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoProposta;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * @author juggernaut
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

@Etiqueta("Diária Contabilização")
public class DiariaContabilizacao extends SuperEntidadeContabilGerarContaAuxiliar implements Serializable, EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data")
    @Tabelavel
    @Pesquisavel
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private Date dataDiaria;
    @Tabelavel
    @Etiqueta("Número")
    @Pesquisavel
    private String numero;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Lançamento")
    @ErroReprocessamentoContabil
    private TipoLancamento tipoLancamento;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação")
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    private OperacaoDiariaContabilizacao operacaoDiariaContabilizacao;
    @ManyToOne
    @Etiqueta("Conta de Despesa")
    private ContaDespesa contaDespesa;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Proposta")
    @ErroReprocessamentoContabil
    private TipoProposta tipoProposta;
    @ManyToOne
    @Etiqueta("Proposta Concessão Diária")
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    private PropostaConcessaoDiaria propostaConcessaoDiaria;
    @Etiqueta("Histórico")
    @ErroReprocessamentoContabil
    @Obrigatorio
    private String historico;
    @Obrigatorio
    @Etiqueta("Valor (R$)")
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @ErroReprocessamentoContabil
    private BigDecimal valor;
    @Transient
    private Long criadoEm;
    @Etiqueta("Evento Contábil")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private EventoContabil eventoContabil;
    @ManyToOne
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Etiqueta("Unidade Orçamentária")
    private UnidadeOrganizacional unidadeOrganizacional;
    @OneToMany(mappedBy = "diariaContabilizacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DesdobramentoDiaria> desdobramentoDiaria;
    @Etiqueta("Configuração Diária de Campo")
    @ManyToOne
    private ConfigDiariaDeCampo configDiariaDeCampo;
    @Etiqueta("Configuração Diária Cívil")
    @ManyToOne
    private ConfigDiariaCivil configDiariaCivil;
    @Etiqueta("Configuração Suprimento de Fundos")
    @ManyToOne
    private ConfigSuprimentoDeFundos configSuprimentoDeFundos;
    private String historicoNota;
    private String historicoRazao;
    @ManyToOne
    private Exercicio exercicio;

    public DiariaContabilizacao() {
        valor = BigDecimal.ZERO;
        criadoEm = System.nanoTime();
        tipoLancamento = TipoLancamento.NORMAL;
        desdobramentoDiaria = new ArrayList<>();
    }

    public DiariaContabilizacao(Date dataDiaria, OperacaoDiariaContabilizacao operacaoDiariaContabilizacao, ContaDespesa contaDespesa, PropostaConcessaoDiaria propostaConcessaoDiaria, String historico, BigDecimal valor) {
        this.dataDiaria = dataDiaria;
        this.operacaoDiariaContabilizacao = operacaoDiariaContabilizacao;
        this.contaDespesa = contaDespesa;
        this.propostaConcessaoDiaria = propostaConcessaoDiaria;
        this.historico = historico;
        this.valor = valor;
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContaDespesa getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(ContaDespesa contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public Date getDataDiaria() {
        return dataDiaria;
    }

    public void setDataDiaria(Date dataDiaria) {
        this.dataDiaria = dataDiaria;
    }

    public OperacaoDiariaContabilizacao getOperacaoDiariaContabilizacao() {
        return operacaoDiariaContabilizacao;
    }

    public void setOperacaoDiariaContabilizacao(OperacaoDiariaContabilizacao operacaoDiariaContabilizacao) {
        this.operacaoDiariaContabilizacao = operacaoDiariaContabilizacao;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public PropostaConcessaoDiaria getPropostaConcessaoDiaria() {
        return propostaConcessaoDiaria;
    }

    public void setPropostaConcessaoDiaria(PropostaConcessaoDiaria propostaConcessaoDiaria) {
        this.propostaConcessaoDiaria = propostaConcessaoDiaria;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public TipoProposta getTipoProposta() {
        return tipoProposta;
    }

    public void setTipoProposta(TipoProposta tipoProposta) {
        this.tipoProposta = tipoProposta;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<DesdobramentoDiaria> getDesdobramentoDiaria() {
        return desdobramentoDiaria;
    }

    public void setDesdobramentoDiaria(List<DesdobramentoDiaria> desdobramentoDiaria) {
        this.desdobramentoDiaria = desdobramentoDiaria;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ConfigDiariaDeCampo getConfigDiariaDeCampo() {
        return configDiariaDeCampo;
    }

    public void setConfigDiariaDeCampo(ConfigDiariaDeCampo configDiariaDeCampo) {
        this.configDiariaDeCampo = configDiariaDeCampo;
    }

    public ConfigDiariaCivil getConfigDiariaCivil() {
        return configDiariaCivil;
    }

    public void setConfigDiariaCivil(ConfigDiariaCivil configDiariaCivil) {
        this.configDiariaCivil = configDiariaCivil;
    }

    public ConfigSuprimentoDeFundos getConfigSuprimentoDeFundos() {
        return configSuprimentoDeFundos;
    }

    public void setConfigSuprimentoDeFundos(ConfigSuprimentoDeFundos configSuprimentoDeFundos) {
        this.configSuprimentoDeFundos = configSuprimentoDeFundos;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getHistoricoNota() {
        return historicoNota;
    }

    public void setHistoricoNota(String historicoNota) {
        this.historicoNota = historicoNota;
    }

    public String getHistoricoRazao() {
        return historicoRazao;
    }

    public void setHistoricoRazao(String historicoRazao) {
        this.historicoRazao = historicoRazao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public void gerarHistoricoNota() {
        historicoNota = "";
        if (this.numero != null) {
            historicoNota += "N°: ";
        }
        if (this.getPropostaConcessaoDiaria().getCodigo() != null) {
            historicoNota += " Proposta: " + this.getPropostaConcessaoDiaria().getCodigo() + "/" + Util.getAnoDaData(this.getPropostaConcessaoDiaria().getDataLancamento()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getPropostaConcessaoDiaria().getDespesaORC() != null) {
            historicoNota += " Conta de Despesa: " + this.getPropostaConcessaoDiaria().getDespesaORC().getDescricaoContaDespesaPPA() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getPropostaConcessaoDiaria().getFonteDespesaORC().getDescricaoFonteDeRecurso() != null) {
            historicoNota += " Fonte de Recurso: " + this.getPropostaConcessaoDiaria().getFonteDespesaORC().getDescricaoFonteDeRecurso().trim() + ",";
        }
        if (this.getPropostaConcessaoDiaria().getTipoProposta() != null) {
            historicoNota += " Tipo: " + this.getPropostaConcessaoDiaria().getTipoProposta().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getPropostaConcessaoDiaria().getPessoaFisica() != null) {
            historicoNota += " Pessoa: " + this.getPropostaConcessaoDiaria().getPessoaFisica().getNomeCpfCnpj() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getPropostaConcessaoDiaria().getClasseCredor() != null) {
            historicoNota += " Classe: " + this.getPropostaConcessaoDiaria().getClasseCredor().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + historico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil().getClpHistoricoContabil() != null) {
            historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
        }
        this.historicoRazao = historicoEvento + "" + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return "Data: " + new SimpleDateFormat("dd/MM/yyyy").format(dataDiaria) + ", Operação " + operacaoDiariaContabilizacao.getDescricao() + " e Tipo de Lançamento " + tipoLancamento.getDescricao();
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return propostaConcessaoDiaria.getCodigo();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        return new GeradorContaAuxiliarDTO(getUnidadeOrganizacional(), null, propostaConcessaoDiaria.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos(), null, 0, null, null, propostaConcessaoDiaria.getExercicio(), null, null, 0, null, null);
    }
}
