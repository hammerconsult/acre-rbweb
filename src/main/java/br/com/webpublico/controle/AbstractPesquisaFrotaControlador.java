package br.com.webpublico.controle;

import br.com.webpublico.util.Util;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 30/09/14
 * Time: 10:22
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractPesquisaFrotaControlador extends ComponentePesquisaGenerico {

    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();
        SistemaControlador sc = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");

        condicao += getCondicaoVerificacaoUnidadeOrganizacional();

        return condicao;
    }

    protected abstract String[] getAtributoUnidadeOrganizacional();

    public String getCondicaoVerificacaoUnidadeOrganizacional() {
        StringBuffer toReturn = new StringBuffer();
        String juncao = " ";
        if (getAtributoUnidadeOrganizacional() != null && getAtributoUnidadeOrganizacional().length > 0) {
            toReturn.append(" and ( ");
            for (String unidadeAdministrativa : getAtributoUnidadeOrganizacional()) {
                toReturn.append(juncao).append(unidadeAdministrativa).append(" in (select uuo.unidadeOrganizacional.id from UsuarioUnidadeOrganizacional uuo ");
                toReturn.append(" where uuo.usuarioSistema.id = ").append(getSistemaControlador().getUsuarioCorrente().getId()).append(") ");
                juncao = " or ";
            }
            toReturn.append(") ");
        }
        return toReturn.toString();
    }

}
