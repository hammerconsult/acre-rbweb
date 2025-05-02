package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.AtributoRelatorioGenerico;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.BemFacade;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 02/10/14
 * Time: 09:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractPesquisaPatrimonial extends ComponentePesquisaGenerico {
    protected TipoBem tipoBem;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private BemFacade bemFacade;

    public abstract String getNivelHierarquia();

    public abstract TipoDaConsultaDaHirarquia getTipoHierarquia();

    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();
        SistemaControlador sc = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");

        if (getAtributoUnidadeOrganizacional() != null && getAtributoUnidadeOrganizacional().length > 0) {
            List<Long> ids = getIdsDasUnidadesDaSecretariaOndeUsuarioEhGestorPatrimonio(sc.getUsuarioCorrente(), sc.getDataOperacao());
            condicao += getCondicaoVerificacaoUnidadeOrganizacional(ids);
        }

        if (tipoBem != null) {
            condicao += " and " + getNomeCampoTipoBem() + " = '" + tipoBem.name() + "' ";
        }
        return condicao;
    }

    public List<Long> getIdsDasUnidadesDaSecretariaOndeUsuarioEhGestorPatrimonio(UsuarioSistema usuarioCorrente, Date dataOperacao) {
        List<Long> ids = new ArrayList<>();

        if (TipoDaConsultaDaHirarquia.PAI_E_FILHO.equals(getTipoHierarquia())) {
            for (HierarquiaOrganizacional ho : hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPaiEFilhoOndeUsuarioEhGestorPatrimonio("", getNivelHierarquia(), usuarioCorrente, dataOperacao)) {
                ids.add(ho.getSubordinada().getId());
            }
        }

        if (TipoDaConsultaDaHirarquia.FILHO.equals(getTipoHierarquia())) {
            for (HierarquiaOrganizacional ho : hierarquiaOrganizacionalFacade.buscarHierarquiaOrganizacionalAdministrativaDoUsuario("",
                getNivelHierarquia(), dataOperacao, usuarioCorrente, null, Boolean.TRUE)) {
                ids.add(ho.getSubordinada().getId());
            }
        }
        return ids;
    }

    protected abstract String[] getAtributoUnidadeOrganizacional();

    public String getCondicaoVerificacaoUnidadeOrganizacional(List<Long> ids) {
        StringBuffer toReturn = new StringBuffer();
        if (ids != null && ids.size() > 0) {
            for (String unidadeAdministrativa : getAtributoUnidadeOrganizacional()) {
                String juncao = " and (" + unidadeAdministrativa + " in (";
                Integer count = 0;
                for (Long id : ids) {
                    toReturn.append(juncao);
                    toReturn.append(id);
                    juncao = ", ";
                    count++;
                    if (count == 1000) {
                        juncao = ") or " + unidadeAdministrativa + " in (";
                    }
                }

                if (toReturn.length() > 0) {
                    toReturn.append(")) ");
                }
            }
        } else {
            for (String unidadeAdministrativa : getAtributoUnidadeOrganizacional()) {
                toReturn.append(" and ( ").append(unidadeAdministrativa).append(" is null ").append(") ");
            }
        }
        return toReturn.toString();
    }

    public void tipoBemMovel() {
        tipoBem = TipoBem.MOVEIS;
    }

    public void tipoBemImovel() {
        tipoBem = TipoBem.IMOVEIS;
    }

    public void tipoBemIntangivel() {
        tipoBem = TipoBem.INTANGIVEIS;
    }

    public String getNomeCampoTipoBem() {
        return " obj.tipoBem";
    }

    @Override
    public String preencherCampo(Object objeto, AtributoRelatorioGenerico atributo) {
        return bemFacade.preencherCampo(objeto, atributo);
    }

    public enum TipoDaConsultaDaHirarquia {
        PAI_E_FILHO,
        FILHO;
    }
}
