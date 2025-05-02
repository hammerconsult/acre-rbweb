package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusDocumentoOtt;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.ott.AnexoCredenciamentoOttDTO;
import br.com.webpublico.ott.enums.StatusDocumentoDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Table(name = "OPERADORATRANSPARQUIVO")
@Entity
@Audited
@GrupoDiagrama(nome = "RBTrans")
public class OperadoraTransporteDetentorArquivo extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Operadora de tecnologia de transporte")
    @ManyToOne
    private OperadoraTecnologiaTransporte operadoraTecTransporte;
    @Etiqueta("Detentor de arquivo")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComp;
    @ManyToOne
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    private StatusDocumentoOtt status;
    private String observacao;

    private String descricaoDocumento;
    private String extensoesPermitidas;
    private Boolean obrigatorio;
    private Boolean renovacao;

    public OperadoraTransporteDetentorArquivo() {
        super();
        this.status = StatusDocumentoOtt.AGUARDANDO_AVALIACAO;
        this.obrigatorio = Boolean.FALSE;
    }

    public OperadoraTransporteDetentorArquivo(OperadoraTecnologiaTransporte operadoraTecTransporte) {
        this.operadoraTecTransporte = operadoraTecTransporte;
        this.obrigatorio = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperadoraTecnologiaTransporte getOperadoraTecTransporte() {
        return operadoraTecTransporte;
    }

    public void setOperadoraTecTransporte(OperadoraTecnologiaTransporte operadoraTecTransporte) {
        this.operadoraTecTransporte = operadoraTecTransporte;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComp;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComp = detentorArquivoComposicao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public DetentorArquivoComposicao getDetentorArquivoComp() {
        return detentorArquivoComp;
    }

    public void setDetentorArquivoComp(DetentorArquivoComposicao detentorArquivoComp) {
        this.detentorArquivoComp = detentorArquivoComp;
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

    public void setObservacao(String observacao) {
        this.observacao = observacao;
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

    public void popularDadosDocumentoCredenciamento(AnexoCredenciamentoOttDTO anexoDto) {
        descricaoDocumento = anexoDto.getDescricao();
        extensoesPermitidas = anexoDto.getExtensoesPermitidas();
        obrigatorio = anexoDto.getObrigatorio();
        renovacao = anexoDto.getRenovacao();
    }

    public void popularDadosDocumentoCredenciamento(DocumentoCredenciamentoOtt doc) {
        descricaoDocumento = doc.getDescricao();
        extensoesPermitidas = doc.getExtensoesPermitidas();
        obrigatorio = doc.getObrigatorio();
        renovacao = doc.getRenovacao();
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
