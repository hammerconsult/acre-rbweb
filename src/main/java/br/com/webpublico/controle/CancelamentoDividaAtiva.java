package br.com.webpublico.controle;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.ItemInscricaoDividaAtiva;
import br.com.webpublico.entidadesauxiliares.AssistenteCancelamentoDA;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaDividaAtiva;
import br.com.webpublico.entidadesauxiliares.ResultadoPesquisaDividaAtiva;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.InscricaoDividaAtivaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean(name = "cancelamentoDividaAtiva")
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "pesquisaCancelamentoDividaAtiva",
        pattern = "/cancelamento-de-divida-ativa/pesquisar/",
        viewId = "/faces/tributario/dividaativa/cancelamentoinscricaodividaativa/lista.xhtml"),
    @URLMapping(id = "logCancelamentoDividaAtiva",
        pattern = "/cancelamento-de-divida-ativa/log/",
        viewId = "/faces/tributario/dividaativa/cancelamentoinscricaodividaativa/log.xhtml"),
    @URLMapping(id = "resumoCancelamentoDividaAtiva",
        pattern = "/cancelamento-de-divida-ativa/resumo/",
        viewId = "/faces/tributario/dividaativa/cancelamentoinscricaodividaativa/resumo.xhtml"),
})
public class CancelamentoDividaAtiva implements Serializable {

    private final static int QUANTIDADE_DE_REGISTROS = 200;
    private final static Logger logger = LoggerFactory.getLogger(CancelamentoDividaAtiva.class);
    @EJB
    private InscricaoDividaAtivaFacade inscricaoDividaAtivaFacade;
    private FiltroPesquisaDividaAtiva filtros;
    private List<ResultadoPesquisaDividaAtiva> itens;
    private List<ResultadoPesquisaDividaAtiva> selecionados;
    private AssistenteCancelamentoDA assistente;
    private List<Future<AssistenteCancelamentoDA>> futures;
    private Integer inicioPesquisa;
    private Divida divida;

    @URLAction(mappingId = "pesquisaCancelamentoDividaAtiva", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        filtros = new FiltroPesquisaDividaAtiva();
        filtros.setMaxResults(10);
        itens = Lists.newArrayList();
        inicioPesquisa = 0;
        divida = null;
        futures = new ArrayList<>();
        selecionados = Lists.newArrayList();
    }

