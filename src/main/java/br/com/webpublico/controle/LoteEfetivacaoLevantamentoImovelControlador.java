package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LevantamentoBemImovel;
import br.com.webpublico.entidades.LoteEfetivacaoLevantamentoImovel;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
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

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaEfetivacaoImovel", pattern = "/efetivacao-de-levantamento-imovel/novo/", viewId = "/faces/administrativo/patrimonio/efetivacaolevantamentobem/imovel/edita.xhtml"),
    @URLMapping(id = "editarEfetivacaoImovel", pattern = "/efetivacao-de-levantamento-imovel/editar/#{loteEfetivacaoLevantamentoImovelControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaolevantamentobem/imovel/edita.xhtml"),
    @URLMapping(id = "listarEfetivacaoImovel", pattern = "/efetivacao-de-levantamento-imovel/listar/", viewId = "/faces/administrativo/patrimonio/efetivacaolevantamentobem/imovel/lista.xhtml"),
    @URLMapping(id = "verEfetivacaoImovel", pattern = "/efetivacao-de-levantamento-imovel/ver/#{loteEfetivacaoLevantamentoImovelControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaolevantamentobem/imovel/visualizar.xhtml")
})
public class LoteEfetivacaoLevantamentoImovelControlador extends PrettyControlador<LoteEfetivacaoLevantamentoImovel> implements Serializable, CRUD {

    @EJB
    private LoteEfetivacaoLevantamentoImovelFacade selecionadoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private LevantamentoBemImovelFacade levantamentoBemImovelFacade;

    private List<LevantamentoBemImovel> levantamentosEncontrados;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;

    public LoteEfetivacaoLevantamentoImovelControlador() {
        super(LoteEfetivacaoLevantamentoImovel.class);
        levantamentosEncontrados = new ArrayList<>();
    }

    @URLAction(mappingId = "novaEfetivacaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        selecionado.setDataEfetivacao(sistemaFacade.getDataOperacao());
    }

    @URLAction(mappingId = "verEfetivacaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarEfetivacaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public void efetivar() {
        try {
            Util.validarCampos(selecionado);
            selecionadoFacade.getSingletonGeradorCodigoPatrimonio().bloquearMovimento(LoteEfetivacaoLevantamentoImovel.class);
            selecionado.setCodigo(singletonGeradorCodigo.getProximoCodigo(LoteEfetivacaoLevantamentoImovel.class, "codigo"));
            selecionadoFacade.getSingletonGeradorCodigoPatrimonio().inicializarReset();
            selecionado = selecionadoFacade.salvarAlternativo(selecionado, levantamentosEncontrados);
            selecionadoFacade.getSingletonGeradorCodigoPatrimonio().desbloquear(LoteEfetivacaoLevantamentoImovel.class);
            FacesUtil.addOperacaoRealizada("Operação realizada com sucesso!");
        } catch (ExcecaoNegocioGenerica exg) {
            FacesUtil.addOperacaoNaoRealizada(exg.getMessage());
            selecionadoFacade.getSingletonGeradorCodigoPatrimonio().reset();
            selecionadoFacade.getSingletonGeradorCodigoPatrimonio().desbloquear(LoteEfetivacaoLevantamentoImovel.class);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            selecionadoFacade.getSingletonGeradorCodigoPatrimonio().reset();
            selecionadoFacade.getSingletonGeradorCodigoPatrimonio().desbloquear(LoteEfetivacaoLevantamentoImovel.class);
            logger.error("Erro:", ex);
        } finally {
            selecionadoFacade.getSingletonGeradorCodigoPatrimonio().finalizarReset();
            if (selecionado != null && selecionado.getId() != null) {
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            }
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/efetivacao-de-levantamento-imovel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return selecionadoFacade;
    }

    public List<LevantamentoBemImovel> getLevantamentosEncontrados() {
        return levantamentosEncontrados;
    }

    public void setLevantamentosEncontrados(List<LevantamentoBemImovel> levantamentosEncontrados) {
        this.levantamentosEncontrados = levantamentosEncontrados;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        if (hierarquiaOrcamentaria != null) {
            selecionado.setUnidadeOrcamentaria(hierarquiaOrcamentaria.getSubordinada());
        }
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public void pesquisar() {
        if (hierarquiaOrcamentaria != null) {
            levantamentosEncontrados = levantamentoBemImovelFacade.buscarLevantamentoImovelPorUnidadeOrcamentaria(hierarquiaOrcamentaria.getSubordinada());
            if (levantamentosEncontrados == null || levantamentosEncontrados.isEmpty()) {
                FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), "Não foram encontrados registros para unidade orçamentária.");
            }
        } else {
            FacesUtil.addCampoObrigatorio("Informe a Unidade Orçamentária.");
        }
    }

    public List<SelectItem> retornaHierarquiaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (HierarquiaOrganizacional obj : hierarquiaOrganizacionalFacade.retornaHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(sistemaFacade.getUsuarioCorrente(), sistemaFacade.getDataOperacao())) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public String totalLevantamento() {
        BigDecimal total = BigDecimal.ZERO;
        for (LevantamentoBemImovel levantamentosEncontrado : levantamentosEncontrados) {
            total = total.add(levantamentosEncontrado.getValorBem());
        }
        return Util.formataValor(total);
    }
}
