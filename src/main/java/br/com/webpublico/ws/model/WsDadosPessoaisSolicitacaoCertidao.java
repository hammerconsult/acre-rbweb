package br.com.webpublico.ws.model;

import br.com.webpublico.enums.TipoDocumentoOficialPortal;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public class WsDadosPessoaisSolicitacaoCertidao implements Serializable {
    private Long id;
    private String cpfCnpj;
    private String nomeRazaoSocial;
    private String inscricaoCadastral;
    private TipoDocumentoOficialPortal tipoDocumentoOficialPortal;

    public WsDadosPessoaisSolicitacaoCertidao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public TipoDocumentoOficialPortal getTipoDocumentoOficialPortal() {
        return tipoDocumentoOficialPortal;
    }

    public void setTipoDocumentoOficialPortal(TipoDocumentoOficialPortal tipoDocumentoOficialPortal) {
        this.tipoDocumentoOficialPortal = tipoDocumentoOficialPortal;
    }

    public boolean isPessoaFisica() {
        return !StringUtils.isBlank(cpfCnpj) && Util.valida_CpfCnpj(cpfCnpj) && StringUtil.removeCaracteresEspeciaisSemEspaco(cpfCnpj).length() == 11;
    }

    public boolean isPessoaJuridica() {
        return !StringUtils.isBlank(cpfCnpj) && Util.valida_CpfCnpj(cpfCnpj) && StringUtil.removeCaracteresEspeciaisSemEspaco(cpfCnpj).length() == 14;
    }

    public boolean isCadastro() {
        return !StringUtils.isBlank(inscricaoCadastral);
    }

}