    @URLAction(mappingId = "logCancelamentoDividaAtiva", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void cancelarLog() {
        try {
            selecionados = (List<ResultadoPesquisaDividaAtiva>) Web.pegaDaSessao(selecionados.getClass());
            futures = Lists.newArrayList();
            validarItensSelecionados(selecionados);
            List<ItemInscricaoDividaAtiva> recuperados = recuperarItensInscricaoDividaAtiva(selecionados);
            assistente = new AssistenteCancelamentoDA(recuperados, getSistemaControlador().getUsuarioCorrente());
            int partes = recuperados.size() > 5 ? ((recuperados.size() / 5) + 1) : recuperados.size();
            List<List<ItemInscricaoDividaAtiva>> listaParte = Lists.partition(recuperados, partes);
            for (List<ItemInscricaoDividaAtiva> itens : listaParte) {
                futures.add(inscricaoDividaAtivaFacade.cancelarInscricoesDividaAtiva(itens, assistente));
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public void cancelarInscricoes() {
        try {
            validarItensSelecionados(selecionados);
            FacesUtil.executaJavaScript("cancelarInscricoes.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void efetivarCancelamentoInscricoes() {
        Web.navegacao("/cancelamento-de-divida-ativa/pesquisar/", "/cancelamento-de-divida-ativa/log/", selecionados);
    }

    public List<ItemInscricaoDividaAtiva> recuperarItensInscricaoDividaAtiva(List<ResultadoPesquisaDividaAtiva> lista) {
        List<ItemInscricaoDividaAtiva> itens = new ArrayList<>();
        for (ResultadoPesquisaDividaAtiva resultado : lista) {
            ItemInscricaoDividaAtiva item = new ItemInscricaoDividaAtiva();
            item.setId(resultado.getItemInscricaoId());
            itens.add(item);
        }
        return itens;
    }

    private void validarItensSelecionados(List<ResultadoPesquisaDividaAtiva> lista) {
        ValidacaoException ve = new ValidacaoException();
        if (lista.size() == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione pelo menos uma inscrição!");
        } else {
            boolean temAjuizada = false;
            for (ResultadoPesquisaDividaAtiva itemSelecionado : lista) {
                if (itemSelecionado.getAjuizada()) {
                    temAjuizada = true;
                }
            }
            if (temAjuizada && !hasPermissaoParaCancelarAjuizada()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido cancelar uma inscrição em Dívida Ativa Ajuizada!");
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void terminarCancelamento() {
        boolean terminou = false;
        if (futures != null && !futures.isEmpty()) {
            terminou = true;
            for (Future<AssistenteCancelamentoDA> future : futures) {
                if (!future.isDone()) {
                    terminou = false;
                    break;
                }
            }
        }
        if (terminou) {
            FacesUtil.redirecionamentoInterno("/cancelamento-de-divida-ativa/resumo/");
        }
    }

    public AssistenteCancelamentoDA getAssistente() {
        return assistente;
    }

    public FiltroPesquisaDividaAtiva getFiltros() {
        return filtros;
    }

    public List<ResultadoPesquisaDividaAtiva> getItens() {
        return itens;
    }

    public void filtrar() {
        if (divida != null) {
            addDivida();
        }
        filtros.setSituacao(ItemInscricaoDividaAtiva.Situacao.ATIVO);
        itens = inscricaoDividaAtivaFacade.buscarItensInscricao(filtros, inicioPesquisa);
    }

    public void pesquisar() {
        filtrar();
        filtros.setTotalRegistros(itens.size());
        if (filtros.getTotalRegistros() <= 0) {
            FacesUtil.addOperacaoNaoRealizada("Não foram encontradas Dívidas Ativas abertas para o filtros informados!");
        }
    }

    public List<SelectItem> getTiposCadastro() {
        return TipoCadastroTributario.asSelectItemList();
    }

    public void limpaCadastros() {
        filtros.setCadastroInicial("");
        filtros.setCadastroFinal("");
        filtros.setPessoa(null);
    }

    public int getMaximoDigitosCadastros() {
        if (filtros.getTipoCadastro() != null) {
            switch (filtros.getTipoCadastro()) {
                case IMOBILIARIO:
                    return 15;
                case ECONOMICO:
                    return 7;
                case RURAL:
                    return 20;
            }
        }
        return 0;
    }

    public void proximaPagina() {
        inicioPesquisa += filtros.getMaxResults();
        filtrar();
    }

    public void paginaAnterior() {
        inicioPesquisa -= filtros.getMaxResults();
        filtrar();
    }

    public void primeiraPagina() {
        inicioPesquisa = 0;
        filtrar();
    }

    public void ultimaPagina() {
        int resto = filtros.getTotalRegistros() % filtros.getMaxResults();
        inicioPesquisa = filtros.getTotalRegistros() - resto;
        filtrar();
    }

    public Integer getInicioPesquisa() {
        return inicioPesquisa;
    }

    public Integer getPaginaAtual() {
        try {
            Double inicialD = inicioPesquisa.doubleValue();
            Double maximoD = filtros.getMaxResults().doubleValue();
            Double totalD = filtros.getTotalRegistros().doubleValue();

            Double pDivisao = totalD / maximoD;
            Double psoma = maximoD + inicialD;
            Double pMultiplicacao = pDivisao * psoma;
            pMultiplicacao = Math.ceil(pMultiplicacao);

            Double pResultado = pMultiplicacao / totalD;

            return pResultado.intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public void removeDivida(Divida divida) {
        filtros.getDividas().remove(divida);
    }

    public void addDivida() {
        if (divida != null && !filtros.getDividas().contains(divida)) {
            filtros.getDividas().add(divida);
            divida = null;
        }
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (filtros.getTipoCadastro() != null) {
            List<Divida> dividas = filtros.getTipoCadastro().equals(TipoCadastroTributario.PESSOA) ?
                inscricaoDividaAtivaFacade.getDividaFacade().lista() :
                inscricaoDividaAtivaFacade.getDividaFacade().listaDividasPorTipoCadastro(filtros.getTipoCadastro());
            for (Divida divida : dividas) {
                toReturn.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return toReturn;
    }

    public Boolean isTodosItensSelecionados() {
        for (ResultadoPesquisaDividaAtiva item : itens) {
            if (!selecionados.contains(item)) {
                return Boolean.FALSE;
            }
        }
        return !selecionados.isEmpty();
    }

    public void desmarcarTodos() {
        for (ResultadoPesquisaDividaAtiva iten : itens) {
            if (selecionados.contains(iten)) {
                selecionados.remove(iten);
            }
        }
    }

    public void selecionarTodos() {
        for (ResultadoPesquisaDividaAtiva selecionado : itens) {
            if (!selecionados.contains(selecionado)) {
                if (selecionados.size() < QUANTIDADE_DE_REGISTROS) {
                    if (!selecionado.getAjuizada() || hasPermissaoParaCancelarAjuizada()) {
                        selecionados.add(selecionado);
                    }
                } else {
                    FacesUtil.addOperacaoNaoPermitida("Não é permitido selecionar mais registros, já foram selecionados um lote com " + QUANTIDADE_DE_REGISTROS + " inscrições.");
                }
            }
        }
    }

    public void selecionarItem(ResultadoPesquisaDividaAtiva item) {
        if (!selecionados.contains(item)) {
            if (selecionados.size() < QUANTIDADE_DE_REGISTROS) {
                if (!item.getAjuizada() || hasPermissaoParaCancelarAjuizada()) {
                    selecionados.add(item);
                }
            } else {
                FacesUtil.addOperacaoNaoPermitida("Não é permitido selecionar mais registros, já foram selecionados um lote com " + QUANTIDADE_DE_REGISTROS + " inscrições.");
            }
        }
    }

    public void desmarcarItem(ResultadoPesquisaDividaAtiva item) {
        if (selecionados.contains(item)) {
            selecionados.remove(item);
        }
    }

    public Boolean isSelecionadoItem(ResultadoPesquisaDividaAtiva item) {
        if (selecionados.contains(item)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public boolean hasPermissaoParaCancelarAjuizada() {
        return inscricaoDividaAtivaFacade.hasPermissaoParaCancelarAjuizada(getSistemaControlador().getUsuarioCorrente());
    }
}
