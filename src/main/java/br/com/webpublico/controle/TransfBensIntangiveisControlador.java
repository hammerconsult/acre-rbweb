package br.com.webpublico.controle;

import br.com.webpublico.entidades.BensIntangiveis;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.TransfBensIntangiveis;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.TransfBensIntangiveisFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 17/02/14
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */


@ManagedBean(name = "transfBensIntangiveisControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-transf-bens-intangiveis", pattern = "/contabil/transferencia-bens-intangiveis/novo/", viewId = "/faces/financeiro/orcamentario/bens/transfbensintangiveis/edita.xhtml"),
    @URLMapping(id = "editar-transf-bens-intangiveis", pattern = "/contabil/transferencia-bens-intangiveis/editar/#{transfBensIntangiveisControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/transfbensintangiveis/edita.xhtml"),
    @URLMapping(id = "ver-transf-bens-intangiveis", pattern = "/contabil/transferencia-bens-intangiveis/ver/#{transfBensIntangiveisControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/transfbensintangiveis/visualizar.xhtml"),
    @URLMapping(id = "listar-transf-bens-intangiveis", pattern = "/contabil/transferencia-bens-intangiveis/listar/", viewId = "/faces/financeiro/orcamentario/bens/transfbensintangiveis/lista.xhtml"),
})
public class TransfBensIntangiveisControlador extends PrettyControlador<TransfBensIntangiveis> implements Serializable, CRUD {

    @EJB
    private TransfBensIntangiveisFacade transfBensIntangiveisFacade;
    private ConverterAutoComplete converterUnidade;
    private ConverterAutoComplete converterGrupoBem;
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrigem;
    private HierarquiaOrganizacional hierarquiaOrganizacionalDestino;

    public TransfBensIntangiveisControlador() {
        super(TransfBensIntangiveis.class);
    }

