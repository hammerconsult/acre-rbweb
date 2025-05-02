package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoCredencialRBTrans;

import java.io.Serializable;

/**
 * Created by Wellington on 25/04/2016.
 */
public class CredencialSimples implements Serializable {

    private TipoCredencialRBTrans tipoCredencialRBTrans;
    private Long idPermissaoTransporte;
    private Long idCadastroEconomico;
    private Long idVeiculoPermissionario;

    public CredencialSimples() {
    }

    public TipoCredencialRBTrans getTipoCredencialRBTrans() {
        return tipoCredencialRBTrans;
    }

    public void setTipoCredencialRBTrans(TipoCredencialRBTrans tipoCredencialRBTrans) {
        this.tipoCredencialRBTrans = tipoCredencialRBTrans;
    }

    public Long getIdPermissaoTransporte() {
        return idPermissaoTransporte;
    }

    public void setIdPermissaoTransporte(Long idPermissaoTransporte) {
        this.idPermissaoTransporte = idPermissaoTransporte;
    }

    public Long getIdCadastroEconomico() {
        return idCadastroEconomico;
    }

    public void setIdCadastroEconomico(Long idCadastroEconomico) {
        this.idCadastroEconomico = idCadastroEconomico;
    }

    public Long getIdVeiculoPermissionario() {
        return idVeiculoPermissionario;
    }

    public void setIdVeiculoPermissionario(Long idVeiculoPermissionario) {
        this.idVeiculoPermissionario = idVeiculoPermissionario;
    }
}
