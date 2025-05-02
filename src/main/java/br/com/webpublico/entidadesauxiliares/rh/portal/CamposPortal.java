package br.com.webpublico.entidadesauxiliares.rh.portal;


import br.com.webpublico.pessoa.enumeration.CamposPessoaDTO;
import br.com.webpublico.pessoa.enumeration.TipoPessoaPortal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by peixe on 18/10/17.
 */
public class CamposPortal implements Serializable {

    private Long idPessoa;
    private Long idDependente;
    private TipoPessoaPortal tipoPessoaPortal;
    private Map<CamposPessoaDTO, String> campos;


    public CamposPortal() {
        campos = new HashMap<>();
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public Map<CamposPessoaDTO, String> getCampos() {
        return campos;
    }

    public void setCampos(Map<CamposPessoaDTO, String> campos) {
        this.campos = campos;
    }

    public Long getIdDependente() {
        return idDependente;
    }

    public void setIdDependente(Long idDependente) {
        this.idDependente = idDependente;
    }

    public TipoPessoaPortal getTipoPessoaPortal() {
        return tipoPessoaPortal;
    }

    public void setTipoPessoaPortal(TipoPessoaPortal tipoPessoaPortal) {
        this.tipoPessoaPortal = tipoPessoaPortal;
    }
}
