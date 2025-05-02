package br.com.webpublico.controle;

import br.com.webpublico.entidades.AcaoPPA;
import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.DuplicacaoProjetoAtividade;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DuplicacaoProjetoAtividadeFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "duplicacaoProjetoAtividadeControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-duplicacao-projeto-atividade", pattern = "/duplicacao-projeto-atividade/novo/", viewId = "/faces/financeiro/ppa/duplicacao-projeto-atividade/edita.xhtml"),
    @URLMapping(id = "ver-duplicacao-projeto-atividade", pattern = "/duplicacao-projeto-atividade/ver/#{duplicacaoProjetoAtividadeControlador.id}/", viewId = "/faces/financeiro/ppa/duplicacao-projeto-atividade/visualizar.xhtml"),
    @URLMapping(id = "listar-duplicacao-projeto-atividade", pattern = "/duplicacao-projeto-atividade/listar/", viewId = "/faces/financeiro/ppa/duplicacao-projeto-atividade/lista.xhtml")
})
public class DuplicacaoProjetoAtividadeControlador extends PrettyControlador<DuplicacaoProjetoAtividade> implements Serializable, CRUD {

    @EJB
    private DuplicacaoProjetoAtividadeFacade facade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public DuplicacaoProjetoAtividadeControlador() {
        super(DuplicacaoProjetoAtividade.class);
    }

    @URLAction(mappingId = "novo-duplicacao-projeto-atividade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setData(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        hierarquiaOrganizacional = null;
    }

    @URLAction(mappingId = "ver-duplicacao-projeto-atividade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        hierarquiaOrganizacional = facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(facade.getSistemaFacade().getDataOperacao(), selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public void atualizarDescricao() {
        if (selecionado.getProjetoAtividade() != null) {
            selecionado.setNovaDescricao(selecionado.getProjetoAtividade().getDescricao());
        }
    }

    @Override
    public void salvar() {
        try {
            if (hierarquiaOrganizacional != null && hierarquiaOrganizacional.getSubordinada() != null) {
                selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
            } else {
                selecionado.setUnidadeOrganizacional(null);
            }
            Util.validarCampos(selecionado);
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public List<AtoLegal> completarAtosLegais(String filtro) {
        return facade.getAtoLegalFacade().buscarAtosLegais(filtro);
    }

    public List<AcaoPPA> completarProjetosAtividade(String filtro) {
        return facade.getProjetoAtividadeFacade().buscarAcoesPPAPorExercicio(filtro, facade.getSistemaFacade().getExercicioCorrente());
    }

    public List<HierarquiaOrganizacional> completarUnidades(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.toLowerCase(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), facade.getSistemaFacade().getDataOperacao());
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/duplicacao-projeto-atividade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
