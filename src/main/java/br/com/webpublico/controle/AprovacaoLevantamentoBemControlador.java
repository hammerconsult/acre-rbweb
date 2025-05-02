package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
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
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Suporte Contabil
 * Date: 23/04/14
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "aprovacaoLevantamentoBemControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAprovacaoLevantamento", pattern = "/aprovacao-levantamento-bem/novo/", viewId = "/faces/administrativo/patrimonio/aprovacaolevantamento/edita.xhtml"),
    @URLMapping(id = "editarAprovacaoLevantamento", pattern = "/aprovacao-levantamento-bem/editar/#{aprovacaoLevantamentoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/aprovacaolevantamento/edita.xhtml"),
    @URLMapping(id = "listarAprovacaoLevantamento", pattern = "/aprovacao-levantamento-bem/listar/", viewId = "/faces/administrativo/patrimonio/aprovacaolevantamento/lista.xhtml"),
    @URLMapping(id = "verAprovacaoLevantamento", pattern = "/aprovacao-levantamento-bem/ver/#{aprovacaoLevantamentoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/aprovacaolevantamento/visualizar.xhtml"),
    @URLMapping(id = "consultaAprovacaoLevantamento", pattern = "/consulta-levantamento-bens-patrimoniais/consulta/", viewId = "/faces/administrativo/patrimonio/aprovacaolevantamento/consulta.xhtml")
})
public class AprovacaoLevantamentoBemControlador extends PrettyControlador<AprovacaoLevantamentoBem> implements Serializable, CRUD {

    @EJB
    private AprovacaoLevantamentoBemFacade aprovacaoLevantamentoBemFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LevantamentoBensPatrimoniaisFacade levantamentoFacade;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<LevantamentoBemPatrimonial> levantamentosEncontrados;
    private List<LevantamentoBemPatrimonial> levantamentosSelecionados;
    private List<LevantamentoBemPatrimonial> levantamentosInconsistentes;
    private PesquisaLevantamento pesquisaLevantamento;
    private ConsultaLevantamento consultaLevantamento;
    private List<Object> levantamentosObjectConsulta;
    private BigDecimal total = BigDecimal.ZERO;
    private UnidadeOrganizacional unidadeOrganizacional;


