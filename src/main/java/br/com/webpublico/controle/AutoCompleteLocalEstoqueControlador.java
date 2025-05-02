package br.com.webpublico.controle;

import br.com.webpublico.entidades.LocalEstoque;
import br.com.webpublico.entidades.UnidadeDistribuidora;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HardRock on 26/01/2017.
 */
@ManagedBean
@ViewScoped
public class AutoCompleteLocalEstoqueControlador implements Serializable {

    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private UnidadeDistribuidoraFacade unidadeDistribuidoraFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private UnidadeOrganizacional unidadeOrganizacional;
    private TipoEstoque tipoEstoque;
    private LocalEstoque localEstoque;

    public void novo(UnidadeOrganizacional unidadeOrganizacional, TipoEstoque tipoEstoque, LocalEstoque localEstoque) {
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.tipoEstoque = tipoEstoque;
        this.localEstoque = localEstoque;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public List<LocalEstoque> completarLocalEstoque(String parte) {
        try {
            if (unidadeOrganizacional != null) {
                String codigoDescricaoHierarquia = hierarquiaOrganizacionalFacade.buscarCodigoDescricaoHierarquia("ADMINISTRATIVA", unidadeOrganizacional, sistemaFacade.getDataOperacao());
                List<Long> idsUnidadesDistribuidoras = buscarIDSUnidadesDistribuidoras();
                if (idsUnidadesDistribuidoras.isEmpty()) {
                    FacesUtil.addAtencao("Unidades distribuidoras não encontrada para a unidade: <b> " + codigoDescricaoHierarquia + "</b>. Verificar se mesma possui efetivação de unidade requerente.");
                    return Lists.newArrayList();
                }

                List<LocalEstoque> locaisDaUnidade = localEstoqueFacade.buscarLocaisEstoquePorUnidadesDistribuidoras(parte.trim(), idsUnidadesDistribuidoras);
                if (locaisDaUnidade == null || locaisDaUnidade.isEmpty()) {
                    FacesUtil.addAtencao("Local de estoque não encontrado para unidade organizacional: <b> " + codigoDescricaoHierarquia + "</b>.");
                }
                return locaisDaUnidade;
            } else {
                List<LocalEstoque> locaisEstoqueAbertos = localEstoqueFacade.completarLocalEstoqueAbertos(parte.trim());
                if (locaisEstoqueAbertos == null || locaisEstoqueAbertos.isEmpty()) {
                    FacesUtil.addAtencao("Não existe local de estoque aberto cadastrado no sistema.");
                }
                return locaisEstoqueAbertos;
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), ex.getMessage());
            return Lists.newArrayList();
        }
    }

    private List<Long> buscarIDSUnidadesDistribuidoras() {
        List<Long> ids = new ArrayList<>();
        if (unidadeOrganizacional != null) {
            for (UnidadeDistribuidora unidadeDistribuidora : unidadeDistribuidoraFacade.buscarUnidadesDistribuidorasPorUnidadeRequerente(unidadeOrganizacional)) {
                ids.add(unidadeDistribuidora.getUnidadeOrganizacional().getId());
            }
        }
        return ids;
    }

    public void limparCampos() {
    }
}
