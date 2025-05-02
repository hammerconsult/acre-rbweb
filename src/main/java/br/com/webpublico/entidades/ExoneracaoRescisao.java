/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.cadastrofuncional.AvisoPrevio;
import br.com.webpublico.enums.rh.TipoAvisoPrevio;
import br.com.webpublico.enums.rh.esocial.TipoOperacaoESocial;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.IHistoricoEsocial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Exoneração / Rescisão do Servidor")
public class ExoneracaoRescisao implements Serializable, IHistoricoEsocial {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Servidor")
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private VinculoFP vinculoFP;
    @Etiqueta("Data da Rescisão")
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataRescisao;
    @Etiqueta("Aviso Prévio")
    @ManyToOne
    private AvisoPrevio avisoPrevio;
    @Etiqueta("Motivo da Exoneração/Rescisão")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private MotivoExoneracaoRescisao motivoExoneracaoRescisao;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Aviso Prévio")
    @Tabelavel
    @Pesquisavel
    private TipoAvisoPrevio tipoAvisoPrevio;
    @Etiqueta("Ato Legal")
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private AtoLegal atoLegal;
    @Etiqueta("Provimento FP")
    @ManyToOne
    private ProvimentoFP provimentoFP;
    @ManyToOne
    @Etiqueta("Movimento SEFIP")
    private MovimentoSEFIP movimentoSEFIP;
    @Etiqueta("Recolher FGTS em GRRF")
    private Boolean recolherFGTSGRRF;
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data Aviso Prévio")
    private Date dataAvisoPrevio;
    @Etiqueta("Ato Legal Alteração")
    @Tabelavel
    @ManyToOne
    private AtoLegal atoLegalAlteracao;
    private String numeroCertidaoObito;
    @Etiqueta("Observação")
    private String observacao;
    @Transient
    private List<EventoESocialDTO> eventosEsocial;

    @Enumerated
    @Transient
    private TipoOperacaoESocial tipoOperacaoESocial;

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getDataAvisoPrevio() {
        return dataAvisoPrevio;
    }

    public void setDataAvisoPrevio(Date dataAvisoPrevio) {
        this.dataAvisoPrevio = dataAvisoPrevio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public Date getDataRescisao() {
        return dataRescisao;
    }

    public void setDataRescisao(Date dataRescisao) {
        this.dataRescisao = dataRescisao;
    }

    public MotivoExoneracaoRescisao getMotivoExoneracaoRescisao() {
        return motivoExoneracaoRescisao;
    }

    public void setMotivoExoneracaoRescisao(MotivoExoneracaoRescisao motivoExoneracaoRescisao) {
        this.motivoExoneracaoRescisao = motivoExoneracaoRescisao;
    }

    public String getNumeroCertidaoObito() {
        return numeroCertidaoObito;
    }

    public void setNumeroCertidaoObito(String numeroCertidaoObito) {
        this.numeroCertidaoObito = numeroCertidaoObito;
    }

    public TipoAvisoPrevio getTipoAvisoPrevio() {
        return tipoAvisoPrevio;
    }

    public void setTipoAvisoPrevio(TipoAvisoPrevio tipoAvisoPrevio) {
        this.tipoAvisoPrevio = tipoAvisoPrevio;
    }

    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    public MovimentoSEFIP getMovimentoSEFIP() {
        return movimentoSEFIP;
    }

    public void setMovimentoSEFIP(MovimentoSEFIP movimentoSEFIP) {
        this.movimentoSEFIP = movimentoSEFIP;
    }

    public Boolean getRecolherFGTSGRRF() {
        return recolherFGTSGRRF;
    }

    public void setRecolherFGTSGRRF(Boolean recolherFGTSGRRF) {
        this.recolherFGTSGRRF = recolherFGTSGRRF;
    }

    public AtoLegal getAtoLegalAlteracao() {
        return atoLegalAlteracao;
    }

    public void setAtoLegalAlteracao(AtoLegal atoLegalAlteracao) {
        this.atoLegalAlteracao = atoLegalAlteracao;
    }

    public AvisoPrevio getAvisoPrevio() {
        return avisoPrevio;
    }

    public void setAvisoPrevio(AvisoPrevio avisoPrevio) {
        this.avisoPrevio = avisoPrevio;
    }

    public List<EventoESocialDTO> getEventosEsocial() {
        return eventosEsocial;
    }

    public void setEventosEsocial(List<EventoESocialDTO> eventosEsocial) {
        this.eventosEsocial = eventosEsocial;
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
        if (!(object instanceof ExoneracaoRescisao)) {
            return false;
        }
        ExoneracaoRescisao other = (ExoneracaoRescisao) object;
        if ((id == null && other.id != null) || (id != null && !id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s = vinculoFP + " - ";

        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
        s += sf.format(dataRescisao);

        return s;
    }

    public TipoOperacaoESocial getTipoOperacaoESocial() {
        return tipoOperacaoESocial;
    }

    public void setTipoOperacaoESocial(TipoOperacaoESocial tipoOperacaoESocial) {
        this.tipoOperacaoESocial = tipoOperacaoESocial;
    }

    @Override
    public String getDescricaoCompleta() {
        return this.vinculoFP.toString();
    }

    @Override
    public String getIdentificador() {
        return this.id.toString();
    }
}
