/*
 * Codigo gerado automaticamente em Thu Nov 22 14:40:47 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.entidadesauxiliares.contabil.BarraProgressoAssistente;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.assync.DeferirPagamentoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "deferir-pagamento", pattern = "/deferir-pagamento/", viewId = "/faces/financeiro/orcamentario/pagamento/deferir-pagamento.xhtml"),
})
public class DeferirPagamentoControlador implements Serializable {

    @EJB
    private DeferirPagamentoFacade deferirPagamentoFacade;
    private List<Pagamento> listaPagamentos;
    private List<Pagamento> listaPagamentosSelecionados;
    private List<String> mensagens;
    private List<String> mensagensDeErros;
    private Date dtInicial;
    private Date dtFinal;
    private SubConta contaFinanceiraInicial;
    private SubConta contaFinanceiraFinal;
    private CategoriaOrcamentaria categoria;
    private ConverterAutoComplete converterContaFinanceiraInicial;
    private ConverterAutoComplete converterContaFinanceiraFinal;
    private MoneyConverter moneyConverter;
    private BarraProgressoAssistente barraProgresso;

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public void deferirPagamento() {
        try {
            validarDeferirPagamento();
            abrirDialogProgressBar();
            executarPoll();
            deferirPagamentoFacade.deferirPagamentos(getSistemaControlador().getDataOperacao(), listaPagamentosSelecionados, barraProgresso);
        }catch (ValidacaoException ve){
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }catch (Exception e){
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void abrirDialogProgressBar() {
        FacesUtil.executaJavaScript("dialogProgressBar.show()");
    }

    public void executarPoll() {
        FacesUtil.executaJavaScript("poll.start()");
    }

    public void finalizarBarraProgressao() {
        if (!barraProgresso.getCalculando()) {
            FacesUtil.executaJavaScript("dialogProgressBar.hide()");
            FacesUtil.atualizarComponente("formPagamento");
            FacesUtil.executaJavaScript("dialogPagamento.show()");
            mensagens.addAll(barraProgresso.getMensagens());
            mensagensDeErros.addAll(barraProgresso.getMensagensComErro());
        }
    }


    public void redirecionarParaLista() {
        if (mensagens.size() > 0) {
            FacesUtil.addOperacaoRealizada(mensagens.size() + " Pagamento(s) deferido(s) com sucesso.");
        }
        FacesUtil.redirecionamentoInterno("/pagamento/listar/");
    }

    public String mensagemPagamentosDeferidos() {
        return mensagens.size() + " Pagamento(s) deferido(s) com sucesso.";
    }

    public String mensagemPagamentosNaoDeferidos() {
        return mensagensDeErros.size() + " Pagamento(s) não deferidos. Clique no botão 'Imprimir' para visualizar os erros.";
    }

    public void imprimirLogErrosPagamentosNaoDeferidos() {
        Util.geraPDF("Pagamentos não Deferidos", gerarLogErroPagamentosNaoDeferidos(), FacesContext.getCurrentInstance());
    }

    private String gerarLogErroPagamentosNaoDeferidos() {

        String caminhoDaImagem = FacesUtil.geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif";

        String conteudoMensagem = "<?xml version='1.0' encoding='iso-8859-1'?>";
        conteudoMensagem += " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">";
        conteudoMensagem += " <html>";
        conteudoMensagem += "<html> ";
        conteudoMensagem += "<div style='text-align:center'> ";
        conteudoMensagem += "<img src=\"" + caminhoDaImagem + "\" alt=\"PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO\" /> </br> ";
        conteudoMensagem += "<b> PREFEITURA DE RIO BRANCO </br> ";
        conteudoMensagem += "</br>PAGAMENTO(S) NÃO DEFERIDO(S)</b> ";
        conteudoMensagem += "</div> ";
        conteudoMensagem += "</br>";

        conteudoMensagem += "<div style='text-align:left'> ";
        for (String s : mensagensDeErros) {
            conteudoMensagem += s;
        }
        conteudoMensagem += "</div>";
        conteudoMensagem += "</html>";
        return conteudoMensagem;
    }

    private void validarDeferirPagamento() {
        ValidacaoException va = new ValidacaoException();
        if (listaPagamentosSelecionados.size() <= 0) {
            va.adicionarMensagemDeCampoObrigatorio(" Selecione um ou mais pagamentos para deferir.");
        }
        va.lancarException();
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void limparFiltros() {
        dtInicial = getSistemaControlador().getDataOperacao();
        Calendar c = Calendar.getInstance();
        c.setTime(dtInicial);
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
        dtInicial = c.getTime();
        dtFinal = getSistemaControlador().getDataOperacao();
        contaFinanceiraInicial = new SubConta();
        contaFinanceiraFinal = new SubConta();
    }

    public void pesquisarPagamentos() {
        mensagens.clear();
        mensagensDeErros.clear();
        barraProgresso = new BarraProgressoAssistente();
        try {
            validarFiltros();
            listaPagamentos = deferirPagamentoFacade.getPagamentoFacade().listaPagamentosParaDeferir(dtInicial, dtFinal, contaFinanceiraInicial, contaFinanceiraFinal, getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente(), getSistemaControlador().getExercicioCorrente(), categoria);
            listaPagamentosSelecionados.clear();
            if (listaPagamentos.isEmpty()) {
                FacesUtil.addAtencao("Não foram localizados pagamento(s) para os filtros informados.");
            }
        }catch (ValidacaoException ve){
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }catch (Exception e){
            FacesUtil.addErrorGenerico(e);
        }
    }

    @URLAction(mappingId = "deferir-pagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        listaPagamentos = new ArrayList<>();
        listaPagamentosSelecionados = new ArrayList<>();
        mensagens = new ArrayList<>();
        mensagensDeErros = new ArrayList<>();
        limparFiltros();
    }

    public String icone(Pagamento pag) {
        if (listaPagamentosSelecionados.contains(pag)) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String title(Pagamento pag) {
        if (listaPagamentosSelecionados.contains(pag)) {
            return "Clique para deselecionar este pagamento.";
        }
        return "Clique para selecionar este pagamento.";
    }

    public String iconeTodos() {
        if (listaPagamentosSelecionados.size() == listaPagamentos.size()) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String titleTodos() {
        if (listaPagamentosSelecionados.size() == listaPagamentos.size()) {
            return "Clique para deselecionar todos pagamentos.";
        }
        return "Clique para selecionar todos pagamentos.";
    }

    public void selecionarPagamento(Pagamento pag) {
        if (listaPagamentosSelecionados.contains(pag)) {
            listaPagamentosSelecionados.remove(pag);
        } else {
            listaPagamentosSelecionados.add(pag);
        }
    }

    public void selecionarTodosPagamentos() {
        if (listaPagamentosSelecionados.size() == listaPagamentos.size()) {
            listaPagamentosSelecionados.removeAll(listaPagamentos);
        } else {
            for (Pagamento pagamentos : listaPagamentos) {
                if (!listaPagamentosSelecionados.contains(pagamentos)) {
                    listaPagamentosSelecionados.add(pagamentos);
                }
            }
        }
    }

    public void validarFiltros() {
        ValidacaoException va = new ValidacaoException();
        if (dtInicial == null) {
            va.adicionarMensagemDeCampoObrigatorio(" A Data Inicial deve ser informada para filtrar o pagamento.");
        } else if (dtFinal == null) {
            va.adicionarMensagemDeCampoObrigatorio(" A Data Final deve ser informada para filtrar o pagamento.");
        } else if (dtFinal.before(dtInicial)) {
            va.adicionarMensagemDeOperacaoNaoPermitida(" A Data Final deve maior que a Data Inicial.");
        }
        va.lancarException();
    }

    public List<SubConta> completarContaFinanceiraInicial(String parte) {
        return deferirPagamentoFacade.getPagamentoFacade().getSubContaFacade().listaPorUnidadeOrganizacional(parte.trim(), getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente(), getSistemaControlador().getExercicioCorrente(), getSistemaControlador().getDataOperacao());
    }

    public List<SubConta> completarContaFinanceiraFinal(String parte) {
        return deferirPagamentoFacade.getPagamentoFacade().getSubContaFacade().listaPorUnidadeOrganizacional(parte.trim(), getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente(), getSistemaControlador().getExercicioCorrente(), getSistemaControlador().getDataOperacao());
    }

    public List<SelectItem> getCategoriaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, "Todas"));
        for (CategoriaOrcamentaria object : CategoriaOrcamentaria.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterContaFinanceiraInicial() {
        if (converterContaFinanceiraInicial == null) {
            converterContaFinanceiraInicial = new ConverterAutoComplete(SubConta.class, deferirPagamentoFacade.getPagamentoFacade().getSubContaFacade());
        }
        return converterContaFinanceiraInicial;
    }

    public ConverterAutoComplete getConverterContaFinanceiraFinal() {
        if (converterContaFinanceiraFinal == null) {
            converterContaFinanceiraFinal = new ConverterAutoComplete(SubConta.class, deferirPagamentoFacade.getPagamentoFacade().getSubContaFacade());
        }
        return converterContaFinanceiraFinal;
    }

    public BigDecimal getValorTotalPagamentos() {
        BigDecimal soma = BigDecimal.ZERO;
        if (listaPagamentos != null) {
            for (Pagamento pag : listaPagamentos) {
                soma = soma.add(pag.getValor());
            }
        }
        return soma;
    }

    public List<Pagamento> getListaPagamentos() {
        return listaPagamentos;
    }

    public void setListaPagamentos(List<Pagamento> listaPagamentos) {
        this.listaPagamentos = listaPagamentos;
    }

    public Date getDtInicial() {
        return dtInicial;
    }

    public void setDtInicial(Date dtInicial) {
        this.dtInicial = dtInicial;
    }

    public Date getDtFinal() {
        return dtFinal;
    }

    public void setDtFinal(Date dtFinal) {
        this.dtFinal = dtFinal;
    }

    public SubConta getContaFinanceiraInicial() {
        return contaFinanceiraInicial;
    }

    public void setContaFinanceiraInicial(SubConta contaFinanceiraInicial) {
        this.contaFinanceiraInicial = contaFinanceiraInicial;
    }

    public SubConta getContaFinanceiraFinal() {
        return contaFinanceiraFinal;
    }

    public void setContaFinanceiraFinal(SubConta contaFinanceiraFinal) {
        this.contaFinanceiraFinal = contaFinanceiraFinal;
    }

    public CategoriaOrcamentaria getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaOrcamentaria categoria) {
        this.categoria = categoria;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<String> mensagens) {
        this.mensagens = mensagens;
    }

    public List<String> getMensagensDeErros() {
        return mensagensDeErros;
    }

    public void setMensagensDeErros(List<String> mensagensDeErros) {
        this.mensagensDeErros = mensagensDeErros;
    }

    public BarraProgressoAssistente getBarraProgresso() {
        return barraProgresso;
    }

    public void setBarraProgresso(BarraProgressoAssistente barraProgresso) {
        this.barraProgresso = barraProgresso;
    }
}
