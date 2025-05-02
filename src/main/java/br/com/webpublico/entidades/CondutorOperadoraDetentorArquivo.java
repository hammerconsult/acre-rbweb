package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusDocumentoOtt;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.ott.AnexoCondutorOttDTO;
import br.com.webpublico.ott.enums.StatusDocumentoDTO;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Table(name = "CONDUTOROTTARQUIVO")
@Entity
@Audited
@GrupoDiagrama(nome = "RBTrans")
public class CondutorOperadoraDetentorArquivo extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CondutorOperadoraTecnologiaTransporte condutorOtt;
    @ManyToOne
    private Exercicio exercicio;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComp;
    @Enumerated(EnumType.STRING)
    private StatusDocumentoOtt status;
    private String observacao;

    private String descricaoDocumento;
    private String extensoesPermitidas;
    private Boolean servidorPublico;
    private Boolean renovacao;
    private Boolean obrigatorio;

    public CondutorOperadoraDetentorArquivo() {
        super();
        this.status = StatusDocumentoOtt.AGUARDANDO_AVALIACAO;
        this.servidorPublico = Boolean.FALSE;
        this.renovacao = Boolean.FALSE;
        this.obrigatorio = Boolean.FALSE;
    }

    public CondutorOperadoraDetentorArquivo(CondutorOperadoraTecnologiaTransporte condutorOtt) {
        this.condutorOtt = condutorOtt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public CondutorOperadoraTecnologiaTransporte getCondutorOtt() {
        return condutorOtt;
    }

    public void setCondutorOtt(CondutorOperadoraTecnologiaTransporte condutorOtt) {
        this.condutorOtt = condutorOtt;
    }

    public DetentorArquivoComposicao getDetentorArquivoComp() {
        return detentorArquivoComp;
    }

    public void setDetentorArquivoComp(DetentorArquivoComposicao detentorArquivoComp) {
        this.detentorArquivoComp = detentorArquivoComp;
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

    public Boolean getServidorPublico() {
        return servidorPublico;
    }

    public void setServidorPublico(Boolean servidorPublico) {
        this.servidorPublico = servidorPublico;
    }

    public Boolean getRenovacao() {
        return renovacao;
    }

    public void setRenovacao(Boolean renovacao) {
        this.renovacao = renovacao;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public void popularDadosDocumento(AnexoCondutorOttDTO anexoDTO) {
        descricaoDocumento = anexoDTO.getDescricaoDocumento();
        extensoesPermitidas = anexoDTO.getExtensoesPermitidas();
        servidorPublico = anexoDTO.getServidorPublico();
        renovacao = anexoDTO.getRenovacao();
        obrigatorio = anexoDTO.getObrigatorio();
    }

    public void popularDadosDocumento(DocumentoCondutorOtt doc) {
        descricaoDocumento = doc.getDescricao();
        extensoesPermitidas = doc.getExtensoesPermitidas();
        servidorPublico = doc.getServidorPublico();
        renovacao = doc.getRenovacao();
        obrigatorio = doc.getObrigatorio();
    }

    public AnexoCondutorOttDTO toDTO() {
        AnexoCondutorOttDTO anexoDTO = new AnexoCondutorOttDTO();
        anexoDTO.setId(id);
        anexoDTO.setDescricaoDocumento(descricaoDocumento);
        anexoDTO.setExtensoesPermitidas(extensoesPermitidas);
        anexoDTO.setServidorPublico(servidorPublico);
        anexoDTO.setRenovacao(renovacao);
        anexoDTO.setObrigatorio(obrigatorio);
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
