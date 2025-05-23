/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.interfaces.IManadRegistro;
import br.com.webpublico.negocios.manad.ManadContabilFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ManadUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.TreeMap;

/**
 * @author venon
 */
@Entity
@Audited
@Etiqueta("Desdobramento Liquidação")
public class Desdobramento extends SuperEntidade implements IGeraContaAuxiliar, EntidadeContabil, IManadRegistro {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Liquidação")
    @Tabelavel
    private Liquidacao liquidacao;

    //    Não utiliza nessa associativa
    @ManyToOne
    private Empenho empenho;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta de Despesa")
    @Tabelavel
    private Conta conta;

    @ManyToOne
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;

    @Monetario
    @Etiqueta("Valor")
    @Tabelavel
    private BigDecimal valor;

    @Monetario
    @Etiqueta("Saldo")
    private BigDecimal saldo;

    @ManyToOne
    @Etiqueta("Desdobramento Obrigação a Pagar")
    private DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar;
    @ManyToOne(cascade = CascadeType.MERGE)
    @Etiqueta("Desdobramento do Empenho")
    private DesdobramentoEmpenho desdobramentoEmpenho;
    @OneToOne
    private Desdobramento desdobramento;

    public Desdobramento() {
        super();
        valor = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
    }

    public DesdobramentoObrigacaoPagar getDesdobramentoObrigacaoPagar() {
        return desdobramentoObrigacaoPagar;
    }

