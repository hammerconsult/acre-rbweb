/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.BancoContaConfTributario;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.negocios.BancoFacade;
import br.com.webpublico.negocios.ImprimeMapaArrecadacaoMovimentoFinanceiro;
import br.com.webpublico.negocios.LoteBaixaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @author magraowar
 */

///tributario/arrecadacao/relatorios/arrecadacaomovimentofinanceiro.xhtml
///

@ManagedBean(name = "mapaArrecadacaoMovimentoFinanceiroControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoMapaArrecadacaoMovimentoFinanceiro", pattern = "/tributario/arrecadacao/arrecadacaomovimentofinanceiro/", viewId = "/faces/tributario/arrecadacao/relatorios/arrecadacaomovimentofinanceiro.xhtml")
})
public class MapaArrecadacaoMovimentoFinanceiroControlador extends AbstractReport implements Serializable {

    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private SistemaFacade sisteamFacade;
    private ConverterAutoComplete converterBanco;
    private Banco filtroBanco;
    private Date dataInicialPagamento;
    private Date dataFinalPagamento;
    private String contaReceitaInicial;
    private String contaReceitaFinal;
    private String codigoReduzidoInicial;
    private String codigoReduzidoFinal;
    private String ordenacao;
    private String ordemSql;
    private BancoContaConfTributario conta;
    private transient Converter converterConta;
    @EJB
    private LoteBaixaFacade facade;
    private SubConta subConta;

    public MapaArrecadacaoMovimentoFinanceiroControlador() {
        novo();
    }


    @URLAction(mappingId = "novoMapaArrecadacaoMovimentoFinanceiro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtroBanco = new Banco();
        dataInicialPagamento = new Date();
        dataFinalPagamento = new Date();
        contaReceitaInicial = new String();
        contaReceitaFinal = new String();
        codigoReduzidoInicial = new String();
        codigoReduzidoFinal = new String();
        subConta = new SubConta();
        ordenacao = "S";
    }

    public BancoContaConfTributario getConta() {
        return conta;
    }

    public void setConta(BancoContaConfTributario conta) {
        this.conta = conta;
    }

    public String getOrdemSql() {
        return ordemSql;
    }

    public void setOrdemSql(String ordemSql) {
        this.ordemSql = ordemSql;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public String getCodigoReduzidoFinal() {
        return codigoReduzidoFinal;
    }

    public void setCodigoReduzidoFinal(String codigoReduzidoFinal) {
        this.codigoReduzidoFinal = codigoReduzidoFinal;
    }

    public String getCodigoReduzidoInicial() {
        return codigoReduzidoInicial;
    }

    public void setCodigoReduzidoInicial(String codigoReduzidoInicial) {
        this.codigoReduzidoInicial = codigoReduzidoInicial;
    }

    public String getContaReceitaFinal() {
        return contaReceitaFinal;
    }

    public void setContaReceitaFinal(String contaReceitaFinal) {
        this.contaReceitaFinal = contaReceitaFinal;
    }

    public String getContaReceitaInicial() {
        return contaReceitaInicial;
    }

    public void setContaReceitaInicial(String contaReceitaInicial) {
        this.contaReceitaInicial = contaReceitaInicial;
    }

    public Date getDataFinalPagamento() {
        return dataFinalPagamento;
    }

    public void setDataFinalPagamento(Date dataFinalPagamento) {
        this.dataFinalPagamento = dataFinalPagamento;
    }

    public Date getDataInicialPagamento() {
        return dataInicialPagamento;
    }

    public void setDataInicialPagamento(Date dataInicialPagamento) {
        this.dataInicialPagamento = dataInicialPagamento;
    }

    public Banco getFiltroBanco() {
        return filtroBanco;
    }

    public void setFiltroBanco(Banco filtroBanco) {
        this.filtroBanco = filtroBanco;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public ConverterAutoComplete getConverterBanco() {
        if (converterBanco == null) {
            converterBanco = new ConverterAutoComplete(Banco.class, bancoFacade);
        }
        return converterBanco;
    }

    public List<Banco> completaBanco(String parte) {
        return bancoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public void montaRelatorio() throws JRException, IOException {
        new ImprimeMapaArrecadacaoMovimentoFinanceiro(filtroBanco,
                subConta,
                dataInicialPagamento,
                dataFinalPagamento,
                contaReceitaInicial,
                contaReceitaFinal,
                codigoReduzidoInicial,
                codigoReduzidoFinal,
                ordenacao,
                ordemSql,
                sisteamFacade.getLogin()).imprime();
    }

    public void atribuiContaReceitaInicialaoFinal() {
        contaReceitaFinal = contaReceitaInicial;
        FacesUtil.atualizarComponente("Formulario:contaReceitaFinal");
    }

    public void atribuiCodigoReduzidoInicialaoFinal() {
        codigoReduzidoFinal = codigoReduzidoInicial;
        FacesUtil.atualizarComponente("Formulario:codigoReduzidoFinal");
    }

    public Converter getConverterConta() {
        if (converterConta == null) {
            converterConta = new Converter() {

                @Override
                public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
                    if (string == null || string.isEmpty()) {
                        return null;
                    }
                    return facade.recuperar(BancoContaConfTributario.class, Long.parseLong(string));
                }

                @Override
                public String getAsString(FacesContext fc, UIComponent uic, Object o) {
                    if (o == null) {
                        return null;
                    } else {
                        return String.valueOf(((BancoContaConfTributario) o).getId());
                    }
                }
            };
        }
        return converterConta;
    }

    public List<SelectItem> getContas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        List<BancoContaConfTributario> lista = facade.recuperaContasConfiguracao();
        Collections.sort(lista, new Comparator<BancoContaConfTributario>() {

            @Override
            public int compare(BancoContaConfTributario o1, BancoContaConfTributario o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        for (BancoContaConfTributario object : lista) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public void selecionaContaTst() {
        if (conta != null && conta.getSubConta().getContaBancariaEntidade() != null && conta.getSubConta().getContaBancariaEntidade().getAgencia() != null && conta.getSubConta().getContaBancariaEntidade().getAgencia().getBanco() != null) {
            filtroBanco = (conta.getSubConta().getContaBancariaEntidade().getAgencia().getBanco());
            subConta = (conta.getSubConta());

        } else {
            filtroBanco = new Banco();
            subConta = new SubConta();
            FacesUtil.addError("Atenção", "A conta selecionada não está relacionada a um banco");
        }
    }
}
