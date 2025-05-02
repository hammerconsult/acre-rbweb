package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * @author octavio
 */
@GrupoDiagrama(nome = "RBTrans")
@Entity
@Audited
@Etiqueta("Parâmetros da OTT")
public class ParametrosOtt extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Dívida de Vistoria de Veículo da OTT")
    @Tabelavel
    private Divida dividaVistoriaVeiculoOtt;
    @ManyToOne
    @Etiqueta("Tributo de Vistoria do Veículo da OTT")
    @Tabelavel
    private Tributo tributoVistoriaVeiculoOtt;
    @ManyToOne
    @Etiqueta("Certificado Anual de Credenciamento das Empresas – CAC")
    @Tabelavel
    private TipoDoctoOficial certificadoCredenciamento;
    @ManyToOne
    @Etiqueta("Certificado de Autorização – CA")
    @Tabelavel
    private TipoDoctoOficial certificadoAutorizacao;
    @ManyToOne
    @Etiqueta("Certificado de Renovação – CR")
    @Tabelavel
    private TipoDoctoOficial certificadoRenovacao;
    @OneToMany(mappedBy = "parametrosOtt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentoCredenciamentoOtt> documentosCredenciamento;
    @OneToMany(mappedBy = "parametrosOtt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentoCondutorOtt> documentosCondutor;
    @OneToMany(mappedBy = "parametrosOtt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentoVeiculoOtt> documentosVeiculo;
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;
    @OneToMany(mappedBy = "parametrosOtt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParametrosOttCNAE> cnaes;
    @OneToMany(mappedBy = "parametroOtt", cascade = CascadeType.ALL)
    private List<DigitoVencimento> vencimentos;

    public ParametrosOtt() {
        super();
        this.documentosCredenciamento = Lists.newArrayList();
        this.documentosCondutor = Lists.newArrayList();
        this.documentosVeiculo = Lists.newArrayList();
        this.cnaes = Lists.newArrayList();
        this.vencimentos = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Divida getDividaVistoriaVeiculoOtt() {
        return dividaVistoriaVeiculoOtt;
    }

    public void setDividaVistoriaVeiculoOtt(Divida dividaVistoriaVeiculoOtt) {
        this.dividaVistoriaVeiculoOtt = dividaVistoriaVeiculoOtt;
    }

    public Tributo getTributoVistoriaVeiculoOtt() {
        return tributoVistoriaVeiculoOtt;
    }

    public void setTributoVistoriaVeiculoOtt(Tributo tributoVistoriaVeiculoOtt) {
        this.tributoVistoriaVeiculoOtt = tributoVistoriaVeiculoOtt;
    }

    public TipoDoctoOficial getCertificadoCredenciamento() {
        return certificadoCredenciamento;
    }

    public void setCertificadoCredenciamento(TipoDoctoOficial certificadoCredenciamento) {
        this.certificadoCredenciamento = certificadoCredenciamento;
    }

    public TipoDoctoOficial getCertificadoAutorizacao() {
        return certificadoAutorizacao;
    }

    public void setCertificadoAutorizacao(TipoDoctoOficial certificadoAutorizacao) {
        this.certificadoAutorizacao = certificadoAutorizacao;
    }

    public TipoDoctoOficial getCertificadoRenovacao() {
        return certificadoRenovacao;
    }

    public void setCertificadoRenovacao(TipoDoctoOficial certificadoRenovacao) {
        this.certificadoRenovacao = certificadoRenovacao;
    }

    public List<DocumentoCredenciamentoOtt> getDocumentosCredenciamento() {
        return documentosCredenciamento;
    }

    public void setDocumentosCredenciamento(List<DocumentoCredenciamentoOtt> documentosCredenciamento) {
        this.documentosCredenciamento = documentosCredenciamento;
    }

    public List<DocumentoCondutorOtt> getDocumentosCondutor() {
        return documentosCondutor;
    }

    public void setDocumentosCondutor(List<DocumentoCondutorOtt> documentosCondutor) {
        this.documentosCondutor = documentosCondutor;
    }

    public List<DocumentoVeiculoOtt> getDocumentosVeiculo() {
        return documentosVeiculo;
    }

    public void setDocumentosVeiculo(List<DocumentoVeiculoOtt> documentosVeiculo) {
        this.documentosVeiculo = documentosVeiculo;
    }

    public boolean hasDocumentoCredenciamentoOtt(DocumentoCredenciamentoOtt documentoCredenciamentoOtt) {
        if (documentosCredenciamento != null && !documentosCredenciamento.isEmpty()) {
            return documentosCredenciamento.stream().anyMatch(doc ->
                !Util.isEntidadesIguais(doc, documentoCredenciamentoOtt)
                    && Util.isStringIgual(doc.getDescricao(), documentoCredenciamentoOtt.getDescricao()));
        }
        return false;
    }

    public boolean hasDocumentoCondutorOtt(DocumentoCondutorOtt documentoCondutorOtt) {
        if (documentosCondutor != null && !documentosCondutor.isEmpty()) {
            return documentosCondutor.stream().anyMatch(doc ->
                !Util.isEntidadesIguais(doc, documentoCondutorOtt)
                    && Util.isStringIgual(doc.getDescricao(), documentoCondutorOtt.getDescricao()));
        }
        return false;
    }

    public boolean hasDocumentoVeiculoOtt(DocumentoVeiculoOtt documentoVeiculoOtt) {
        if (documentosVeiculo != null && !documentosVeiculo.isEmpty()) {
            return documentosVeiculo.stream().anyMatch(doc ->
                !Util.isEntidadesIguais(doc, documentoVeiculoOtt)
                    && Util.isStringIgual(doc.getDescricao(), documentoVeiculoOtt.getDescricao()));
        }
        return false;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public List<ParametrosOttCNAE> getCnaes() {
        return cnaes;
    }

    public void setCnaes(List<ParametrosOttCNAE> cnaes) {
        this.cnaes = cnaes;
    }

    public List<DigitoVencimento> getVencimentos() {
        return vencimentos;
    }

    public void setVencimentos(List<DigitoVencimento> vencimentos) {
        this.vencimentos = vencimentos;
    }
}
