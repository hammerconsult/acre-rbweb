package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusDocumentoOtt;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.ott.AnexoCredenciamentoOttDTO;
import br.com.webpublico.ott.enums.StatusDocumentoDTO;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Table(name = "OPERADORARENOVACAOOTTARQ")
@Entity
@Audited
public class OperadoraRenovacaoDetentorArquivo extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private OperadoraTecnologiaTransporte operadora;
    @ManyToOne
    private RenovacaoOperadoraOTT renovacaoOperadoraOTT;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComp;
    @Enumerated(EnumType.STRING)
    private StatusDocumentoOtt status;
    private String observacao;

    private String descricaoDocumento;
    private String extensoesPermitidas;
    private Boolean obrigatorio;
    private Boolean renovacao;

    public OperadoraRenovacaoDetentorArquivo() {
        super();
        this.status = StatusDocumentoOtt.AGUARDANDO_AVALIACAO;
        this.obrigatorio = Boolean.FALSE;
        this.renovacao = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComp;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComp = detentorArquivoComposicao;
    }

    public OperadoraTecnologiaTransporte getOperadora() {
        return operadora;
    }

    public void setOperadora(OperadoraTecnologiaTransporte operadora) {
        this.operadora = operadora;
    }

    public DetentorArquivoComposicao getDetentorArquivoComp() {
        return detentorArquivoComp;
    }

    public void setDetentorArquivoComp(DetentorArquivoComposicao detentorArquivoComp) {
        this.detentorArquivoComp = detentorArquivoComp;
    }

    public RenovacaoOperadoraOTT getRenovacaoOperadoraOTT() {
        return renovacaoOperadoraOTT;
    }

    public void setRenovacaoOperadoraOTT(RenovacaoOperadoraOTT renovacaoOperadoraOTT) {
        this.renovacaoOperadoraOTT = renovacaoOperadoraOTT;
    }

    public StatusDocumentoOtt getStatus() {
        return status;
    }

    public void setStatus(StatusDocumentoOtt status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public String getDescricaoDocumento() {
        return descricaoDocumento;
    }

    public void setDescricaoDocumento(String descricaoDocumento) {
        this.descricaoDocumento = descricaoDocumento;
    }

    public String getExtensoesPermitidas() {
        return extensoesPermitidas;
    }

    public void setExtensoesPermitidas(String extensoesPermitidas) {
        this.extensoesPermitidas = extensoesPermitidas;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public Boolean getRenovacao() {
        return renovacao;
    }

    public void setRenovacao(Boolean renovacao) {
        this.renovacao = renovacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void popularDadosDocumento(AnexoCredenciamentoOttDTO anexoDto) {
        descricaoDocumento = anexoDto.getDescricaoDocumento();
        extensoesPermitidas = anexoDto.getExtensoesPermitidas();
        obrigatorio = anexoDto.getObrigatorio();
        if (anexoDto.getStatus() != null) status = StatusDocumentoOtt.valueOf(anexoDto.getStatus().name());
        renovacao = anexoDto.getRenovacao();
    }

    public void popularDadosDocumento(DocumentoCredenciamentoOtt doc) {
        descricaoDocumento = doc.getDescricao();
        extensoesPermitidas = doc.getExtensoesPermitidas();
        obrigatorio = doc.getObrigatorio();
        renovacao = doc.getRenovacao();
    }

    public AnexoCredenciamentoOttDTO toDTO() {
        AnexoCredenciamentoOttDTO anexoDTO = new AnexoCredenciamentoOttDTO();
        anexoDTO.setId(id);
        anexoDTO.setDescricaoDocumento(descricaoDocumento);
        anexoDTO.setExtensoesPermitidas(extensoesPermitidas);
        anexoDTO.setObrigatorio(obrigatorio);
        anexoDTO.setRenovacao(renovacao);
        Arquivo arquivo = detentorArquivoComp.getArquivoComposicao().getArquivo();
        anexoDTO.setNome(arquivo.getNome());
        anexoDTO.setDescricao(arquivo.getDescricao());
        anexoDTO.setMimeType(arquivo.getMimeType());
        anexoDTO.setTamanho(arquivo.getTamanho());
        anexoDTO.setConteudo(arquivo.montarConteudo());
        if (status != null) {
            anexoDTO.setStatus(StatusDocumentoDTO.valueOf(status.name()));
        }
        anexoDTO.setObservacao(observacao);
        return anexoDTO;
    }

    public String buscarExtensoesPermitidas() {
        if (Strings.isNullOrEmpty(extensoesPermitidas)) return "";
        extensoesPermitidas = extensoesPermitidas.replace(".", "");
        extensoesPermitidas = extensoesPermitidas.replace(",", "|");
        extensoesPermitidas = extensoesPermitidas.replace(" ", "");
        return "/(\\.|\\/)(" + extensoesPermitidas + ")$/";
    }

    public String buscarMensagemExtensoesPermitidas() {
        if (Strings.isNullOrEmpty(extensoesPermitidas)) return "";
        return "Extensão invalida, apenas as extenções (" + extensoesPermitidas + ") são permitidas.";
    }
}
