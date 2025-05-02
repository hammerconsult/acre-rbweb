package br.com.webpublico.controle;

import br.com.webpublico.entidades.EfetivacaoMaterial;
import br.com.webpublico.entidades.Material;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.EfetivacaoMaterialVO;
import br.com.webpublico.entidadesauxiliares.FiltroEfetivacaoMaterial;
import br.com.webpublico.enums.StatusMaterial;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EfetivacaoMaterialFacade;
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
import java.util.*;

/**
 * Criado por Mateus
 * Data: 27/01/2017.
 */

@ManagedBean(name = "efetivacaoMaterialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-efetivacao-material", pattern = "/efetivacao-material/novo/", viewId = "/faces/administrativo/materiais/efetivacaomaterial/edita.xhtml"),
    @URLMapping(id = "editar-efetivacao-material", pattern = "/efetivacao-material/editar/#{efetivacaoMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/efetivacaomaterial/edita.xhtml"),
    @URLMapping(id = "ver-efetivacao-material", pattern = "/efetivacao-material/ver/#{efetivacaoMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/efetivacaomaterial/visualizar.xhtml"),
    @URLMapping(id = "listar-efetivacao-material", pattern = "/efetivacao-material/listar/", viewId = "/faces/administrativo/materiais/efetivacaomaterial/lista.xhtml"),
})

public class EfetivacaoMaterialControlador extends PrettyControlador<EfetivacaoMaterial> implements Serializable, CRUD {

    @EJB
    private EfetivacaoMaterialFacade facade;
    private FiltroEfetivacaoMaterial filtro;
    private List<EfetivacaoMaterialVO> efetivacoes;
    private List<Material> materiaisSemelhantes;

    public EfetivacaoMaterialControlador() {
        super(EfetivacaoMaterial.class);
    }

    @Override
    @URLAction(mappingId = "novo-efetivacao-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        filtro = new FiltroEfetivacaoMaterial();
        efetivacoes = new ArrayList<>();
        instanciarMateriaisSemelhantes();

        selecionado.setDataRegistro(new Date());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setUnidadeAdministrativa(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
    }

    @Override
    @URLAction(mappingId = "editar-efetivacao-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "ver-efetivacao-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public void pesquisarMateriais() {
        efetivacoes = new ArrayList<>();
        List<EfetivacaoMaterial> efetivacoesMateriais = facade.buscarMateriaisFiltrando(filtro);
        for (EfetivacaoMaterial efetivacao : efetivacoesMateriais) {
            EfetivacaoMaterialVO novaEfetVO = new EfetivacaoMaterialVO();
            novaEfetVO.setMaterial(efetivacao.getMaterial());
            novaEfetVO.setUsuarioSistema(efetivacao.getMaterial().getUltimaEfetivacao().getUsuarioSistema());
            if (efetivacao.getMaterial().getUltimaEfetivacao().getUnidadeAdministrativa() != null) {
                novaEfetVO.setUnidadeAdministrativa(efetivacao.getMaterial().getUltimaEfetivacao().getUnidadeAdministrativa());
                novaEfetVO.setSiglaAdministrativa(facade.getHierarquiaOrganizacionalFacade().getSiglaHierarquia(TipoHierarquiaOrganizacional.ADMINISTRATIVA, novaEfetVO.getUnidadeAdministrativa()));
            }
            efetivacoes.add(novaEfetVO);
        }
    }

    public List<SelectItem> getStatusMateriais() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(StatusMaterial.DEFERIDO, StatusMaterial.DEFERIDO.getDescricao()));
        toReturn.add(new SelectItem(StatusMaterial.AGUARDANDO, StatusMaterial.AGUARDANDO.getDescricao()));
        toReturn.add(new SelectItem(StatusMaterial.INDEFERIDO, StatusMaterial.INDEFERIDO.getDescricao()));
        return toReturn;
    }

    public void pesquisarMateriaisSemelhantes(Material material) {
        materiaisSemelhantes = facade.getMaterialFacade().buscarMateriaisPorDescricaoAndStatusDeferido(material.getDescricao());
    }

    public void instanciarMateriaisSemelhantes() {
        materiaisSemelhantes = new ArrayList<>();
    }

    public void validarSituacaoEfetivacao(EfetivacaoMaterialVO efetivacaoMaterial) {
        if (efetivacaoMaterial.hasSituacao()
            && efetivacaoMaterial.getSituacao().equals(efetivacaoMaterial.getMaterial().getStatusMaterial())) {
            FacesUtil.addOperacaoNaoPermitida("A situação selecionada não deve ser igual a atual situação do material.");
            efetivacaoMaterial.setSituacao(null);
        }
    }

