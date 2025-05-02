package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 26/08/15
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractPesquisaLicitacaoCompra extends ComponentePesquisaGenerico {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public abstract String getNivelHierarquia();


    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();
        SistemaControlador sc = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");

        List<Long> ids = getIdsDasUnidadesDaSecretariaOndeUsuarioEhGestorLicitacao(sc.getUsuarioCorrente(), sc.getDataOperacao());

        condicao += getCondicaoVerificacaoUnidadeOrganizacional(ids);

        return condicao;
    }

    public List<Long> getIdsDasUnidadesDaSecretariaOndeUsuarioEhGestorLicitacao(UsuarioSistema usuarioCorrente, Date dataOperacao) {
        List<Long> ids = new ArrayList<>();

        for (HierarquiaOrganizacional ho : hierarquiaOrganizacionalFacade.buscarHierarquiaOrganizacionalAdministrativaPorCodigoOrDescricaoAndNivelAndDataAndUsuarioCorrenteAndGestorLicitacao("", getNivelHierarquia(), dataOperacao, usuarioCorrente)) {
            ids.add(ho.getSubordinada().getId());
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

    @Override
    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
