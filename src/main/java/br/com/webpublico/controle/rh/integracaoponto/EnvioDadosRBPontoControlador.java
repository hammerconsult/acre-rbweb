/*
 * Codigo gerado automaticamente em Mon Sep 05 09:12:19 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle.rh.integracaoponto;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.EnvioDadosRBPonto;
import br.com.webpublico.entidades.rh.ItemEnvioDadosRBPonto;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.rh.TipoEnvioDadosRBPonto;
import br.com.webpublico.enums.rh.TipoInformacaoEnvioRBPonto;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.integracaoponto.EnvioDadosRBPontoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.beust.jcommander.internal.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean(name = "envioDadosRBPontoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoEnvioDadosRBPonto", pattern = "/envio-de-dados-ponto/novo/", viewId = "/faces/rh/administracaodepagamento/enviodadosrbponto/edita.xhtml"),
    @URLMapping(id = "listaEnvioDadosRBPonto", pattern = "/envio-de-dados-ponto/listar/", viewId = "/faces/rh/administracaodepagamento/enviodadosrbponto/lista.xhtml"),
    @URLMapping(id = "verEnvioDadosRBPonto", pattern = "/envio-de-dados-ponto/ver/#{envioDadosRBPontoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/enviodadosrbponto/visualizar.xhtml"),
})
public class EnvioDadosRBPontoControlador extends PrettyControlador<EnvioDadosRBPonto> implements Serializable, CRUD {

    @EJB
    private EnvioDadosRBPontoFacade envioDadosRBPontoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private ConcessaoFeriasLicencaFacade concessaoFeriasLicencaFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<ItemEnvioDadosRBPonto> itens;
    private Future<EnvioDadosRBPonto> future;

    public EnvioDadosRBPontoControlador() {
        super(EnvioDadosRBPonto.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return envioDadosRBPontoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/envio-de-dados-ponto/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoEnvioDadosRBPonto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUnidadeOrganizacional(null);
        selecionado.setContratoFP(null);
        selecionado.setDataInicial(null);
        selecionado.setDataFinal(null);
        selecionado.setDataEnvio(null);
        selecionado.setTipoInformacaoEnvioRBPonto(null);
        selecionado.setApenasNaoEnviados(Boolean.TRUE);
        selecionado.setItensEnvioDadosRBPontos(new ArrayList<ItemEnvioDadosRBPonto>());
        itens = Lists.newArrayList();
    }

    @URLAction(mappingId = "verEnvioDadosRBPonto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            future = null;
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional != null ? hierarquiaOrganizacional.getSubordinada() : null);
            selecionado.getItensEnvioDadosRBPontos().addAll(itens);
            selecionado.setUsuario(sistemaFacade.getUsuarioCorrente());
            future = envioDadosRBPontoFacade.salvarEnviodeDados(selecionado);
            FacesUtil.executaJavaScript("iniciar()");
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            logger.error("Erro ", e);
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void verificarTermino() throws ExecutionException, InterruptedException {
        if (future != null && future.isDone()) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParaVerOrEditar(future.get().getId(), "ver");
        }
    }

    public void carregarItens() {
        try {
            validarCamposCarregarItens();
            itens = Lists.newArrayList();
            if (TipoInformacaoEnvioRBPonto.AFASTAMENTO.equals(selecionado.getTipoInformacaoEnvioRBPonto())) {
                List<Afastamento> afastamentos = envioDadosRBPontoFacade.buscarAfastamentosParaEnvio(selecionado.getContratoFP(), selecionado.getDataInicial(), selecionado.getDataFinal(), hierarquiaOrganizacional, selecionado.getApenasNaoEnviados());
                for (Afastamento afastamento : afastamentos) {
                    ItemEnvioDadosRBPonto item = new ItemEnvioDadosRBPonto();
                    item.setContratoFP(afastamento.getContratoFP());
                    item.setDataInicial(afastamento.getInicio());
                    item.setDataFinal(afastamento.getTermino());
                    item.setIdentificador(afastamento.getId());
                    item.setEnvioDadosRBPonto(selecionado);
                    itens.add(item);
                }
            } else {
                List<ConcessaoFeriasLicenca> ferias = envioDadosRBPontoFacade.buscarFeriasParaEnvio(selecionado.getContratoFP(), selecionado.getDataInicial(), selecionado.getDataFinal(), hierarquiaOrganizacional, selecionado.getApenasNaoEnviados());
                for (ConcessaoFeriasLicenca f : ferias) {
                    ItemEnvioDadosRBPonto item = new ItemEnvioDadosRBPonto();
                    item.setContratoFP(f.getPeriodoAquisitivoFL().getContratoFP());
                    item.setDataInicial(f.getDataInicial());
                    item.setDataFinal(f.getDataFinal());
                    item.setIdentificador(f.getId());
                    item.setEnvioDadosRBPonto(selecionado);
                    itens.add(item);
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            logger.error("Erro ao carregar os itens do envio de dados ao RBPonto {}", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private void validarCamposCarregarItens() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoInformacaoEnvioRBPonto() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Informações é obrigatório.");
        }
        ve.lancarException();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (itens.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário inserir ao menos um afastamento ou férias.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTipoInformacoesEnvio() {
        return Util.getListSelectItem(TipoInformacaoEnvioRBPonto.values());
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public String recuperarTipoEnvio(ItemEnvioDadosRBPonto itemEnvioDadosRBPonto) {
        if (itemEnvioDadosRBPonto.getIdentificador() != null) {
            if (TipoInformacaoEnvioRBPonto.AFASTAMENTO.equals(itemEnvioDadosRBPonto.getEnvioDadosRBPonto().getTipoInformacaoEnvioRBPonto())) {
                Afastamento afastamento =  afastamentoFacade.recuperar(itemEnvioDadosRBPonto.getIdentificador());
                return afastamento.getTipoEnvioDadosRBPonto() != null ? afastamento.getTipoEnvioDadosRBPonto().toString() : TipoEnvioDadosRBPonto.NAO_ENVIADO.toString();
            } else {
                ConcessaoFeriasLicenca concessaoFeriasLicenca =  concessaoFeriasLicencaFacade.recuperar(itemEnvioDadosRBPonto.getIdentificador());
                return concessaoFeriasLicenca.getTipoEnvioDadosRBPonto() != null ? concessaoFeriasLicenca.getTipoEnvioDadosRBPonto().toString() : TipoEnvioDadosRBPonto.NAO_ENVIADO.toString();
            }
        }
        return "";
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return contratoFPFacade.buscarContratos(parte.trim());
    }

    public void removerItemEnvioDadosRBPonto(ItemEnvioDadosRBPonto itemEnvioDadosRBPonto) {
        itens.remove(itemEnvioDadosRBPonto);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<ItemEnvioDadosRBPonto> getItens() {
        return itens;
    }

    public void setItens(List<ItemEnvioDadosRBPonto> itens) {
        this.itens = itens;
    }
}
