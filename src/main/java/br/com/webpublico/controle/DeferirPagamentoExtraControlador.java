/*
 * Codigo gerado automaticamente em Thu Nov 22 14:40:47 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.ContaExtraorcamentaria;
import br.com.webpublico.entidades.PagamentoExtra;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.entidadesauxiliares.contabil.BarraProgressoAssistente;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.assync.DeferirPagamentoExtraFacade;
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "deferir-pagamento-extra", pattern = "/deferir-despesa-extraorcamentaria/", viewId = "/faces/financeiro/extraorcamentario/pagamentoextra/deferir-pagamento-extra.xhtml"),
})
public class DeferirPagamentoExtraControlador implements Serializable {

    @EJB
    private DeferirPagamentoExtraFacade deferirPagamentoExtraFacade;
    private List<PagamentoExtra> listaPagamentosExtra;
    private List<PagamentoExtra> listaPagamentosExtraSelecionados;
    private List<PagamentoExtra> listaPagamentosDeferidos;
    private List<String> mensagens;
    private List<String> mensagensDeErros;
    private Date dtInicial;
    private Date dtFinal;
    private SubConta contaFinanceiraInicial;
    private SubConta contaFinanceiraFinal;
    private Conta contaExtra;
    private ConverterAutoComplete converterContaFinanceiraInicial;
    private ConverterAutoComplete converterContaFinanceiraFinal;
    private ConverterAutoComplete converterContaExtra;
    private MoneyConverter moneyConverter;
    private BarraProgressoAssistente barraProgresso;


    public DeferirPagamentoExtraFacade getFacade() {
        return deferirPagamentoExtraFacade;
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
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

    public void deferirPagamento() {
        try {
            validarDeferirPagamento();
            abrirDialogProgressBar();
            executarPoll();
            deferirPagamentoExtraFacade.deferirPagamentos(getSistemaControlador().getDataOperacao(), listaPagamentosExtraSelecionados, barraProgresso);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }


    public void redirecionarParaLista() {
        if (listaPagamentosDeferidos.size() > 0) {
            FacesUtil.addOperacaoRealizada(listaPagamentosDeferidos.size() + " Despesa(s) Extraoçamentárias deferida(s) com sucesso.");
        }
        FacesUtil.redirecionamentoInterno("/despesa-extra/listar/");
    }

    public String mensagemPagamentosDeferidos() {
        return listaPagamentosDeferidos.size() + " Despesa(s) Extraorçamentária(s) deferida(s) com sucesso.";
    }

    public String mensagemPagamentosNaoDeferidos() {
        return mensagensDeErros.size() + " Despesa(s) Extraorçamentária(s) não deferida(s). Clique no botão 'Imprimir' para visualizar os erros.";
    }

    public void imprimirLogErrosPagamentosNaoDeferidos() {
        Util.geraPDF("Despesa Extra não Deferidas", gerarLogErroPagamentosNaoDeferidos(), FacesContext.getCurrentInstance());
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
        conteudoMensagem += "</br>DESPESA(S) EXTRAORÇAMENTÁRIAS(S) NÃO DEFERIDA(S)</b> ";
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
        ValidacaoException ve = new ValidacaoException();
        if (listaPagamentosExtraSelecionados.size() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Selecione um ou mais despesa extrorçamentária para deferir.");
        }
        ve.lancarException();
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
        contaExtra = new Conta();
    }

    public void pesquisarPagamentos() {
        mensagens.clear();
        mensagensDeErros.clear();
        barraProgresso = new BarraProgressoAssistente();
        try {
            validarFiltros();
            listaPagamentosExtra = deferirPagamentoExtraFacade.getPagamentoExtraFacade().listaDespesaExtraParaDeferir(dtInicial, dtFinal, contaFinanceiraInicial, contaFinanceiraFinal, getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente(), getSistemaControlador().getExercicioCorrente(), contaExtra);
            listaPagamentosExtraSelecionados.clear();
            if (listaPagamentosExtra.isEmpty()) {
                FacesUtil.addAtencao("Não foram localizados despesas extraorçamentárias para os filtros informados.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    @URLAction(mappingId = "deferir-pagamento-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        listaPagamentosExtra = new ArrayList<>();
        listaPagamentosExtraSelecionados = new ArrayList<>();
        mensagens = new ArrayList<>();
        mensagensDeErros = new ArrayList<>();
        listaPagamentosDeferidos = new ArrayList<>();
        limparFiltros();
    }

    public String icone(PagamentoExtra pag) {
        if (listaPagamentosExtraSelecionados.contains(pag)) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String title(PagamentoExtra pag) {
        if (listaPagamentosExtraSelecionados.contains(pag)) {
            return "Clique para deselecionar esta despesa extraorçamentária.";
        }
        return "Clique para selecionar esta despesa extraorçamentária.";
    }

    public String iconeTodos() {
        if (listaPagamentosExtraSelecionados.size() == listaPagamentosExtra.size()) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String titleTodos() {
        if (listaPagamentosExtraSelecionados.size() == listaPagamentosExtra.size()) {
            return "Clique para deselecionar todas despesa extraorçamentária.";
        }
        return "Clique para selecionar todas despesa extraorçamentária.";
    }

    public void selecionarPagamento(PagamentoExtra pag) {
        if (listaPagamentosExtraSelecionados.contains(pag)) {
            listaPagamentosExtraSelecionados.remove(pag);
        } else {
            listaPagamentosExtraSelecionados.add(pag);
        }
    }

    public void selecionarTodosPagamentos() {
        if (listaPagamentosExtraSelecionados.size() == listaPagamentosExtra.size()) {
            listaPagamentosExtraSelecionados.removeAll(listaPagamentosExtra);
        } else {
            for (PagamentoExtra pagamentos : listaPagamentosExtra) {
                selecionarPagamento(pagamentos);
            }
        }
    }

    public void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (dtInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" A Data Inicial deve ser informada para filtrar a despesa extraorçamentária.");
        } else if (dtFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" A Data Final deve ser informada para filtrar a despesa extraorçamentária.");
        } else if (dtFinal.before(dtInicial)) {
            ve.adicionarMensagemDeCampoObrigatorio(" A Data Final deve ser maior que a Data Inicial.");
        }
        ve.lancarException();
    }

    public List<SubConta> completaContaFinanceiraInicial(String parte) {
        return deferirPagamentoExtraFacade.getPagamentoExtraFacade().getSubContaFacade().listaPorUnidadeOrganizacional(parte.trim(), getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente(), getSistemaControlador().getExercicioCorrente(), getSistemaControlador().getDataOperacao());
    }

    public List<SubConta> completaContaFinanceiraFinal(String parte) {
        return deferirPagamentoExtraFacade.getPagamentoExtraFacade().getSubContaFacade().listaPorUnidadeOrganizacional(parte.trim(), getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente(), getSistemaControlador().getExercicioCorrente(), getSistemaControlador().getDataOperacao());
    }

    public List<ContaExtraorcamentaria> completaContaExtraorcamentaria(String parte) {
        return deferirPagamentoExtraFacade.getPagamentoExtraFacade().getContaExtraorcamentariaFacade().listaFiltrandoTodasContaExtraPorExercicio(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public ConverterAutoComplete getConverterContaFinanceiraInicial() {
        if (converterContaFinanceiraInicial == null) {
            converterContaFinanceiraInicial = new ConverterAutoComplete(SubConta.class, deferirPagamentoExtraFacade.getPagamentoExtraFacade().getSubContaFacade());
        }
        return converterContaFinanceiraInicial;
    }

    public ConverterAutoComplete getConverterContaFinanceiraFinal() {
        if (converterContaFinanceiraFinal == null) {
            converterContaFinanceiraFinal = new ConverterAutoComplete(SubConta.class, deferirPagamentoExtraFacade.getPagamentoExtraFacade().getSubContaFacade());
        }
        return converterContaFinanceiraFinal;
    }

    public ConverterAutoComplete getConverterContaExtra() {
        if (converterContaExtra == null) {
            converterContaExtra = new ConverterAutoComplete(Conta.class, deferirPagamentoExtraFacade.getPagamentoExtraFacade().getContaExtraorcamentariaFacade());
        }
        return converterContaExtra;
    }

    public BigDecimal getValorTotalPagamentos() {
        BigDecimal soma = BigDecimal.ZERO;
        if (listaPagamentosExtra != null) {
            for (PagamentoExtra pag : listaPagamentosExtra) {
                soma = soma.add(pag.getValor());
            }
        }
        return soma;
    }

    public List<PagamentoExtra> getListaPagamentosExtra() {
        return listaPagamentosExtra;
    }

    public void setListaPagamentosExtra(List<PagamentoExtra> listaPagamentosExtra) {
        this.listaPagamentosExtra = listaPagamentosExtra;
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

    public Conta getContaExtra() {
        return contaExtra;
    }

    public void setContaExtra(Conta contaExtra) {
        this.contaExtra = contaExtra;
    }

    public List<PagamentoExtra> getListaPagamentosDeferidos() {
        return listaPagamentosDeferidos;
    }

    public void setListaPagamentosDeferidos(List<PagamentoExtra> listaPagamentosDeferidos) {
        this.listaPagamentosDeferidos = listaPagamentosDeferidos;
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
