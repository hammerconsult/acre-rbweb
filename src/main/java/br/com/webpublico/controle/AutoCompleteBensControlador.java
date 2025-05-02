package br.com.webpublico.controle;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.VODadosBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.BemFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by HardRock on 20/02/2017.
 */
@ManagedBean
@ViewScoped
public class AutoCompleteBensControlador implements Serializable {

    @EJB
    private BemFacade bemFacade;
    private UnidadeOrganizacional unidadeOrganizacional;
    private GrupoBem grupoBem;
    private VODadosBem voDadosBem;
    private String tipoBem;
    private String estadoDoBemDiferente;

    public AutoCompleteBensControlador() {
    }

    public void novo(UnidadeOrganizacional unidadeOrganizacional, GrupoBem grupoBem, String tipoBem, String estadoDoBemDiferente) {
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.grupoBem = grupoBem;
        this.tipoBem = tipoBem;
        this.estadoDoBemDiferente = estadoDoBemDiferente;
    }

    public List<Bem> completarBens(String parte) {
        List<Bem> lista = Lists.newArrayList();
        try {
            lista = bemFacade.buscarBensPorAdministrativaAndGrupoAndTipoNotAntEstadoBem(
                parte.trim(),
                unidadeOrganizacional,
                grupoBem,
                tipoBem,
                estadoDoBemDiferente);
            verificarSeExisteBensParaUnidade(lista);

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        return lista;
    }

    private void verificarSeExisteBensParaUnidade(List<Bem> lista) {
        ValidacaoException ve = new ValidacaoException();
        if (unidadeOrganizacional != null && lista.isEmpty()) {
            String descricaoHierarquia = bemFacade.getHierarquiaOrganizacionalFacade().buscarCodigoDescricaoHierarquia(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                unidadeOrganizacional,
                bemFacade.getSistemaFacade().getDataOperacao()
            );
            ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhum bem encontrado para unidade administrativa <b>" + descricaoHierarquia + " </b>");
        } else if (lista.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhum bem encontrado");
        }
        ve.lancarException();
    }

    public void recuperarDadosBem(Bem bem) {
        VODadosBem voDadosBem = bemFacade.recuperarDadosBem(bem);
        if (voDadosBem != null) {
            this.voDadosBem = voDadosBem;
        }
    }

    public void itemSelect() {
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public VODadosBem getVoDadosBem() {
        return voDadosBem;
    }

    public void setVoDadosBem(VODadosBem voDadosBem) {
        this.voDadosBem = voDadosBem;
    }
}
