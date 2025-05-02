package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.PeriodoFaseUnidadeVO;
import br.com.webpublico.enums.SituacaoPeriodoFase;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.PeriodoFaseFacade;
import br.com.webpublico.seguranca.service.RecursoSistemaService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 16/10/13
 * Time: 09:42
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "manutencao-periodofase", pattern = "/manutencao/periodo-fase/", viewId = "/faces/admin/controleusuario/periodofase/encerramento.xhtml"),
})
public class EncerramentoPeriodoFaseControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(EncerramentoPeriodoFaseControlador.class);
    @EJB
    private PeriodoFaseFacade periodoFaseFacade;
    private List<PeriodoFase> listaPeriodosFaseSelecionados;
    private List<PeriodoFase> listaPeriodoFases;
    private List<PeriodoFaseUnidade> listaPeriodoFaseUnidadeSelecionadas;
    private List<HierarquiaOrganizacional> listaUnidades;
    private UnidadeGestora unidadeGestora;
    private PeriodoFaseUnidade periodoFaseUnidade;
    private PeriodoFase periodoFase;
    private Fase fase;
    @Enumerated
    private SituacaoPeriodoFase situacaoPeriodo;
    @ManagedProperty(name = "recursoSistemaService", value = "#{recursoSistemaService}")
    private RecursoSistemaService recursoSistemaService;
    private List<PeriodoFaseUnidadeVO> listaPeriodoFaseUnidadeVO;
    private List<PeriodoFaseUnidadeVO> listaPeriodoFaseUnidadeVOSelecionadas;
    private List<PeriodoFaseUnidadeVO> listaForFiltro;

    public EncerramentoPeriodoFaseControlador() {
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "manutencao-periodofase", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        parametrosIniciais();
    }

    private void parametrosIniciais() {
        Calendar calendar = DataUtil.ultimoDiaMes(getSistemaControlador().getDataOperacao());
        periodoFase = new PeriodoFase();
        periodoFaseUnidade = new PeriodoFaseUnidade();
        periodoFaseUnidade.setFimVigencia(calendar.getTime());
        periodoFaseUnidade.setInicioVigencia(DataUtil.adicionarMeses(periodoFaseUnidade.getFimVigencia(), -1));
        periodoFaseUnidade.setSituacaoPeriodoFase(SituacaoPeriodoFase.ABERTO);
        listaPeriodosFaseSelecionados = Lists.newArrayList();
        listaPeriodoFaseUnidadeSelecionadas = Lists.newArrayList();
        listaPeriodoFases = Lists.newArrayList();
        listaUnidades = Lists.newArrayList();
        listaPeriodoFaseUnidadeVO = Lists.newArrayList();
        listaPeriodoFaseUnidadeVOSelecionadas = Lists.newArrayList();
        listaForFiltro = Lists.newArrayList();
        buscarPeriodosFase();
    }

    private void buscarPeriodosFase() {
        try {
            listaPeriodoFases = periodoFaseFacade.listaPeriodoFasePorUnidadesUnidadeGestoraExercicioFaseEData(getSistemaControlador().getExercicioCorrente(), filtroUnidade(), null, null, getSistemaControlador().getDataOperacao(), getSistemaControlador().getDataOperacao(), SituacaoPeriodoFase.ABERTO);
            listaPeriodosFaseSelecionados.clear();
            if (listaPeriodoFases.isEmpty()) {
                FacesUtil.addAtencao(" Não existem períodos da fase para os filtros informados.");
            }
            Collections.sort(listaPeriodoFases, new Comparator<PeriodoFase>() {
                @Override
                public int compare(PeriodoFase o1, PeriodoFase o2) {
                    return Integer.valueOf(o1.getFase().getCodigo()).compareTo(Integer.valueOf(o2.getFase().getCodigo()));
                }
            });
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void limparFiltros() {
        fase = null;
        unidadeGestora = null;
        situacaoPeriodo = null;
        listaUnidades.clear();
    }

    public void selecionarFase(ActionEvent evento) {
        fase = (Fase) evento.getComponent().getAttributes().get("objeto");
    }

    public void setaPeriodoFaseNoPeriodoFaseUnidade(PeriodoFase periodoFase) {
        this.periodoFase = periodoFase;
        adicionarPeriodoFase(periodoFase);
        recuperarUnidades(periodoFase);
    }

    public void recuperarUnidades(PeriodoFase periodoFase) {
        listaPeriodoFaseUnidadeVO.clear();
        listaForFiltro.clear();
        listaPeriodoFaseUnidadeVOSelecionadas.clear();
        for (PeriodoFaseUnidade unidade : periodoFase.getPeriodoFaseUnidades()) {
            HierarquiaOrganizacional hierarquiaOrganizacional = periodoFaseFacade.getHierarquiaOrganizacionalFacade()
                .getHierarquiaOrganizacionalPorUnidade(UtilRH.getDataOperacao(),
                    unidade.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
            if (hierarquiaOrganizacional != null) {
                PeriodoFaseUnidadeVO pfuVO = new PeriodoFaseUnidadeVO();
                pfuVO.setHierarquiaOrganizacional(hierarquiaOrganizacional);
                pfuVO.setPeriodoFaseUnidade(unidade);
                Util.adicionarObjetoEmLista(listaPeriodoFaseUnidadeVO, pfuVO);
                Util.adicionarObjetoEmLista(listaForFiltro, pfuVO);
                if (listaPeriodoFaseUnidadeSelecionadas.contains(unidade)) {
                    Util.adicionarObjetoEmLista(listaPeriodoFaseUnidadeVOSelecionadas, pfuVO);
                }
            }
        }
        Util.ordenarListas(listaPeriodoFaseUnidadeVO, listaForFiltro);
    }

    public void setaPeriodoFaseUnidadeNoUsuario(PeriodoFaseUnidade periodoFaseUnidade) {
        this.periodoFaseUnidade = periodoFaseUnidade;
    }

    public List<SelectItem> getSituacaoPeriodoFase() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (SituacaoPeriodoFase s : SituacaoPeriodoFase.values()) {
            toReturn.add(new SelectItem(s, s.getDescricao()));
        }
        return toReturn;
    }

    private String filtroUnidade() {
        StringBuilder stb = new StringBuilder();

        if (this.listaUnidades.size() > 0) {
            StringBuilder idUnidades = new StringBuilder();
            String concat = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                idUnidades.append(concat).append(lista.getSubordinada().getId());
                concat = ",";
            }
            stb.append(" AND ").append(" PFU.UNIDADEORGANIZACIONAL_ID IN (").append(idUnidades).append(")");
        }
        return stb.toString();
    }

    public String icone(PeriodoFase pf) {
        if (listaPeriodosFaseSelecionados.contains(pf)) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public Boolean containsTodasUnidades() {
        for (PeriodoFaseUnidadeVO vo : listaPeriodoFaseUnidadeVO) {
            if (!listaPeriodoFaseUnidadeVOSelecionadas.contains(vo)) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public void removerTodasUnidades() {
        listaPeriodoFaseUnidadeVOSelecionadas.clear();
        listaPeriodoFaseUnidadeSelecionadas.clear();
    }

    public void adicionarTodasUnidades() {
        listaPeriodoFaseUnidadeVOSelecionadas.clear();
        listaPeriodoFaseUnidadeVOSelecionadas.addAll(listaPeriodoFaseUnidadeVO);
        for (PeriodoFaseUnidadeVO vo : listaPeriodoFaseUnidadeVOSelecionadas) {
            Util.adicionarObjetoEmLista(listaPeriodoFaseUnidadeSelecionadas, vo.getPeriodoFaseUnidade());
        }
    }

    public Boolean containsUnidade(PeriodoFaseUnidadeVO pfuVO) {
        return listaPeriodoFaseUnidadeVOSelecionadas.contains(pfuVO);
    }

    public void removerUnidade(PeriodoFaseUnidadeVO pfuVO) {
        listaPeriodoFaseUnidadeVOSelecionadas.remove(pfuVO);
        listaPeriodoFaseUnidadeSelecionadas.remove(pfuVO.getPeriodoFaseUnidade());
    }

    public void adicionarUnidade(PeriodoFaseUnidadeVO pfuVO) {
        if (!listaPeriodoFaseUnidadeVOSelecionadas.contains(pfuVO)) {
            listaPeriodoFaseUnidadeVOSelecionadas.add(pfuVO);
            Util.adicionarObjetoEmLista(listaPeriodoFaseUnidadeSelecionadas, pfuVO.getPeriodoFaseUnidade());
        }
    }

    public String title(PeriodoFase pf) {
        if (listaPeriodosFaseSelecionados.contains(pf)) {
            return "Clique para deselecionar este período fase.";
        }
        return "Clique para selecionar este período fase.";
    }

    public String iconeTodos() {
        if (listaPeriodosFaseSelecionados.size() == listaPeriodoFases.size()) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String titleTodos() {
        if (listaPeriodosFaseSelecionados.size() == listaPeriodoFases.size()) {
            return "Clique para deselecionar todos os período fase.";
        }
        return "Clique para selecionar todos os período fase.";
    }

    public void adicionarPeriodoFase(PeriodoFase pf) {
        if (!listaPeriodosFaseSelecionados.contains(pf)) {
            listaPeriodosFaseSelecionados.add(pf);
        }
    }

    public void selecionarPeriodoFase(PeriodoFase pf) {
        if (listaPeriodosFaseSelecionados.contains(pf)) {
            listaPeriodosFaseSelecionados.remove(pf);
        } else {
            listaPeriodosFaseSelecionados.add(pf);
            periodoFase = pf;
        }
    }

    public void selecionarTodosPeriodoFase() {
        if (listaPeriodosFaseSelecionados.size() == listaPeriodoFases.size()) {
            listaPeriodosFaseSelecionados.removeAll(listaPeriodoFases);
        } else {
            for (PeriodoFase listaPeriodoFase : listaPeriodoFases) {
                if (!listaPeriodosFaseSelecionados.contains(listaPeriodoFase)) {
                    listaPeriodosFaseSelecionados.add(listaPeriodoFase);
                    periodoFase = listaPeriodoFase;
                    selecionarTodosPeriodoFaseUnidade();
                }
            }
        }
    }


    public void selecionarTodosPeriodoFaseUnidade() {
        if (listaPeriodoFaseUnidadeSelecionadas.size() == periodoFase.getPeriodoFaseUnidades().size()) {
            listaPeriodoFaseUnidadeSelecionadas.removeAll(periodoFase.getPeriodoFaseUnidades());
        } else {
            for (PeriodoFaseUnidade pfu : periodoFase.getPeriodoFaseUnidades()) {
                if (!listaPeriodoFaseUnidadeSelecionadas.contains(pfu)) {
                    listaPeriodoFaseUnidadeSelecionadas.add(pfu);
                }
            }
        }
    }

    public void salvar() {
        try {
            validarCampos();
            periodoFaseFacade.salvar(listaPeriodosFaseSelecionados, listaPeriodoFaseUnidadeSelecionadas, periodoFaseUnidade);
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso");
            FacesUtil.redirecionamentoInterno("/manutencao/periodo-fase/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            periodoFaseFacade.lancarException(ex);
        }
    }

    public void salvarLimpandoFases() {
        try {
            validarCampos();
            periodoFaseFacade.salvar(listaPeriodosFaseSelecionados, listaPeriodoFaseUnidadeSelecionadas, periodoFaseUnidade);
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso");
            FacesUtil.redirecionamentoInterno("/manutencao/periodo-fase/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Detalhes do erro: " + ex.getMessage());
        }
        limparFases();
    }

    public void limparFases() {
        try {
            recursoSistemaService.getSingletonRecursosSistema().limparFases();
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Operação Não Realizada ao limpar as fases!", e.getMessage());
        }
    }

    public void cancelar() {
        FacesUtil.redirecionamentoInterno("/periodofase/listar/");
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (listaPeriodosFaseSelecionados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para salvar, é necessário selecionar um período fase.");
        }
        if (listaPeriodoFaseUnidadeSelecionadas.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Clique no botão Unidades para selecionar o período fase que será alterado.");
        }
        if (periodoFaseUnidade.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Fechamento deve ser informado.");
        }
        if (periodoFaseUnidade.getFimVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Último Movimento deve ser informado.");
        }
        if (periodoFaseUnidade.getInicioVigencia() != null && periodoFaseUnidade.getFimVigencia() != null) {
            if (periodoFaseUnidade.getFimVigencia().before(periodoFaseUnidade.getInicioVigencia())) {
                ve.adicionarMensagemDeCampoObrigatorio("A data do Último Movimento deve ser maior que a data de Fechamento.");
            }
        }
        ve.lancarException();
    }

    public PeriodoFase getPeriodoFase() {
        return periodoFase;
    }

    public void setPeriodoFase(PeriodoFase periodoFase) {
        this.periodoFase = periodoFase;
    }

    public List<PeriodoFase> getListaPeriodoFases() {
        return listaPeriodoFases;
    }

    public void setListaPeriodoFases(List<PeriodoFase> listaPeriodoFases) {
        this.listaPeriodoFases = listaPeriodoFases;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public Fase getFase() {
        return fase;
    }

    public void setFase(Fase fase) {
        this.fase = fase;
    }

    public PeriodoFaseUnidade getPeriodoFaseUnidade() {
        return periodoFaseUnidade;
    }

    public void setPeriodoFaseUnidade(PeriodoFaseUnidade periodoFaseUnidade) {
        this.periodoFaseUnidade = periodoFaseUnidade;
    }

    public SituacaoPeriodoFase getSituacaoPeriodo() {
        return situacaoPeriodo;
    }

    public void setSituacaoPeriodo(SituacaoPeriodoFase situacaoPeriodo) {
        this.situacaoPeriodo = situacaoPeriodo;
    }

    public RecursoSistemaService getRecursoSistemaService() {
        return recursoSistemaService;
    }

    public void setRecursoSistemaService(RecursoSistemaService recursoSistemaService) {
        this.recursoSistemaService = recursoSistemaService;
    }

    public List<PeriodoFaseUnidadeVO> getListaPeriodoFaseUnidadeVO() {
        return listaPeriodoFaseUnidadeVO;
    }

    public void setListaPeriodoFaseUnidadeVO(List<PeriodoFaseUnidadeVO> listaPeriodoFaseUnidadeVO) {
        this.listaPeriodoFaseUnidadeVO = listaPeriodoFaseUnidadeVO;
    }

    public List<PeriodoFaseUnidadeVO> getListaForFiltro() {
        return listaForFiltro;
    }

    public void setListaForFiltro(List<PeriodoFaseUnidadeVO> listaForFiltro) {
        this.listaForFiltro = listaForFiltro;
    }
}


