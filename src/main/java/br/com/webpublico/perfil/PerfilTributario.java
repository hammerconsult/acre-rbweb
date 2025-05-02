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
 * @author jaimaum
 */
public class PerfilTributario extends Perfil {

    public PerfilTributario() {
        super();
        modificaAtributo("oSituacaoCadastral", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iSituacaoCadastral", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mSituacaoCadastral", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oNome", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iNome", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mNome", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oRazaoSocial", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iRazaoSocial", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mRazaoSocial", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oNomeReduzido", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iNomeReduzido", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mNomeReduzido", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oCPFRH", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iCPFRH", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mCPFRH", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oDataNascimento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iDataNascimento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mDataNascimento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oPai", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iPai", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mPai", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oMae", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iMae", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mMae", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oRacaCor", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iRacaCor", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mRacaCor", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oTipoDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iTipoDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mTipoDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oTipoSanguineo", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iTipoSanguineo", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mTipoSanguineo", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oDoador", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iDoador", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mDoador", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oEstadoCivil", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iEstadoCivil", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mEstadoCivil", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oNivelEscolaridade", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iNivelEscolaridade", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mNivelEscolaridade", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oNaturalidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iNaturalidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mNaturalidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("bNaturalidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oNacionalidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iNacionalidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mNacionalidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("bNacionalidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pTitulo", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pCarteiraTrabalho", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pMilitar", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pCertidaoCasamento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pCertidaoNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pEspecialidade", Boolean.FALSE, Boolean.FALSE);

        modificaAtributo("oAnoChegada", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iAnoChegada", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mAnoChegada", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("sAnoChegada", Boolean.FALSE, Boolean.FALSE);

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
        modificaAtributo("pFoto", Boolean.TRUE, Boolean.FALSE);
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
        modificaAtributo("oClassePessoa", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iClassePessoa", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mClassePessoa", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("pContaCorrente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oCarteiraVacinacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iCarteiraVacinacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mCarteiraVacinacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pCarteiraVacinacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oDataInvalidez", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iDataInvalidez", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mDataInvalidez", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("botaoContaCorrenteRH", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("botaoContaCorrente", Boolean.TRUE, Boolean.FALSE);
    }

    @Override
    public Boolean validarPessoa(Pessoa p) throws Exception {
        //super.validarPessoa(p);
        boolean constrole = true;
        if (p.getEmail() != null && !p.getEmail().trim().isEmpty() && !p.getEmail().contains("@")) {
            constrole = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "e-mail inválido", "e-mail inválido"));

        }
        if (!super.valida_CpfCnpj(p.getCpf_Cnpj())) {
            constrole = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "CPF/CNPJ inválido", "CPF/CNPJ inválido"));
        }
        if (!p.getHomePage().trim().isEmpty() && !p.getHomePage().toLowerCase().contains("www.")) {
            constrole = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Home Page inválida", "Home Page inválida"));
        }
        if (p.getNome().length() < 3) {
            constrole = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nome/Razão Social menor que 3 caracteres", "Nome/Razão Social menor que 3 caracteres"));
        }
        return constrole;
        //Validaçoes especificas do perfil
    }
}
