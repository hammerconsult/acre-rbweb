/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IManadRegistro;
import br.com.webpublico.negocios.manad.ManadContabilFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ManadUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author wiplash
 */
@Entity(name = "DesdobramentoLiqEstorno")
@Audited
public class DesdobramentoLiquidacaoEstorno extends SuperEntidadeContabilGerarContaAuxiliar implements EntidadeContabil, IManadRegistro {

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

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        AcaoPPA acaoPPA = liquidacaoEstorno.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (liquidacaoEstorno.getLiquidacao().getEmpenho().getCodigoCO() != null && liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao() != null) {
            liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao().setCodigoCOEmenda(liquidacaoEstorno.getLiquidacao().getEmpenho().getCodigoCO().getCodigo());
        }
        if (CategoriaOrcamentaria.RESTO.equals(liquidacaoEstorno.getLiquidacao().getCategoriaOrcamentaria())) {
            return new GeradorContaAuxiliarDTO(liquidacaoEstorno.getLiquidacao().getUnidadeOrganizacional(),
                acaoPPA.getCodigoFuncaoSubFuncao(),
                liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao(),
                liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDespesa(),
                liquidacaoEstorno.getLiquidacao().getEmpenho().getCodigoEs(),
                liquidacaoEstorno.getLiquidacao().getEmpenho().getEmpenho().getExercicio().getAno(),
                liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDespesa(),
                liquidacaoEstorno.getLiquidacao().getEmpenho().getExercicio(),
                liquidacaoEstorno.getLiquidacao().getEmpenho().getEmpenho().getExercicio(),
                null, 0, null, liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDespesa().getCodigoContaSiconf());
        } else {
            return new GeradorContaAuxiliarDTO(liquidacaoEstorno.getLiquidacao().getEmpenho().getUnidadeOrganizacional(),
                acaoPPA.getCodigoFuncaoSubFuncao(),
                liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDeDestinacao(),
                liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDespesa(),
                liquidacaoEstorno.getLiquidacao().getEmpenho().getCodigoEs(),
                null,
                liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDespesa(),
                liquidacaoEstorno.getLiquidacao().getExercicio(), null, null, 0,
                null,
                (liquidacaoEstorno.getLiquidacao().getEmpenho().getContaDespesa().getCodigoContaSiconf()));
        }
    }
}
