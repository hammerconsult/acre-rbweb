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
public class PerfilRH extends Perfil {

    public PerfilRH() {
        super();
        modificaAtributo("oNome", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iNome", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mNome", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oDataNascimento", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iDataNascimento", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mDataNascimento", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oCPF", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iCPF", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mCpf", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oSexo", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iSexo", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mSexo", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oTipoDeficiente", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iTipoDeficiente", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mTipoDeficiente", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oNumeroRg", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iNumeroRg", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mNumeroRg", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oDataEmissaoRg", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iDataEmissaoRg", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mDataEmissaoRg", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oOrgaoEmissorRg", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iOrgaoEmissorRg", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mOrgaoEmissorRg", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oEstadoRg", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iEstadoRg", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oSituacaoCadastral", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iSituacaoCadastral", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pCertidaoNascimento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oNomeCartorioNascimento", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iNomeCartorioNascimento", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mNomeCartorioNascimento", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oLivroNascimento", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iLivroNascimento", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mLivroNascimento", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oFolhaNascimento", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iFolhaNascimento", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mFolhaNascimento", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oRegistroNascimento", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iRegistroNascimento", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mRegistroNascimento", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("pCertidaoCasamento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oNomeCartorioCasamento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iNomeCartorioCasamento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mNomeCartorioCasamento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oLivroCasamento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iLivroCasamento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mLivroCasamento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oFolhaCasamento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iFolhaCasamento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mFolhaCasamento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oRegistroCasamento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iRegistroCasamento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mRegistroCasamento", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oNomeConjuge", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iNomeConjuge", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mNomeConjuge", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oNacionalidadeConjuge", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iNacionalidadeConjuge", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mNacionalidadeConjuge", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("bNacionalidadeConjuge", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oUFConjuge", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iUFConjuge", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mUFConjuge", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oOrgaoExpedidorCarteiraTrabalho", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iOrgaoExpedidorCarteiraTrabalho", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mOrgaoExpedidorCarteiraTrabalho", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("pEspecialidade", Boolean.TRUE, Boolean.FALSE);
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
        modificaAtributo("oSecretariaRequerente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iSecretariaRequerente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mSecretariaRequerente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oAnoChegada", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iAnoChegada", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mAnoChegada", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oCarteiraVacinacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iCarteiraVacinacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mCarteiraVacinacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oDataInvalidez", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iDataInvalidez", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mDataInvalidez", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("botaoContaCorrenteRH", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("botaoContaCorrente", Boolean.FALSE, Boolean.FALSE);
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