    public void setDesdobramentoObrigacaoPagar(DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar) {
        this.desdobramentoObrigacaoPagar = desdobramentoObrigacaoPagar;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public Liquidacao getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
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

    public Desdobramento getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(Desdobramento desdobramento) {
        this.desdobramento = desdobramento;
    }

    public DesdobramentoEmpenho getDesdobramentoEmpenho() {
        return desdobramentoEmpenho;
    }

    public void setDesdobramentoEmpenho(DesdobramentoEmpenho desdobramentoEmpenho) {
        this.desdobramentoEmpenho = desdobramentoEmpenho;
    }

    @Override
    public String toString() {
        try {
            return "Liquidação: " + liquidacao + " - desdobramento.: " + conta + " - " + Util.formataValor(valor);
        } catch (Exception ex) {
            return "";
        }
    }

    public String toStringDesdobramento() {
        try {
            if (this.desdobramentoObrigacaoPagar != null && this.desdobramentoObrigacaoPagar.getObrigacaoAPagar() != null) {
                ObrigacaoAPagar obrigacao = this.desdobramentoObrigacaoPagar.getObrigacaoAPagar();
                return this.conta + " -  Obrigação Nº " + obrigacao.getNumero() + " (" + DataUtil.getDataFormatada(obrigacao.getDataLancamento()) + ")";
            }
            return conta.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        return UtilGeradorContaAuxiliar.gerarContaAuxiliarDesdobramento(this);
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        AcaoPPA acaoPPA = liquidacao.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (liquidacao.getEmpenho().getCodigoCO() != null && liquidacao.getEmpenho().getContaDeDestinacao() != null) {
            liquidacao.getEmpenho().getContaDeDestinacao().setCodigoCOEmenda(liquidacao.getEmpenho().getCodigoCO().getCodigo());
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(liquidacao.getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(liquidacao.getUnidadeOrganizacional(), contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(liquidacao.getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    liquidacao.getEmpenho().getContaDeDestinacao(),
                    liquidacao.getEmpenho().getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(liquidacao.getUnidadeOrganizacional(),
                    liquidacao.getEmpenho().getContaDeDestinacao(),
                    liquidacao.getEmpenho().getExercicio());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada7(liquidacao.getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    liquidacao.getEmpenho().getContaDeDestinacao(),
                    liquidacao.getEmpenho().getContaDespesa(),
                    (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "98":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada8(liquidacao.getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    0,
                    liquidacao.getEmpenho().getContaDeDestinacao());
            case "99":
                if (CategoriaOrcamentaria.RESTO.equals(liquidacao.getCategoriaOrcamentaria())) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(liquidacao.getUnidadeOrganizacional(),
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        liquidacao.getEmpenho().getContaDeDestinacao(),
                        liquidacao.getEmpenho().getContaDespesa(),
                        (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        liquidacao.getEmpenho().getEmpenho().getExercicio().getAno(),
                        liquidacao.getEmpenho().getExercicio(),
                        liquidacao.getEmpenho().getEmpenho().getExercicio());
                }
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
        AcaoPPA acaoPPA = liquidacao.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (liquidacao.getEmpenho().getCodigoCO() != null && liquidacao.getEmpenho().getContaDeDestinacao() != null) {
            liquidacao.getEmpenho().getContaDeDestinacao().setCodigoCOEmenda(liquidacao.getEmpenho().getCodigoCO().getCodigo());
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(liquidacao.getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(liquidacao.getUnidadeOrganizacional(), contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(liquidacao.getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    liquidacao.getEmpenho().getContaDeDestinacao());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(liquidacao.getUnidadeOrganizacional(),
                    liquidacao.getEmpenho().getContaDeDestinacao());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar7(liquidacao.getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    liquidacao.getEmpenho().getContaDeDestinacao(),
                    (liquidacao.getEmpenho().getContaDespesa().getCodigoSICONFI() != null ?
                        liquidacao.getEmpenho().getContaDespesa().getCodigoSICONFI() :
                        liquidacao.getEmpenho().getContaDespesa().getCodigo().replace(".", "")),
                    (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "98":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar8(liquidacao.getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    0,
                    liquidacao.getEmpenho().getContaDeDestinacao());
            case "99":
                if (CategoriaOrcamentaria.RESTO.equals(liquidacao.getCategoriaOrcamentaria())) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(liquidacao.getUnidadeOrganizacional(),
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        liquidacao.getEmpenho().getContaDeDestinacao(),
                        liquidacao.getEmpenho().getContaDespesa(),
                        (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        liquidacao.getEmpenho().getEmpenho().getExercicio().getAno(),
                        liquidacao.getEmpenho().getExercicio());
                }
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

    public String getCodigoExtensaoFonteRecursoAsString() {
        return liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString();
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return ((EntidadeContabil) liquidacao).getReferenciaArquivoPrestacaoDeContas();
    }

    @Override
    public ManadRegistro.ManadModulo getModulo() {
        return ManadRegistro.ManadModulo.CONTABIL;
    }

    @Override
    public ManadRegistro.ManadRegistroTipo getTipoRegistro() {
        return ManadRegistro.ManadRegistroTipo.L100;
    }

    @Override
    public void getConteudoManad(ManadContabilFacade facade, StringBuilder conteudo) {
        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L100.name(), conteudo);
        ManadUtil.criaLinha(2, this.getLiquidacao().getEmpenho().getNumero(), conteudo);
        ManadUtil.criaLinha(3, this.getLiquidacao().getNumero(), conteudo);
        ManadUtil.criaLinha(4, ManadUtil.getDataSemBarra(this.getLiquidacao().getDataLiquidacao()), conteudo);
        ManadUtil.criaLinha(5, ManadUtil.getValor(valor), conteudo);
        ManadUtil.criaLinha(6, "D", conteudo);
        ManadUtil.criaLinha(7, this.getLiquidacao().getComplemento(), conteudo);
        ManadUtil.quebraLinha(conteudo);
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        String retorno = toString();
        if (liquidacao.getEmpenho().isEmpenhoRestoPagar()) {
            retorno = "Liquidação de Restos a Pagar: nº ";
            if (liquidacao.getLiquidacao() != null) {
                retorno += liquidacao.getLiquidacao().getNumero() + " - " + DataUtil.getDataFormatada(liquidacao.getLiquidacao().getDataLiquidacao()) + " - ";
            } else {
                retorno += liquidacao.getNumero() + " - " + DataUtil.getDataFormatada(liquidacao.getDataLiquidacao()) + " - ";
            }
            retorno += "Restos a Pagar: nº " + liquidacao.getEmpenho().getNumero() + " - " +
                DataUtil.getDataFormatada(liquidacao.getEmpenho().getDataEmpenho()) + " - " +
                "Empenho: nº " + liquidacao.getEmpenho().getEmpenho().getNumero() + " - " +
                DataUtil.getDataFormatada(liquidacao.getEmpenho().getEmpenho().getDataEmpenho()) + " - " +
                "Função " + liquidacao.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getFuncao().getCodigo() + " - " +
                liquidacao.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getFuncao().getDescricao() + " - " +
                "Subfunção " + liquidacao.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getSubFuncao().getCodigo() + " - " +
                liquidacao.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getSubFuncao().getDescricao() + " - " +
                "Conta de Despesa " + conta.getCodigo() + " - " + conta.getDescricao() + " - " +
                "Fonte de Recurso " + liquidacao.getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getCodigo() + " - " +
                liquidacao.getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getDescricao() + " - " +
                "Pessoa " + liquidacao.getEmpenho().getFornecedor().getNome() + " - " +
                "Valor " + Util.formataValor(valor);
        }
        return retorno;
    }
}
