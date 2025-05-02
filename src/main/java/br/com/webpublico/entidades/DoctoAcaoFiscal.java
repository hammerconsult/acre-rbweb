/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoDoctoAcaoFiscal;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.DocumentoFiscalEmail;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @author Claudio
 */
@GrupoDiagrama(nome = "Fiscalizacao")
@Entity

@Audited
public class DoctoAcaoFiscal extends SuperEntidade implements Serializable, DocumentoFiscalEmail {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Ação Fiscal")
    private AcaoFiscal acaoFiscal;
    @ManyToOne(cascade = CascadeType.ALL)
    private DocumentoOficial documentoOficial;
    @Enumerated(EnumType.STRING)
    private TipoDoctoAcaoFiscal tipoDoctoAcaoFiscal;
    private boolean ativo;
    @Transient
    private Long criadoEm;

    public DoctoAcaoFiscal() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AcaoFiscal getAcaoFiscal() {
        return acaoFiscal;
    }

    public void setAcaoFiscal(AcaoFiscal acaoFiscal) {
        this.acaoFiscal = acaoFiscal;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public TipoDoctoAcaoFiscal getTipoDoctoAcaoFiscal() {
        return tipoDoctoAcaoFiscal;
    }

    public void setTipoDoctoAcaoFiscal(TipoDoctoAcaoFiscal tipoDoctoAcaoFiscal) {
        this.tipoDoctoAcaoFiscal = tipoDoctoAcaoFiscal;
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
        return tipoDoctoAcaoFiscal.getDescricao();
    }

    @Override
    public String getNome() {
        return this.tipoDoctoAcaoFiscal.getDescricao();
    }

    @Override
    public DoctoAcaoFiscal getDocumento() {
        return this;
    }

    @Override
    public String getIdentificacao() {
        switch (tipoDoctoAcaoFiscal) {
            case FISCALIZACAO:
            case FINALIZACAO:
            case HOMOLOGACAO:
                return acaoFiscal.getNumeroTermoFiscalizacao().toString();
            case ORDEMSERVICO:
                return acaoFiscal.getOrdemServico().toString();
            case RELATORIO_FISCAL:
                return acaoFiscal.getProgramacaoFiscal().getNumero().toString();
        }
        return this.documentoOficial.getNumero().toString();
    }

    @Override
    public TipoDoctoAcaoFiscal getTipoDocumento() {
        return this.getTipoDoctoAcaoFiscal();
    }
}
