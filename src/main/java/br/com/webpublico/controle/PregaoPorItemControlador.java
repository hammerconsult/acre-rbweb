/*
 * Codigo gerado automaticamente em Tue Dec 13 12:09:35 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.administrativo.licitacao.ItemPregaoLanceVencedor;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PregaoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-pregao-por-item", pattern = "/pregao/por-item/novo/", viewId = "/faces/administrativo/licitacao/pregao/por-item/edita.xhtml"),
    @URLMapping(id = "editar-pregao-por-item", pattern = "/pregao/por-item/editar/#{pregaoPorItemControlador.id}/", viewId = "/faces/administrativo/licitacao/pregao/por-item/edita.xhtml"),
    @URLMapping(id = "ver-pregao-por-item", pattern = "/pregao/por-item/ver/#{pregaoPorItemControlador.id}/", viewId = "/faces/administrativo/licitacao/pregao/por-item/visualizar.xhtml"),
    @URLMapping(id = "cancelar-pregao-por-item", pattern = "/pregao/por-item/cancelar-item/#{pregaoPorItemControlador.id}/", viewId = "/faces/administrativo/licitacao/pregao/por-item/cancelar-item.xhtml"),
    @URLMapping(id = "listar-pregao-por-item", pattern = "/pregao/por-item/listar/", viewId = "/faces/administrativo/licitacao/pregao/por-item/lista.xhtml")
})

public class PregaoPorItemControlador extends PrettyControlador<Pregao> implements Serializable, CRUD {

    @EJB
    private PregaoFacade facade;
    private ItemPregao itemPregaoSelecionado;
    private RodadaPregao rodadaPregaoSelecionada;
    private List<ItemPropostaFornecedor> listaDeItemPropostaFornecedorAuxFiltrada;
    private List<ItemPropostaFornecedor> listaDeItemPropostaFornecedorParaLances;
    private List<ItemPregao> itensPregaoNaoCotados;
    private LancePregao lancePregaoQueAuxiliaNaFinalizaoDoPregao;
    private int indiceAba;
    private Pessoa fornecedorMenorValorMicroPequenaEmpresa;
    private LancePregao lancePregaoSegurado;
    private IntencaoDeRecurso intencaoDeRecurso;

    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;

    public PregaoPorItemControlador() {
        super(Pregao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-pregao-por-item", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        indiceAba = 0;
        selecionado.setRealizadoEm(facade.getSistemaFacade().getDataOperacao());
        listaDeItemPropostaFornecedorParaLances = Lists.newArrayList();
    }

    @URLAction(mappingId = "editar-pregao-por-item", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        operacao = Operacoes.EDITAR;
        recuperarEditarVer();
        try {
            facade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
            validarEdicaoPregao();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            redirecionaParaVer();
        }
    }

    @URLAction(mappingId = "ver-pregao-por-item", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        verPregao();
    }

    private void verPregao() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarPregao(getId());
        listaDeItemPropostaFornecedorAuxFiltrada = new ArrayList<>();
        listaDeItemPropostaFornecedorParaLances = new ArrayList<>();
        for (ItemPregao item : selecionado.getListaDeItemPregao()) {
            item.setLancesVencedores(facade.buscarItensPregaoLanceVencedor(item));
        }
    }

    @URLAction(mappingId = "cancelar-pregao-por-item", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void cancelarItens() {
        verPregao();
        buscarItensNaoCotados();

    }

    public void buscarItensNaoCotados() {
        itensPregaoNaoCotados = Lists.newArrayList();
        for (ItemPregao itemPregao : selecionado.getListaDeItemPregao()) {
            if (itemPregao.getTipoStatusItemPregao() == null) {
                itensPregaoNaoCotados.add(itemPregao);
            }
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/pregao/por-item/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void excluir() {
        try {
            validarExclusao();
            facade.excluirPregao(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void verificarItensNaoCotados() {
        buscarItensNaoCotados();
        if (!itensPregaoNaoCotados.isEmpty()) {
            FacesUtil.executaJavaScript("dlgItensNaoCotados.show()");
            FacesUtil.atualizarComponente("formItensNaoCotados");
        } else {
            FacesUtil.addAtencao("Todos os itens do pregão já possuem um status definido.");
        }
    }

    public String linkCancelarItensPregao() {
        return "Existem itens do pregão não cotados, deseja cancelar esses itens? " + Util.linkBlack("/pregao/por-item/cancelar-item/" + selecionado.getId() + "/", "Clique no link para cancelar os itens no pregão.");
    }

    public void iniciarCancelamentoItensPregao() {
        itemPregaoSelecionado = new ItemPregao();
        FacesUtil.executaJavaScript("dlgCancelarItem.show()");
    }

    public void cancelarItensPregao() {
        try {
            if (itemPregaoSelecionado.getTipoStatusItemPregao() == null) {
                FacesUtil.addCampoObrigatorio("O campo situação deve ser informado.");
                FacesUtil.executaJavaScript("aguarde.hide()");
                return;
            }
            facade.cancelarItensPregao(itensPregaoNaoCotados, itemPregaoSelecionado.getTipoStatusItemPregao());
            FacesUtil.addOperacaoRealizada("Cancelamento de itens realizado com sucesso.");
            redirecionaParaVer();
        } catch (StatusLicitacaoException se) {
            redireciona();
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarSePregaoPossuiItensComRodadas();
            facade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
            selecionado = facade.salvarPregao(selecionado);
            redirecionaParaVer();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (StatusLicitacaoException se) {
            redireciona();
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void visualizarFornecedores(ItemPregao itemPregao) {
        itemPregaoSelecionado = facade.buscarRodadasAndLancesPregao(itemPregao);
        listaDeItemPropostaFornecedorAuxFiltrada = Lists.newArrayList();
        listaDeItemPropostaFornecedorParaLances = Lists.newArrayList();
        listaDeItemPropostaFornecedorAuxFiltrada = facade.getPropostaFornecedorFacade().recuperarItemPropostaFornecedorPorItemProcessoCompra(itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra());
        if (!listaDeItemPropostaFornecedorAuxFiltrada.isEmpty()) {
            if (itemPregaoSelecionado.getListaDeRodadaPregao() != null && !itemPregaoSelecionado.getListaDeRodadaPregao().isEmpty()) {
                recuperarFornecedoresSelecionados();
                Collections.sort(listaDeItemPropostaFornecedorParaLances);
            } else {
                listaDeItemPropostaFornecedorParaLances.addAll(listaDeItemPropostaFornecedorAuxFiltrada);
            }
        }
    }

    public void visualizarRodadasAndLances(ItemPregao itemPregao) {
        try {
            itemPregaoSelecionado = facade.buscarRodadasAndLancesPregao(itemPregao);
            ordenarRodadas();
            if (itemPregaoSelecionado.getListaDeRodadaPregao().size() > 0) {
                RodadaPregao rodadaPregao = retornarUltimaRodadaDoPregao();
                if (rodadaPregao != null) {
                    rodadaPregaoSelecionada = rodadaPregao;
                    listaDeItemPropostaFornecedorParaLances = facade.getPropostaFornecedorFacade().recuperarItemPropostaFornecedorPorItemProcessoCompra(itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra());
                    for (RodadaPregao rodada : itemPregaoSelecionado.getListaDeRodadaPregao()) {
                        selecionado.atribuirValorLanceRodadaAnterior(itemPregaoSelecionado, rodada);

                        for (LancePregao lancePregao : rodada.getListaDeLancePregao()) {
                            facade.atribuirValorPrimeiraPropostaNoLance(lancePregao, listaDeItemPropostaFornecedorParaLances);
                        }
                    }
                    FacesUtil.executaJavaScript("dialogRodadas.show()");
                }
            }
        } catch (ArrayIndexOutOfBoundsException a) {
            FacesUtil.addAtencao("O item: " + itemPregaoSelecionado.getItemPregaoItemProcesso().getItemProcessoDeCompra() + " não possui rodadas para serem exibidas.");
        }
    }

    public void novaItencaoRecurso() {
        intencaoDeRecurso = new IntencaoDeRecurso();
    }

    public List<Licitacao> completarLicitacao(String parte) {
        return facade.getLicitacaoFacade().buscarLicitacaoPorTipoApuracaoAndTipoSolicitacao(TipoApuracaoLicitacao.ITEM, parte.trim());
    }

    public void cancelarRodadas() {
        if (isOperacaoNovo()) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
        } else {
            indiceAba = 0;
            FacesUtil.atualizarComponente("Formulario:tabGeral");
            selecionado = facade.recuperarPregao(selecionado.getId());
        }
    }

    public void finalizarRodadaParaTodosProcesso() {
        salvarItemPregaoRodadaAndLance();
        FacesUtil.atualizarComponente("Formulario:tabGeral");
        if (isOperacaoNovo()) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
        } else {
            recuperarEditarVer();
        }
        indiceAba = 0;
    }

    private void validarSePregaoPossuiItensComRodadas() {
        ValidacaoException ve = new ValidacaoException();
        List<ItemPregao> itensSemRodadas = Lists.newArrayList();
        List<ItemPregao> itensPregao = isOperacaoNovo() ? selecionado.getListaDeItemPregao() : facade.buscarItensPregao(selecionado);
        for (ItemPregao itemPregao : itensPregao) {
            List<RodadaPregao> rodadas = isOperacaoNovo() ? itemPregao.getListaDeRodadaPregao() : facade.buscarRodadasPregao(itemPregao);
            if (rodadas.isEmpty()) {
                itensSemRodadas.add(itemPregao);
            }
        }
        if (itensSemRodadas.size() == itensPregao.size()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Os itens desse pregão não possuem rodadas de lance. Realizar rodada para ao menos um item.");
        }
        ve.lancarException();
    }

    private void redirecionaParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void definiStatusFornecedor() {
        if (rodadaPregaoSelecionada != null && !rodadaPregaoSelecionada.getListaDeLancePregao().isEmpty()) {
            for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
                StatusLancePregao statusLanceSelecionado = lancePregao.getStatusLancePregao().getStatusLancePregaoPorTipoStatusItemPregao(itemPregaoSelecionado.getTipoStatusItemPregao());
                if (statusLanceSelecionado != null) {
                    lancePregao.setStatusLancePregao(statusLanceSelecionado);
                }
            }
            Util.adicionarObjetoEmLista(itemPregaoSelecionado.getListaDeRodadaPregao(), rodadaPregaoSelecionada);
        }
        if (!TipoStatusItemPregao.EM_ANDAMENTO.equals(itemPregaoSelecionado.getTipoStatusItemPregao())
            && !TipoStatusItemPregao.FINALIZADO.equals(itemPregaoSelecionado.getTipoStatusItemPregao())) {
            verificarNecessidadeCancelamentoItem();
        }
    }

    public List<SelectItem> getTipoStatusItem() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoStatusItemPregao obj : TipoStatusItemPregao.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public void removerIntencaoRecurso(IntencaoDeRecurso ir) {
        selecionado.getListaDeIntencaoDeRecursos().remove(ir);
    }

    private void instanciarItemPregao(List<ItemProcessoDeCompra> listaDeIPDC) {
        for (ItemProcessoDeCompra itemProcessoDeCompra : listaDeIPDC) {
            selecionado.getListaDeItemPregao().add(new ItemPregao(itemProcessoDeCompra, selecionado));
        }
    }

    public int porcentagem(ItemPropostaFornecedor ipf) {
        if (listaDeItemPropostaFornecedorAuxFiltrada.size() > 0) {
            ItemPropostaFornecedor ipfMenorValor = listaDeItemPropostaFornecedorAuxFiltrada.get(0);
            double valorIPF = ipf.getPreco().doubleValue();
            double menorValor = ipfMenorValor.getPreco().doubleValue();
            double multiplicacao = valorIPF * 100;
            double divisao = multiplicacao / menorValor;
            int resto = (int) (divisao - 100);
            if (resto < 0) {
                resto = resto * -1;
            }
            return resto;
        }
        return 0;
    }

    private void salvarItemPregaoRodadaAndLance() {
        atribuirLanceVencedorComoNullParaItemNaoFinalizado();
        itemPregaoSelecionado.setPregao(selecionado);
        itemPregaoSelecionado = facade.salvarItemRodadaEspecifica(itemPregaoSelecionado, rodadaPregaoSelecionada);
    }

    private void atribuirLanceVencedorComoNullParaItemNaoFinalizado() {
        if (!TipoStatusItemPregao.FINALIZADO.equals(itemPregaoSelecionado.getTipoStatusItemPregao())) {
            if (itemPregaoSelecionado.getItemPregaoLanceVencedor() != null) {
                itemPregaoSelecionado.getItemPregaoLanceVencedor().setLancePregao(null);
                itemPregaoSelecionado.setItemPregaoLanceVencedor(null);
            }
            itemPregaoSelecionado.setStatusFornecedorVencedor(null);
        }
    }

    public void recuperarEditarVer() {
        parametrosIniciais();
        selecionado = facade.recuperarPregao(getId());
    }

    private void parametrosIniciais() {
        listaDeItemPropostaFornecedorAuxFiltrada = new ArrayList<>();
        itemPregaoSelecionado = null;
        lancePregaoQueAuxiliaNaFinalizaoDoPregao = new LancePregao();
        rodadaPregaoSelecionada = new RodadaPregao();
        listaDeItemPropostaFornecedorParaLances = new ArrayList<>();
    }

    private boolean isMenorValorMicroPequenaEmpresa() {
        if (fornecedorMenorValorMicroPequenaEmpresa == null) {
            return false;
        }
        if (fornecedorMenorValorMicroPequenaEmpresa instanceof PessoaFisica) {
            return false;
        }
        try {
            if (!TipoEmpresa.NORMAL.equals(((PessoaJuridica) fornecedorMenorValorMicroPequenaEmpresa).getTipoEmpresa())) {
                return true;
            }
        } catch (NullPointerException e) {
            logger.error("isMenorValorMicroPequenaEmpresa {}", e);
        }
        return false;
    }

    public void finalizarRodada() {
        try {
            validarFinalizarRodada();
            LancePregao lanceVencedor = recuperarLanceVencedorDoItemPregao(rodadaPregaoSelecionada);
            definirLanceVencedorParaItemPregao(lanceVencedor);
            FacesUtil.executaJavaScript("dialogPropostaFinal.hide()");
            finalizarRodadaParaTodosProcesso();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.executaJavaScript("dialogPropostaFinal.hide()");
            logger.error("finalizarRodada {}", ex);
        }
    }

    private LancePregao recuperarLanceVencedorDoItemPregao(RodadaPregao rodadaPregaoSelecionada) {
        for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
            if (StatusLancePregao.VENCEDOR.equals(lancePregao.getStatusLancePregao())) {
                return lancePregao;
            }
        }
        return null;
    }

    private void definirLanceVencedorParaItemPregao(LancePregao lanceVencedor) {
        if (itemPregaoSelecionado.getItemPregaoLanceVencedor() == null) {
            itemPregaoSelecionado.setItemPregaoLanceVencedor(new ItemPregaoLanceVencedor());
            itemPregaoSelecionado.getItemPregaoLanceVencedor().setStatus(StatusLancePregao.VENCEDOR);
            itemPregaoSelecionado.getItemPregaoLanceVencedor().setOrigem(OrigemItemPregaoLanceVencedor.PREGAO);
            itemPregaoSelecionado.getItemPregaoLanceVencedor().setItemPregao(itemPregaoSelecionado);
        }
        itemPregaoSelecionado.getItemPregaoLanceVencedor().setLancePregao(lanceVencedor);
        if (selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
            itemPregaoSelecionado.getItemPregaoLanceVencedor().setPercentualDesconto(lanceVencedor.getValorFinal());
            itemPregaoSelecionado.getItemPregaoLanceVencedor().getLancePregao().setPercentualDesconto(lanceVencedor.getValorFinal());
        } else {
            itemPregaoSelecionado.getItemPregaoLanceVencedor().setValor(lanceVencedor.getValorFinal());
            itemPregaoSelecionado.getItemPregaoLanceVencedor().getLancePregao().setValor(lanceVencedor.getValorFinal());
        }
        itemPregaoSelecionado.setStatusFornecedorVencedor(TipoClassificacaoFornecedor.INABILITADO);
    }

    private void validarFinalizarRodada() {
        ValidacaoException ve = new ValidacaoException();
        validarLanceMaiorQueValorReservadoNaDotacao(lancePregaoQueAuxiliaNaFinalizaoDoPregao, ve);
        validarLanceFinal(ve);
        ve.lancarException();
    }

    private void validarLanceFinal(ValidacaoException ve) {
        if (lancePregaoQueAuxiliaNaFinalizaoDoPregao.getValorFinal() == null || lancePregaoQueAuxiliaNaFinalizaoDoPregao.getValorFinal().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Final deve ser informado com valor positivo.");
        }
        ve.lancarException();

        BigDecimal ultimoValor = recuperaUltimoValor();
        if (selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
            if (lancePregaoQueAuxiliaNaFinalizaoDoPregao.getValorFinal() == null ||
                lancePregaoQueAuxiliaNaFinalizaoDoPregao.getValorFinal().compareTo(ultimoValor) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Desconto Final (%)' deve ser informado e deve ser igual ou superior a " + Util.formataPercentual(ultimoValor));
                lancePregaoQueAuxiliaNaFinalizaoDoPregao.setValorFinal(ultimoValor);
            }
        } else {
            if (lancePregaoQueAuxiliaNaFinalizaoDoPregao.getValorFinal() == null ||
                lancePregaoQueAuxiliaNaFinalizaoDoPregao.getValorFinal().compareTo(ultimoValor) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Lance Final' deve ser informado e deve ser igual ou inferior a " + Util.formataValorQuatroCasasDecimais(ultimoValor));
                lancePregaoQueAuxiliaNaFinalizaoDoPregao.setValorFinal(ultimoValor);
            }
        }
    }

    private void preSelecionarFornecedores
        (List<ItemPropostaFornecedor> listaDeItemPropostaFornecedorAuxFiltrada) {
        if (listaDeItemPropostaFornecedorParaLances != null) {
            listaDeItemPropostaFornecedorParaLances.clear();
        }

        if (TipoNaturezaDoProcedimentoLicitacao.ELETRONICO.equals(itemPregaoSelecionado.getPregao().getLicitacao().getProcessoDeCompra().getSolicitacaoMaterial().getTipoNaturezaDoProcedimento())
            || TipoNaturezaDoProcedimentoLicitacao.ELETRONICO_COM_REGISTRO_DE_PRECO.equals(itemPregaoSelecionado.getPregao().getLicitacao().getProcessoDeCompra().getSolicitacaoMaterial().getTipoNaturezaDoProcedimento())) {
            listaDeItemPropostaFornecedorParaLances.addAll(listaDeItemPropostaFornecedorAuxFiltrada);
            return;
        }
        if (itemPregaoSelecionado.getListaDeRodadaPregao().isEmpty()) {
            for (ItemPropostaFornecedor itemPropostaFornecedor : listaDeItemPropostaFornecedorAuxFiltrada) {
                if (selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
                    if (itemPropostaFornecedor.getPercentualDesconto().compareTo(itemPropostaFornecedor.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getPercentualDescontoMinimo()) >= 0) {
                        listaDeItemPropostaFornecedorParaLances.add(itemPropostaFornecedor);
                    }
                } else {
                    if (porcentagem(itemPropostaFornecedor) <= 10) {
                        listaDeItemPropostaFornecedorParaLances.add(itemPropostaFornecedor);
                    }
                }
            }
        } else {
            recuperarFornecedoresSelecionados();
        }
    }

    private void recuperarItensEPreencherListaDeItemPregao() {
        if (selecionado.getLicitacao() != null) {
            List<ItemProcessoDeCompra> listaDeIPDC = facade.buscarItensProcessoCompraLicitacao(selecionado.getLicitacao());
            instanciarItemPregao(listaDeIPDC);
        }
    }

    private boolean permiteUsoDaLicitacao(Licitacao licitacao) {
        licitacao.setListaDeStatusLicitacao(facade.getLicitacaoFacade().recuperarListaDeStatus(licitacao));
        if (!licitacao.getStatusAtualLicitacao().getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.ANDAMENTO)
            && !licitacao.getStatusAtualLicitacao().getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.EM_RECURSO)) {
            return false;
        }
        return true;
    }

    public void preencherListaDeItemPregao(SelectEvent event) {
        preencherListaDeItemPregao((Licitacao) event.getObject());
    }

    public void preencherListaDeItemPregao(Licitacao licitacao) {
        if (permiteUsoDaLicitacao(licitacao)) {
            selecionado.setLicitacao(licitacao);
            selecionado.setListaDeItemPregao(new ArrayList<ItemPregao>());

            recuperarItensEPreencherListaDeItemPregao();

        } else {
            if (selecionado.getListaDeItemPregao() != null) {
                selecionado.getListaDeItemPregao().clear();
            }
            listaDeItemPropostaFornecedorAuxFiltrada = Lists.newArrayList();
            itemPregaoSelecionado = null;
        }
    }

    private Object clonaBean(Object obj) {
        try {
            return BeanUtils.cloneBean(obj);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    public List<SelectItem> getStatusLancePregao() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (StatusLancePregao statusLancePregao : StatusLancePregao.values()) {
            if (rodadaPregaoSelecionada.getListaDeLancePregao().size() == 1) {
                toReturn.add(new SelectItem(statusLancePregao, statusLancePregao.getDescricao()));

            } else if (!statusLancePregao.isVencedor()) {
                toReturn.add(new SelectItem(statusLancePregao, statusLancePregao.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getStatusLancePregaoExcetoAtivo() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (StatusLancePregao statusLancePregao : StatusLancePregao.values()) {
            if (!statusLancePregao.isAtivo()) {
                toReturn.add(new SelectItem(statusLancePregao, statusLancePregao.getDescricao()));
            }
        }
        return toReturn;
    }

    public void reiniciarRodadas() {
        for (RodadaPregao rodadaPregao : itemPregaoSelecionado.getListaDeRodadaPregao()) {
            itemPregaoSelecionado.setStatusFornecedorVencedor(null);
            facade.excluirRodadaAndLances(rodadaPregao, itemPregaoSelecionado);
        }
        itemPregaoSelecionado.setListaDeRodadaPregao(new ArrayList<RodadaPregao>());
        iniciarPregao();
        itemPregaoSelecionado.setTipoStatusItemPregao(itemPregaoSelecionado.definirStatus());
        facade.updateItemPregao(itemPregaoSelecionado);
    }

    public void navegarPrimeraRodada() {
        rodadaPregaoSelecionada = retornarPrimeiraRodadaDoPregao();
    }

    public void navegarUltimaRodada() {
        rodadaPregaoSelecionada = retornarUltimaRodadaDoPregao();
    }

    public void navegarRodadaAnterior() {
        try {
            if (itemPregaoSelecionado.getListaDeRodadaPregao().size() == 1) {
                rodadaPregaoSelecionada = retornarPrimeiraRodadaDoPregao();
            } else {
                rodadaPregaoSelecionada = retornarRodadaAnterior();
            }
        } catch (Exception ex) {
            rodadaPregaoSelecionada = retornarPrimeiraRodadaDoPregao();
        }
    }

    private RodadaPregao retornarRodadaAnterior() {
        return itemPregaoSelecionado.getListaDeRodadaPregao().get(retornarIndiceDaRodadaAnterior());
    }

    private int retornarIndiceDaRodadaAnterior() {
        return itemPregaoSelecionado.getListaDeRodadaPregao().indexOf(rodadaPregaoSelecionada) - 1;
    }

    private RodadaPregao retornarRodadaSeguinteDoPregao() {
        return itemPregaoSelecionado.getListaDeRodadaPregao().get(retornarIndiceDaRodadaSeguinte());
    }

    private int retornarIndiceDaRodadaSeguinte() {
        return itemPregaoSelecionado.getListaDeRodadaPregao().indexOf(rodadaPregaoSelecionada) + 1;
    }

    private RodadaPregao retornarPrimeiraRodadaDoPregao() {
        return itemPregaoSelecionado.getListaDeRodadaPregao().get(0);
    }

    private RodadaPregao retornarUltimaRodadaDoPregao() {
        ordenarRodadas();
        return itemPregaoSelecionado.getListaDeRodadaPregao().get(itemPregaoSelecionado.getListaDeRodadaPregao().size() - 1);
    }

    public void navegarProximaRodada() {
        try {
            if (itemPregaoSelecionado.getListaDeRodadaPregao().size() == 1) {
                rodadaPregaoSelecionada = retornarUltimaRodadaDoPregao();
            } else {
                rodadaPregaoSelecionada = retornarRodadaSeguinteDoPregao();
            }
        } catch (Exception ex) {
            rodadaPregaoSelecionada = retornarUltimaRodadaDoPregao();
        }
    }

    public void excluirRodada() {
        try {
            itemPregaoSelecionado.validarExclusaoRodada();
            RodadaPregao rodadaParaExcluir = rodadaPregaoSelecionada;
            if (itemPregaoSelecionado.getListaDeRodadaPregao().size() == 1) {
                itemPregaoSelecionado.setListaDeRodadaPregao(new ArrayList<RodadaPregao>());
                itemPregaoSelecionado.getItemPregaoLanceVencedor().setLancePregao(null);
                iniciarPregao();
            } else {
                itemPregaoSelecionado.getListaDeRodadaPregao().remove(rodadaPregaoSelecionada);
                for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
                    if (StatusLancePregao.VENCEDOR.equals(lancePregao.getStatusLancePregao())
                        && itemPregaoSelecionado.getItemPregaoLanceVencedor().getLancePregao().equals(lancePregao)) {
                        itemPregaoSelecionado.getItemPregaoLanceVencedor().setLancePregao(null);
                    }
                }
                itemPregaoSelecionado.getListaDeRodadaPregao().remove(rodadaPregaoSelecionada);
                renumerarRodadas(itemPregaoSelecionado.getListaDeRodadaPregao());
            }
            itemPregaoSelecionado.setTipoStatusItemPregao(itemPregaoSelecionado.definirStatus());
            facade.excluirRodadaAndLances(rodadaParaExcluir, itemPregaoSelecionado);
            ordenarRodadas();
            rodadaPregaoSelecionada = retornarUltimaRodadaDoPregao();
            indiceAba = 1;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void ordenarRodadas() {
        Collections.sort(itemPregaoSelecionado.getListaDeRodadaPregao());
    }

    private void validarLanceMaiorQueValorReservadoNaDotacao(LancePregao lancePregao, ValidacaoException ve) {
        if (!selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
            if (lancePregao.getStatusLancePregao().isAtivoOrVencedor()) {
                BigDecimal valorReservado = getValorReservadoNaDotacao(lancePregao);
                if (lancePregao.getValor().compareTo(valorReservado) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do lance do fornecedor " + lancePregao.getPropostaFornecedor().getFornecedor().getNome()
                        + " é maior que o valor reservado. Reserva de Dotação: " + Util.formataValorQuatroCasasDecimais(valorReservado));
                }
            }
        }
        ve.lancarException();
    }

    private BigDecimal getValorReservadoNaDotacao(LancePregao lp) {
        List<ItemPropostaFornecedor> itensProposta = facade.getPropostaFornecedorFacade().recuperaItensDaPropostaFornecedor(lp.getPropostaFornecedor());
        for (ItemPropostaFornecedor ipf : itensProposta) {
            if (ipf.getItemProcessoDeCompra().equals(itemPregaoSelecionado.getItemPregaoItemProcesso().getItemProcessoDeCompra())) {
                return ipf.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getPreco();
            }
        }
        return BigDecimal.ZERO;
    }

    private boolean isLanceMaiorQueMenorValor(LancePregao lancePregao, BigDecimal menorValor) {
        if (lancePregao.getStatusLancePregao().isAtivoOrVencedor()) {
            if (lancePregao.getValor() != null && lancePregao.getValor().compareTo(menorValor) == 1) {
                return true;
            }
        }
        return false;
    }

    private boolean isLanceMenorQueMaiorDesconto(LancePregao lancePregao, BigDecimal maiorDesconto) {
        if (lancePregao.getStatusLancePregao().isAtivo()) {
            if (lancePregao.getPercentualDesconto().compareTo(maiorDesconto) <= 0) {
                return true;
            }
        } else if (lancePregao.getStatusLancePregao().isVencedor()) {
            if (lancePregao.getPercentualDesconto().compareTo(maiorDesconto) < 0) {
                return true;
            }
        }
        return false;
    }

    private void validarLanceZerado(LancePregao lancePregao) {
        ValidacaoException ve = new ValidacaoException();
        if (lancePregao.getStatusLancePregao().isAtivoOrVencedor()) {
            boolean maiorDesconto = selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto();
            BigDecimal valor = maiorDesconto ? lancePregao.getPercentualDesconto() : lancePregao.getValor();
            String descricao = maiorDesconto ? "desconto atual" : "lance atual";

            if (valor == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo " + descricao + " deve ser informado.");

            } else if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O " + descricao + " do lance do fornecedor " + lancePregao.getPropostaFornecedor().getFornecedor().getNome() + " está zerado!");
            }
        }
        ve.lancarException();
    }

    private BigDecimal getMenorValorRodadaAnterior() {
        BigDecimal menorValor = recuperarPropostaAnterior(rodadaPregaoSelecionada.getListaDeLancePregao().get(0));
        for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
            if (recuperarPropostaAnterior(lancePregao).compareTo(menorValor) == -1 && lancePregao.getStatusLancePregao().isAtivo()) {
                menorValor = recuperarPropostaAnterior(lancePregao);
                this.setFornecedorMenorValorMicroPequenaEmpresa(lancePregao.getPropostaFornecedor().getFornecedor());
            }
        }
        return menorValor;
    }

    private BigDecimal getMaiorDescontoRodada() {
        BigDecimal maiorDesconto = BigDecimal.ZERO;
        for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
            if (lancePregao.getStatusLancePregao().isAtivoOrVencedor()) {
                BigDecimal percentualAtual = BigDecimal.ZERO;
                if (rodadaPregaoSelecionada.getNumero() == 1) {
                    facade.atribuirValorPrimeiraPropostaNoLance(lancePregao, listaDeItemPropostaFornecedorParaLances);
                    percentualAtual = lancePregao.getValorPropostaInicial();
                } else {
                    LancePregao lanceAnterior = recuperarLanceAnterior(lancePregao);
                    if (lanceAnterior != null) {
                        percentualAtual = lanceAnterior.getPercentualDesconto();
                    }
                }
                if (maiorDesconto.compareTo(percentualAtual) < 0) {
                    maiorDesconto = percentualAtual;
                }
            }
        }
        return maiorDesconto;
    }

    private void solicitarCancelamentoItem() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (context != null) {
            context.update(":formCancelaItem");
            context.execute("dialogCancelarFornecedor.hide()");
            context.execute("dialogCancelarItem.show()");
        }
    }

    private void renumerarRodadas(List<RodadaPregao> listaDeRodadaPregao) {
        int i = 1;
        for (RodadaPregao rodadaPregao : listaDeRodadaPregao) {
            rodadaPregao.setNumero(i);
            i++;
        }
    }

    public boolean rodadaEditavel() {
        Boolean validou = true;
        if (itemPregaoSelecionado != null && rodadaPregaoSelecionada != null && itemPregaoSelecionado.getListaDeRodadaPregao() != null) {
            if ((itemPregaoSelecionado.getListaDeRodadaPregao().indexOf(rodadaPregaoSelecionada) == itemPregaoSelecionado.getListaDeRodadaPregao().size() - 1)
                || ((itemPregaoSelecionado.getListaDeRodadaPregao().indexOf(rodadaPregaoSelecionada) == itemPregaoSelecionado.getListaDeRodadaPregao().size() - 1)
                && (itemPregaoSelecionado.getListaDeRodadaPregao().indexOf(rodadaPregaoSelecionada) == 0))) {
                validou = false;
            }
        }
        return validou;
    }

    public boolean isDesabilitarLance(LancePregao lancePregao) {
        return rodadaEditavel() || lancePregao.getStatusLancePregao().isInativo();
    }

    public Boolean desabilitarBotaoFinalizar() {
        if (rodadaPregaoSelecionada != null) {
            for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
                if (lancePregao.getStatusLancePregao().equals(StatusLancePregao.VENCEDOR)) {
                    lancePregaoQueAuxiliaNaFinalizaoDoPregao = lancePregao;
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isPermitidoNovaRodada() {
        return rodadaEditavel() || (rodadaPregaoSelecionada != null && (rodadaPregaoSelecionada.getLancesAtivo().isEmpty()));
    }

    private void validarRodadaAtual() {
        validarValorDosLances();
        validarSeJaPossuiVencedor(rodadaPregaoSelecionada);
    }

    private void validarSeJaPossuiVencedor(RodadaPregao rodadaPregao) {
        ValidacaoException ve = new ValidacaoException();
        if (isExisteVencedorRodada(rodadaPregao)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível criar uma nova rodada, pois o item já possui um vencedor.");
        }
        ve.lancarException();
    }

    private void validarLanceMaiorQueMenorValor(LancePregao lancePregao, BigDecimal menorValor) {
        ValidacaoException ve = new ValidacaoException();
        if (isLanceMaiorQueMenorValor(lancePregao, menorValor)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do lance do fornecedor " + lancePregao.getPropostaFornecedor().getFornecedor().getNome()
                + " é maior que o valor proposto para o item. Valor Proposto: " + Util.formataValorQuatroCasasDecimais(menorValor));
        }
        ve.lancarException();
    }

    private void validarLanceMenorQueMaiorDesconto(LancePregao lancePregao, BigDecimal maiorDesconto) {
        ValidacaoException ve = new ValidacaoException();
        if (isLanceMenorQueMaiorDesconto(lancePregao, maiorDesconto)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O desconto do lance do fornecedor " + lancePregao.getPropostaFornecedor().getFornecedor().getNome()
                + " é menor que o desconto proposto para o item. Desconto Proposto: " + Util.formataPercentual(maiorDesconto));
        }
        ve.lancarException();
    }

    private void validarValorDosLances() {
        for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
            validarValoresRodadaAtual(lancePregao);
        }
    }

    public boolean podeExcluirRodada() {
        return rodadaEditavel();
    }

    public void processarAlteracaoStatusLancePregaoNaoVencedor(LancePregao lance) {
        lancePregaoSegurado = lance;
        verificarNecessidadeDeCancelamentoDoFornecedor(lancePregaoSegurado);
        verificarNecessidadeCancelamentoItem();
        verificarNecessidadeReativarVencedor();
        definirVencedor();
        if (lance.getStatusLancePregao().equals(StatusLancePregao.ATIVO)) {
            itemPregaoSelecionado.setTipoStatusItemPregao(TipoStatusItemPregao.EM_ANDAMENTO);
        }
        Util.adicionarObjetoEmLista(itemPregaoSelecionado.getListaDeRodadaPregao(), rodadaPregaoSelecionada);
    }

    public boolean verificarSeTodosFornecedoresEstaoDeclinados() {
        for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
            if (!StatusLancePregao.DECLINADO.equals(lancePregao.getStatusLancePregao())) {
                return false;
            }
        }
        return true;
    }

    public void processarAlteracaoStatusLancePregaoVencedor(LancePregao lance) {
        lancePregaoSegurado = lance;
        verificarNecessidadeCancelamentoItem();
        itemPregaoSelecionado.setTipoStatusItemPregao(itemPregaoSelecionado.definirStatus());
        if (lancePregaoSegurado != null && StatusLancePregao.VENCEDOR.equals(lancePregaoSegurado.getStatusLancePregao())) {
            itemPregaoSelecionado.setTipoStatusItemPregao(TipoStatusItemPregao.FINALIZADO);
        }
        Util.adicionarObjetoEmLista(itemPregaoSelecionado.getListaDeRodadaPregao(), rodadaPregaoSelecionada);
    }

    private void verificarNecessidadeReativarVencedor() {
        if (!rodadaPregaoSelecionada.getLancesAtivo().isEmpty() && rodadaPregaoSelecionada.getLanceVencedor() != null) {
            rodadaPregaoSelecionada.getLanceVencedor().setStatusLancePregao(StatusLancePregao.ATIVO);
        }
    }

    private void verificarNecessidadeCancelamentoItem() {
        if (isTodosLancesInativos()) {
            solicitarCancelamentoItem();
        }
    }

    private void verificarNecessidadeDeCancelamentoDoFornecedor(LancePregao lance) {
        if (lance.getStatusLancePregao() != null && lance.getStatusLancePregao().isCancelado()) {
            FacesUtil.atualizarComponente("formCancelarFornecedo");
            FacesUtil.executaJavaScript("dialogCancelarFornecedor.show()");
        }
    }

    public void limpaLanceSegurado() {
        lancePregaoSegurado.setJustificativaCancelamento(null);
    }

    public void definirVencedor() {
        int quantidadeLancesAtivos = 0;
        LancePregao lanceVencedor = null;

        for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
            if (lancePregao.getStatusLancePregao().equals(StatusLancePregao.ATIVO)) {
                lanceVencedor = lancePregao;
                quantidadeLancesAtivos++;
            }
        }
        if (quantidadeLancesAtivos == 1 && !isExisteVencedorRodada(rodadaPregaoSelecionada)) {
            lanceVencedor.setStatusLancePregao(StatusLancePregao.VENCEDOR);
            lancePregaoQueAuxiliaNaFinalizaoDoPregao = lanceVencedor;
            itemPregaoSelecionado.setTipoStatusItemPregao(itemPregaoSelecionado.definirStatus());
        }
        if (!desabilitarBotaoFinalizar()) {
            itemPregaoSelecionado.setTipoStatusItemPregao(TipoStatusItemPregao.FINALIZADO);
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    public void cancelarSelecaoFornecedor() {
        itemPregaoSelecionado.setTipoStatusItemPregao(null);
    }

    public String atribuirNomeBotaoRodadas() {
        if (itemPregaoSelecionado.getTipoStatusItemPregao() != null) {
            if (TipoStatusItemPregao.DESERTO.equals(itemPregaoSelecionado.getTipoStatusItemPregao())) {
                return "Iniciar Rodadas";
            }
            if (itemPregaoSelecionado.getTipoStatusItemPregao().equals(TipoStatusItemPregao.FINALIZADO)
                || itemPregaoSelecionado.getTipoStatusItemPregao().equals(TipoStatusItemPregao.CANCELADO)
                || itemPregaoSelecionado.getTipoStatusItemPregao().equals(TipoStatusItemPregao.DECLINADO)
                || itemPregaoSelecionado.getTipoStatusItemPregao().equals(TipoStatusItemPregao.INEXEQUIVEL)
                || itemPregaoSelecionado.getTipoStatusItemPregao().equals(TipoStatusItemPregao.PREJUDICADO)) {
                return "Visualizar/Editar Rodadas";
            }
            if (itemPregaoSelecionado.getTipoStatusItemPregao().equals(TipoStatusItemPregao.EM_ANDAMENTO)) {
                return "Continuar Rodadas";
            }
        }
        return "";
    }

    public boolean mostraBotaoRodadas() {
        return itemPregaoSelecionado != null;
    }

    private void verificarFornecedoresSelecionados() {
        ValidacaoException ve = new ValidacaoException();
        if (listaDeItemPropostaFornecedorParaLances == null || listaDeItemPropostaFornecedorParaLances.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não houve nenhuma proposta de fornecedor para este item.");
        }
        ve.lancarException();
    }

    private void completarSelecaoFornecedores() {
        if (listaDeItemPropostaFornecedorParaLances.size() == 1 && listaDeItemPropostaFornecedorAuxFiltrada.size() >= 2) {
            listaDeItemPropostaFornecedorParaLances.add(listaDeItemPropostaFornecedorAuxFiltrada.get(1));
        }

        if (listaDeItemPropostaFornecedorParaLances.size() == 2 && listaDeItemPropostaFornecedorAuxFiltrada.size() >= 3) {
            listaDeItemPropostaFornecedorParaLances.add(listaDeItemPropostaFornecedorAuxFiltrada.get(2));
        }
    }

    private boolean isExisteVencedorRodada(RodadaPregao rodadaPregaoSelecionada) {
        for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
            if (lancePregao.getStatusLancePregao().equals(StatusLancePregao.VENCEDOR)) {
                return true;
            }
        }
        return false;
    }

    public void iniciarPregaoSalvando() {
        try {
            verificarFornecedoresSelecionados();
            salvarPregaoAndItensOperacaoNovo();
            iniciarPregao();
            indiceAba = 1;
            FacesUtil.executaJavaScript("dialogIniciarRodada.hide()");
            FacesUtil.atualizarComponente("Formulario:tabGeral");
            if (rodadaPregaoSelecionada.getListaDeLancePregao().size() > 1) {
                String campoValor = selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto() ? "descontoAtual" : "valorLanceAtual";
                FacesUtil.executaJavaScript("setaFoco('Formulario:tabGeral:tablePregao:0:" + campoValor + "')");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.executaJavaScript("dialogIniciarRodada.hide()");
            logger.error("Iniciar pregao {}", ex);
        }
    }

    private void salvarPregaoAndItensOperacaoNovo() {
        if (isOperacaoNovo() && selecionado.getId() == null) {
            List<ItemPregao> itensPregao = Lists.newArrayList(selecionado.getListaDeItemPregao());
            Pregao pregaoSalvo = facade.salvarPregao(selecionado);
            pregaoSalvo.setListaDeItemPregao(itensPregao);
            itemPregaoSelecionado = facade.salvarItemPregaoPorItem(pregaoSalvo, itemPregaoSelecionado);
            selecionado = pregaoSalvo;
        }
    }

    private void iniciarPregao() {
        try {
            if (itemPregaoSelecionado.getListaDeRodadaPregao().isEmpty()) {
                gerarNovaRodadaComNumero();
                Collections.sort(listaDeItemPropostaFornecedorParaLances);
                for (ItemPropostaFornecedor ipf : listaDeItemPropostaFornecedorParaLances) {
                    criarNovoLancePregao(ipf);
                }
                ordenarLancesPorPrecoInicialDesc(rodadaPregaoSelecionada.getListaDeLancePregao());
            } else {
                if (TipoStatusItemPregao.DESERTO.equals(itemPregaoSelecionado.getTipoStatusItemPregao())) {
                    itemPregaoSelecionado.setListaDeRodadaPregao(new ArrayList<RodadaPregao>());
                    iniciarPregao();
                }
                rodadaPregaoSelecionada = retornarUltimaRodadaDoPregao();
                atribuirValorRodadaAnteriorAndPropostaInicial();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Iniciar pregao {}", ex);
        }
    }

    private void atribuirValorRodadaAnteriorAndPropostaInicial() {
        for (RodadaPregao rodadaPregao : itemPregaoSelecionado.getListaDeRodadaPregao()) {
            selecionado.atribuirValorLanceRodadaAnterior(itemPregaoSelecionado, rodadaPregao);
            for (LancePregao lancePregao : rodadaPregao.getListaDeLancePregao()) {
                facade.atribuirValorPrimeiraPropostaNoLance(lancePregao, listaDeItemPropostaFornecedorParaLances);
            }
        }
    }

    private void criarNovoLancePregao(ItemPropostaFornecedor ipf) {
        LancePregao novoLance = new LancePregao();
        novoLance.setRodadaPregao(rodadaPregaoSelecionada);
        novoLance.setPropostaFornecedor(ipf.getPropostaFornecedor());
        novoLance.setMarca(ipf.getMarca());
        novoLance.setValor(BigDecimal.ZERO);
        novoLance.setPercentualDesconto(BigDecimal.ZERO);
        novoLance.setValorPropostaInicial(selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto() ? ipf.getPercentualDesconto() : ipf.getPreco());
        StatusLancePregao statusLanceInicial = ((ipf.getPreco() == null || ipf.getPreco().compareTo(BigDecimal.ZERO) <= 0)
            && (ipf.getPercentualDesconto() == null || ipf.getPercentualDesconto().compareTo(BigDecimal.ZERO) <= 0))
            ? StatusLancePregao.CANCELADO : StatusLancePregao.ATIVO;
        novoLance.setStatusLancePregao(statusLanceInicial);
        rodadaPregaoSelecionada.getListaDeLancePregao().add(novoLance);
    }

    private BigDecimal recuperarPropostaAnterior(LancePregao lance) {
        try {
            int indiceRodada = itemPregaoSelecionado.getListaDeRodadaPregao().indexOf(rodadaPregaoSelecionada) - 1;
            for (LancePregao lanceDaVez : itemPregaoSelecionado.getListaDeRodadaPregao().get(indiceRodada).getListaDeLancePregao()) {
                if (lanceDaVez.getPropostaFornecedor().getFornecedor().equals(lance.getPropostaFornecedor().getFornecedor())) {

                    if (selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
                        lance.setValorNaRodadaAnterior(lanceDaVez.getPercentualDesconto());
                        return lanceDaVez.getPercentualDesconto();
                    } else {
                        lance.setValorNaRodadaAnterior(lanceDaVez.getValor());
                        return lanceDaVez.getValor();
                    }
                }
            }
            if (selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
                lance.setValorPropostaInicial(lance.getPercentualDesconto());
                return lance.getPercentualDesconto();
            } else {
                lance.setValorPropostaInicial(lance.getValor());
                return lance.getValor();
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            facade.atribuirValorPrimeiraPropostaNoLance(lance, listaDeItemPropostaFornecedorParaLances);
            return lance.getValorPropostaInicial();
        }
    }

    private void ordenarLancesPorPrecoInicialDesc(List<LancePregao> lances) {
        Collections.sort(lances, new Comparator<LancePregao>() {
            @Override
            public int compare(LancePregao o1, LancePregao o2) {
                if (selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
                    return o1.getValorPropostaInicial().compareTo(o2.getValorPropostaInicial());
                } else {
                    return o2.getValorPropostaInicial().compareTo(o1.getValorPropostaInicial());
                }
            }
        });
    }

    private void ordenarLancesPorPrecoNaRodadaAnteriorDesc(List<LancePregao> lances) {
        Collections.sort(lances, new Comparator<LancePregao>() {
            @Override
            public int compare(LancePregao o1, LancePregao o2) {
                if (selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
                    return o1.getValorNaRodadaAnterior().compareTo(o2.getValorNaRodadaAnterior());
                } else {
                    return o2.getValorNaRodadaAnterior().compareTo(o1.getValorNaRodadaAnterior());
                }
            }
        });
    }

    private LancePregao recuperarLanceAnterior(LancePregao lance) {
        try {
            int indiceRodada = itemPregaoSelecionado.getListaDeRodadaPregao().indexOf(rodadaPregaoSelecionada) - 1;
            for (LancePregao lanceDaVez : itemPregaoSelecionado.getListaDeRodadaPregao().get(indiceRodada).getListaDeLancePregao()) {
                if (lanceDaVez.getPropostaFornecedor().getFornecedor().equals(lance.getPropostaFornecedor().getFornecedor())) {
                    return lanceDaVez;
                }
            }
            return null;
        } catch (ArrayIndexOutOfBoundsException ex) {
            return null;
        }
    }

    private Integer gerarNumeroRodada() {
        return itemPregaoSelecionado.getListaDeRodadaPregao().size() + 1;
    }

    public void novaRodada() {
        try {
            validarRodadaAtual();
            String campoValor = selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto() ? "descontoAtual" : "valorLanceAtual";
            RodadaPregao rodadaPregao = rodadaPregaoSelecionada;
            itemPregaoSelecionado = facade.salvarItemRodadaEspecifica(itemPregaoSelecionado, rodadaPregao);
            gerarNovaRodadaComNumero();
            novoLanceClonandoAnterior(rodadaPregao);
            ordenarRodadas();
            ordenarLancesPorPrecoNaRodadaAnteriorDesc(rodadaPregaoSelecionada.getListaDeLancePregao());
            itemPregaoSelecionado.setTipoStatusItemPregao(itemPregaoSelecionado.definirStatus());
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabGeral:tablePregao:0:" + campoValor + "')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void novoLanceClonandoAnterior(RodadaPregao rodadaPregao) {
        for (LancePregao lancePregao : rodadaPregao.getListaDeLancePregao()) {
            if (!lancePregao.getStatusLancePregao().isInativo()) {
                LancePregao novoLance = (LancePregao) clonaBean(lancePregao);
                novoLance.setId(null);
                novoLance.setRodadaPregao(rodadaPregaoSelecionada);
                novoLance.setPercentualDesconto(BigDecimal.ZERO);
                novoLance.setValor(BigDecimal.ZERO);
                novoLance.setValorNaRodadaAnterior(selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto() ? lancePregao.getPercentualDesconto() : lancePregao.getValor());
                rodadaPregaoSelecionada.getListaDeLancePregao().add(novoLance);
            }
        }
    }

    private void gerarNovaRodadaComNumero() {
        rodadaPregaoSelecionada = new RodadaPregao();
        rodadaPregaoSelecionada.setItemPregao(itemPregaoSelecionado);
        rodadaPregaoSelecionada.setNumero(gerarNumeroRodada());
        itemPregaoSelecionado.getListaDeRodadaPregao().add(rodadaPregaoSelecionada);
    }

    public BigDecimal recuperaUltimoValor() {
        if (selecionado.getLicitacao() == null) {
            return BigDecimal.ZERO;
        }
        if (selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
            return lancePregaoQueAuxiliaNaFinalizaoDoPregao.getPercentualDesconto();
        }
        return lancePregaoQueAuxiliaNaFinalizaoDoPregao.getValor();
    }

    private void recuperarFornecedoresSelecionados() {
        listaDeItemPropostaFornecedorParaLances = Lists.newArrayList();
        for (LancePregao lancePregao : itemPregaoSelecionado.getListaDeRodadaPregao().get(0).getListaDeLancePregao()) {
            for (ItemPropostaFornecedor itemProposta : listaDeItemPropostaFornecedorAuxFiltrada) {
                if (lancePregao.getPropostaFornecedor().equals(itemProposta.getPropostaFornecedor())) {
                    lancePregao.setMarca(itemProposta.getMarca());
                    listaDeItemPropostaFornecedorParaLances.add(itemProposta);
                }
            }
        }
    }

    private void definirParametroAoIniciarRodada() {
        if (itemPregaoSelecionado != null) {
            ordenarRodadas();
            if (!isOperacaoVer()) {
                itemPregaoSelecionado.setTipoStatusItemPregao(itemPregaoSelecionado.definirStatus());
            }
        }
    }

    public boolean hasFornecedorParaRodada(ItemPregao itemPregao) {
        if (itemPregao != null && itemPregao.getItemPregaoItemProcesso() != null && itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra() != null) {
            return facade.verificarSeItemPregaoPossuiPropostaFornecedor(itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra());
        }
        return false;
    }

    public void selecionarItemDefinindoParametrosParaRodada(ItemPregao itemPregao) {
        selecionaItem(itemPregao);
        List<ItemPropostaFornecedor> itensProposta = facade.getPropostaFornecedorFacade().recuperarItemPropostaFornecedorPorItemProcessoCompra(itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra());
        listaDeItemPropostaFornecedorAuxFiltrada = new ArrayList<>(itensProposta);
        preSelecionarFornecedores(listaDeItemPropostaFornecedorAuxFiltrada);
        definirParametroAoIniciarRodada();
    }

    private void validarAnulacaoItemPregao() {
        ValidacaoException ve = new ValidacaoException();
        if (itemPregaoSelecionado.getTipoStatusItemPregao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Situação do Item deve ser informado.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposStatusItemAnulacao() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, " "));
        toReturn.add(new SelectItem(TipoStatusItemPregao.DESERTO, TipoStatusItemPregao.DESERTO.getDescricao()));
        toReturn.add(new SelectItem(TipoStatusItemPregao.PREJUDICADO, TipoStatusItemPregao.PREJUDICADO.getDescricao()));
        toReturn.add(new SelectItem(TipoStatusItemPregao.DECLINADO, TipoStatusItemPregao.DECLINADO.getDescricao()));
        toReturn.add(new SelectItem(TipoStatusItemPregao.INEXEQUIVEL, TipoStatusItemPregao.INEXEQUIVEL.getDescricao()));
        toReturn.add(new SelectItem(TipoStatusItemPregao.CANCELADO, TipoStatusItemPregao.CANCELADO.getDescricao()));
        return toReturn;
    }

    public void anularItemPregao() {
        try {
            validarAnulacaoItemPregao();
            salvarPregaoAndItensOperacaoNovo();
            itemPregaoSelecionado.setTipoStatusItemPregao(itemPregaoSelecionado.getTipoStatusItemPregao());
            facade.anularItensPregao(itemPregaoSelecionado);
            FacesUtil.atualizarComponente("Formulario:tabGeral:tabelaSimplesDeItens");
            FacesUtil.executaJavaScript("dlgAnularItem.hide()");
            if (isOperacaoNovo()) {
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
            }
            FacesUtil.addOperacaoRealizada("Item anulado com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void selecionaItem(ItemPregao itemPregao) {
        itemPregaoSelecionado = itemPregao;
        if (!isOperacaoNovo()) {
            itemPregaoSelecionado = facade.buscarRodadasAndLancesPregao(itemPregao);
        }
    }

    public void cancelarAnulacaoItemPregao() {
        itemPregaoSelecionado = null;
    }

    public void adicionarFornecedor(ActionEvent event) {
        listaDeItemPropostaFornecedorParaLances.add((ItemPropostaFornecedor) event.getComponent().getAttributes().get("ipf"));
    }

    public void removerFornecedor(ActionEvent event) {
        listaDeItemPropostaFornecedorParaLances.remove((ItemPropostaFornecedor) event.getComponent().getAttributes().get("ipf"));
    }

    public boolean fornecedorEstaNaLista(ItemPropostaFornecedor ipf) {
        return listaDeItemPropostaFornecedorParaLances.contains(ipf);
    }

    public Boolean desabilitaBotaoSelecionarFornecedor() {
        if (atribuirNomeBotaoRodadas().equals("Iniciar Rodadas")) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private boolean isTodosLancesAtivosEComPreco() {
        for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
            if (StatusLancePregao.ATIVO.equals(lancePregao.getStatusLancePregao())
                && ((!selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto()
                && lancePregao.getValor() != null
                && lancePregao.getValor().compareTo(BigDecimal.ZERO) == 0) ||
                (selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto() && lancePregao.getPercentualDesconto().compareTo(BigDecimal.ZERO) == 0))) {
                return false;
            }
        }
        return true;
    }

    public void executarLance(LancePregao lance, int linha) {
        int indice = linha + 1;
        try {
            validarMenorValor(lance);
            validarMaiorDesconto(lance);
            validarLanceMaiorQueAoDaSolicitacao(lance);
            if (lance.getStatusLancePregao().isAtivo()) {
                itemPregaoSelecionado.setTipoStatusItemPregao(TipoStatusItemPregao.EM_ANDAMENTO);
            }
            String campoValor = selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto() ? "descontoAtual" : "valorLanceAtual";
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabGeral:tablePregao:" + indice + ":" + campoValor + "')");
        } catch (ValidacaoException ve) {
            lance.setValor(BigDecimal.ZERO);
            lance.setPercentualDesconto(BigDecimal.ZERO);
            FacesUtil.atualizarComponente("Formulario:tabGeral:tablePregao");
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        if (isTodosLancesAtivosEComPreco() && !isExisteVencedorRodada(rodadaPregaoSelecionada)) {
            novaRodada();
            FacesUtil.atualizarComponente("Formulario:tabGeral:tablePregao");
        }
    }

    private void validarValoresRodadaAtual(LancePregao lance) {
        validarLanceZerado(lance);
        validarMenorValor(lance);
        validarMaiorDesconto(lance);
        validarLanceMaiorQueAoDaSolicitacao(lance);
    }

    private void validarLanceMaiorQueAoDaSolicitacao(LancePregao lancePregao) {
        ValidacaoException ve = new ValidacaoException();
        if (lancePregao.getValor() != null && lancePregao.getValor().compareTo(selecionado.getLicitacao().getProcessoDeCompra().getSolicitacaoMaterial().getValor()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do lance do fornecedor " + lancePregao.getPropostaFornecedor().getFornecedor().getNome()
                + " é maior que o valor da solicitação de compra para o item. Valor da solicitação: "
                + Util.formataValorQuatroCasasDecimais(selecionado.getLicitacao().getProcessoDeCompra().getSolicitacaoMaterial().getValor()));
        }
        ve.lancarException();
    }

    private void validarMenorValor(LancePregao lance) {
        if (!selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
            BigDecimal menorValor = getMenorValorRodadaAnterior();

            validarLanceMaiorQueMenorValor(lance, menorValor);

            validarLanceMicroPequenaEmpresa(lance, menorValor);
        }
    }

    private void validarMaiorDesconto(LancePregao lance) {
        if (selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
            BigDecimal maiorDesconto = getMaiorDescontoRodada();
            validarLanceMenorQueMaiorDesconto(lance, maiorDesconto);
        }
    }

    private void validarLanceMicroPequenaEmpresa(LancePregao lance, BigDecimal menorValor) {
        if (isMenorValorMicroPequenaEmpresa() && !isLanceCincoPorCentoMenor(lance, menorValor) && isLanceEmpresaNormal(lance)) {
            FacesUtil.addWarn("Atenção", "O lance do fornecedor " + lance.getPropostaFornecedor().getFornecedor().getNome() + " não é 5% menor que o do fornecedor "
                + fornecedorMenorValorMicroPequenaEmpresa.getNome());
        }
    }

    private boolean isLanceCincoPorCentoMenor(LancePregao lance, BigDecimal menorValor) {
        double diferenca = calculaDiferencaPorcentagem(lance, menorValor);
        if (diferenca < 5) {
            return false;
        }
        return true;
    }

    private double calculaDiferencaPorcentagem(LancePregao lance, BigDecimal menorValor) {
        double lanceValor = lance.getValor().doubleValue();
        double dbMenorValor = menorValor.doubleValue();

        double porcentagem = (lanceValor * 100) / dbMenorValor;
        return (100 - porcentagem);
    }

    private boolean isLanceEmpresaNormal(LancePregao lance) {
        Pessoa fornecedor = lance.getPropostaFornecedor().getFornecedor();
        if (fornecedor instanceof PessoaJuridica) {
            return TipoEmpresa.NORMAL.equals(((PessoaJuridica) fornecedor).getTipoEmpresa());
        }
        return false;
    }

    public boolean itemPropostaFornecedorPessoaJuridica(PropostaFornecedor propostaFornecedor) {
        return propostaFornecedor != null && propostaFornecedor.getFornecedor() != null && propostaFornecedor.getFornecedor() instanceof PessoaJuridica;
    }

    private boolean isTodosLancesInativos() {
        for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
            if (!lancePregao.getStatusLancePregao().isInativo()) {
                return false;
            }
        }
        return true;
    }

    private void validarEdicaoPregao() {
        ValidacaoException ve = new ValidacaoException();
        selecionado.setLicitacao(facade.getLicitacaoFacade().recuperarSomenteStatus(selecionado.getLicitacao().getId()));
        if (selecionado.getLicitacao().isHomologada()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O pregão não pode ser alterado, pois sua licitação encontra-se homoloagada.");
        }
        ve.lancarException();
    }

    private void validarExclusao() {
        ValidacaoException ve = new ValidacaoException();
        Licitacao licitacao = selecionado.getLicitacao();
        licitacao.setListaDeStatusLicitacao(facade.getLicitacaoFacade().recuperarListaDeStatus(licitacao));
        if (!licitacao.getStatusAtualLicitacao().getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.ANDAMENTO)
            && !licitacao.getStatusAtualLicitacao().getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.EM_RECURSO)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O pregão selecionado não está em andamento ou em recurso.");
            ve.lancarException();
        }
        for (ItemPregao itemPregao : selecionado.getListaDeItemPregao()) {
            ItemProcessoDeCompra itemProcessoCompra = itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra();
            if (SituacaoItemProcessoDeCompra.ADJUDICADO.equals(itemProcessoCompra.getSituacaoItemProcessoDeCompra())
                || SituacaoItemProcessoDeCompra.HOMOLOGADO.equals(itemProcessoCompra.getSituacaoItemProcessoDeCompra())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O pregão possui itens " + itemProcessoCompra.getSituacaoItemProcessoDeCompra().getDescricao() +
                    ", para continuar com a exclusão, é necessário exluir a adjudicação e homologação dos itens.");
                break;
            }
        }
        ve.lancarException();
    }

    public void iniciarProcessoFinalizarRodadas() {
        try {
            validarValorDosLances();
            lancePregaoQueAuxiliaNaFinalizaoDoPregao.setValorFinal(recuperaUltimoValor());
            FacesUtil.executaJavaScript("dialogPropostaFinal.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public IntencaoDeRecurso getIntencaoDeRecurso() {
        return intencaoDeRecurso;
    }

    public void setIntencaoDeRecurso(IntencaoDeRecurso intencaoDeRecurso) {
        this.intencaoDeRecurso = intencaoDeRecurso;
    }

    public void prejudicado() {
        itemPregaoSelecionado.setTipoStatusItemPregao(TipoStatusItemPregao.PREJUDICADO);
        finalizarRodadaParaTodosProcesso();
    }

    public void addIntencaoRecurso() {
        if (Strings.isNullOrEmpty(intencaoDeRecurso.getMotivo()) || intencaoDeRecurso.getMotivo().trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("Informe o motivo para intenção de recurso.");
        } else {
            intencaoDeRecurso.setPregao(selecionado);
            selecionado.getListaDeIntencaoDeRecursos().add(intencaoDeRecurso);
            novaItencaoRecurso();
        }
    }

    public void concluirIntencaoRecurso() {
        selecionado = facade.salvarItencaoRecurso(selecionado);
        redirecionaParaVer();
    }

    public void selecionarItemLancesVencedores(ItemPregao item) {
        this.itemPregaoSelecionado = item;
        Collections.sort(item.getLancesVencedores());
    }

    public boolean isFornecedorPessoaFisica() {
        return intencaoDeRecurso.getFornecedor() instanceof PessoaFisica;
    }

    public List<SelectItem> getListSelectItemFornecedoresParticipantes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (LicitacaoFornecedor lf : facade.getLicitacaoFacade().buscarFornecedoresLicitacao(selecionado.getLicitacao())) {
            retorno.add(new SelectItem(lf.getEmpresa(), lf.getEmpresa().getNome()));
        }
        return retorno;
    }

    public boolean renderizarStatusItemPregao() {
        return isOperacaoEditar() && itemPregaoSelecionado != null && itemPregaoSelecionado.getTipoStatusItemPregao() != null;
    }

    public int getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(int indiceAba) {
        this.indiceAba = indiceAba;
    }

    public void setFornecedorMenorValorMicroPequenaEmpresa(Pessoa fornecedor) {
        fornecedorMenorValorMicroPequenaEmpresa = fornecedor;
    }

    public PregaoFacade getFacade() {
        return facade;
    }


    public ItemPregao getItemPregaoSelecionado() {
        return itemPregaoSelecionado;
    }

    public void setItemPregaoSelecionado(ItemPregao itemPregaoSelecionado) {
        this.itemPregaoSelecionado = itemPregaoSelecionado;
    }

    public LancePregao getLancePregaoQueAuxiliaNaFinalizaoDoPregao() {
        return lancePregaoQueAuxiliaNaFinalizaoDoPregao;
    }

    public void setLancePregaoQueAuxiliaNaFinalizaoDoPregao(LancePregao lancePregaoQueAuxiliaNaFinalizaoDoPregao) {
        this.lancePregaoQueAuxiliaNaFinalizaoDoPregao = lancePregaoQueAuxiliaNaFinalizaoDoPregao;
    }

    public LancePregao getLancePregaoSegurado() {
        return lancePregaoSegurado;
    }

    public void setLancePregaoSegurado(LancePregao lancePregaoSegurado) {
        this.lancePregaoSegurado = lancePregaoSegurado;
    }

    public List<ItemPropostaFornecedor> getListaDeItemPropostaFornecedorAuxFiltrada() {
        return listaDeItemPropostaFornecedorAuxFiltrada;
    }

    public void setListaDeItemPropostaFornecedorAuxFiltrada
        (List<ItemPropostaFornecedor> listaDeItemPropostaFornecedorAuxFiltrada) {
        this.listaDeItemPropostaFornecedorAuxFiltrada = listaDeItemPropostaFornecedorAuxFiltrada;
    }

    public RodadaPregao getRodadaPregaoSelecionada() {
        return rodadaPregaoSelecionada;
    }

    public void setRodadaPregaoSelecionada(RodadaPregao rodadaPregaoSelecionada) {
        this.rodadaPregaoSelecionada = rodadaPregaoSelecionada;
    }

    public List<ItemPropostaFornecedor> getListaDeItemPropostaFornecedorParaLances() {
        return listaDeItemPropostaFornecedorParaLances;
    }

    public void setListaDeItemPropostaFornecedorParaLances
        (List<ItemPropostaFornecedor> listaDeItemPropostaFornecedorParaLances) {
        this.listaDeItemPropostaFornecedorParaLances = listaDeItemPropostaFornecedorParaLances;
    }

    public List<ItemPregao> getItensPregaoNaoCotados() {
        return itensPregaoNaoCotados;
    }

    public void setItensPregaoNaoCotados(List<ItemPregao> itensPregaoNaoCotados) {
        this.itensPregaoNaoCotados = itensPregaoNaoCotados;
    }

    public BigDecimal diferencaEmPorcentagemPropostaFinal() {
        if (itemPregaoSelecionado != null && lancePregaoQueAuxiliaNaFinalizaoDoPregao != null) {
            return diferencaEmPorcentagem(itemPregaoSelecionado.getPrecoItemSolicitacaoMaterial(), lancePregaoQueAuxiliaNaFinalizaoDoPregao.getValorFinal());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal diferencaEmPorcentagem(BigDecimal valorReferencia, BigDecimal valorLance) {
        BigDecimal retorno = BigDecimal.ZERO;
        if (valorReferencia != null && valorLance != null && valorLance.compareTo(BigDecimal.ZERO) != 0 && valorReferencia.compareTo(BigDecimal.ZERO) != 0) {
            retorno = valorLance.multiply(new BigDecimal(100)).divide(valorReferencia, 8, RoundingMode.FLOOR);
            retorno = new BigDecimal(100).subtract(retorno).setScale(3, RoundingMode.HALF_EVEN);
        }
        return retorno;
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.PREGAO_POR_ITEM);
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            novoFiltroHistoricoProcesso();
        }
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }

}
