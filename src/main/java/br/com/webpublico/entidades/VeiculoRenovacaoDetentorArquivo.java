package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusDocumentoOtt;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.ott.AnexoCondutorOttDTO;
import br.com.webpublico.ott.AnexoVeiculoOttDTO;
import br.com.webpublico.ott.enums.StatusDocumentoDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Table(name = "VEICULORENOVACAOOTTARQ")
@Entity
@Audited
public class VeiculoRenovacaoDetentorArquivo extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Operadora de tecnologia de transporte")
    @ManyToOne
    private VeiculoOperadoraTecnologiaTransporte veiculoOtt;
    @ManyToOne
    private RenovacaoVeiculoOTT renovacaoVeiculoOTT;
    @Etiqueta("Detentor de arquivo")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComp;
    @Enumerated(EnumType.STRING)
    private StatusDocumentoOtt status;
    private String observacao;

    private String descricaoDocumento;
    private String extensoesPermitidas;
    private Boolean alugado;
    private Boolean renovacao;
    private Boolean obrigatorio;

    public VeiculoRenovacaoDetentorArquivo() {
        super();
        this.status = StatusDocumentoOtt.AGUARDANDO_AVALIACAO;
        this.alugado = Boolean.FALSE;
        this.renovacao = Boolean.FALSE;
        this.obrigatorio = Boolean.FALSE;
    }

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

    public DetentorArquivoComposicao getDetentorArquivoComp() {
        return detentorArquivoComp;
    }

    public void setDetentorArquivoComp(DetentorArquivoComposicao detentorArquivoComp) {
        this.detentorArquivoComp = detentorArquivoComp;
    }

    public VeiculoOperadoraTecnologiaTransporte getVeiculoOtt() {
        return veiculoOtt;
    }

    public void setVeiculoOtt(VeiculoOperadoraTecnologiaTransporte veiculoOtt) {
        this.veiculoOtt = veiculoOtt;
    }

    public RenovacaoVeiculoOTT getRenovacaoVeiculoOTT() {
        return renovacaoVeiculoOTT;
    }

    public void setRenovacaoVeiculoOTT(RenovacaoVeiculoOTT renovacaoVeiculoOTT) {
        this.renovacaoVeiculoOTT = renovacaoVeiculoOTT;
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

    public Boolean getAlugado() {
        return alugado;
    }

    public void setAlugado(Boolean alugado) {
        this.alugado = alugado;
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

    public void popularDadosDocumento(AnexoVeiculoOttDTO anexoDto) {
        descricaoDocumento = anexoDto.getDescricaoDocumento();
        extensoesPermitidas = anexoDto.getExtensoesPermitidas();
        alugado = anexoDto.getAlugado();
        obrigatorio = anexoDto.getObrigatorio();
        renovacao = anexoDto.getRenovacao();
    }

    public AnexoVeiculoOttDTO toDTO() {
        AnexoVeiculoOttDTO anexoDTO = new AnexoVeiculoOttDTO();
        anexoDTO.setId(id);
        anexoDTO.setDescricaoDocumento(descricaoDocumento);
        anexoDTO.setExtensoesPermitidas(extensoesPermitidas);
        anexoDTO.setAlugado(alugado);
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
