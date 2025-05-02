package br.com.webpublico.perfil;

import br.com.webpublico.entidades.Perfil;
import br.com.webpublico.entidades.Pessoa;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * @author Fabio
 */
public class PerfilEspecial extends Perfil {

    public PerfilEspecial() {
        super();
        modificaAtributo("oNome", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iNome", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mNome", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oDataNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iDataNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mDataNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oCPF", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iCPF", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mCPF", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("oSexo", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iSexo", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mSexo", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oTipoDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iTipoDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mTipoDeficiente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oNumeroRg", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iNumeroRg", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mNumeroRg", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oDataEmissaoRg", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iDataEmissaoRg", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mDataEmissaoRg", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oOrgaoEmissorRg", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iOrgaoEmissorRg", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("mOrgaoEmissorRg", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oEstadoRg", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("iEstadoRg", Boolean.TRUE, Boolean.FALSE);
        modificaAtributo("oSituacaoCadastral", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iSituacaoCadastral", Boolean.FALSE, Boolean.FALSE);

        modificaAtributo("pCertidaoNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oNomeCartorioNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iNomeCartorioNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mNomeCartorioNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oLivroNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iLivroNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mLivroNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oFolhaNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iFolhaNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mFolhaNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oRegistroNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iRegistroNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mRegistroNascimento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pCertidaoCasamento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oNomeCartorioCasamento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iNomeCartorioCasamento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mNomeCartorioCasamento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oLivroCasamento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iLivroCasamento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mLivroCasamento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oFolhaCasamento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iFolhaCasamento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mFolhaCasamento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oRegistroCasamento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iRegistroCasamento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mRegistroCasamento", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oNomeConjuge", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iNomeConjuge", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mNomeConjuge", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pEspecialidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oClasseCredor", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iClasseCredor", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mClasseCredor", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oSecretariaRequerente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iSecretariaRequerente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mSecretariaRequerente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pClassificacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oBloqueado", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("iBloqueado", Boolean.TRUE, Boolean.TRUE);
        modificaAtributo("mBloqueado", Boolean.TRUE, Boolean.TRUE);
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
        modificaAtributo("pCarteiraVacinacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oCarteiraVacinacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iCarteiraVacinacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mCarteiraVacinacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oDataInvalidez", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iDataInvalidez", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mDataInvalidez", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pHabilitacao", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pContaCorrente", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pTitulo", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pCarteiraTrabalho", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("pMilitar", Boolean.FALSE, Boolean.FALSE);

        modificaAtributo("oNaturalidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iNaturalidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("bNaturalidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mNaturalidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oNacionalidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iNacionalidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("bNacionalidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mNacionalidade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oAnoChegada", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iAnoChegada", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("sAnoChegada", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mAnoChegada", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oRacaCor", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iRacaCor", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mRacaCor", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oTipoSanguineo", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iTipoSanguineo", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mTipoSanguineo", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oDoador", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iDoador", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mDoador", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oEstadoCivil", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iEstadoCivil", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mEstadoCivil", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("oNivelEscolaridade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("iNivelEscolaridade", Boolean.FALSE, Boolean.FALSE);
        modificaAtributo("mNivelEscolaridade", Boolean.FALSE, Boolean.FALSE);
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
