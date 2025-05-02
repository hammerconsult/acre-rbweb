package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 08/06/15
 * Time: 11:12
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "bemMovelPropriedadeDeTerceiroControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoBemTerceiro", pattern = "/bem-movel-propriedade-terceiro/novo/", viewId = "/faces/administrativo/patrimonio/bemmovelpropriedadeterceiro/edita.xhtml"),
        @URLMapping(id = "editarBemTerceiro", pattern = "/bem-movel-propriedade-terceiro/editar/#{bemMovelPropriedadeDeTerceiroControlador.id}/", viewId = "/faces/administrativo/patrimonio/bemmovelpropriedadeterceiro/edita.xhtml"),
        @URLMapping(id = "listarBemTerceiro", pattern = "/bem-movel-propriedade-terceiro/listar/", viewId = "/faces/administrativo/patrimonio/bemmovelpropriedadeterceiro/lista.xhtml"),
        @URLMapping(id = "verBemTerceiro", pattern = "/bem-movel-propriedade-terceiro/ver/#{bemMovelPropriedadeDeTerceiroControlador.id}/", viewId = "/faces/administrativo/patrimonio/bemmovelpropriedadeterceiro/visualizar.xhtml")
})
public class BemMovelPropriedadeDeTerceiroControlador extends PrettyControlador<BemMovelPropriedadeTerceiro> implements CRUD {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonHierarquiaOrganizacional singletonHO;
    @EJB
    private LoteEfetivacaoLevantamentoBemFacade loteEfetivacaoLevantamentoBemFacade;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade grupoObjetoCompraGrupoBemFacade;
    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private BemMovelPropriedadeTerceiroFacade bemMovelPropriedadeTerceiroFacade;

    private HierarquiaOrganizacional hierarquiaOrganizacionalAdministrativa;
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria;
    private MoneyConverter moneyConverter;
    private OrigemRecursoBem origemRecursoBemSelecionada;
    private GrupoBem grupoBem;

    public BemMovelPropriedadeDeTerceiroControlador() {
        super(BemMovelPropriedadeTerceiro.class);
    }

    @URLAction(mappingId = "novoBemTerceiro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        origemRecursoBemSelecionada = new OrigemRecursoBem();
    }

