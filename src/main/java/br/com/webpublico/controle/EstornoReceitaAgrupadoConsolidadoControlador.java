/*
 * Codigo gerado automaticamente em Thu Nov 22 14:40:47 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ReceitaORCEstornoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
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
    @URLMapping(id = "estornar-receita-agrupado-consolidado", pattern = "/estorno-receita-agrupado/consolidado/", viewId = "/faces/financeiro/orcamentario/lancamentoreceitaorc/estornoreceitaagrupadoconsolidado.xhtml"),
})
public class EstornoReceitaAgrupadoConsolidadoControlador extends ReceitaORCEstornoControlador implements Serializable, CRUD {


    @EJB
    private ReceitaORCEstornoFacade receitaORCEstornoFacade;
    private List<LancamentoReceitaOrc> receitas;
    private List<LancamentoReceitaOrc> receitasSelecionadas;
    private List<LancamentoReceitaOrc> receitasEstornadas;
    private List<String> mensagens;
    private Date dtInicial;
    private Date dtFinal;
    private Long conjuntoFonte;
    private SubConta contaFinanceira;
    private Conta contaDeReceita;
    private HierarquiaOrganizacional hierarquiaOrg;

    public EstornoReceitaAgrupadoConsolidadoControlador() {
    }

    @URLAction(mappingId = "estornar-receita-agrupado-consolidado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        novoEstorno();
        selecionado = new ReceitaORCEstorno();
        setHierarquiaOrganizacional(null);
        setReceitaLOAFonte(new ReceitaLOAFonte());
        selecionado.setIntegracao(Boolean.FALSE);
        selecionado.setExercicio(getSistemaControlador().getExercicioCorrente());
        selecionado.setDataEstorno(getSistemaControlador().getDataOperacao());
        setFontesDaReceitaLoa(new ArrayList<FonteDeRecursos>());

    }

    public void novoEstorno() {
        receitas = new ArrayList<>();
        receitasSelecionadas = new ArrayList<>();
        receitasEstornadas = new ArrayList<>();
        mensagens = new ArrayList<>();
        dtInicial = getSistemaControlador().getDataOperacao();
        Calendar c = Calendar.getInstance();
        c.setTime(dtInicial);
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
        dtInicial = c.getTime();
        dtFinal = getSistemaControlador().getDataOperacao();
        contaFinanceira = null;
        contaDeReceita = null;
        hierarquiaOrg = null;
    }


    public void salvarAgrupado() {
        if (validarEstornoPorLote()) {
            try {
                configurarReceitaParaEstornar();
            } catch (Exception ex) {
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            }
        }
    }

    public void salvarConsolidado() {
        try {
            selecionado.realizarValidacoes();
            receitaORCEstornoFacade.salvarNovoEstorno(selecionado, getConjuntoFontes());
            redirecionarParaLista();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }


    private void configurarReceitaParaEstornar() {
        for (LancamentoReceitaOrc lanc : receitasSelecionadas) {
            try {
                ReceitaORCEstorno estorno = new ReceitaORCEstorno();
                estorno.setId(null);
                estorno.setDataEstorno(selecionado.getDataEstorno());
                estorno.setDataConciliacao(null);
                estorno.setValor(lanc.getValorEstorno());
                estorno.setUnidadeOrganizacionalOrc(lanc.getUnidadeOrganizacional());
                estorno.setUnidadeOrganizacionalAdm(lanc.getUnidadeOrganizacionalAdm());
                estorno.setIntegracao(false);
                estorno.setComplementoHistorico(lanc.getComplemento());
                estorno.setContaFinanceira(lanc.getSubConta());
                estorno.setLancamentoReceitaOrc(lanc);
                estorno.setReceitaLOA(lanc.getReceitaLOA());
                estorno.setPessoa(lanc.getPessoa());
                estorno.setClasseCredor(lanc.getClasseCredor());
                estorno.setOperacaoReceitaRealizada(lanc.getOperacaoReceitaRealizada());
                estorno.setExercicio(lanc.getExercicio());
                receitaORCEstornoFacade.definirEventoContabil(estorno);
                recuperarConjuntoFonteDaReceitaRealizada(lanc);
                receitaORCEstornoFacade.geraLancamentos(estorno, conjuntoFonte);
                receitaORCEstornoFacade.salvarNovoEstorno(estorno, conjuntoFonte);
                receitasEstornadas.add(lanc);
            } catch (ExcecaoNegocioGenerica ex) {
                mensagens.add("<div style='font-size: 12px'>Receita Realizada: " + lanc + ". Erro(s): " + "<font style='color: red;'> " + ex.getMessage() + "</font></div></br>");
                continue;
            } catch (Exception e) {
                FacesUtil.addOperacaoNaoRealizada(e.getMessage());
                mensagens.add(e.getMessage());
                continue;
            }
        }
        FacesUtil.executaJavaScript("dialogFinalizar.show()");
    }

    private void recuperarConjuntoFonteDaReceitaRealizada(LancamentoReceitaOrc lanc) {
        if (lanc != null) {
            LancamentoReceitaOrc receita = receitaORCEstornoFacade.getLancamentoReceitaOrcFacade().recuperar(lanc.getId());
            for (Long conj : receita.getConjuntos()) {
                conjuntoFonte = conj;
            }
        }
    }

    public void definirEventoAndFiltroContaReceita() {
        recuperarEventoContabil();
    }

    public void redirecionarParaLista() {
        if (receitasEstornadas.size() > 0) {
            FacesUtil.addOperacaoRealizada("Foram estornadas " + receitasEstornadas.size() + " receitas realizada.");
        }
        FacesUtil.redirecionamentoInterno("/receita-realizada-estorno/listar/");
    }


    private boolean validarEstornoPorLote() {
        if (receitasSelecionadas.size() == 0) {
            FacesUtil.addOperacaoNaoPermitida(" Selecione uma ou mais receitas para estornar.");
            return false;
        }
        for (LancamentoReceitaOrc lanc : receitasSelecionadas) {
            if (lanc.getValorEstorno().compareTo(lanc.getSaldo()) > 0) {
                FacesUtil.addOperacaoNaoPermitida(" A Receita Nº " + lanc.getNumero() + " está com valor maior que o saldo disponível. Saldo: " + Util.formataValor(lanc.getSaldo()));
                return false;
            }
        }
        return true;
    }

    public String mensagemReceitaEstornada() {
        return receitasEstornadas.size() + " Receita(s) Realizada(s) estornada(s) com sucesso.";
    }

    public String mensagemReceitaNaoEstornada() {
        return mensagens.size() + " Receita(s) Realizada(s) não estornada(s). Clique no botão 'Imprimir' para visualizar os erros.";
    }

    public void imprimirLogErrosReceitaNaoEstornada() {
        Util.geraPDF("Receita Realizada não Estornada", gerarLogErroReceitaNaoEstornada(), FacesContext.getCurrentInstance());
    }

    private String gerarLogErroReceitaNaoEstornada() {

        String caminhoDaImagem = FacesUtil.geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif";

        String conteudoMensagem = "<?xml version='1.0' encoding='iso-8859-1'?>";
        conteudoMensagem += " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">";
        conteudoMensagem += " <html>";
        conteudoMensagem += "<html> ";
        conteudoMensagem += "<div style='text-align:center'> ";
        conteudoMensagem += "<img src=\"" + caminhoDaImagem + "\" alt=\"PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO\" /> </br> ";
        conteudoMensagem += "<b> PREFEITURA DE RIO BRANCO </br> ";
        conteudoMensagem += "</br>RECEITA(S) REALZIADA NÃO ESTORNADA(S)</b> ";
        conteudoMensagem += "</div> ";
        conteudoMensagem += "</br>";
        conteudoMensagem += "<div style='text-align:left'> ";
        for (String s : mensagens) {
            conteudoMensagem += s;
        }
        conteudoMensagem += "</div>";
        conteudoMensagem += "<div style='position: fixed; bottom: 0; min-width: 100%;font-size: 11px'> ";
        conteudoMensagem += "Emitido por: " + getSistemaControlador().getUsuarioCorrente() + ", em " + DataUtil.getDataFormatada(new Date()) + " às " + Util.hourToString(new Date());
        conteudoMensagem += "</div> ";
        conteudoMensagem += "</html>";
        return conteudoMensagem;
    }

    public List<LancamentoReceitaOrc> pesquisarReceitas() {
        if (validaFiltros()) {
            receitas = receitaORCEstornoFacade.getLancamentoReceitaOrcFacade().recuperarReceitaPorPeriodo(
                getSistemaControlador().getExercicioCorrente(),
                hierarquiaOrg,
                dtInicial,
                dtFinal,
                contaFinanceira,
                contaDeReceita);
            for (LancamentoReceitaOrc receita : receitas) {
                receita.setValorEstorno(receita.getSaldo());
            }
            receitasSelecionadas.clear();
            if (receitas.isEmpty()) {
                FacesUtil.addAtencao("Não foram localizado receita(s) para os filtros informados.");
            }
            return receitas;
        }
        return new ArrayList<>();
    }


    public String icone(LancamentoReceitaOrc lanc) {
        if (receitasSelecionadas.contains(lanc)) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String title(LancamentoReceitaOrc lanc) {
        if (receitasSelecionadas.contains(lanc)) {
            return "Clique para deselecionar esta receita.";
        }
        return "Clique para selecionar esta receita.";
    }

    public String iconeTodos() {
        if (receitasSelecionadas.size() == receitas.size()) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String titleTodos() {
        if (receitasSelecionadas.size() == receitas.size()) {
            return "Clique para deselecionar todas receitas.";
        }
        return "Clique para selecionar todas receitas.";
    }

    public void selecionar(LancamentoReceitaOrc lanc) {
        if (receitasSelecionadas.contains(lanc)) {
            receitasSelecionadas.remove(lanc);
        } else {
            FacesUtil.executaJavaScript("setaFoco('Formulario:valor')");
            receitasSelecionadas.add(lanc);
        }
    }

    public void selecionarTodas() {
        if (receitasSelecionadas.size() == receitas.size()) {
            receitasSelecionadas.removeAll(receitas);
        } else {
            for (LancamentoReceitaOrc lanc : receitas) {
                selecionar(lanc);
            }
        }
    }

    public Boolean habilitarValor(LancamentoReceitaOrc lanc) {
        if (receitasSelecionadas.contains(lanc)) {
            return true;
        }
        return false;
    }

    public boolean validaFiltros() {
        boolean controle = true;
        if (dtInicial == null) {
            FacesUtil.addCampoObrigatorio(" A Data Inicial deve ser informada para filtrar..");
            controle = false;
        } else if (dtFinal == null) {
            FacesUtil.addCampoObrigatorio(" A Data Final deve ser informada para filtrar.");
            controle = false;
        } else if (dtFinal.before(dtInicial)) {
            FacesUtil.addCampoObrigatorio(" A Data Final deve maior que a Data Inicial.");
            controle = false;
        } else if (contaDeReceita == null) {
            FacesUtil.addCampoObrigatorio(" A Conta de Receita deve ser informada para filtrar.");
            controle = false;
        }
        return controle;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return receitaORCEstornoFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<ReceitaLOA> completarReceitaLOA(String parte) {
        List<ReceitaLOA> lista = new ArrayList<>();
        try {
            validarOperacaoReceita();
            lista = receitaORCEstornoFacade.getReceitaLOAFacade().buscarContaReceitaPorUnidadeExercicioAndOperacaoReceita(
                parte.trim(),
                receitaORCEstornoFacade.getSistemaFacade().getExercicioCorrente(),
                selecionado.getUnidadeOrganizacionalOrc(),
                selecionado.getOperacaoReceitaRealizada());
            if (lista.isEmpty()) {
                FacesUtil.addAtencao("Conta de receita não encontrada para a operação: " + selecionado.getOperacaoReceitaRealizada().getDescricao());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        return lista;
    }

    private void validarOperacaoReceita() {
        ValidacaoException ve = new ValidacaoException();
        if (getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Organizacional deve ser informado para filtrar a conta de receita.");
        }
        if (selecionado.getOperacaoReceitaRealizada() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Operação deve ser informado para filtrar a conta de receita.");
        }
        ve.lancarException();
    }

    public List<Conta> completaContaReceita(String parte) {
        try {
            return receitaORCEstornoFacade.getContaFacade().listaFiltrandoReceita(parte.trim(), getSistemaControlador().getExercicioCorrente());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<SubConta> completaContaFinanceiraFiltro(String parte) {
        try {
            return receitaORCEstornoFacade.getLancamentoReceitaOrcFacade().getSubContaFacade().listaPorExercicio(parte.trim(), getSistemaControlador().getExercicioCorrente());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }


    public BigDecimal getValorTotalReceita() {
        BigDecimal soma = BigDecimal.ZERO;
        if (receitas != null) {
            for (LancamentoReceitaOrc lanc : receitas) {
                soma = soma.add(lanc.getValor());
            }
        }
        return soma;
    }

    public BigDecimal getSaldoTotalReceita() {
        BigDecimal soma = BigDecimal.ZERO;
        if (receitas != null) {
            for (LancamentoReceitaOrc lanc : receitas) {
                soma = soma.add(lanc.getSaldo());
            }
        }
        return soma;
    }

    public BigDecimal getValorTotalReceitasSelecionada() {
        BigDecimal soma = BigDecimal.ZERO;
        if (receitas != null) {
            for (LancamentoReceitaOrc lanc : receitasSelecionadas) {
                soma = soma.add(lanc.getValorEstorno());
            }
        }
        return soma;
    }

    public List<LancamentoReceitaOrc> getReceitas() {
        return receitas;
    }

    public void setReceitas(List<LancamentoReceitaOrc> receitas) {
        this.receitas = receitas;
    }

    public List<LancamentoReceitaOrc> getReceitasSelecionadas() {
        return receitasSelecionadas;
    }

    public void setReceitasSelecionadas(List<LancamentoReceitaOrc> receitasSelecionadas) {
        this.receitasSelecionadas = receitasSelecionadas;
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

    public SubConta getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(SubConta contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

    public Conta getContaDeReceita() {
        return contaDeReceita;
    }

    public void setContaDeReceita(Conta contaDeReceita) {
        this.contaDeReceita = contaDeReceita;
    }

    public HierarquiaOrganizacional getHierarquiaOrg() {
        return hierarquiaOrg;
    }

    public void setHierarquiaOrg(HierarquiaOrganizacional hierarquiaOrg) {
        this.hierarquiaOrg = hierarquiaOrg;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<String> mensagens) {
        this.mensagens = mensagens;
    }

    public List<LancamentoReceitaOrc> getReceitasEstornadas() {
        return receitasEstornadas;
    }

    public void setReceitasEstornadas(List<LancamentoReceitaOrc> receitasEstornadas) {
        this.receitasEstornadas = receitasEstornadas;
    }
}
