/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCredencialRBTrans;
import br.com.webpublico.enums.TipoPagamentoCredencialRBTrans;
import br.com.webpublico.enums.TipoRequerenteCredencialRBTrans;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * @author cheles
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Credencial de Tráfego")
public class CredencialTrafego extends CredencialRBTrans {

    @ManyToOne
    @Etiqueta("Veículo")
    private VeiculoPermissionario veiculoPermissionario;

    public CredencialTrafego() {
    }

    public CredencialTrafego(Date dataGeracao, Date dataValidade, TipoCredencialRBTrans tipoCredencialRBTrans, TipoRequerenteCredencialRBTrans tipoRequerente, TipoPagamentoCredencialRBTrans statusPagamento, Boolean foiEmitida, VeiculoPermissionario veiculo, PermissaoTransporte permissaoTransporte) {
        super(dataGeracao, dataValidade, tipoCredencialRBTrans, tipoRequerente, statusPagamento, foiEmitida, permissaoTransporte);
        this.veiculoPermissionario = veiculo;
    }

    @Override
    public String getNomeRequerente() {
        return getVeiculoPermissionario().getPermissaoTransporte().getPermissionarioVigente().getCadastroEconomico().getPessoa().getNome();
    }

    @Override
    public CadastroEconomico getCadastroEconomico() {
        return getVeiculoPermissionario().getPermissaoTransporte().getPermissionarioVigente().getCadastroEconomico();
    }

    public VeiculoPermissionario getVeiculoPermissionario() {
        return veiculoPermissionario;
    }

    public void setVeiculoPermissionario(VeiculoPermissionario veiculoPermissionario) {
        this.veiculoPermissionario = veiculoPermissionario;
    }
}
