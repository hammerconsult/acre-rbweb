package br.com.webpublico.controle.contabil;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.AcompanhamentoLancamentoContabil;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.contabil.financeiro.AcompanhamentoLancamentoContabilFacade;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

/**
 * Created by romanini on 16/10/17.
 */
@ManagedBean(name = "acompanhamentoLancamentoContabilControlador")
@ViewScoped
@URLMapping(id = "novo-acompanhamento-lancamento-contabil", pattern = "/acompanhamento-lancamento-contabil/", viewId = "/faces/financeiro/acompanhamento-lancamento-contabil/edita.xhtml")
public class AcompanhamentoLancamentoContabilControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(AcompanhamentoLancamentoContabilControlador.class);
    private AcompanhamentoLancamentoContabil selecionado;
    //dialog
    private EntidadeContabil entidadeContabil;
    private List<LancamentoContabil> lancamentoContabils;
    @EJB
    private AcompanhamentoLancamentoContabilFacade acompanhamentoLancamentoContabilFacade;


    @URLAction(mappingId = "novo-acompanhamento-lancamento-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparFiltros();
    }

    public void limparFiltros() {
        selecionado = new AcompanhamentoLancamentoContabil();
    }

    public void limparFiltrosMenosTipoEData() {
        Date dataInicial = selecionado.getDataInicial();
        Date dataFinal = selecionado.getDataFinal();
        TipoEventoContabil tipoEventoContabil = selecionado.getTipoEventoContabil();
        selecionado = new AcompanhamentoLancamentoContabil();
        selecionado.setTipoEventoContabil(tipoEventoContabil);
        selecionado.setDataFinal(dataFinal);
        selecionado.setDataInicial(dataInicial);
    }

    public void recuperarLancamentos() {
        try {
            validarSelecionado();
            acompanhamentoLancamentoContabilFacade.recuperarLancamentos(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarSelecionado() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoEventoContabil() == null && selecionado.getIdParametroEvento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Evento Contábil ou Identificador TCE deve ser informado.");
        }
        if (selecionado.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo data inicial é obrigatória.");
        }
        if (selecionado.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo data final é obrigatória.");
        }
        if (selecionado.getDataInicial() != null && selecionado.getDataFinal() != null && selecionado.getDataFinal().before(selecionado.getDataInicial())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo data inicial deve ser anterior a data final.");
        }
        if (selecionado.getCampoOrdenar().getCampo() != null && selecionado.getCampoOrdenar().getTipoOrdem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Tipo de ordenação é obrigatória.");
        }
        if (selecionado.getCampoOrdenar().getCampo() == null && selecionado.getCampoOrdenar().getTipoOrdem() != null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo ordenar por é obrigatória.");
        }
        ve.lancarException();
    }

    public void visualizarLancamento(EntidadeContabil entidadeContabil) {
        this.entidadeContabil = entidadeContabil;
        lancamentoContabils = acompanhamentoLancamentoContabilFacade.recuperarLancamentosContabeis(this.entidadeContabil);
    }

    public AcompanhamentoLancamentoContabil getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(AcompanhamentoLancamentoContabil selecionado) {
        this.selecionado = selecionado;
    }

    public EntidadeContabil getEntidadeContabil() {
        return entidadeContabil;
    }

    public void setEntidadeContabil(EntidadeContabil entidadeContabil) {
        this.entidadeContabil = entidadeContabil;
    }

    public List<LancamentoContabil> getLancamentoContabils() {
        return lancamentoContabils;
    }

    public void setLancamentoContabils(List<LancamentoContabil> lancamentoContabils) {
        this.lancamentoContabils = lancamentoContabils;
    }

    //GEt/Completar

    public List<SelectItem> getTiposEventos() {
        return Util.getListSelectItem(TipoEventoContabil.values());
    }

    public List<SelectItem> getCamposOrdenador() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(ConsultaMovimentoContabil.Campo.NUMERO, ConsultaMovimentoContabil.Campo.NUMERO.getDescricao()));
        toReturn.add(new SelectItem(ConsultaMovimentoContabil.Campo.DATA_INICIAL, ConsultaMovimentoContabil.Campo.DATA_INICIAL.getDescricao()));
        toReturn.add(new SelectItem(ConsultaMovimentoContabil.Campo.UNIDADE, ConsultaMovimentoContabil.Campo.UNIDADE.getDescricao()));
        toReturn.add(new SelectItem(ConsultaMovimentoContabil.Campo.VALOR, ConsultaMovimentoContabil.Campo.VALOR.getDescricao()));
        return Util.ordenaSelectItem(toReturn);
    }

    public List<SelectItem> getQuantidadeRegistro() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(1, "1"));
        toReturn.add(new SelectItem(10, "10"));
        toReturn.add(new SelectItem(25, "25"));
        toReturn.add(new SelectItem(50, "50"));
        toReturn.add(new SelectItem(100, "100"));
        return toReturn;
    }

    public List<SelectItem> getTiposOrdenacao() {
        return Util.getListSelectItemSemCampoVazio(ConsultaMovimentoContabil.Ordem.TipoOrdem.values());
    }

    public List<EventoContabil> completarEventoContabil(String param) {
        if (selecionado.getTipoEventoContabil() != null) {
            List<EventoContabil> eventoContabils = acompanhamentoLancamentoContabilFacade.getEventoContabilFacade().buscarEventosContabeisPorTipoEvento(param, selecionado.getTipoEventoContabil());
            return eventoContabils;
        }
        FacesUtil.addWarn("Atenção!", "Selecione primeiro o campo tipo de evento contábil.");
        return Lists.newArrayList();
    }

    public List<SelectItem> getOperacoesReceitaRealizada() {
        return Util.getListSelectItem(OperacaoReceita.values());
    }

    public List<SelectItem> getOperacoesBensMoveis() {
        return Util.getListSelectItem(TipoOperacaoBensMoveis.values());
    }

    public List<SelectItem> getOperacoesBensImoveis() {
        return Util.getListSelectItem(TipoOperacaoBensImoveis.values());
    }

    public List<SelectItem> getOperacoesBensIntangiveis() {
        return Util.getListSelectItem(TipoOperacaoBensIntangiveis.values());
    }

    public List<SelectItem> getOperacoesBensEstoque() {
        return Util.getListSelectItem(TipoOperacaoBensEstoque.values());
    }

    public List<SelectItem> getOperacoesDividaPublica() {
        return Util.getListSelectItem(OperacaoMovimentoDividaPublica.values());
    }

    public List<SelectItem> getTipoEstoque() {
        return Util.getListSelectItem(TipoEstoque.values());
    }

    public List<Conta> completarContaReceita(String parte) {
        return acompanhamentoLancamentoContabilFacade.getContaFacade().listaFiltrandoReceita(parte.trim(), selecionado.getExercicio());
    }

    public List<Conta> completarContaDespesa(String parte) {
        if (TipoEventoContabil.LIQUIDACAO_DESPESA.equals(selecionado.getTipoEventoContabil())
            || TipoEventoContabil.LIQUIDACAO_RESTO_PAGAR.equals(selecionado.getTipoEventoContabil())) {
            return acompanhamentoLancamentoContabilFacade.getContaFacade().listaFiltrandoContaDespesa(parte.trim(), getExercicioFiltro());
        }
        return acompanhamentoLancamentoContabilFacade.getContaFacade().listaFiltrandoDespesaCriteria(parte.trim(), getExercicioFiltro());
    }

    public List<Conta> completarContaExtraorcamentaria(String parte) {
        return acompanhamentoLancamentoContabilFacade.getContaFacade().listaFiltrandoExtraorcamentario(parte.trim(), selecionado.getExercicio());
    }

    public List<FonteDeRecursos> completarFonteDeRecursos(String parte) {
        if (TipoEventoContabil.LIQUIDACAO_RESTO_PAGAR.equals(selecionado.getTipoEventoContabil())) {
            return acompanhamentoLancamentoContabilFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), getExercicioAnterior());
        }
        return acompanhamentoLancamentoContabilFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), getExercicioFiltro());
    }

    private Exercicio getExercicioFiltro() {
        if (isTipoEventoContabilResto()) {
            return getExercicioAnterior();
        } else {
            return selecionado.getExercicio();
        }
    }

    private Exercicio getExercicioAnterior() {
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(selecionado.getDataInicial());
        return acompanhamentoLancamentoContabilFacade.getExercicioFacade().getExercicioPorAno(c.get(Calendar.YEAR) - 1);
    }

    private Boolean isTipoEventoContabilResto() {
        if (TipoEventoContabil.CANCELAMENTO_RESTO_PAGAR.equals(selecionado.getTipoEventoContabil())
            || TipoEventoContabil.RESTO_PAGAR.equals(selecionado.getTipoEventoContabil())
            || TipoEventoContabil.PAGAMENTO_RESTO_PAGAR.equals(selecionado.getTipoEventoContabil())) {
            return true;
        }
        return false;
    }

    public List<Pessoa> completarFornecedor(String parte) {
        return acompanhamentoLancamentoContabilFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<ContaBancariaEntidade> completarContaBancariaEntidade(String parte) {
        try {
            return acompanhamentoLancamentoContabilFacade.getContaBancariaEntidadeFacade().listaFiltrandoAtivaPorUnidade(parte.trim(), null, selecionado.getExercicio(), null, null, null);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<SubConta> completarContaFinanceira(String parte) {
        try {
            return acompanhamentoLancamentoContabilFacade.getSubContaFacade().buscarContasFinanceirasPorBancariaEntidadeOuTodosPorUnidadeAndContaDeDestinacao(parte.trim(), selecionado.getContaBancariaEntidade(), null, selecionado.getExercicio(), null, null, null, true);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public void atribuirNullContaFinanceira() {
        selecionado.setContaFinanceira(null);
    }

    public void definirContaBancaria() {
        try {
            selecionado.setContaBancariaEntidade(selecionado.getContaFinanceira().getContaBancariaEntidade());
            List<SelectItem> fontesDeRecursosConcedida = getFontesDeRecursos();
            if (!fontesDeRecursosConcedida.isEmpty()) {
                selecionado.setFonteDeRecursos((FonteDeRecursos) fontesDeRecursosConcedida.get(0).getValue());
            }
        } catch (Exception e) {
            logger.error("erro ao setar conta bancária " + e.getMessage());
        }
    }

    public List<SelectItem> getFontesDeRecursos() {
        List<SelectItem> dados = new ArrayList<SelectItem>();
        if (selecionado.getContaFinanceira() != null) {
            for (FonteDeRecursos fonte : acompanhamentoLancamentoContabilFacade.getFonteDeRecursosFacade().listaFonteDeRecursosPorContaFinanceira(selecionado.getContaFinanceira(), selecionado.getExercicio())) {
                dados.add(new SelectItem(fonte, fonte.toString()));
            }
        }
        return Util.ordenaSelectItem(dados);
    }


    public List<SelectItem> getClassesCredor() {
        List<SelectItem> list = new ArrayList<>();
        list.add(new SelectItem(null, ""));
        if (selecionado.getCredor() != null) {
            List<ClasseCredor> classesDoFornecedor = acompanhamentoLancamentoContabilFacade.getPessoaFacade().getClasseCredorFacade().buscarClassesPorPessoa("", selecionado.getCredor());
            for (ClasseCredor classeCredor : classesDoFornecedor) {
                list.add(new SelectItem(classeCredor));
            }
        }
        return list;

    }

    public List<GrupoMaterial> completarGrupoMaterial(String parte) {
        try {
            return acompanhamentoLancamentoContabilFacade.getGrupoMaterialFacade().listaFiltrandoGrupoDeMaterial(parte);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<GrupoBem> completarGrupoPatrimonial(String parte) {
        try {
            TipoBem tipoBem = getTipoBem();
            return acompanhamentoLancamentoContabilFacade.getGrupoBemFacade().buscarGrupoBemPorTipoBem(parte, tipoBem);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    private TipoBem getTipoBem() {
        if (TipoEventoContabil.BENS_MOVEIS.equals(selecionado.getTipoEventoContabil())
            || TipoEventoContabil.TRANSFERENCIA_BENS_MOVEIS.equals(selecionado.getTipoEventoContabil())) {
            return TipoBem.MOVEIS;
        }
        if (TipoEventoContabil.BENS_IMOVEIS.equals(selecionado.getTipoEventoContabil())
            || TipoEventoContabil.TRANSFERENCIA_BENS_IMOVEIS.equals(selecionado.getTipoEventoContabil())) {
            return TipoBem.IMOVEIS;
        }
        if (TipoEventoContabil.BENS_INTANGIVEIS.equals(selecionado.getTipoEventoContabil())
            || TipoEventoContabil.TRANSFERENCIA_BENS_INTANGIVEIS.equals(selecionado.getTipoEventoContabil())) {
            return TipoBem.INTANGIVEIS;
        }
        return TipoBem.MOVEIS;
    }

    public Boolean mostrarGrupoBem(){
        return mostrarBensImoveis() || mostrarBensMoveis() || mostrarBensIntangiveis();
    }

    //renders
    public Boolean mostrarCredor() {
        if (selecionado.getTipoEventoContabil() != null
            && selecionado.isTipoEventoContabil(Arrays.asList(TipoEventoContabil.EMPENHO_DESPESA,
            TipoEventoContabil.RESTO_PAGAR,
            TipoEventoContabil.LIQUIDACAO_DESPESA,
            TipoEventoContabil.LIQUIDACAO_RESTO_PAGAR,
            TipoEventoContabil.PAGAMENTO_DESPESA,
            TipoEventoContabil.PAGAMENTO_RESTO_PAGAR,
            TipoEventoContabil.RECEITA_EXTRA_ORCAMENTARIA,
            TipoEventoContabil.DESPESA_EXTRA_ORCAMENTARIA,
            TipoEventoContabil.RESPONSABILIDADE_VTB,
            TipoEventoContabil.OBRIGACAO_APAGAR,
            TipoEventoContabil.CREDITO_RECEBER,
            TipoEventoContabil.DIARIAS_CIVIL,
            TipoEventoContabil.DIARIA_CAMPO,
            TipoEventoContabil.SUPRIMENTO_FUNDO))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean mostrarFinanceiro() {
        if (selecionado.getTipoEventoContabil() != null
            && selecionado.isTipoEventoContabil(Arrays.asList(TipoEventoContabil.LIBERACAO_FINANCEIRA,
            TipoEventoContabil.TRANSFERENCIA_MESMA_UNIDADE,
            TipoEventoContabil.TRANSFERENCIA_FINANCEIRA,
            TipoEventoContabil.PAGAMENTO_DESPESA,
            TipoEventoContabil.DESPESA_EXTRA_ORCAMENTARIA,
            TipoEventoContabil.AJUSTE_ATIVO_DISPONIVEL))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean mostrarReceita() {
        if (selecionado.getTipoEventoContabil() != null
            && selecionado.isTipoEventoContabil(Arrays.asList(TipoEventoContabil.RECEITA_REALIZADA,
            TipoEventoContabil.PREVISAO_INICIAL_RECEITA,
            TipoEventoContabil.PREVISAO_ADICIONAL_RECEITA,
            TipoEventoContabil.CREDITO_RECEBER
        ))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean mostrarDespesa() {
        if (selecionado.getTipoEventoContabil() != null
            && selecionado.isTipoEventoContabil(Arrays.asList(TipoEventoContabil.EMPENHO_DESPESA,
            TipoEventoContabil.RESTO_PAGAR,
            TipoEventoContabil.LIQUIDACAO_DESPESA,
            TipoEventoContabil.LIQUIDACAO_RESTO_PAGAR,
            TipoEventoContabil.CREDITO_INICIAL,
            TipoEventoContabil.CREDITO_ADICIONAL,
            TipoEventoContabil.PAGAMENTO_DESPESA,
            TipoEventoContabil.PAGAMENTO_RESTO_PAGAR,
            TipoEventoContabil.OBRIGACAO_APAGAR,
            TipoEventoContabil.DIARIAS_CIVIL,
            TipoEventoContabil.DIARIA_CAMPO,
            TipoEventoContabil.SUPRIMENTO_FUNDO))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean mostrarFonte() {
        if (selecionado.getTipoEventoContabil() != null
            && selecionado.isTipoEventoContabil(Arrays.asList(TipoEventoContabil.EMPENHO_DESPESA,
            TipoEventoContabil.RESTO_PAGAR,
            TipoEventoContabil.LIQUIDACAO_DESPESA,
            TipoEventoContabil.LIQUIDACAO_RESTO_PAGAR,
            TipoEventoContabil.CREDITO_INICIAL,
            TipoEventoContabil.CREDITO_ADICIONAL,
            TipoEventoContabil.PAGAMENTO_DESPESA,
            TipoEventoContabil.PAGAMENTO_RESTO_PAGAR,
            TipoEventoContabil.RECEITA_EXTRA_ORCAMENTARIA,
            TipoEventoContabil.DESPESA_EXTRA_ORCAMENTARIA,
            TipoEventoContabil.LIBERACAO_FINANCEIRA,
            TipoEventoContabil.TRANSFERENCIA_MESMA_UNIDADE,
            TipoEventoContabil.TRANSFERENCIA_FINANCEIRA,
            TipoEventoContabil.DIARIAS_CIVIL,
            TipoEventoContabil.DIARIA_CAMPO,
            TipoEventoContabil.OBRIGACAO_APAGAR,
            TipoEventoContabil.SUPRIMENTO_FUNDO,
            TipoEventoContabil.CREDITO_INICIAL,
            TipoEventoContabil.PREVISAO_INICIAL_RECEITA,
            TipoEventoContabil.CREDITO_ADICIONAL,
            TipoEventoContabil.PREVISAO_ADICIONAL_RECEITA,
            TipoEventoContabil.AJUSTE_ATIVO_DISPONIVEL))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean mostrarExtraOrcamentario() {
        if (selecionado.getTipoEventoContabil() != null
            && selecionado.isTipoEventoContabil(Arrays.asList(TipoEventoContabil.AJUSTE_DEPOSITO,
            TipoEventoContabil.RECEITA_EXTRA_ORCAMENTARIA,
            TipoEventoContabil.DESPESA_EXTRA_ORCAMENTARIA))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean mostrarBensMoveis() {
        if (selecionado.getTipoEventoContabil() != null
            && selecionado.isTipoEventoContabil(Arrays.asList(TipoEventoContabil.BENS_MOVEIS,
            TipoEventoContabil.TRANSFERENCIA_BENS_MOVEIS))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean mostrarBensImoveis() {
        if (selecionado.getTipoEventoContabil() != null
            && selecionado.isTipoEventoContabil(Arrays.asList(TipoEventoContabil.BENS_IMOVEIS,
            TipoEventoContabil.TRANSFERENCIA_BENS_IMOVEIS))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean mostrarBensIntangiveis() {
        if (selecionado.getTipoEventoContabil() != null
            && selecionado.isTipoEventoContabil(Arrays.asList(TipoEventoContabil.BENS_INTANGIVEIS,
            TipoEventoContabil.TRANSFERENCIA_BENS_INTANGIVEIS))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean mostrarBensEstoque() {
        if (selecionado.getTipoEventoContabil() != null
            && selecionado.isTipoEventoContabil(Arrays.asList(TipoEventoContabil.BENS_ESTOQUE,
            TipoEventoContabil.TRANSFERENCIA_BENS_ESTOQUE))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean mostrarDividaPublica() {
        if (selecionado.getTipoEventoContabil() != null
            && selecionado.isTipoEventoContabil(Arrays.asList(TipoEventoContabil.DIVIDA_PUBLICA,
            TipoEventoContabil.PROVISAO_MATEMATICA_PREVIDENCIARIA))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
