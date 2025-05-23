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
import java.math.BigInteger;
import java.util.TreeMap;

/**
 * @author wiplash
 */
@Entity(name = "DesdobramentoLiqEstorno")
@Audited
public class DesdobramentoLiquidacaoEstorno extends SuperEntidade implements IGeraContaAuxiliar, EntidadeContabil, IManadRegistro {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Liquidação")
    private Liquidacao liquidacao;

    @ManyToOne
    @Etiqueta("Estorno de Liquidação")
    @Tabelavel
    private LiquidacaoEstorno liquidacaoEstorno;

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

    @ManyToOne
    @Etiqueta("Desdobramento Obrigação a Pagar")
    private DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar;

    public DesdobramentoLiquidacaoEstorno() {
        valor = new BigDecimal(BigInteger.ZERO);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Liquidacao getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
    }

    public LiquidacaoEstorno getLiquidacaoEstorno() {
        return liquidacaoEstorno;
    }

    public void setLiquidacaoEstorno(LiquidacaoEstorno liquidacaoEstorno) {
        this.liquidacaoEstorno = liquidacaoEstorno;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public DesdobramentoObrigacaoPagar getDesdobramentoObrigacaoPagar() {
        return desdobramentoObrigacaoPagar;
    }

    public void setDesdobramentoObrigacaoPagar(DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar) {
        this.desdobramentoObrigacaoPagar = desdobramentoObrigacaoPagar;
    }

    @Override
    public String toString() {
        return "Liquidação Estorno : " + liquidacaoEstorno + " - desdobramento.: " + conta + " - " + Util.formataValor(valor);
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
        return UtilGeradorContaAuxiliar.gerarContaAuxiliarDesdobramentoLiquidacaoEstorno(this);
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        AcaoPPA acaoPPA = liquidacaoEstorno.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (liquidacaoEstorno.getLiquidacao().getEmpenho().getCodigoCO() != null && liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao() != null) {
            liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao().setCodigoCOEmenda(liquidacaoEstorno.getLiquidacao().getEmpenho().getCodigoCO().getCodigo());
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(liquidacaoEstorno.getLiquidacao().getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(liquidacaoEstorno.getLiquidacao().getUnidadeOrganizacional(), contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(liquidacaoEstorno.getLiquidacao().getUnidadeOrganizacional(), contaContabil.getSubSistema(),
                    liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao(),
                    liquidacaoEstorno.getLiquidacao().getEmpenho().getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(liquidacaoEstorno.getLiquidacao().getUnidadeOrganizacional(),
                    liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao(),
                    liquidacaoEstorno.getLiquidacao().getEmpenho().getExercicio());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada7(liquidacaoEstorno.getLiquidacao().getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao(),
                    liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDespesa(),
                    (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "98":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada8(liquidacaoEstorno.getLiquidacao().getUnidadeOrganizacional(), contaContabil.getSubSistema(), 0,
                    liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao());
            case "99":
                if (CategoriaOrcamentaria.RESTO.equals(liquidacaoEstorno.getLiquidacao().getCategoriaOrcamentaria())) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(liquidacaoEstorno.getLiquidacao().getUnidadeOrganizacional(),
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao(),
                        liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDespesa(),
                        (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        liquidacaoEstorno.getLiquidacao().getEmpenho().getEmpenho().getExercicio().getAno(),
                        liquidacaoEstorno.getLiquidacao().getEmpenho().getExercicio(),
                        liquidacaoEstorno.getLiquidacao().getEmpenho().getEmpenho().getExercicio());
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
        AcaoPPA acaoPPA = liquidacaoEstorno.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (liquidacaoEstorno.getLiquidacao().getEmpenho().getCodigoCO() != null && liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao() != null) {
            liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao().setCodigoCOEmenda(liquidacaoEstorno.getLiquidacao().getEmpenho().getCodigoCO().getCodigo());
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(liquidacaoEstorno.getLiquidacao().getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(liquidacaoEstorno.getLiquidacao().getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(liquidacaoEstorno.getLiquidacao().getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(liquidacaoEstorno.getLiquidacao().getUnidadeOrganizacional(),
                    liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar7(liquidacaoEstorno.getLiquidacao().getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao(),
                    (liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDespesa().getCodigoSICONFI() != null ?
                        liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDespesa().getCodigoSICONFI() :
                        liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDespesa().getCodigo().replace(".", "")),
                    (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "98":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar8(liquidacaoEstorno.getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    0,
                    liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao());
            case "99":
                if (CategoriaOrcamentaria.RESTO.equals(liquidacaoEstorno.getLiquidacao().getCategoriaOrcamentaria())) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(liquidacaoEstorno.getLiquidacao().getUnidadeOrganizacional(),
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao(),
                        liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDespesa(),
                        (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        liquidacaoEstorno.getLiquidacao().getEmpenho().getEmpenho().getExercicio().getAno(),
                        liquidacaoEstorno.getLiquidacao().getEmpenho().getExercicio());
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
        return liquidacaoEstorno.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString();
    }


    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return ((EntidadeContabil) liquidacaoEstorno).getReferenciaArquivoPrestacaoDeContas();
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
        ManadUtil.criaLinha(2, this.getLiquidacaoEstorno().getLiquidacao().getEmpenho().getNumero(), conteudo);
        ManadUtil.criaLinha(3, this.getLiquidacaoEstorno().getLiquidacao().getNumero(), conteudo);
        ManadUtil.criaLinha(4, ManadUtil.getDataSemBarra(this.getLiquidacaoEstorno().getLiquidacao().getDataLiquidacao()), conteudo);
        ManadUtil.criaLinha(5, ManadUtil.getValor(this.getValor()), conteudo);
        ManadUtil.criaLinha(6, "C", conteudo);
        ManadUtil.criaLinha(7, this.getLiquidacaoEstorno().getLiquidacao().getComplemento(), conteudo);
        ManadUtil.quebraLinha(conteudo);
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        String retorno = toString();
        if (liquidacaoEstorno.getLiquidacao().getEmpenho().isEmpenhoRestoPagar()) {
            retorno = "Estorno de Liquidação de Restos a Pagar: nº " + liquidacaoEstorno.getNumero() + " - " + DataUtil.getDataFormatada(liquidacaoEstorno.getDataEstorno()) + " - " +
                "Liquidação de Restos a Pagar: nº ";
            if (liquidacaoEstorno.getLiquidacao().getLiquidacao() != null) {
                retorno += liquidacaoEstorno.getLiquidacao().getLiquidacao().getNumero() + " - " + DataUtil.getDataFormatada(liquidacaoEstorno.getLiquidacao().getLiquidacao().getDataLiquidacao()) + " - ";
            } else {
                retorno += liquidacaoEstorno.getLiquidacao().getNumero() + " - " + DataUtil.getDataFormatada(liquidacaoEstorno.getLiquidacao().getDataLiquidacao()) + " - ";
            }
            retorno += "Restos a Pagar: nº " + liquidacaoEstorno.getLiquidacao().getEmpenho().getNumero() + " - " +
                DataUtil.getDataFormatada(liquidacaoEstorno.getLiquidacao().getEmpenho().getDataEmpenho()) + " - " +
                "Empenho: nº " + liquidacaoEstorno.getLiquidacao().getEmpenho().getEmpenho().getNumero() + " - " +
                DataUtil.getDataFormatada(liquidacaoEstorno.getLiquidacao().getEmpenho().getEmpenho().getDataEmpenho()) + " - " +
                "Função " + liquidacaoEstorno.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getFuncao().getCodigo() + " - " +
                liquidacaoEstorno.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getFuncao().getDescricao() + " - " +
                "Subfunção " + liquidacaoEstorno.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getSubFuncao().getCodigo() + " - " +
                liquidacaoEstorno.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getSubFuncao().getDescricao() + " - " +
                "Conta de Despesa " + conta.getCodigo() + " - " + conta.getDescricao() + " - " +
                "Fonte de Recurso " + liquidacaoEstorno.getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getCodigo() + " - " +
                liquidacaoEstorno.getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getDescricao() + " - " +
                "Pessoa " + liquidacaoEstorno.getLiquidacao().getEmpenho().getFornecedor().getNome() + " - " +
                "Valor " + Util.formataValor(valor);
        }
        return retorno;
    }
}
