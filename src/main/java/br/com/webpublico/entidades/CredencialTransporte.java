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
@Etiqueta("Credencial de Transporte")
public class CredencialTransporte extends CredencialRBTrans {

    @ManyToOne
    @Etiqueta("Permissão de Transporte")
    private CadastroEconomico cadastroEconomico;

    public CredencialTransporte() {
    }

    public CredencialTransporte(Date dataGeracao,
                                Date dataValidade,
                                TipoCredencialRBTrans tipoCredencialRBTrans,
                                TipoRequerenteCredencialRBTrans tipoRequerente,
                                TipoPagamentoCredencialRBTrans statusPagamento,
                                Boolean foiEmitida,
                                PermissaoTransporte permissao,
                                CadastroEconomico cadastroEconomico) {
        super(dataGeracao, dataValidade, tipoCredencialRBTrans, tipoRequerente, statusPagamento, foiEmitida, permissao);
        this.cadastroEconomico = cadastroEconomico;
    }

    @Override
    public String getNomeRequerente() {
        if (getTipoRequerente().equals(TipoRequerenteCredencialRBTrans.PERMISSIONARIO)) {
            return getCadastroEconomico().getPessoa().getNome();
        }
        return null;
    }


    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }
}
