/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.enums.OrigemReceitaAlteracaoORC;
import br.com.webpublico.enums.TipoAlteracaoORC;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.interfaces.IManadRegistro;
import br.com.webpublico.negocios.manad.ManadContabilFacade;
import br.com.webpublico.util.*;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.TreeMap;

/**
 * @author reidocrime
 */
@GrupoDiagrama(nome = "Orcamentario")
@Etiqueta("Receita")
@Audited
@Entity

public class ReceitaAlteracaoORC extends SuperEntidade implements IManadRegistro, EntidadeContabil, IGeraContaAuxiliar {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Positivo(permiteZero = false)
    @Etiqueta("Valor")
    @Tabelavel
    @Monetario
    private BigDecimal valor;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta de Receita")
    @Tabelavel
    private ReceitaLOA receitaLOA;

    @ManyToOne
    @Etiqueta("Fonte de Recursos")
    @Tabelavel
    private FonteDeRecursos fonteDeRecurso;

    @Etiqueta("Conta de Destinação de Recurso")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;

    @ManyToOne
    @Etiqueta("Alteração orçamentária")
    @Tabelavel
    private AlteracaoORC alteracaoORC;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Origem do Recurso")
    @Tabelavel
    private OrigemReceitaAlteracaoORC origemReceitaAlteracaoORC;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Alteração")
    private TipoAlteracaoORC tipoAlteracaoORC;

    @Monetario
    @Etiqueta("Saldo")
    private BigDecimal saldo;

    private String historicoNota;
    private String historicoRazao;

    @ManyToOne
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Etiqueta("Evento Contabil")
    private EventoContabil eventoContabil;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Operação da Receita")
    private OperacaoReceita operacaoReceita;

    @Version
    private Long versao;

    public ReceitaAlteracaoORC() {
        valor = new BigDecimal(BigInteger.ZERO);
        saldo = new BigDecimal(BigInteger.ZERO);
        this.dataRegistro = new Date();
    }

    public String getHistoricoRazao() {
        return historicoRazao;
    }

    public void setHistoricoRazao(String historicoRazao) {
        this.historicoRazao = historicoRazao;
    }

    public String getHistoricoNota() {
        return historicoNota;
    }

    public void setHistoricoNota(String historicoNota) {
        this.historicoNota = historicoNota;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlteracaoORC getAlteracaoORC() {
        return alteracaoORC;
    }

    public void setAlteracaoORC(AlteracaoORC alteracaoORC) {
        this.alteracaoORC = alteracaoORC;
    }

    public OrigemReceitaAlteracaoORC getOrigemReceitaAlteracaoORC() {
        return origemReceitaAlteracaoORC;
    }

    public void setOrigemReceitaAlteracaoORC(OrigemReceitaAlteracaoORC origemReceitaAlteracaoORC) {
        this.origemReceitaAlteracaoORC = origemReceitaAlteracaoORC;
    }

    public ReceitaLOA getReceitaLOA() {
        return receitaLOA;
    }

    public void setReceitaLOA(ReceitaLOA receitaLOA) {
        this.receitaLOA = receitaLOA;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public TipoAlteracaoORC getTipoAlteracaoORC() {
        return tipoAlteracaoORC;
    }

    public void setTipoAlteracaoORC(TipoAlteracaoORC tipoAlteracaoORC) {
        this.tipoAlteracaoORC = tipoAlteracaoORC;
    }

    public FonteDeRecursos getFonteDeRecurso() {
        return fonteDeRecurso;
    }

    public void setFonteDeRecurso(FonteDeRecursos fonteDeRecurso) {
        this.fonteDeRecurso = fonteDeRecurso;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public OperacaoReceita getOperacaoReceita() {
        return operacaoReceita;
    }

    public void setOperacaoReceita(OperacaoReceita operacaoReceita) {
        this.operacaoReceita = operacaoReceita;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public void gerarHistoricoNota() {
        historicoNota = "";
        if (this.getAlteracaoORC().getCodigo() != null) {
            historicoNota += "N°" + this.getAlteracaoORC().getCodigo() + "/" + Util.getAnoDaData(this.getAlteracaoORC().getDataAlteracao()) + ",";
        }
        if (this.getTipoAlteracaoORC() != null) {
            historicoNota += " Tipo de Alteração: " + this.getTipoAlteracaoORC().getDescricao() + ",";
        }
        if (this.getReceitaLOA() != null) {
            historicoNota += " Conta de Receita: " + this.getReceitaLOA() + ",";
        }
        if (this.getFonteDeRecurso() != null) {
            historicoNota += " Conta de Destinação de Recurso: " + this.getContaDeDestinacao().toString().trim() + ",";
        }

        historicoNota = historicoNota + this.getAlteracaoORC().getDescricao();
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
        return this.receitaLOA.getContaDeReceita() + " -  " + this.alteracaoORC + " " + Util.formataValor(valor) + "(" + DataUtil.getDataFormatada(this.alteracaoORC.getDataAlteracao()) + ").";
    }

    @Override
    public ManadRegistro.ManadModulo getModulo() {
        return ManadRegistro.ManadModulo.CONTABIL;
    }

    @Override
    public ManadRegistro.ManadRegistroTipo getTipoRegistro() {
        return ManadRegistro.ManadRegistroTipo.L300;
    }

    @Override
    public void getConteudoManad(ManadContabilFacade facade, StringBuilder conteudo) {
        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L300.name(), conteudo);
        ManadUtil.criaLinha(2, this.getAlteracaoORC().getAtoLegal().getNumero(), conteudo);
        ManadUtil.criaLinha(3, ManadUtil.getDataSemBarra(this.getAlteracaoORC().getAtoLegal().getDataPublicacao()), conteudo);
        if (this.getTipoAlteracaoORC().equals(TipoAlteracaoORC.PREVISAO)) {
            ManadUtil.criaLinha(4, ManadUtil.getValor(this.getValor()), conteudo);
            ManadUtil.criaLinha(5, ManadUtil.getValor(BigDecimal.ZERO), conteudo);
        } else {
            ManadUtil.criaLinha(5, ManadUtil.getValor(BigDecimal.ZERO), conteudo);
            ManadUtil.criaLinha(5, ManadUtil.getValor(this.getValor()), conteudo);
        }
        ManadUtil.criaLinha(6, "1", conteudo);
        ManadUtil.criaLinha(7, this.getOrigemReceitaAlteracaoORC().equals(OrigemReceitaAlteracaoORC.OPERACAO_CREDITO) ? "3" : this.getOrigemReceitaAlteracaoORC().equals(OrigemReceitaAlteracaoORC.OPERACAO_EXCESSO) ? "2" : "1", conteudo);
        ManadUtil.quebraLinha(conteudo);
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return alteracaoORC.getCodigo();
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
        switch (tipoContaAuxiliar.getCodigo()) {
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(receitaLOA.getEntidade(), contaDeDestinacao, getAlteracaoORC().getExercicio());
            case "96":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada6(receitaLOA.getEntidade(),
                    contaDeDestinacao,
                    receitaLOA.getContaDeReceita(),
                    getAlteracaoORC().getExercicio());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(receitaLOA.getEntidade(), contaDeDestinacao);
            case "96":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar6(receitaLOA.getEntidade(),
                    contaDeDestinacao,
                    (!Strings.isNullOrEmpty(receitaLOA.getContaDeReceita().getCodigoSICONFI()) ?
                        receitaLOA.getContaDeReceita().getCodigoSICONFI() :
                        receitaLOA.getContaDeReceita().getCodigo()).replace(".", ""));
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }
}