    @URLAction(mappingId = "verBemTerceiro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarAssociacaoComGrupoBem();
        this.hierarquiaOrganizacionalAdministrativa = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), this.selecionado.getUnidadeAdministrativa(), sistemaFacade.getDataOperacao());
        this.hierarquiaOrganizacionalOrcamentaria = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), this.selecionado.getUnidadeOrcamentaria(), sistemaFacade.getDataOperacao());
    }

    @URLAction(mappingId = "editarBemTerceiro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        origemRecursoBemSelecionada = new OrigemRecursoBem();
        recuperarAssociacaoComGrupoBem();
        this.hierarquiaOrganizacionalAdministrativa = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), this.selecionado.getUnidadeAdministrativa(), sistemaFacade.getDataOperacao());
    }

    @Override
    public void salvar() {
        if (hierarquiaOrganizacionalAdministrativa != null) {
            selecionado.setUnidadeAdministrativa(this.hierarquiaOrganizacionalAdministrativa.getSubordinada());
        }

        if (Util.validaCampos(selecionado)) {
            try {
                selecionado.validarNegocio(sistemaFacade.getDataOperacao());
                bemMovelPropriedadeTerceiroFacade.salvar(selecionado);
                FacesUtil.addOperacaoRealizada("O bem movel foi cadastrado com sucesso.");
                redireciona();
            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
            } catch (Exception e) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    public AbstractFacade getFacede() {
        return this.bemMovelPropriedadeTerceiroFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/bem-movel-propriedade-terceiro/";
    }

    @Override
    public Object getUrlKeyValue() {
        return this.selecionado.getId();
    }

    public void recuperarAssociacaoComGrupoBem() {
        try {
            GrupoObjetoCompraGrupoBem grupoObjetoCompraGrupoBem;
            if (selecionado.getItem() != null) {
                grupoObjetoCompraGrupoBem = grupoObjetoCompraGrupoBemFacade.recuperarAssociacaoDoGrupoObjetoCompra(selecionado.getObjetoCompra().getGrupoObjetoCompra(), sistemaFacade.getDataOperacao());
                if (grupoObjetoCompraGrupoBem != null) {
                    selecionado.setGrupoBem(grupoObjetoCompraGrupoBem.getGrupoBem());
                } else {
                    if (selecionado.getItem() != null && selecionado.getGrupoObjetoCompra() != null) {
                        FacesUtil.addAtencao("Não foi possível encontrar uma associação de Grupo Objeto de Compra " + selecionado.getGrupoObjetoCompra().getDescricao() + " com um Grupo Patrimonial. Informe está associação no 'Cadastro de Grupo de Objeto de Compra com Grupos Patrimonial'");
                    }
                    selecionado.setGrupoBem(null);
                }
            }
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalAdministrativa() {
        return hierarquiaOrganizacionalAdministrativa;
    }

    public void setHierarquiaOrganizacionalAdministrativa(HierarquiaOrganizacional hierarquiaOrganizacionalAdministrativa) {
        this.hierarquiaOrganizacionalAdministrativa = hierarquiaOrganizacionalAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrcamentaria() {
        return hierarquiaOrganizacionalOrcamentaria;
    }

    public void setHierarquiaOrganizacionalOrcamentaria(HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria) {
        this.hierarquiaOrganizacionalOrcamentaria = hierarquiaOrganizacionalOrcamentaria;
    }

    public OrigemRecursoBem getOrigemRecursoBemSelecionada() {
        return origemRecursoBemSelecionada;
    }

    public void setOrigemRecursoBemSelecionada(OrigemRecursoBem origemRecursoBemSelecionada) {
        this.origemRecursoBemSelecionada = origemRecursoBemSelecionada;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public List<SelectItem> getListaSelectItemTipoRecursoAquisicaoBem() {
        return Util.getListSelectItem(Arrays.asList(TipoRecursoAquisicaoBem.values()));
    }

    public List<SelectItem> getListaSelectItemTipoDeAquisicaoBem() {
        return Util.getListSelectItem(Arrays.asList(LevantamentoBemPatrimonial.tiposDeAquisicaoPermitidosNoCadastro));
    }

    public List<SelectItem> getItensSelectTodosTipoDeAquisicaoBem() {
        return Util.getListSelectItem(TipoAquisicaoBem.values());
    }

    public List<SelectItem> getListaSelectItemEstadosDeConservacao() {
        return Util.getListSelectItem(Arrays.asList(EstadoConservacaoBem.getValoresPossiveisParaLevantamentoDeBemPatrimonial()));
    }

    public List<SelectItem> getListaSelectItemSituacaoConservacaoBem() {
        try {
            return Util.getListSelectItem(this.selecionado.getEstadoConservacaoBem().getSituacoes());
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }

    public List<SelectItem> getTipoGrupo() {
        try {
            return Util.getListSelectItem(Arrays.asList(TipoGrupo.values()));
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }

    public void adicionarOrigemRecurso() {
        try {
            validarOrigem();
            origemRecursoBemSelecionada.setDetentorOrigemRecurso(selecionado.getDetentorOrigemRecurso());
            selecionado.getDetentorOrigemRecurso().getListaDeOriemRecursoBem().add(origemRecursoBemSelecionada);
            origemRecursoBemSelecionada = new OrigemRecursoBem();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError("Operação não permitida", ex.getMessage());
        }
    }

    private void validarOrigem() {
        if (origemRecursoBemSelecionada.getTipoRecursoAquisicaoBem() == null) {
            throw new ExcecaoNegocioGenerica("Informe o tipo da origem.");
        }

        if (origemRecursoBemSelecionada.getDescricao() == null || origemRecursoBemSelecionada.getDescricao().isEmpty()) {
            throw new ExcecaoNegocioGenerica("Informe a descrição da origem.");
        }

        for (OrigemRecursoBem origem : selecionado.getDetentorOrigemRecurso().getListaDeOriemRecursoBem()) {
            if (origem.getTipoRecursoAquisicaoBem().equals(origemRecursoBemSelecionada.getTipoRecursoAquisicaoBem())) {
                throw new ExcecaoNegocioGenerica("Adicione origens de tipos diferentes.");
            }
        }
    }

    public void removerOrigemRecurso(OrigemRecursoBem origem) {
        selecionado.getDetentorOrigemRecurso().getListaDeOriemRecursoBem().remove(origem);
    }

    public List<SelectItem> retornaHierarquiaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (hierarquiaOrganizacionalAdministrativa != null && hierarquiaOrganizacionalAdministrativa.getSubordinada() != null) {
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : hierarquiaOrganizacionalFacade.retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(hierarquiaOrganizacionalAdministrativa.getSubordinada(), sistemaFacade.getDataOperacao())) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
            return toReturn;
        }

        toReturn.add(new SelectItem(null, ""));
        for (HierarquiaOrganizacional obj : hierarquiaOrganizacionalFacade.retornaHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(sistemaFacade.getUsuarioCorrente(), sistemaFacade.getDataOperacao())) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> retornaUnidadeOrcamentaria() {
        if (hierarquiaOrganizacionalAdministrativa != null && hierarquiaOrganizacionalAdministrativa.getSubordinada() != null) {
            List<SelectItem> toReturn = new ArrayList<>();
            toReturn.add(new SelectItem(null, ""));

            for (HierarquiaOrganizacional obj : hierarquiaOrganizacionalFacade.retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(hierarquiaOrganizacionalAdministrativa.getSubordinada(), sistemaFacade.getDataOperacao())) {
                toReturn.add(new SelectItem(obj.getSubordinada(), obj.toString()));
            }

            return toReturn;
        }
        return null;
    }
}
