package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 27/12/13
 * Time: 17:38
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
@Etiqueta("Responsabilidade por Valores, Títulos e Bens")
public class ResponsabilidadeVTB extends SuperEntidadeContabilGerarContaAuxiliar implements Serializable, EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numero;
    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data")
    @ReprocessamentoContabil
    private Date dataRegistro;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Lançamento")
    @Enumerated(EnumType.STRING)
    private TipoLancamento tipoLancamento;
    @ManyToOne
    @Etiqueta("Evento Contábil")
    @Tabelavel
    @Pesquisavel
    @ReprocessamentoContabil
    @Obrigatorio
    private EventoContabil eventoContabil;
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Pessoa")
    @Obrigatorio
    private Pessoa pessoa;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Classe da Pessoa")
    @Obrigatorio
    private ClasseCredor classeCredor;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Histórico")
    @Obrigatorio
    @Length(maximo = 3000)
    private String historico;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Etiqueta("Valor (R$)")
    @Monetario
    @Obrigatorio
    private BigDecimal valor;
    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    private String historicoNota;
    private String historicoRazao;

    public ResponsabilidadeVTB() {
        super();
        this.dataRegistro = new Date();
        this.tipoLancamento = TipoLancamento.NORMAL;
        this.valor = new BigDecimal(BigInteger.ZERO);
    }

    public ResponsabilidadeVTB(Date dataRegistro, String numero, TipoLancamento tipoLancamento, UnidadeOrganizacional unidadeOrganizacional, EventoContabil eventoContabil, Pessoa pessoa, ClasseCredor classeCredor, String historico, BigDecimal valor) {
        super();
        this.dataRegistro = dataRegistro;
        this.numero = numero;
        this.tipoLancamento = tipoLancamento;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.eventoContabil = eventoContabil;
        this.pessoa = pessoa;
        this.classeCredor = classeCredor;
        this.historico = historico;
        this.valor = valor;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
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
        if (this.getNumero() != null) {
            historicoNota += "N°: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataRegistro()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getPessoa() != null) {
            historicoNota += " Pessoa: " + this.getPessoa() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getClasseCredor() != null) {
            historicoNota += " Classe: " + this.getClasseCredor() + UtilBeanContabil.SEPARADOR_HISTORICO;
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
        return numero + " - " + Util.formataValor(valor) + " - " + DataUtil.getDataFormatada(dataRegistro);
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero;
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        return new GeradorContaAuxiliarDTO(getUnidadeOrganizacional());
    }
}
