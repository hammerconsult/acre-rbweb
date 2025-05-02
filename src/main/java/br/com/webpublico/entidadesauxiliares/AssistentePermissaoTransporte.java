package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ParametrosTransitoTransporte;
import br.com.webpublico.entidades.PermissaoTransporte;
import br.com.webpublico.entidades.Permissionario;
import br.com.webpublico.entidades.VeiculoPermissionario;

import java.io.Serializable;

public class AssistentePermissaoTransporte implements Serializable {
    private VeiculoPermissionario veiculoPermissionarioBaixaInsercao;
    private Permissionario permissionario;
    private PermissaoTransporte selecionado;
    private ParametrosTransitoTransporte parametro;
    private VeiculoPermissionario veiculoPermissionario;

    public VeiculoPermissionario getVeiculoPermissionarioBaixaInsercao() {
        return veiculoPermissionarioBaixaInsercao;
    }

    public void setVeiculoPermissionarioBaixaInsercao(VeiculoPermissionario veiculoPermissionarioBaixaInsercao) {
        this.veiculoPermissionarioBaixaInsercao = veiculoPermissionarioBaixaInsercao;
    }

    public Permissionario getPermissionario() {
        return permissionario;
    }

    public void setPermissionario(Permissionario permissionario) {
        this.permissionario = permissionario;
    }

    public PermissaoTransporte getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(PermissaoTransporte selecionado) {
        this.selecionado = selecionado;
    }

    public ParametrosTransitoTransporte getParametro() {
        return parametro;
    }

    public void setParametro(ParametrosTransitoTransporte parametro) {
        this.parametro = parametro;
    }

    public VeiculoPermissionario getVeiculoPermissionario() {
        return veiculoPermissionario;
    }

    public void setVeiculoPermissionario(VeiculoPermissionario veiculoPermissionario) {
        this.veiculoPermissionario = veiculoPermissionario;
    }
}
