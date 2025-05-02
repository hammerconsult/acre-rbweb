/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusPagamentoBaixa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.RBTransGeradorDeTaxas;
import br.com.webpublico.interfaces.RBTransProcesso;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author cheles
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Segunda Via de Credencial RBTRANS")
public class SegundaViaCredencial implements Serializable, RBTransGeradorDeTaxas {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PermissaoTransporte permissaoTransporte;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataLancamento;
    /*
     * No sistema antigo do rbtrans, salvava-se somente o nome do usuário que
     * fez determinada alteração. Devido os usuário não terem sido migrados até
     * o momento, neste atributo será armazenado o nome dos mesmos. Ou seja,
     * este atributo deve conter o nome do usuário responsável por ter lançado
     * esta taxa.
     */
    private String usuarioLancamento;
    @Invisivel
    @Transient
    private Long criadoEm;
    @OneToMany(mappedBy = "segundaViaCredencial")
    private List<ProcessoCalculoSegundaViaCredencial> listaDeProcessoCalculoSegundaViaCredencial;
    @Etiqueta("Status Pagamento")
    @Enumerated(EnumType.STRING)
    private StatusPagamentoBaixa statusPagamento;

    public SegundaViaCredencial() {
        this.criadoEm = System.nanoTime();
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuarioLancamento() {
        return usuarioLancamento;
    }

    public void setUsuarioLancamento(String usuarioLancamento) {
        this.usuarioLancamento = usuarioLancamento;
    }

    public PermissaoTransporte getPermissaoTransporte() {
        return permissaoTransporte;
    }

    public void setPermissaoTransporte(PermissaoTransporte permissaoTransporte) {
        this.permissaoTransporte = permissaoTransporte;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<ProcessoCalculoSegundaViaCredencial> getListaDeProcessoCalculoSegundaViaCredencial() {
        return listaDeProcessoCalculoSegundaViaCredencial;
    }

    public void setListaDeProcessoCalculoSegundaViaCredencial(List<ProcessoCalculoSegundaViaCredencial> listaDeProcessoCalculoSegundaViaCredencial) {
        this.listaDeProcessoCalculoSegundaViaCredencial = listaDeProcessoCalculoSegundaViaCredencial;
    }

    public StatusPagamentoBaixa getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamentoBaixa statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return this.getClass().getAnnotation(Etiqueta.class).value() + " id = " + this.getId();
    }

    @Override
    public TaxaTransito obterTaxaEquivalente(ParametrosTransitoRBTrans parametrosTransitoRBTrans) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RBTransProcesso obterNovoProcesso() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean temProcessoDeCalculo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void novaListaDeProcessos() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void adicionarNaListaDeProcessos(ProcessoCalculo processo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CalculoRBTrans obterCalculoDaLista() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PermissaoTransporte obterPermissaoTransporte() {
        return getPermissaoTransporte();
    }

    @Override
    public void executaQuandoPagarGuia() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
