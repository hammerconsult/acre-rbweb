package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 13/10/14
 * Time: 11:54
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Documento Comprobatório de Incorporação Móvel")
@Table(name = "DOCCOMPROBATORIOINCORPORAC")
public class DocumetoComprobatorioIncorporacaoMovel extends SuperEntidade implements PossuidorArquivo{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Data")
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    private Date dataDocumento;

    @Etiqueta("Numero")
    @Obrigatorio
    private Long numero;

    @Etiqueta("Tipo")
    @Obrigatorio
    @ManyToOne
    private TipoDocumentoFiscal tipoDocumento;

    @Etiqueta("Anexos")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivo;

    @ManyToOne
    private SolicitacaoIncorporacaoMovel solicitacao;


    public DocumetoComprobatorioIncorporacaoMovel() {
        super();
    }

    public DocumetoComprobatorioIncorporacaoMovel(SolicitacaoIncorporacaoMovel solicitacao) {
        super();
        this.solicitacao = solicitacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataDocumento() {
        return dataDocumento;
    }

    public void setDataDocumento(Date dataDocumento) {
        this.dataDocumento = dataDocumento;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public TipoDocumentoFiscal getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumentoFiscal tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public DetentorArquivoComposicao getDetentorArquivo() {
        return detentorArquivo;
    }

    public void setDetentorArquivo(DetentorArquivoComposicao detentorArquivo) {
        this.detentorArquivo = detentorArquivo;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivo;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivo = detentorArquivoComposicao;
    }

    public SolicitacaoIncorporacaoMovel getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoIncorporacaoMovel solicitacao) {
        this.solicitacao = solicitacao;
    }
}