    public TransfBensIntangiveisFacade getFacade() {
        return transfBensIntangiveisFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return transfBensIntangiveisFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contabil/transferencia-bens-intangiveis/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-transf-bens-intangiveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataTransferencia(transfBensIntangiveisFacade.getSistemaFacade().getDataOperacao());
        selecionado.setExercicio(transfBensIntangiveisFacade.getSistemaFacade().getExercicioCorrente());
        if (transfBensIntangiveisFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso! ", transfBensIntangiveisFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "ver-transf-bens-intangiveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-transf-bens-intangiveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    public void recuperaEditarVer() {
        selecionado = transfBensIntangiveisFacade.recuperar(selecionado.getId());
        if (selecionado.getUnidadeOrigem() != null) {
            hierarquiaOrganizacionalOrigem = transfBensIntangiveisFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataTransferencia(), selecionado.getUnidadeOrigem(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        }
        if (selecionado.getUnidadeDestino() != null) {
            hierarquiaOrganizacionalDestino = transfBensIntangiveisFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataTransferencia(), selecionado.getUnidadeDestino(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        }
    }

    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarCampos();
            definirUnidadeOrigem();
            definirUnidadeDestino();
            if (isOperacaoNovo()) {
                transfBensIntangiveisFacade.salvarNovo(selecionado);
                FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso. ");
            } else {
                transfBensIntangiveisFacade.salvar(selecionado);
                FacesUtil.addOperacaoRealizada(" Registro alterado com sucesso. ");
            }
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(" Não foi possível salvar. Detalhes do erro: " + ex.getMessage() + ". Contate o Suporte.");
        }
    }

    public void definirOperacaoRecuperandoEvento() {
        if (selecionado.getOperacaoOrigem() != null) {
            if (TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_CONCEDIDA.equals(selecionado.getOperacaoOrigem())) {
                selecionado.setOperacaoDestino(TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_RECEBIDA);
                selecionado.setEventoContabilDestino(null);
                selecionado.setEventoContabilOrigem(null);
                definirEventoOrigem();
                definirEventoDestino();
            } else if (TipoOperacaoBensIntangiveis.TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_CONCEDIDA.equals(selecionado.getOperacaoOrigem())) {
                selecionado.setOperacaoDestino(TipoOperacaoBensIntangiveis.TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_RECEBIDA);
                selecionado.setEventoContabilDestino(null);
                selecionado.setEventoContabilOrigem(null);
                definirEventoOrigem();
                definirEventoDestino();
            }
        }

    }

    public void definirEventoDestino() {
        try {
            transfBensIntangiveisFacade.buscarEventoDestino(selecionado);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void definirEventoOrigem() {
        try {
            transfBensIntangiveisFacade.buscarEventoOrigem(selecionado);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void definirUnidadeOrigem() {
        if (hierarquiaOrganizacionalOrigem.getSubordinada() != null) {
            selecionado.setUnidadeOrigem(hierarquiaOrganizacionalOrigem.getSubordinada());
        }
    }

    public void definirUnidadeDestino() {
        if (hierarquiaOrganizacionalDestino.getSubordinada() != null) {
            selecionado.setUnidadeDestino(hierarquiaOrganizacionalDestino.getSubordinada());
        }
    }

    public List<SelectItem> getOperacoesBensIntangiveisOrigem() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        toReturn.add(new SelectItem(TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_CONCEDIDA, TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_CONCEDIDA.getDescricao()));
        toReturn.add(new SelectItem(TipoOperacaoBensIntangiveis.TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_CONCEDIDA, TipoOperacaoBensIntangiveis.TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_CONCEDIDA.getDescricao()));
        return Util.ordenaSelectItem(toReturn);
    }


    public List<SelectItem> getOperacoesBensIntangiveisDestino() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        toReturn.add(new SelectItem(TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_RECEBIDA, TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_RECEBIDA.getDescricao()));
        toReturn.add(new SelectItem(TipoOperacaoBensIntangiveis.TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_RECEBIDA, TipoOperacaoBensIntangiveis.TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_RECEBIDA.getDescricao()));
        return Util.ordenaSelectItem(toReturn);
    }

    public List<SelectItem> getListaTipoLancamento() {
        List<SelectItem> lista = new ArrayList<>();
        for (TipoLancamento lanc : TipoLancamento.values()) {
            lista.add(new SelectItem(lanc, lanc.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getListaTipoGrupoBem() {
        BensIntangiveis bensIntangiveis = new BensIntangiveis();
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoGrupo object : TipoGrupo.values()) {
            if (object.getClasseDeUtilizacao() == bensIntangiveis.getClass()) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> completaUnidadeOrganizacional(String parte) {
        try {
            return transfBensIntangiveisFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "3", "ORCAMENTARIA", transfBensIntangiveisFacade.getSistemaFacade().getDataOperacao());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<GrupoBem> completaGrupoBemIntangiveis(String parte) {
        return transfBensIntangiveisFacade.getGrupoBemFacade().buscarGrupoBemPorTipoBem(parte, TipoBem.INTANGIVEIS);
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacionalOrigem == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Organizacional de Origem deve ser informado.");
        }
        if (hierarquiaOrganizacionalDestino == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Organizacional de Destino deve ser informado.");
        }
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo valor deve ser maior que zero(0). ");
        }
        if (selecionado.getEventoContabilOrigem() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Não foi encontrado nenhum evento contábil de origem para a transferência.");
        }
        if (selecionado.getEventoContabilDestino() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Não foi encontrado nenhum evento contábil de destino para a transferência.");
        }
        ve.lancarException();
    }

    public ConverterAutoComplete getConverterUnidade() {
        if (converterUnidade == null) {
            return converterUnidade = new ConverterAutoComplete(HierarquiaOrganizacional.class, transfBensIntangiveisFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterUnidade;
    }

    public ConverterAutoComplete getConverterGrupoBem() {
        if (converterGrupoBem == null) {
            return converterGrupoBem = new ConverterAutoComplete(GrupoBem.class, transfBensIntangiveisFacade.getGrupoBemFacade());
        }
        return converterGrupoBem;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrigem() {
        return hierarquiaOrganizacionalOrigem;
    }

    public void setHierarquiaOrganizacionalOrigem(HierarquiaOrganizacional hierarquiaOrganizacionalOrigem) {
        this.hierarquiaOrganizacionalOrigem = hierarquiaOrganizacionalOrigem;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalDestino() {
        return hierarquiaOrganizacionalDestino;
    }

    public void setHierarquiaOrganizacionalDestino(HierarquiaOrganizacional hierarquiaOrganizacionalDestino) {
        this.hierarquiaOrganizacionalDestino = hierarquiaOrganizacionalDestino;
    }
}
