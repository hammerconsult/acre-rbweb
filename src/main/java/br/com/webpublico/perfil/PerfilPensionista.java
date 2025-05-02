/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.perfil;

import br.com.webpublico.entidades.Perfil;
import br.com.webpublico.entidades.Pessoa;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * @author peixe
 */
public class PerfilPensionista extends Perfil {

    public PerfilPensionista() {
        super();
        modificaAtributo("oNome", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iNome", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mNome", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oCPF", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iCPF", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mCPF", Boolean.TRUE, Boolean.TRUE);

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
        modificaAtributo("oCarteiraVacinacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iCarteiraVacinacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mCarteiraVacinacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oDataInvalidez", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iDataInvalidez", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mDataInvalidez", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("botaoContaCorrenteRH", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("botaoContaCorrente", Boolean.TRUE, Boolean.FALSE);
    }

    @Override
    public Boolean validarPessoa(Pessoa p) throws Exception {
        //Validaçoes especificas do perfil
        boolean controle = true;
        if (super.validarPessoa(p)) {
            if (!super.valida_CpfCnpj(p.getCpf_Cnpj())) {
                controle = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção ! O CPF/CNPJ informado é inválido !", ""));
            }
        } else {
            controle = false;
        }
        return controle;
    }
}
