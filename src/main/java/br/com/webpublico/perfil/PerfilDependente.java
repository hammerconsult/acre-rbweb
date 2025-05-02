/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.perfil;

import br.com.webpublico.entidades.Perfil;
import br.com.webpublico.entidades.Pessoa;

/**
 * @author andreperes
 */
public class PerfilDependente extends Perfil {

    public PerfilDependente() {
        super();
        modificaAtributo("oNome", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iNome", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mNome", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oSexo", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iSexo", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mSexo", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oTipoDeficiente", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iTipoDeficiente", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mTipoDeficiente", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oSituacaoCadastral", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iSituacaoCadastral", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pEspecialidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pClasseCredor", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oClasseCredor", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iClasseCredor", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mClasseCredor", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oSecretariaRequerente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iSecretariaRequerente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mSecretariaRequerente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pClassificacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oBloqueado", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iBloqueado", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mBloqueado", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oMotivoBloqueio", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iMotivoBloqueio", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mMotivoBloqueio", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oBloqueado", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iBloqueado", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mBloqueado", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oMotivoBloqueio", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iMotivoBloqueio", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mMotivoBloqueio", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pFoto", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oNacionalidadeConjuge", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iNacionalidadeConjuge", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mNacionalidadeConjuge", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("bNacionalidadeConjuge", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oOrgaoExpedidorCarteiraTrabalho", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iOrgaoExpedidorCarteiraTrabalho", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mOrgaoExpedidorCarteiraTrabalho", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oUFConjuge", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iUFConjuge", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mUFConjuge", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oCarteiraVacinacao", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iCarteiraVacinacao", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mCarteiraVacinacao", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oDataInvalidez", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iDataInvalidez", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mDataInvalidez", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("botaoContaCorrenteRH", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("botaoContaCorrente", Boolean.TRUE, Boolean.FALSE);
    }

    @Override
    public Boolean validarPessoa(Pessoa p) throws Exception {
        super.validarPessoa(p);
        return true;
        //Validaçoes especificas do perfil
    }
}