    public void validarObservacao() {
        ValidacaoException ve = new ValidacaoException();
        for (EfetivacaoMaterialVO efetivacao : efetivacoes) {
            if (efetivacao.hasSituacao() && efetivacao.getSituacao().isIndeferido() && Util.isStringNulaOuVazia(efetivacao.getObservacao())) {
                ve.adicionarMensagemDeCampoObrigatorio("Em caso de indeferimento, a observação deve ser preenchida.");
                ve.lancarException();
            }
        }
        ve.lancarException();
    }

    public void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        boolean hasAlteracao = false;
        for (EfetivacaoMaterialVO efetivacao : efetivacoes) {
            if (efetivacao.getSituacao() != null) {
                hasAlteracao = true;
            }
        }
        if (!hasAlteracao) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para salvar o registro, é necessário efetivar ao menos um material.");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarObservacao();
            validarSalvar();
            facade.salvar(efetivacoes);
            facade.desbloquearEntradaPorCompraComPendenciaEfetivacaoMaterial(efetivacoes);
            FacesUtil.addOperacaoRealizada("Registros salvos com sucesso.");
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public List<EfetivacaoMaterialVO> buscarMateriais() {
        if (!Util.isListNullOrEmpty(efetivacoes)) {
            return efetivacoes;
        }
        return new ArrayList<>();
    }

    public void deferirTodos() {
        for (EfetivacaoMaterialVO efetivacao : efetivacoes) {
            efetivacao.setSituacao(StatusMaterial.DEFERIDO);
        }
    }

    public void indeferirTodos() {
        for (EfetivacaoMaterialVO efetivacao : efetivacoes) {
            efetivacao.setSituacao(StatusMaterial.INDEFERIDO);
        }
    }

    public void limparSituacaoEfetivacao() {
        for (EfetivacaoMaterialVO efetivacao : efetivacoes) {
            efetivacao.setSituacao(null);
            efetivacao.setObservacao(null);
        }
    }

    public void inativarTodos() {
        for (EfetivacaoMaterialVO efetivacao : efetivacoes) {
            efetivacao.setSituacao(StatusMaterial.INATIVO);
        }
    }

    public List<SelectItem> getSituacoesEfetivacaoMaterial() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (StatusMaterial.DEFERIDO.equals(filtro.getStatusMaterial())) {
            toReturn.add(new SelectItem(StatusMaterial.INATIVO, StatusMaterial.INATIVO.getDescricao()));
        } else {
            toReturn.add(new SelectItem(StatusMaterial.DEFERIDO, StatusMaterial.DEFERIDO.getDescricao()));
            toReturn.add(new SelectItem(StatusMaterial.INDEFERIDO, StatusMaterial.INDEFERIDO.getDescricao()));
        }
        return toReturn;
    }

    public void atribuirObservacaoTodos() {
        for (EfetivacaoMaterialVO efetivacao : efetivacoes) {
            if (Util.isStringNulaOuVazia(efetivacao.getObservacao())) {
                efetivacao.setObservacao(filtro.getObservacao());
            }
        }
    }

    public void limparCampoObservacao(EfetivacaoMaterialVO efetivacaoMaterial) {
        if (!efetivacaoMaterial.hasSituacao()) {
            efetivacaoMaterial.setObservacao(null);
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/efetivacao-material/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }


    public List<Material> getMateriaisSemelhantes() {
        ordenarMaterialPorCodigo(materiaisSemelhantes);
        return materiaisSemelhantes;
    }

    public void setMateriaisSemelhantes(List<Material> materiaisSemelhantes) {
        this.materiaisSemelhantes = materiaisSemelhantes;
    }

    public List<EfetivacaoMaterialVO> getEfetivacoes() {
        return efetivacoes;
    }

    public void setEfetivacoes(List<EfetivacaoMaterialVO> efetivacoes) {
        this.efetivacoes = efetivacoes;
    }

    public FiltroEfetivacaoMaterial getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroEfetivacaoMaterial filtro) {
        this.filtro = filtro;
    }

    private void ordenarMaterialPorCodigo(List<Material> materiais) {
        Collections.sort(materiais, new Comparator<Material>() {
            @Override
            public int compare(Material o1, Material o2) {
                Long i1 = o1.getCodigo();
                Long i2 = o2.getCodigo();
                return i2.compareTo(i1);
            }
        });
    }
}
