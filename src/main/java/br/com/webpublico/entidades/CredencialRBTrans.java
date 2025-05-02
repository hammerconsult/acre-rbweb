/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCredencialRBTrans;
import br.com.webpublico.enums.TipoPagamentoCredencialRBTrans;
import br.com.webpublico.enums.TipoRequerenteCredencialRBTrans;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Credencial RBTRANS")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CredencialRBTrans implements Serializable, Comparable<CredencialRBTrans> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Número")
    @Pesquisavel
    private Integer numero;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data da Geração")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataGeracao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Validade")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataValidade;
    @Pesquisavel
    @Etiqueta("Tipo da Credencial")
    @Enumerated(EnumType.STRING)
    private TipoCredencialRBTrans tipoCredencialRBTrans;
    @Pesquisavel
    @Etiqueta("Tipo do Requerente")
    @Enumerated(EnumType.STRING)
    private TipoRequerenteCredencialRBTrans tipoRequerente;
    @Invisivel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoPagamentoCredencialRBTrans statusPagamento;
    @Etiqueta("Permissão")
    @ManyToOne
    private PermissaoTransporte permissaoTransporte;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEmissao;
    @OneToMany(mappedBy = "credencialRBTrans", cascade = CascadeType.ALL)
    private List<CalculoCredencialRBTrans> calculosCredencial;

    public CredencialRBTrans() {
        this.criadoEm = System.nanoTime();
        calculosCredencial = new ArrayList<>();
    }

    public CredencialRBTrans(Date dataGeracao,
                             Date dataValidade,
                             TipoCredencialRBTrans tipoCredencialRBTrans,
                             TipoRequerenteCredencialRBTrans tipoRequerente,
                             TipoPagamentoCredencialRBTrans statusPagamento,
                             Boolean foiEmitida,
                             PermissaoTransporte permissaoTransporte) {
        this.dataGeracao = dataGeracao;
        this.dataValidade = dataValidade;
        this.tipoCredencialRBTrans = tipoCredencialRBTrans;
        this.tipoRequerente = tipoRequerente;
        this.statusPagamento = statusPagamento;
        this.criadoEm = System.nanoTime();
        this.permissaoTransporte = permissaoTransporte;
    }

    public boolean isRequerentePermissionario() {
        return this.getTipoRequerente() == null ? false : this.getTipoRequerente().equals(TipoRequerenteCredencialRBTrans.PERMISSIONARIO);
    }

    public boolean isRequerenteMotorista() {
        return this.getTipoRequerente() == null ? false : this.getTipoRequerente().equals(TipoRequerenteCredencialRBTrans.MOTORISTA);
    }

    public boolean ehCredencialTransporte() {
        return this instanceof CredencialTransporte;
    }

    public boolean ehCredencialTrafego() {
        return this instanceof CredencialTrafego;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public abstract String getNomeRequerente();

    public abstract CadastroEconomico getCadastroEconomico();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public TipoPagamentoCredencialRBTrans getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(TipoPagamentoCredencialRBTrans statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public Date getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(Date dataValidade) {
        this.dataValidade = dataValidade;
    }

    public TipoCredencialRBTrans getTipoCredencialRBTrans() {
        return tipoCredencialRBTrans;
    }

    public void setTipoCredencialRBTrans(TipoCredencialRBTrans tipoCredencialRBTrans) {
        this.tipoCredencialRBTrans = tipoCredencialRBTrans;
    }

    public TipoRequerenteCredencialRBTrans getTipoRequerente() {
        return tipoRequerente;
    }

    public void setTipoRequerente(TipoRequerenteCredencialRBTrans tipoRequerente) {
        this.tipoRequerente = tipoRequerente;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    private String getNumeroComAno() {
        return this.getNumero() + "/" + Util.getAnoDaData(dataGeracao);
    }

    public PermissaoTransporte getPermissaoTransporte() {
        return permissaoTransporte;
    }

    public void setPermissaoTransporte(PermissaoTransporte permissaoTransporte) {
        this.permissaoTransporte = permissaoTransporte;
    }

    public List<CalculoCredencialRBTrans> getCalculosCredencial() {
        return calculosCredencial;
    }

    public void setCalculosCredencial(List<CalculoCredencialRBTrans> calculosCredencial) {
        this.calculosCredencial = calculosCredencial;
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
        return this.getClass().getAnnotation(Etiqueta.class).value() + " nº " + this.getNumeroComAno();
    }

    @Override
    public int compareTo(CredencialRBTrans o) {
        int i = this.getPermissaoTransporte().getNumero().compareTo(o.getPermissaoTransporte().getNumero());
        if (i != 0) {
            return i;
        }
        i = o.getDataGeracao().compareTo(this.getDataGeracao());
        if (i != 0) {
            return i;
        }
        return 0;
    }
}
