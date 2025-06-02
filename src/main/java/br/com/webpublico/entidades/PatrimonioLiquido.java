package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.enums.OperacaoPatrimonioLiquido;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabilComValor;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mga on 18/10/2017.
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Etiqueta("Patrimônio Líquido")
public class PatrimonioLiquido extends SuperEntidadeContabilGerarContaAuxiliar implements Serializable, EntidadeContabilComValor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numero;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data")
    @ReprocessamentoContabil
    private Date dataLancamento;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Lançamento")
    private TipoLancamento tipoLancamento;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Operação")
    @ReprocessamentoContabil
    private OperacaoPatrimonioLiquido operacaoPatrimonioLiquido;

    @ManyToOne
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Evento Contábil")
    @ReprocessamentoContabil
    private EventoContabil eventoContabil;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;

    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @ManyToOne
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Pessoa")
    private Pessoa pessoa;

    @ManyToOne
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Classe")
    private ClasseCredor classe;

    @Obrigatorio
    @Etiqueta("Histórico")
    private String historico;
    private String historicoNota;
    private String historicoRazao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Monetario
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;

    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Exercicio exercicio;

    public PatrimonioLiquido() {
        valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public OperacaoPatrimonioLiquido getOperacaoPatrimonioLiquido() {
        return operacaoPatrimonioLiquido;
    }

    public void setOperacaoPatrimonioLiquido(OperacaoPatrimonioLiquido operacaoPatrimonioLiquido) {
        this.operacaoPatrimonioLiquido = operacaoPatrimonioLiquido;
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

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ClasseCredor getClasse() {
        return classe;
    }

    public void setClasse(ClasseCredor classe) {
        this.classe = classe;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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
            historicoNota += getNumeroExercicioParaHistorico();
        }

        if (this.getTipoLancamento() != null) {
            historicoNota += " Tipo de Lançamento: " + this.getTipoLancamento().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getOperacaoPatrimonioLiquido() != null) {
            historicoNota += " Tipo de Patrimônio: " + this.getOperacaoPatrimonioLiquido().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getPessoa() != null) {
            historicoNota += " Pessoa: " + this.getPessoa() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getClasse() != null) {
            historicoNota += " Classe: " + this.getClasse() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + historico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public String getNumeroExercicioParaHistorico() {
        return "Patrimônio Líquido: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataLancamento()) + ",";
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil() != null
            && this.getEventoContabil().getClpHistoricoContabil() != null) {
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
        try {
            return numero + " - " + DataUtil.getDataFormatada(dataLancamento) + " - " + pessoa + " - " + classe;
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero + "/" + exercicio.getAno();
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
