package br.com.webpublico.controle;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroSubstituicaoObjetoCompra;
import br.com.webpublico.entidadesauxiliares.SubstituicaoObjetoCompraMovimento;
import br.com.webpublico.entidadesauxiliares.SubstituicaoObjetoCompraVo;
import br.com.webpublico.enums.OrigemSubstituicaoObjetoCompra;
import br.com.webpublico.enums.SituacaoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SubstituicaoObjetoCompraFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-substituicao-ob", pattern = "/substituicao-objeto-compra/novo/", viewId = "/faces/administrativo/licitacao/substituicao-objeto-compra/edita.xhtml"),
    @URLMapping(id = "listar-substituicao-ob", pattern = "/substituicao-objeto-compra/listar/", viewId = "/faces/administrativo/licitacao/substituicao-objeto-compra/lista.xhtml"),
    @URLMapping(id = "ver-substituicao-ob", pattern = "/substituicao-objeto-compra/ver/#{substituicaoObjetoCompraControlador.id}/", viewId = "/faces/administrativo/licitacao/substituicao-objeto-compra/visualizar.xhtml")
})
public class SubstituicaoObjetoCompraControlador extends PrettyControlador<SubstituicaoObjetoCompra> implements Serializable, CRUD {

    @EJB
    private SubstituicaoObjetoCompraFacade facade;
    private FiltroSubstituicaoObjetoCompra filtro;
    private SubstituicaoObjetoCompraVo objetoCompraSelecionado;
    private List<SubstituicaoObjetoCompraVo> objetosCompraDoProcesso;
    private List<SubstituicaoObjetoCompraVo> objetosCompraDoProcessoFilterBy;
    private List<SubstituicaoObjetoCompraVo> objetosCompraSubstituicao;

