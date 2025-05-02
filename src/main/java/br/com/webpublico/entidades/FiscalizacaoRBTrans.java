package br.com.webpublico.entidades;

import br.com.webpublico.enums.EspecieTransporte;
import br.com.webpublico.enums.OrgaoAutuadorRBTrans;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.RBTransGeradorDeTaxas;
import br.com.webpublico.interfaces.RBTransProcesso;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Fiscalização RBTrans")
public class FiscalizacaoRBTrans implements Serializable, RBTransGeradorDeTaxas {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Órgão Autuador")
    @Tabelavel
    private OrgaoAutuadorRBTrans orgaoAutuador;
    @OneToMany(mappedBy = "fiscalizacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SituacaoFiscalizacaoRBTrans> situacoesFiscalizacao;
    @OneToMany(mappedBy = "fiscalizacaoRBTrans", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FiscalizacaoArquivoRBTrans> fiscalizacaoArquivo;
    @Pesquisavel
    @Etiqueta("Autuação Fiscalização")
    @OneToOne
    @Tabelavel
    private AutuacaoFiscalizacaoRBTrans autuacaoFiscalizacao;
    @ManyToOne
    private DocumentoOficial documentoNotificacao;
    private boolean multaPaga;
    @Transient
    private SituacaoFiscalizacaoRBTrans situacaoAtual;
    @Transient
    private PermissaoTransporte permissaoTransporte;
    private Boolean infratorReincidente;
    @Enumerated(EnumType.STRING)
    private EspecieTransporte especieTransporte;

    public FiscalizacaoRBTrans() {
        fiscalizacaoArquivo = new ArrayList<FiscalizacaoArquivoRBTrans>();
        situacoesFiscalizacao = new ArrayList<SituacaoFiscalizacaoRBTrans>();
        orgaoAutuador = OrgaoAutuadorRBTrans.ORGAO_201_390;
        multaPaga = false;
        especieTransporte = EspecieTransporte.TAXI;
    }

    public SituacaoFiscalizacaoRBTrans getSituacaoAtual() {
        return situacaoAtual;
    }

    public void setSituacaoAtual(SituacaoFiscalizacaoRBTrans situacaoAtual) {
        this.situacaoAtual = situacaoAtual;
    }

    public PermissaoTransporte getPermissaoTransporte() {
        return permissaoTransporte;
    }

    public void setPermissaoTransporte(PermissaoTransporte permissaoTransporte) {
        this.permissaoTransporte = permissaoTransporte;
    }

    public Boolean getInfratorReincidente() {
        return infratorReincidente;
    }

    public void setInfratorReincidente(Boolean infratorReincidente) {
        this.infratorReincidente = infratorReincidente;
    }

    public boolean isMultaPaga() {
        return multaPaga;
    }

    public void setMultaPaga(boolean multaPaga) {
        this.multaPaga = multaPaga;
    }

    public DocumentoOficial getDocumentoNotificacao() {
        return documentoNotificacao;
    }

    public void setDocumentoNotificacao(DocumentoOficial documentoNotificacao) {
        this.documentoNotificacao = documentoNotificacao;
    }

    public AutuacaoFiscalizacaoRBTrans getAutuacaoFiscalizacao() {
        return autuacaoFiscalizacao;
    }

    public void setAutuacaoFiscalizacao(AutuacaoFiscalizacaoRBTrans autuacaoFiscalizacao) {
        this.autuacaoFiscalizacao = autuacaoFiscalizacao;
    }

    public List<FiscalizacaoArquivoRBTrans> getFiscalizacaoArquivo() {
        return fiscalizacaoArquivo;
    }

    public void setFiscalizacaoArquivo(List<FiscalizacaoArquivoRBTrans> fiscalizacaoArquivo) {
        this.fiscalizacaoArquivo = fiscalizacaoArquivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrgaoAutuadorRBTrans getOrgaoAutuador() {
        return orgaoAutuador;
    }

    public void setOrgaoAutuador(OrgaoAutuadorRBTrans orgaoAutuador) {
        this.orgaoAutuador = orgaoAutuador;
    }

    public List<SituacaoFiscalizacaoRBTrans> getSituacoesFiscalizacao() {
        return situacoesFiscalizacao;
    }

    public void setSituacoesFiscalizacao(List<SituacaoFiscalizacaoRBTrans> situacoesFiscalizacao) {
        this.situacoesFiscalizacao = situacoesFiscalizacao;
    }

    public EspecieTransporte getEspecieTransporte() {
        return especieTransporte;
    }

    public void setEspecieTransporte(EspecieTransporte especieTransporte) {
        this.especieTransporte = especieTransporte;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FiscalizacaoRBTrans)) {
            return false;
        }
        FiscalizacaoRBTrans other = (FiscalizacaoRBTrans) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return
            "Código: "+ getAutuacaoFiscalizacao().getCodigo().toString() + "Data: "+ getAutuacaoFiscalizacao().getDataAutuacao().toString();
    }

    @Override
    public void executaQuandoPagarGuia() {
        this.multaPaga = true;
    }



    @Override
    public TaxaTransito obterTaxaEquivalente(ParametrosTransitoRBTrans parametrosTransitoRBTrans) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RBTransProcesso obterNovoProcesso() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean temProcessoDeCalculo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void novaListaDeProcessos() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void adicionarNaListaDeProcessos(ProcessoCalculo processo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CalculoRBTrans obterCalculoDaLista() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PermissaoTransporte obterPermissaoTransporte() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
