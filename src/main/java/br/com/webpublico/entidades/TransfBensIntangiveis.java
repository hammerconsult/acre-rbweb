package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.financeiro.SuperEntidadeContabilFinanceira;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensIntangiveis;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 17/02/14
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Etiqueta("Transferência de Bens Intangíveis")
public class TransfBensIntangiveis extends SuperEntidadeContabilFinanceira implements Serializable, EntidadeContabil, IGeraContaAuxiliar {

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
    @ReprocessamentoContabil
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data")
    private Date dataTransferencia;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Lançamento")
    private TipoLancamento tipoLancamento;
    @ManyToOne
    @Etiqueta("Unidade de Origem")
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrigem;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Grupo Patrimonial de Origem")
    private GrupoBem grupoBemOrigem;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Tipo Grupo de Origem")
    private TipoGrupo tipoGrupoOrigem;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Unidade de Destino")
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeDestino;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Grupo Patrimonial de  Destino")
    private GrupoBem grupoBemDestino;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Tipo Grupo de Destino")
    private TipoGrupo tipoGrupoDestino;
    @Etiqueta("Operação de Origem")
    @Obrigatorio
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoOperacaoBensIntangiveis operacaoOrigem;
    @Etiqueta("Operação de Destino")
    @Obrigatorio
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoOperacaoBensIntangiveis operacaoDestino;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Obrigatorio
    @Monetario
    @Pesquisavel
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;
    @Obrigatorio
    @Etiqueta("Histórico")
    private String historico;
    @ManyToOne
    @Etiqueta("Evento Contábil Origem")
    private EventoContabil eventoContabilOrigem;
    @ManyToOne
    @Etiqueta("Evento Contábil Destino")
    @ReprocessamentoContabil
    private EventoContabil eventoContabilDestino;
    @Etiqueta("Exercício")
    @ManyToOne
    private Exercicio exercicio;
    private String historicoNota;
    private String historicoRazao;

    public TransfBensIntangiveis() {
        tipoLancamento = TipoLancamento.NORMAL;
        valor = new BigDecimal(BigInteger.ZERO);
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
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

    public EventoContabil getEventoContabilOrigem() {
        return eventoContabilOrigem;
    }

    public void setEventoContabilOrigem(EventoContabil eventoContabilOrigem) {
        this.eventoContabilOrigem = eventoContabilOrigem;
    }

    public EventoContabil getEventoContabilDestino() {
        return eventoContabilDestino;
    }

    public void setEventoContabilDestino(EventoContabil eventoContabilDestino) {
        this.eventoContabilDestino = eventoContabilDestino;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataTransferencia() {
        return dataTransferencia;
    }

    public void setDataTransferencia(Date dataTransferencia) {
        this.dataTransferencia = dataTransferencia;
    }

    public GrupoBem getGrupoBemOrigem() {
        return grupoBemOrigem;
    }

    public void setGrupoBemOrigem(GrupoBem grupoBemOrigem) {
        this.grupoBemOrigem = grupoBemOrigem;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public GrupoBem getGrupoBemDestino() {
        return grupoBemDestino;
    }

    public void setGrupoBemDestino(GrupoBem grupoBemDestino) {
        this.grupoBemDestino = grupoBemDestino;
    }

    public TipoGrupo getTipoGrupoOrigem() {
        return tipoGrupoOrigem;
    }

    public void setTipoGrupoOrigem(TipoGrupo tipoGrupoOrigem) {
        this.tipoGrupoOrigem = tipoGrupoOrigem;
    }

    public TipoGrupo getTipoGrupoDestino() {
        return tipoGrupoDestino;
    }

    public void setTipoGrupoDestino(TipoGrupo tipoGrupoDestino) {
        this.tipoGrupoDestino = tipoGrupoDestino;
    }

    public UnidadeOrganizacional getUnidadeOrigem() {
        return unidadeOrigem;
    }

    public void setUnidadeOrigem(UnidadeOrganizacional unidadeOrigem) {
        this.unidadeOrigem = unidadeOrigem;
    }

    public UnidadeOrganizacional getUnidadeDestino() {
        return unidadeDestino;
    }

    public void setUnidadeDestino(UnidadeOrganizacional unidadeDestino) {
        this.unidadeDestino = unidadeDestino;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoOperacaoBensIntangiveis getOperacaoOrigem() {
        return operacaoOrigem;
    }

    public void setOperacaoOrigem(TipoOperacaoBensIntangiveis operacaoOrigem) {
        this.operacaoOrigem = operacaoOrigem;
    }

    public TipoOperacaoBensIntangiveis getOperacaoDestino() {
        return operacaoDestino;
    }

    public void setOperacaoDestino(TipoOperacaoBensIntangiveis operacaoDestino) {
        this.operacaoDestino = operacaoDestino;
    }

    public void gerarHistoricoNota(HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        historicoNota = "";
        if (this.getNumero() != null) {
            historicoNota += "N°: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataTransferencia()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        HierarquiaOrganizacional hierarquiaOrigem = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataTransferencia, unidadeOrigem, TipoHierarquiaOrganizacional.ORCAMENTARIA);
        HierarquiaOrganizacional hierarquiaDestino = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataTransferencia, unidadeDestino, TipoHierarquiaOrganizacional.ORCAMENTARIA);
        if (hierarquiaOrigem != null) {
            historicoNota += " Unidade Orçamentária de Origem: " + hierarquiaOrigem.getCodigo() + " - " + hierarquiaOrigem.getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (hierarquiaDestino != null) {
            historicoNota += " Unidade Orçamentária de Destino: " + hierarquiaDestino.getCodigo() + " - " + hierarquiaDestino.getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getTipoGrupoOrigem() != null) {
            historicoNota += " Tipo Bem de Origem: " + this.getTipoGrupoOrigem().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getTipoGrupoDestino() != null) {
            historicoNota += " Tipo Bem de Destino: " + this.getTipoGrupoDestino().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getGrupoBemOrigem() != null) {
            historicoNota += " Grupo Patrimonial Origem: " + this.getGrupoBemOrigem() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getGrupoBemDestino() != null) {
            historicoNota += " Grupo Patrimonial Destino: " + this.getGrupoBemDestino() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getOperacaoOrigem() != null) {
            historicoNota += " Operação Origem: " + this.getOperacaoOrigem() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getOperacaoDestino() != null) {
            historicoNota += " Operação Destino: " + this.getOperacaoDestino() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + historico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabilOrigem().getClpHistoricoContabil() != null) {
            historicoEvento = "Evento Contábil Origem: " + this.getEventoContabilOrigem().getClpHistoricoContabil().toString();
        }
        if (this.getEventoContabilDestino().getClpHistoricoContabil() != null) {
            historicoEvento = "Evento Contábil Destino: " + this.getEventoContabilDestino().getClpHistoricoContabil().toString();
        }
        this.historicoRazao = historicoEvento + " " + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos(HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        gerarHistoricoNota(hierarquiaOrganizacionalFacade);
        gerarHistoricoRazao();
    }

    public String toString() {
        return numero + " - " + Util.formataValor(valor);
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero + " - " + grupoBemOrigem.toString();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(unidadeDestino);
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(unidadeDestino, contaContabil.getSubSistema());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(unidadeOrigem);
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(unidadeOrigem, contaContabil.getSubSistema());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(unidadeDestino);
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(unidadeDestino,
                    contaContabil.getSubSistema());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(unidadeOrigem);
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(unidadeOrigem,
                    contaContabil.getSubSistema());
        }
        return null;
    }
}