    public SubstituicaoObjetoCompraControlador() {
        super(SubstituicaoObjetoCompra.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/substituicao-objeto-compra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-substituicao-ob", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        novoFiltro();
        selecionado.setDataSubstituicao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setOrigemSubstituicao(OrigemSubstituicaoObjetoCompra.CONTRATO);
        objetosCompraSubstituicao = Lists.newArrayList();
    }

    private void novoFiltro() {
        filtro = new FiltroSubstituicaoObjetoCompra();
    }

    @URLAction(mappingId = "ver-substituicao-ob", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        objetosCompraSubstituicao = facade.buscarObjetosCompraAndMovimentos(selecionado);
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            selecionado = facade.salvarRetornando(selecionado, objetosCompraSubstituicao);
            redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            selecionado.setItens(Lists.<SubstituicaoObjetoCompraItem>newArrayList());
            descobrirETratarException(e);
        }
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getOrigemSubstituicao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de substituição deve ser informado.");
        }
        if (!hasObjetosCompraSelecionados()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A lista de objetos para substituição está vazia.");
        }
        ve.lancarException();
    }

    public void selecionarEspecificacao(ActionEvent evento) {
        ObjetoDeCompraEspecificacao especificacao = (ObjetoDeCompraEspecificacao) evento.getComponent().getAttributes().get("objeto");
        objetoCompraSelecionado.setEspecificacaoPara(especificacao.getTexto());
    }

    public void limparEspecificacao() {
        objetoCompraSelecionado.setEspecificacaoPara(null);
    }

    private void validarAdicionarObjetoCompra() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getOrigemSubstituicao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de substituição deve ser informado.");
        }
        if (!hasObjetoCompraSelecionado()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo objeto 'de' compra deve ser informado.");
        }
        ve.lancarException();
        if (objetoCompraSelecionado.getUnidadeMedidaPara() == null
            && objetoCompraSelecionado.getQuantidadePara() == null
            && objetoCompraSelecionado.getObjetoCompraPara() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Nenhum alteração encontrada para adicionar o objeto para substituição.");

        }
        if (objetoCompraSelecionado.getQuantidadeDe() != null
            && objetoCompraSelecionado.getQuantidadeDe().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo quantidade deve ser maior que zero(0).");
        }
        ve.lancarException();
        validarObjetoCompraPara();
    }

    public void adicionarObjetoCompra() {
        try {
            validarAdicionarObjetoCompra();
            validarOnRowSelect();
            Util.adicionarObjetoEmLista(objetosCompraSubstituicao, objetoCompraSelecionado);
            selecionado.setHistorico(facade.gerarHistorico(objetosCompraSubstituicao));
            setObjetoCompraSelecionado(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerObjetoCompra(SubstituicaoObjetoCompraVo obj) {
        objetosCompraSubstituicao.remove(obj);
        selecionado.setHistorico(facade.gerarHistorico(objetosCompraSubstituicao));
    }

    public void editarObjetoCompra(SubstituicaoObjetoCompraVo obj) {
        objetoCompraSelecionado = (SubstituicaoObjetoCompraVo) Util.clonarObjeto(obj);
    }

    public void selecionarObjetoCompra(SubstituicaoObjetoCompraVo obj) {
        objetoCompraSelecionado = obj;
    }

    public void onRowSelectObjetoCompra(SelectEvent event) {
        try {
            cancelarObjetoCompra();
            objetoCompraSelecionado = (SubstituicaoObjetoCompraVo) event.getObject();
            montarCondicaoSqlMovimentos();
            filtro.setObjetoCompra(objetoCompraSelecionado.getObjetoCompraDe());
            filtro.getObjetoCompra().setGrupoContaDespesa(facade.getGrupoContaDespesa(filtro.getObjetoCompra()));
            objetoCompraSelecionado.setMovimentos(facade.buscarMovimentosObjetoCompra(filtro));
            validarOnRowSelect();
            FacesUtil.executaJavaScript("setaFoco('Formulario:oc-para_input')");
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void buscarObjetosCompraDoProcesso() {
        try {
            objetosCompraDoProcesso = Lists.newArrayList();
            if (hasObjetoCompraSelecionado()) {
                objetoCompraSelecionado.setMovimentos(Lists.<SubstituicaoObjetoCompraMovimento>newArrayList());
                objetoCompraSelecionado = null;
            }
            montarCondicaoSqlObjetoCompra();
            objetosCompraDoProcesso = facade.buscarObjetosCompraPorProcesso(filtro);
            Collections.sort(objetosCompraDoProcesso);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void montarCondicaoSqlObjetoCompra() {
        String condicao = "";
        if (filtro.getContrato() != null) {
            condicao += " where cont.id = " + filtro.getContrato().getId();
        }
        if (filtro.getLicitacao() != null) {
            condicao += " where lic.id = " + filtro.getLicitacao().getId();
        }
        if (filtro.getDispensaLicitacao() != null) {
            condicao += " where disp.id = " + filtro.getDispensaLicitacao().getId();
        }
        if (filtro.getSolicitacaoCompra() != null) {
            condicao += " where sol.id = " + filtro.getSolicitacaoCompra().getId();
        }
        if (filtro.getFormularioCotacao() != null) {
            condicao += " where fc.id = " + filtro.getFormularioCotacao().getId();
        }
        if (filtro.getCotacao() != null) {
            condicao += " where cot.id = " + filtro.getCotacao().getId();
        }
        if (filtro.getIntencaoRegistroPreco() != null) {
            condicao += " where irp.id = " + filtro.getIntencaoRegistroPreco().getId();
        }
        if (filtro.getRegistroSolicitacaoMaterialExterno() != null) {
            condicao += " where reg.id = " + filtro.getRegistroSolicitacaoMaterialExterno().getId();
        }
        filtro.setCondicaoSql(condicao);
        filtro.setOrigemSubstituicao(selecionado.getOrigemSubstituicao());
    }

    private void montarCondicaoSqlMovimentos() {
        String condicao = " where oc.id = " + objetoCompraSelecionado.getObjetoCompraDe().getId();
        condicao += " and item.numero = " + objetoCompraSelecionado.getNumeroItem();
        if (filtro.getContrato() != null) {
            if (filtro.getContrato().getTipoAquisicao().isDispensa()) {
                condicao += " and disp.id = " + filtro.getContrato().getDispensaDeLicitacao().getId();
            }
            if (filtro.getContrato().isProcessoLicitatorio()) {
                condicao += " and lic.id = " + filtro.getContrato().getLicitacao().getId();
            }
            filtro.setContratoRegistroPrecoExterno(filtro.getContrato().isDeRegistroDePrecoExterno());
            if (filtro.getContratoRegistroPrecoExterno()) {
                condicao += " and cont.id = " + filtro.getContrato().getId();
            }
            selecionado.setIdMovimento(filtro.getContrato().getId());
        }
        if (filtro.getLicitacao() != null) {
            condicao += " and lic.id = " + filtro.getLicitacao().getId();
            selecionado.setIdMovimento(filtro.getLicitacao().getId());
        }
        if (filtro.getDispensaLicitacao() != null) {
            condicao += " and disp.id = " + filtro.getDispensaLicitacao().getId();
            selecionado.setIdMovimento(filtro.getDispensaLicitacao().getId());
        }
        if (filtro.getSolicitacaoCompra() != null) {
            condicao += " and sol.id = " + filtro.getSolicitacaoCompra().getId();
            selecionado.setIdMovimento(filtro.getSolicitacaoCompra().getId());
        }
        if (filtro.getCotacao() != null) {
            condicao += " and cot.id = " + filtro.getCotacao().getId();
            selecionado.setIdMovimento(filtro.getCotacao().getId());
        }
        if (filtro.getFormularioCotacao() != null) {
            condicao += " and fc.id = " + filtro.getFormularioCotacao().getId();
            selecionado.setIdMovimento(filtro.getFormularioCotacao().getId());
        }
        if (filtro.getIntencaoRegistroPreco() != null) {
            condicao += " and irp.id = " + filtro.getIntencaoRegistroPreco().getId();
            selecionado.setIdMovimento(filtro.getIntencaoRegistroPreco().getId());
        }
        if (filtro.getRegistroSolicitacaoMaterialExterno() != null) {
            condicao += " and reg.id = " + filtro.getRegistroSolicitacaoMaterialExterno().getId();
            selecionado.setIdMovimento(filtro.getRegistroSolicitacaoMaterialExterno().getId());
        }
        filtro.setCondicaoSql(condicao);
        filtro.setOrigemSubstituicao(selecionado.getOrigemSubstituicao());
    }

    public boolean hasObjetoCompraSelecionado() {
        return objetoCompraSelecionado != null;
    }

    public void validarOnRowSelect() {
        ValidacaoException ve = new ValidacaoException();
        if (objetoCompraSelecionado.getGrupoContaDespesaDe() != null && !objetoCompraSelecionado.getGrupoContaDespesaDe().hasContasDespesa()
                && objetoCompraSelecionado.getObjetoCompraDe().getTipoObjetoCompra().isPermanenteOrConsumo()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhuma conta de despesa configurada para o grupo do objeto de compra: " + objetoCompraSelecionado.getObjetoCompraDe() + ".");
        }
        for (SubstituicaoObjetoCompraMovimento mov : objetoCompraSelecionado.getMovimentos()) {
            String msg = "A <b>" + selecionado.getOrigemSubstituicao().getDescricao() + "</b>"
                + " e o objeto de compra " + objetoCompraSelecionado.getObjetoCompraDe().getCodigo()
                + " estão inseridos no movimento <b>" + mov.getTipoMovimento().getDescricao() + "</b> nº " + mov.getNumero()
                + ", esse movimento deve ser a origem da substituição.";

            switch (selecionado.getOrigemSubstituicao()) {
                case LICITACAO:
                case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                case REGISTRO_PRECO_EXTERNO:
                    if (mov.getTipoMovimento().isContrato()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida(msg);
                    }
                    break;
                case SOLICITACAO_COMPRA:
                    if (mov.getTipoMovimento().isLicitacao() || mov.getTipoMovimento().isDispensa()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida(msg);
                    }
                    break;
                case COTACAO:
                    if (mov.getTipoMovimento().isSolicitacaoCompra()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida(msg);
                    }
                    break;
                case FORMULARIO_COTACAO:
                    if (mov.getTipoMovimento().isCotacao()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida(msg);
                    }
                    break;
                case IRP:
                    if (mov.getTipoMovimento().isFormularioCotacao()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida(msg);
                    }
                    break;
            }
        }
        ve.lancarException();
    }

    public void listenerObjetoCompraPara() {
        try {
            objetoCompraSelecionado.setGrupoContaDespesaPara(null);
            objetoCompraSelecionado.setEspecificacaoPara(null);
            if (objetoCompraSelecionado.hasObjetoCompraPara()) {
                objetoCompraSelecionado.setGrupoContaDespesaPara(facade.getGrupoContaDespesa(objetoCompraSelecionado.getObjetoCompraPara()));
                validarObjetoCompraPara();

                TabelaEspecificacaoObjetoCompraControlador controlador = (TabelaEspecificacaoObjetoCompraControlador) Util.getControladorPeloNome("tabelaEspecificacaoObjetoCompraControlador");
                controlador.recuperarObjetoCompra(objetoCompraSelecionado.getObjetoCompraPara());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarObjetoCompraPara() {
        ValidacaoException ve = new ValidacaoException();
        for (SubstituicaoObjetoCompraMovimento mov : objetoCompraSelecionado.getMovimentos()) {
            if (mov.getTipoMovimento().isRequisicaoCompra()) {
                RequisicaoDeCompra requisicao = facade.getRequisicaoDeCompraFacade().recuperarSemDependencias(mov.getId());
                if (requisicao != null && requisicao.getSituacaoRequisicaoCompra().isEfetivada())
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido de/para com requisição de compra efetivada. Req: " + requisicao.getNumero() + ".");
            }
            if (mov.getTipoMovimento().isExecucaoContrato()) {
                if (!objetoCompraSelecionado.getObjetoCompraDe().getTipoObjetoCompra().equals(objetoCompraSelecionado.getObjetoCompraPara().getTipoObjetoCompra())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido de/para com Tipos de Objeto diferentes em processos com execução.");
                    break;
                }
                if (objetoCompraSelecionado.getGrupoContaDespesaPara() != null
                    && objetoCompraSelecionado.getGrupoContaDespesaPara().getContasDespesa() != null
                    && mov.getContaDesdobrada() != null) {
                    if (objetoCompraSelecionado.getGrupoContaDespesaPara().getContasDespesa()
                        .stream()
                        .noneMatch(conta -> conta.getCodigo().equals(mov.getContaDesdobrada().getCodigo()))) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido de/para com desdobramento diferente do empenho em processos com execução.");
                        break;
                    }
                }
                if (objetoCompraSelecionado.getGrupoContaDespesaPara() != null
                    && objetoCompraSelecionado.getGrupoContaDespesaDe() != null
                    && !objetoCompraSelecionado.getGrupoContaDespesaDe().getIdGrupo().equals(objetoCompraSelecionado.getGrupoContaDespesaPara().getIdGrupo())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido de/para com grupos diferentes em processos com execução.");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public boolean hasProcessoComPregaoPorLote() {
        if (hasObjetoCompraSelecionado()) {
            for (SubstituicaoObjetoCompraMovimento movimento : objetoCompraSelecionado.getMovimentos()) {
                if (movimento.getTipoMovimento().isLicitacao()) {
                    Licitacao licitacao = facade.getLicitacaoFacade().recuperarSomenteLicitacao(movimento.getId());
                    return licitacao != null && licitacao.getTipoApuracao().isPorLote();
                }
            }
        }
        return false;
    }

    public boolean hasMovimentoContrato() {
        if (hasObjetoCompraSelecionado()) {
            for (SubstituicaoObjetoCompraMovimento movimento : objetoCompraSelecionado.getMovimentos()) {
                if (movimento.getTipoMovimento().isContrato()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasObjetosCompraSelecionados() {
        return objetosCompraSubstituicao != null && !objetosCompraSubstituicao.isEmpty();
    }

    public void cancelarObjetoCompra() {
        if (hasObjetoCompraSelecionado()) {
            objetoCompraSelecionado.setObjetoCompraPara(null);
            objetoCompraSelecionado.setEspecificacaoPara(null);
            objetoCompraSelecionado.setGrupoContaDespesaPara(null);
            objetoCompraSelecionado.setUnidadeMedidaPara(null);
            objetoCompraSelecionado.setQuantidadePara(null);
        }
    }

    public void limparDadosObjetoCompra() {
        objetosCompraDoProcesso = Lists.newArrayList();
        if (hasObjetoCompraSelecionado()) {
            objetoCompraSelecionado.setMovimentos(Lists.<SubstituicaoObjetoCompraMovimento>newArrayList());
            objetoCompraSelecionado = null;
        }
        filtro.limparFiltros();
    }

    public void limparDadosObjetoCompraPara() {
        objetoCompraSelecionado.setGrupoContaDespesaPara(null);
        objetoCompraSelecionado.setEspecificacaoPara(null);
        objetoCompraSelecionado.setObjetoCompraPara(null);
    }

    public List<SelectItem> getOrigensSubstituicao() {
        return Util.getListSelectItemSemCampoVazio(OrigemSubstituicaoObjetoCompra.getTiposSubstituicao().toArray(), false);
    }

    public List<ObjetoCompra> completarObjetoCompra(String parte) {
        return facade.getObjetoCompraFacade().buscarObjetoCompraPorSituacao(parte.trim(), SituacaoObjetoCompra.DEFERIDO);
    }

    public List<Licitacao> completarLicitacao(String parte) {
        return facade.getLicitacaoFacade().buscarLicitacaoPorNumeroModalidadeOrNumeroLicitacao(parte.trim());
    }

    public List<DispensaDeLicitacao> completarDispensaLicitacao(String parte) {
        return facade.getDispensaDeLicitacaoFacade().buscarDispensaDeLicitacaoPorNumeroOrExercicioOrResumo(parte.trim());
    }

    public List<Contrato> completarContrato(String parte) {
        return facade.getContratoFacade().buscarContratos(parte.trim());
    }

    public List<RegistroSolicitacaoMaterialExterno> completarRegistroPrecoExterno(String parte) {
        return facade.getRegistroSolicitacaoMaterialExternoFacade().buscarRegistroPrecoExterno(parte.trim());
    }

    public List<SolicitacaoMaterial> completarSolicitacaoCompra(String parte) {
        return facade.getSolicitacaoMaterialFacade().buscarSolicitacaoMaterial(parte.trim());
    }

    public List<Cotacao> completarCotacao(String parte) {
        return facade.getCotacaoFacade().buscarCotacao(parte.trim());
    }

    public List<FormularioCotacao> completarFormularioCotacao(String parte) {
        return facade.getFormularioCotacaoFacade().buscarFormularioCotacao(parte.trim());
    }

    public List<IntencaoRegistroPreco> completarIRP(String parte) {
        return facade.getIntencaoRegistroPrecoFacade().buscarIRP(parte.trim());
    }

    public FiltroSubstituicaoObjetoCompra getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroSubstituicaoObjetoCompra filtro) {
        this.filtro = filtro;
    }

    public List<SubstituicaoObjetoCompraVo> getObjetosCompraDoProcesso() {
        return objetosCompraDoProcesso;
    }

    public void setObjetosCompraDoProcesso(List<SubstituicaoObjetoCompraVo> objetosCompraDoProcesso) {
        this.objetosCompraDoProcesso = objetosCompraDoProcesso;
    }

    public SubstituicaoObjetoCompraVo getObjetoCompraSelecionado() {
        return objetoCompraSelecionado;
    }

    public void setObjetoCompraSelecionado(SubstituicaoObjetoCompraVo objetoCompraSelecionado) {
        this.objetoCompraSelecionado = objetoCompraSelecionado;
    }

    public List<SubstituicaoObjetoCompraVo> getObjetosCompraSubstituicao() {
        return objetosCompraSubstituicao;
    }

    public void setObjetosCompraSubstituicao(List<SubstituicaoObjetoCompraVo> objetosCompraSubstituicao) {
        this.objetosCompraSubstituicao = objetosCompraSubstituicao;
    }

    public List<SubstituicaoObjetoCompraVo> getObjetosCompraDoProcessoFilterBy() {
        return objetosCompraDoProcessoFilterBy;
    }

    public void setObjetosCompraDoProcessoFilterBy
        (List<SubstituicaoObjetoCompraVo> objetosCompraDoProcessoFilterBy) {
        this.objetosCompraDoProcessoFilterBy = objetosCompraDoProcessoFilterBy;
    }
}
