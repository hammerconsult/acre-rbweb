package br.com.webpublico.controle;


import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.TipoAquisicaoContrato;
import br.com.webpublico.enums.TipoContrato;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConsultaContratoEmVigenciaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "contratoVigConsulta", pattern = "/contrato-em-vigencia/consulta/", viewId = "/faces/administrativo/contrato/consulta-contrato-vigencia/consulta.xhtml"),
    @URLMapping(id = "contratoVigConsultaDetalhada", pattern = "/contrato-em-vigencia/consulta-detalhada/", viewId = "/faces/administrativo/contrato/consulta-contrato-vigencia/consulta-detalhada.xhtml")
})
public class ConsultaContratoEmVigenciaControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ConsultaContratoEmVigenciaControlador.class);
    public static final String AGRUPADOR = "AGRUPADOR";
    public static final String ASSISTENTE = "ASSISTENTE";
    public static final String FILTRO_CONSULTA = "FILTRO_CONSULTA";

    @EJB
    private ConsultaContratoEmVigenciaFacade fadade;
    private FiltroRelatorioContrato filtroConsulta;
    private Boolean dadosAgrupado;
    private AgrupadorContratoEmVigencia agrupadorSelecionado;
    private AssistenteAgrupadorContratoEmVigencia assistenteAgrupador;

    @URLAction(mappingId = "contratoVigConsulta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void consulta() {
        limparDadosConsulta();
        novoFiltro();
        AssistenteAgrupadorContratoEmVigencia assistente = (AssistenteAgrupadorContratoEmVigencia) Web.pegaDaSessao(ASSISTENTE);
        if (assistente != null) {
            assistenteAgrupador = assistente;
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    @URLAction(mappingId = "contratoVigConsultaDetalhada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void consultaDetalhada() {
        AgrupadorContratoEmVigencia agrupador = (AgrupadorContratoEmVigencia) Web.pegaDaSessao(AGRUPADOR);
        if (agrupador != null) {
            agrupadorSelecionado = agrupador;
            agrupadorSelecionado.setContratosPesquisa(agrupador.getContratos());
            Collections.sort(agrupadorSelecionado.getContratos());
            for (ContratoEmVigencia contrato : agrupador.getContratos()) {
                Collections.sort(contrato.getItens());
            }
            preencherTotalizadorGrupoObjetoCompra();
            novoFiltro();
        }
    }

    public void preencherTotalizadorGrupoObjetoCompra() {
        Map<Long, List<Long>> map = new HashMap<>();
        agrupadorSelecionado.setGruposObjetoCompra(Lists.<AgrupadorContratoEmVigencia>newArrayList());

        for (ContratoEmVigencia contrato : agrupadorSelecionado.getContratosPesquisa()) {
            for (ItemContratoEmVigencia item : contrato.getItens()) {
                if (!map.containsKey(item.getIdGrupoObjetoCompra().longValue())) {
                    map.put(item.getIdGrupoObjetoCompra().longValue(), Lists.<Long>newArrayList(contrato.getIdProcesso()));
                } else {
                    List<Long> ids = map.get(item.getIdGrupoObjetoCompra().longValue());
                    if (!ids.contains(contrato.getIdProcesso())) {
                        ids.add(contrato.getIdProcesso());
                        map.put(item.getIdGrupoObjetoCompra().longValue(), ids);
                    }
                }
            }
        }
        for (Map.Entry<Long, List<Long>> entry : map.entrySet()) {
            List<AgrupadorContratoEmVigencia> grupos = fadade.getQuantidadeLicitadaoPorGrupoObjetoCompra(entry.getKey(), entry.getValue());
            agrupadorSelecionado.getGruposObjetoCompra().addAll(grupos);

        }
    }

    public void novoFiltro() {
        filtroConsulta = new FiltroRelatorioContrato();
        dadosAgrupado = true;
    }

    public List<HierarquiaOrganizacional> completarUnidadeAdministrativa(String parte) {
        return fadade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaHorganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public List<SelectItem> getTiposAquisicaoContrato() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAquisicaoContrato object : TipoAquisicaoContrato.values()) {
            if (!TipoAquisicaoContrato.COMPRA_DIRETA.equals(object)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTiposObjetoCompra() {
        return Util.getListSelectItem(TipoObjetoCompra.values(), false);
    }

    public List<SelectItem> getTiposContrato() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoContrato object : TipoContrato.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void limparDadosConsulta() {
        if (assistenteAgrupador != null) {
            assistenteAgrupador.setAgrupadores(Lists.<AgrupadorContratoEmVigencia>newArrayList());
        }
        novoFiltro();
    }

    public void voltar() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesUtil.getRequestContextPath() + "/home");
        } catch (IOException e) {
            logger.error("Erro ao voltar home{}", e);
        }
    }

    private Date getDataReferencia() {
        return fadade.getSistemaFacade().getDataOperacao();
    }

    public void limparFiltrosPesquisa() {
        agrupadorSelecionado.setContratosPesquisa(agrupadorSelecionado.getContratos());
        for (ContratoEmVigencia contrato : agrupadorSelecionado.getContratosPesquisa()) {
            for (ItemContratoEmVigencia item : contrato.getItens()) {
                item.setPesquisado(true);
            }
        }
        novoFiltro();
    }

    public void pesquisar() {
        try {
            String palavra = filtroConsulta.getPalavra();
            if (Strings.isNullOrEmpty(palavra)) {
                limparFiltrosPesquisa();
                return;
            }
            agrupadorSelecionado.setContratosPesquisa(Lists.<ContratoEmVigencia>newArrayList());
            Iterator<ContratoEmVigencia> iteratorCont = agrupadorSelecionado.getContratos().iterator();
            while (iteratorCont.hasNext()) {
                ContratoEmVigencia contrato = iteratorCont.next();

                if (contrato.getNumeroTermo().trim().contains(palavra.trim())
                    || contrato.getNumeroContrato().trim().contains(palavra.trim())
                    || contrato.getContratado().toLowerCase().trim().contains(palavra.toLowerCase().trim())
                    || contrato.getUnidadeAdministrativa().toLowerCase().trim().contains(palavra.toLowerCase().trim())
                    || contrato.getNumeroProcesso().toLowerCase().trim().contains(palavra.toLowerCase().trim())
                    || contrato.getObjeto().toLowerCase().trim().contains(palavra.toLowerCase().trim())) {
                    agrupadorSelecionado.getContratosPesquisa().add(contrato);
                } else {
                    Iterator<ItemContratoEmVigencia> iteratorItem = contrato.getItens().iterator();
                    while (iteratorItem.hasNext()) {
                        ItemContratoEmVigencia item = iteratorItem.next();
                        if (item.getObjetoCompra().toLowerCase().trim().contains(palavra.toLowerCase().trim())) {
                            agrupadorSelecionado.getContratosPesquisa().add(contrato);
                        } else {
                            item.setPesquisado(false);
                        }
                    }
                }
            }
            if (agrupadorSelecionado.getContratosPesquisa().isEmpty()) {
                FacesUtil.addOperacaoNaoRealizada("Não existe resultado para os filtros informados.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void consultarAndCriarAgrupadores() {
        try {
            assistenteAgrupador = fadade.buscarContratos(getCondicao(), DataUtil.getDataFormatada(getDataReferencia()));
            assistenteAgrupador.setAgrupadores(assistenteAgrupador.getAgrupadores());
            if (assistenteAgrupador.getAgrupadores() == null || assistenteAgrupador.getAgrupadores().isEmpty()) {
                FacesUtil.addAtencao("Não foi possível criar os agrupadores por grupo de objeto de compra para os contratos em vigência relacionados.");
                setDadosAgrupado(false);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void redirecionarParaConsultaDetalhada(AgrupadorContratoEmVigencia agrupador) {
        if (agrupador != null && agrupador.getAgrupadorGOC() != null) {
            Web.poeNaSessao(AGRUPADOR, agrupador);
            Web.poeNaSessao(ASSISTENTE, assistenteAgrupador);
            Web.poeNaSessao(FILTRO_CONSULTA, filtroConsulta);
            FacesUtil.redirecionamentoInterno("/contrato-em-vigencia/consulta-detalhada/");
        }
    }

    public void redirecionarParaConsulta() {
        FacesUtil.redirecionamentoInterno("/contrato-em-vigencia/consulta/");
    }

    private String getCondicao() {
        String retorno = "";
        if (filtroConsulta.getUnidadeAdministrativa() != null) {
            retorno += " and uc.unidadeadministrativa_id = " + filtroConsulta.getUnidadeAdministrativa().getSubordinada().getId();
        }
        if (filtroConsulta.getTipoContrato() != null) {
            retorno += " and cont.tipocontrato = '" + filtroConsulta.getTipoContrato().name() + "'";
        }
        if (filtroConsulta.getTipoAquisicaoContrato() != null) {
            retorno += " and cont.tipoaquisicao = '" + filtroConsulta.getTipoAquisicaoContrato().name() + "'";
        }
        if (filtroConsulta.getTipoObjetoCompra() != null) {
            retorno += " and coalesce(obj.tipoobjetocompra, obj_ise.tipoobjetocompra, obj_icv.tipoobjetocompra) = '" + filtroConsulta.getTipoObjetoCompra().name() + "'";
        }
        return retorno;
    }

    public FiltroRelatorioContrato getFiltroConsulta() {
        return filtroConsulta;
    }

    public void setFiltroConsulta(FiltroRelatorioContrato filtroConsulta) {
        this.filtroConsulta = filtroConsulta;
    }

    public AgrupadorContratoEmVigencia getAgrupadorSelecionado() {
        return agrupadorSelecionado;
    }

    public void setAgrupadorSelecionado(AgrupadorContratoEmVigencia agrupadorSelecionado) {
        this.agrupadorSelecionado = agrupadorSelecionado;
    }

    public AssistenteAgrupadorContratoEmVigencia getAssistenteAgrupador() {
        return assistenteAgrupador;
    }

    public void setAssistenteAgrupador(AssistenteAgrupadorContratoEmVigencia assistenteAgrupador) {
        this.assistenteAgrupador = assistenteAgrupador;
    }

    public Boolean getDadosAgrupado() {
        return dadosAgrupado;
    }

    public void setDadosAgrupado(Boolean dadosAgrupado) {
        this.dadosAgrupado = dadosAgrupado;
    }
}