    public AprovacaoLevantamentoBemControlador() {
        super(AprovacaoLevantamentoBem.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return aprovacaoLevantamentoBemFacade;
    }

    @Override
    public void salvar() {
        try {
            ParametroPatrimonio parametroPatrimonio = parametroPatrimonioFacade.recuperarParametro();
            selecionado.aprovarLevantamentos(levantamentosSelecionados, levantamentosInconsistentes, parametroPatrimonio != null ? parametroPatrimonio.getDataDeCorte() : null);
            Web.limpaNavegacao();
            super.salvar();
        } catch (ValidacaoException ve) {
            levantamentosSelecionados.removeAll(levantamentosInconsistentes);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    @URLAction(mappingId = "novoAprovacaoLevantamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarAtributos();
        inicializarSelecionado();
    }

    @URLAction(mappingId = "verAprovacaoLevantamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarAprovacaoLevantamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "consultaAprovacaoLevantamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void consulta() {
        inicializarConsulta();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/aprovacao-levantamento-bem/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private void inicializarAtributos() {
        pesquisaLevantamento = new PesquisaLevantamento();
        levantamentosEncontrados = new ArrayList<>();
        levantamentosInconsistentes = new ArrayList<>();
        levantamentosSelecionados = new ArrayList<>();
    }

    private void inicializarSelecionado() {
        selecionado.setAprovador(sistemaFacade.getUsuarioCorrente());
        selecionado.setDataAprovacao(sistemaFacade.getDataOperacao());
    }

    private void inicializarConsulta() {
        consultaLevantamento = new ConsultaLevantamento();
        levantamentosEncontrados = new ArrayList<>();
        selecionado = new AprovacaoLevantamentoBem();
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<LevantamentoBemPatrimonial> getLevantamentosSelecionados() {
        return levantamentosSelecionados;
    }

    public void setLevantamentosSelecionados(List<LevantamentoBemPatrimonial> levantamentosSelecionados) {
        this.levantamentosSelecionados = levantamentosSelecionados;
    }

    public List<LevantamentoBemPatrimonial> getLevantamentosEncontrados() {
        return levantamentosEncontrados;
    }

    public void setLevantamentosEncontrados(List<LevantamentoBemPatrimonial> levantamentosEncontrados) {
        this.levantamentosEncontrados = levantamentosEncontrados;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String formatarValor(BigDecimal valor) {
        return Util.formataValor(valor);
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<LevantamentoBemPatrimonial> getLevantamentosInconsistentes() {
        return levantamentosInconsistentes;
    }

    public void setLevantamentosInconsistentes(List<LevantamentoBemPatrimonial> levantamentosInconsistentes) {
        this.levantamentosInconsistentes = levantamentosInconsistentes;
    }

    public void pesquisar() {
        pesquisaLevantamento.setUnidadeOrganizacional(hierarquiaOrganizacional != null ? hierarquiaOrganizacional.getSubordinada() : null);
        try {
            levantamentosEncontrados = pesquisaLevantamento.pesquisar(aprovacaoLevantamentoBemFacade);
            if (levantamentosEncontrados != null && levantamentosEncontrados.isEmpty()) {
                FacesUtil.addAtencao("Nenhum levantamento encontrado para aprovação.");
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    public void pesquisarConsulta() {
        consultaLevantamento.setUnidadeOrganizacional(hierarquiaOrganizacional != null ? hierarquiaOrganizacional.getSubordinada() : null);
        try {
            levantamentosObjectConsulta = consultaLevantamento.pesquisar(aprovacaoLevantamentoBemFacade);
            if (levantamentosObjectConsulta.isEmpty()) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Nenhum registro encontrado.");
            } else {
                total = BigDecimal.ZERO;
                for (Object obj : levantamentosObjectConsulta) {
                    Object[] aux = (Object[]) obj;
                    total = total.add((BigDecimal) aux[10]);
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            levantamentosObjectConsulta = new ArrayList<>();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
            levantamentosObjectConsulta = new ArrayList<>();
        }
    }

    public boolean mostrarBotaoSelecionarTodos() {
        return levantamentosSelecionados.size() != levantamentosEncontrados.size();
    }

    public void selecionarTodos() {
        desmarcarTodos();
        levantamentosSelecionados.addAll(levantamentosEncontrados);
    }

    public void desmarcarTodos() {
        levantamentosSelecionados.clear();
    }

    public boolean mostrarBotaoSelecionarLevantamento(LevantamentoBemPatrimonial lev) {
        return !levantamentosSelecionados.contains(lev);
    }

    public void desmarcarLevantamento(LevantamentoBemPatrimonial lev) {
        levantamentosSelecionados.remove(lev);
    }

    public void selecionarLevantamento(LevantamentoBemPatrimonial lev) {
        levantamentosSelecionados.add(lev);
    }

    public ConsultaLevantamento getConsultaLevantamento() {
        return consultaLevantamento;
    }

    public List<Object> getLevantamentosObjectConsulta() {
        return levantamentosObjectConsulta;
    }

    public void setLevantamentosObjectConsulta(List<Object> levantamentosObjectConsulta) {
        this.levantamentosObjectConsulta = levantamentosObjectConsulta;
    }

    public void setConsultaLevantamento(ConsultaLevantamento consultaLevantamento) {
        this.consultaLevantamento = consultaLevantamento;
    }

    public class PesquisaLevantamento {
        private UnidadeOrganizacional unidadeOrganizacional;

        public List<LevantamentoBemPatrimonial> pesquisar(AprovacaoLevantamentoBemFacade aprovacaoBemFacade) throws ExcecaoNegocioGenerica {
            return aprovacaoBemFacade.recuperarLevantamentosNaoAprovados(hierarquiaOrganizacional,
                LevantamentoBemPatrimonial.tiposDeAquisicaoQueRequeremAprovacaoDoLevantamento, Boolean.FALSE);
        }

        public UnidadeOrganizacional getUnidadeOrganizacional() {
            return unidadeOrganizacional;
        }

        public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
            this.unidadeOrganizacional = unidadeOrganizacional;
        }
    }

    public void estornarLevantamento(ItemAprovacaoLevantamento item) {
        if (aprovacaoLevantamentoBemFacade.podeEstornar(item)) {
            item.setSituacao(SituacaoEventoBem.ESTORNADO);
            aprovacaoLevantamentoBemFacade.salvar(selecionado);

            if (selecionado.getListaItemAprovacaoLevantamentos().isEmpty()) {
                aprovacaoLevantamentoBemFacade.remover(selecionado);
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "Aprovação de levantamento estornada com sucesso!");
                redireciona();
            } else {
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "Levantamento estornado com sucesso!");
            }
        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "O levantamento já está efetivado e não pode ser estornado!");
        }
    }

    public void estornarTodos() {
        if (aprovacaoLevantamentoBemFacade.podeEstornarTodos(selecionado.getListaItemAprovacaoLevantamentos())) {
            for (ItemAprovacaoLevantamento item : selecionado.getListaItemAprovacaoLevantamentos()) {
                item.setSituacao(SituacaoEventoBem.ESTORNADO);
            }
            selecionado.setSituacao(SituacaoEventoBem.ESTORNADO);
            aprovacaoLevantamentoBemFacade.salvar(selecionado);
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "Aprovação de Levantamento estornada com sucesso!");
            redireciona();
        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Existem levantamentos que já foram efetivados e não poderão ser estornados.");
        }
    }

    public class ConsultaLevantamento {
        private UnidadeOrganizacional unidadeOrganizacional;
        private AprovacaoLevantamentoBemFacade.TipoFiltroConsultaLevantamento tipoFiltroConsultaLevantamento;

        public List<Object> pesquisar(AprovacaoLevantamentoBemFacade aprovacaoBemFacade) throws ExcecaoNegocioGenerica {
            return aprovacaoBemFacade.recuperarLevantamentosConsulta(
                unidadeOrganizacional,
                LevantamentoBemPatrimonial.tiposDeAquisicaoQueRequeremAprovacaoDoLevantamento,
                tipoFiltroConsultaLevantamento);
        }

        public UnidadeOrganizacional getUnidadeOrganizacional() {
            return unidadeOrganizacional;
        }

        public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
            this.unidadeOrganizacional = unidadeOrganizacional;
        }

        public AprovacaoLevantamentoBemFacade.TipoFiltroConsultaLevantamento getTipoFiltroConsultaLevantamento() {
            return tipoFiltroConsultaLevantamento;
        }

        public void setTipoFiltroConsultaLevantamento(AprovacaoLevantamentoBemFacade.TipoFiltroConsultaLevantamento tipoFiltroConsultaLevantamento) {
            this.tipoFiltroConsultaLevantamento = tipoFiltroConsultaLevantamento;
        }

        public boolean isTipoFiltroInconsistente() {
            return AprovacaoLevantamentoBemFacade.TipoFiltroConsultaLevantamento.INCONSISTENTE.equals(tipoFiltroConsultaLevantamento);
        }
    }

    public List<SelectItem> filtros() {
        return Util.getListSelectItem(Arrays.asList(AprovacaoLevantamentoBemFacade.TipoFiltroConsultaLevantamento.values()));
    }

    public List<SelectItem> retornaHierarquiaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (HierarquiaOrganizacional obj : hierarquiaOrganizacionalFacade.retornaHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(sistemaFacade.getUsuarioCorrente(), sistemaFacade.getDataOperacao())) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

}
