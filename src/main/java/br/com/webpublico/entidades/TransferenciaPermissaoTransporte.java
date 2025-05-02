/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusLancamentoTransporte;
import br.com.webpublico.enums.TipoMotivoTransferenciaPermissao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author cheles
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Transferência de Permissão de Transporte")
@Table(name = "TRANSFERENCIAPERMTRANS")
public class TransferenciaPermissaoTransporte extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Invisivel
    @OneToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Permissão de Transporte Original")
    private PermissaoTransporte permissaoAntiga;
    @Invisivel
    @OneToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Permissão de Transporte Originada")
    private CadastroEconomico permissaoNova;
    @Etiqueta("Efetuada em")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    private Date efetuadaEm;
    @Etiqueta("Solicitante")
    @ManyToOne
    private Pessoa solicitante;
    @Etiqueta("Motivo")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    private TipoMotivoTransferenciaPermissao motivo;
    @Etiqueta("Status do Pagamento")
    @Enumerated(EnumType.STRING)
    private StatusLancamentoTransporte statusLancamento;
    @ManyToOne
    private CalculoRBTrans calculoRBTrans;
    @ManyToOne
    private CalculoRBTrans calculoMotoTaxi; // outras taxas
    @Invisivel
    @Transient
    private Long criadoEm;
    @Invisivel
    @Transient
    private Boolean transfereVeiculo;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "transferencia", orphanRemoval = true)
    private CertidaoObitoRBTrans certidaoObitoRBTrans;
    @Invisivel
    @Transient
    private Boolean geraCredencialPermissionario;
    @Invisivel
    @Transient
    private Boolean geraCredencialVeiculo;


    public TransferenciaPermissaoTransporte() {
        this.criadoEm = System.nanoTime();
        transfereVeiculo = true;
        geraCredencialPermissionario = false;
        geraCredencialVeiculo = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getTransfereVeiculo() {
        return transfereVeiculo;
    }

    public void setTransfereVeiculo(Boolean transfereVeiculo) {
        this.transfereVeiculo = transfereVeiculo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public CalculoRBTrans getCalculoRBTrans() {
        return calculoRBTrans;
    }

    public void setCalculoRBTrans(CalculoRBTrans calculoRBTrans) {
        this.calculoRBTrans = calculoRBTrans;
    }

    public Date getEfetuadaEm() {
        return efetuadaEm;
    }

    public void setEfetuadaEm(Date efetuadaEm) {
        this.efetuadaEm = efetuadaEm;
    }

    public TipoMotivoTransferenciaPermissao getMotivo() {
        return motivo;
    }

    public void setMotivo(TipoMotivoTransferenciaPermissao motivo) {
        this.motivo = motivo;
    }

    public PermissaoTransporte getPermissaoAntiga() {
        return permissaoAntiga;
    }

    public void setPermissaoAntiga(PermissaoTransporte permissaoAntiga) {
        this.permissaoAntiga = permissaoAntiga;
    }

    public CadastroEconomico getPermissaoNova() {
        return permissaoNova;
    }

    public void setPermissaoNova(CadastroEconomico permissaoNova) {
        this.permissaoNova = permissaoNova;
    }

    public Pessoa getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Pessoa solicitante) {
        this.solicitante = solicitante;
    }

    public StatusLancamentoTransporte getStatusLancamento() {
        return statusLancamento;
    }

    public void setStatusLancamento(StatusLancamentoTransporte statusPagamentoTransporte) {
        this.statusLancamento = statusPagamentoTransporte;
    }

    public CalculoRBTrans getCalculoMotoTaxi() {
        return calculoMotoTaxi;
    }

    public void setCalculoMotoTaxi(CalculoRBTrans calculoMotoTaxi) {
        this.calculoMotoTaxi = calculoMotoTaxi;
    }


    public CertidaoObitoRBTrans getCertidaoObitoRBTrans() {
        return certidaoObitoRBTrans;
    }

    public void setCertidaoObitoRBTrans(CertidaoObitoRBTrans certidaoObitoRBTrans) {
        this.certidaoObitoRBTrans = certidaoObitoRBTrans;
    }

    public Boolean getGeraCredencialPermissionario() {
        return geraCredencialPermissionario;
    }

    public void setGeraCredencialPermissionario(Boolean geraCredencialPermissionario) {
        this.geraCredencialPermissionario = geraCredencialPermissionario;
    }

    public Boolean getGeraCredencialVeiculo() {
        return geraCredencialVeiculo;
    }

    public void setGeraCredencialVeiculo(Boolean geraCredencialVeiculo) {
        this.geraCredencialVeiculo = geraCredencialVeiculo;
    }

    @Override
    public String toString() {
        return "Transferência da Permissão de Transporte nº " + this.permissaoAntiga.getNumero();
    }
}
