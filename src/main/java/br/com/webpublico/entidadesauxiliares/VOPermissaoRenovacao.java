package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoPermissaoRBTrans;

import java.util.Date;

/**
 * Created by AndreGustavo on 10/09/2014.
 */
public class VOPermissaoRenovacao {
    private Long idPermissao;
    private Integer numero;
    private TipoPermissaoRBTrans tipoPermissaoRBTrans;
    private String nomePermissionario;
    private Date finalVigencia;


    public VOPermissaoRenovacao() {
        idPermissao = null;
        numero = null;
        tipoPermissaoRBTrans = null;
        nomePermissionario = "";
        finalVigencia = null;
    }

    public Long getIdPermissao() {
        return idPermissao;
    }

    public void setIdPermissao(Long idPermissao) {
        this.idPermissao = idPermissao;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public TipoPermissaoRBTrans getTipoPermissaoRBTrans() {
        return tipoPermissaoRBTrans;
    }

    public void setTipoPermissaoRBTrans(TipoPermissaoRBTrans tipoPermissaoRBTrans) {
        this.tipoPermissaoRBTrans = tipoPermissaoRBTrans;
    }

    public String getNomePermissionario() {
        return nomePermissionario;
    }

    public void setNomePermissionario(String nomePermissionario) {
        this.nomePermissionario = nomePermissionario;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }
}
