package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusDocumentoOtt;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.ott.AnexoCondutorOttDTO;
import br.com.webpublico.ott.AnexoCredenciamentoOttDTO;
import br.com.webpublico.ott.enums.StatusDocumentoDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Table(name = "CONDUTORRENOVACAOOTTARQ")
@Entity
@Audited
public class CondutorRenovacaoDetentorArquivo extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private RenovacaoCondutorOTT renovacaoCondutorOTT;
    @Etiqueta("Operadora de tecnologia de transporte")
    @ManyToOne
    private CondutorOperadoraTecnologiaTransporte condutorOtt;
    @Etiqueta("Detentor de arquivo")
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

    public CondutorRenovacaoDetentorArquivo() {
        super();
        this.status = StatusDocumentoOtt.AGUARDANDO_AVALIACAO;
        this.servidorPublico = Boolean.FALSE;
        this.renovacao = Boolean.FALSE;
        this.obrigatorio = Boolean.FALSE;
    }

    public CondutorRenovacaoDetentorArquivo(CondutorOperadoraTecnologiaTransporte condutorOtt) {
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

    public RenovacaoCondutorOTT getRenovacaoCondutorOTT() {
        return renovacaoCondutorOTT;
    }

    public void setRenovacaoCondutorOTT(RenovacaoCondutorOTT renovacaoCondutorOTT) {
        this.renovacaoCondutorOTT = renovacaoCondutorOTT;
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

    public RenovacaoCondutorOTT getRenovacaoOTT() {
        return renovacaoCondutorOTT;
    }

    public void setRenovacaoOTT(RenovacaoCondutorOTT renovacaoCondutorOTT) {
        this.renovacaoCondutorOTT = renovacaoCondutorOTT;
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

    public void popularDadosDocumento(AnexoCondutorOttDTO anexoDto) {
        descricaoDocumento = anexoDto.getDescricaoDocumento();
        extensoesPermitidas = anexoDto.getExtensoesPermitidas();
        servidorPublico = anexoDto.getServidorPublico();
        obrigatorio = anexoDto.getObrigatorio();
        renovacao = anexoDto.getRenovacao();
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
}
