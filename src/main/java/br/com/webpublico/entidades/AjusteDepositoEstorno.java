package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 24/08/2017.
 */
@Entity

@Audited
@Etiqueta("Estorno de Ajuste em Depósito")
public class AjusteDepositoEstorno extends SuperEntidadeContabilGerarContaAuxiliar implements EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Data de Referência")
    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private Date dataEstorno;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número")
    @ErroReprocessamentoContabil
    private String numero;

    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;

    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalAdm;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ajuste em Depósito")
    private AjusteDeposito ajusteDeposito;

    @ErroReprocessamentoContabil
    @Obrigatorio
    @Etiqueta("Histórico")
    private String historico;

    @Pesquisavel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Valor (R$)")
    @Monetario
    @Obrigatorio
    @ErroReprocessamentoContabil
    private BigDecimal valor;

    @ManyToOne
    @Obrigatorio
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;

    private String historicoNota;
    private String historicoRazao;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ajusteDepositoEstorno", orphanRemoval = true)
    private List<AjusteDepositoReceitaExtraEstorno> estornosReceitasExtra;

    public AjusteDepositoEstorno() {
        valor = BigDecimal.ZERO;
        estornosReceitasExtra = Lists.newArrayList();
    }

    public List<AjusteDepositoReceitaExtraEstorno> getEstornosReceitasExtra() {
        return estornosReceitasExtra;
    }

    public void setEstornosReceitasExtra(List<AjusteDepositoReceitaExtraEstorno> estornosReceitasExtra) {
        this.estornosReceitasExtra = estornosReceitasExtra;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return unidadeOrganizacionalAdm;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public AjusteDeposito getAjusteDeposito() {
        return ajusteDeposito;
    }

    public void setAjusteDeposito(AjusteDeposito ajusteDeposito) {
        this.ajusteDeposito = ajusteDeposito;
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

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
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

    public void gerarHistoricoNota() {
        historicoNota = " ";
        if (this.getNumero() != null) {
            historicoNota += "N°: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataEstorno()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getAjusteDeposito().getNumero() != null) {
            historicoNota += "Ajuste Depósito: " + this.getAjusteDeposito().getNumero() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getAjusteDeposito().getFonteDeRecurso() != null) {
            historicoNota += "Fonte de Recurso: " + this.getAjusteDeposito().getFonteDeRecurso() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getAjusteDeposito().getContaExtraorcamentaria() != null) {
            historicoNota += "Conta Extraorçamentária: " + this.getAjusteDeposito().getContaExtraorcamentaria() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getAjusteDeposito().getPessoa() != null) {
            historicoNota += "Pessoa: " + this.getAjusteDeposito().getPessoa().getNomeCpfCnpj() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getAjusteDeposito().getClasseCredor() != null) {
            historicoNota += "Classe: " + this.getAjusteDeposito().getClasseCredor().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + historico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil().getClpHistoricoContabil() != null) {
            historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
        }
        this.historicoRazao = historicoEvento + " " + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
    }

    @Override
    public String toString() {
        return numero + " - " + Util.formataValor(valor) + " - " + DataUtil.getDataFormatada(dataEstorno);
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero;
    }

    public BigDecimal getValorTotalEstornoReceita() {
        BigDecimal total = BigDecimal.ZERO;
        for (AjusteDepositoReceitaExtraEstorno ajusteRec : this.getEstornosReceitasExtra()) {
            total = total.add(ajusteRec.getReceitaExtraEstorno().getValor());
        }
        return total;
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        return new GeradorContaAuxiliarDTO(getUnidadeOrganizacional(), null, ajusteDeposito.getContaDeDestinacao(), null, null, null, null, ajusteDeposito.getFonteDeRecurso().getExercicio(), null, null, 0, null, null);
    }
}
