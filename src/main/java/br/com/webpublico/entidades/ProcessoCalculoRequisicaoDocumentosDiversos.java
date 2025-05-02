/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.RBTransGeradorDeTaxas;
import br.com.webpublico.interfaces.RBTransProcesso;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author cheles
 */
@GrupoDiagrama(nome = "RBTrans")
@Audited
@Entity

@Table(name = "PROCCALCREQDOCSDIVERSOS")
@Etiqueta(value = "Processo de Cálculo de Requisição de Documentos Diversos")
public class ProcessoCalculoRequisicaoDocumentosDiversos extends ProcessoCalculoRBTrans implements RBTransProcesso {

    @ManyToOne
    private RequisicaoDocumentosDiversos requisicaoDocsDiversos;

    public RequisicaoDocumentosDiversos getRequisicaoDocsDiversos() {
        return requisicaoDocsDiversos;
    }

    public void setRequisicaoDocsDiversos(RequisicaoDocumentosDiversos requisicaoDocsDiversos) {
        this.requisicaoDocsDiversos = requisicaoDocsDiversos;
    }

    @Override
    public PermissaoTransporte obterPermissaoTransporte() {
        return requisicaoDocsDiversos.getPermissaoTransporte();
    }

    @Override
    public long obterIdDoGeradorDeTaxas() {
        return requisicaoDocsDiversos.getId();
    }

    @Override
    public void atribuirGeradorDeTaxas(RBTransGeradorDeTaxas entidade) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RBTransGeradorDeTaxas obterGeradorDeTaxas() {
        return requisicaoDocsDiversos;
    }
}
