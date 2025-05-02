package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoVeiculoOTT;
import br.com.webpublico.enums.VeiculoPoluente;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.ott.VeiculoOttDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Table(name = "VEICULOOTTRANSPORTE")
@Entity
@GrupoDiagrama(nome = "RBTrans")
@Audited
@Etiqueta("Veículo da Operadora de Tecnologia de Transporte (OTT)")
public class VeiculoOperadoraTecnologiaTransporte extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Operadora de Transporte")
    private OperadoraTecnologiaTransporte operadoraTransporte;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Placa")
    private String placaVeiculo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Modelo")
    private String modeloVeiculo;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Marca")
    private Marca marca;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ano de Fabricação")
    private Integer anoFabricacaoVeiculo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Cor")
    @ManyToOne
    private Cor cor;
    @Etiqueta("Registro de Licenciamento")
    private String crlv;
    @Etiqueta("Veículo Adaptado")
    private Boolean veiculoAdaptado;
    @Etiqueta("Veículo Poluente")
    @Enumerated(EnumType.STRING)
    private VeiculoPoluente veiculoPoluente;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    private SituacaoVeiculoOTT situacaoVeiculoOTT;
    @Etiqueta("Data de Cadastro")
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "veiculoOtt", orphanRemoval = true)
    private List<VeiculoOperadoraDetentorArquivo> veiculoOperadoraDetentorArquivos;
    @Etiqueta("Motivo do indeferimento")
    private String motivoIndeferimento;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do indeferimento")
    private Date dataIndeferimento;
    @ManyToOne
    @Etiqueta("Usuário do indeferimento")
    private UsuarioSistema usuarioIndeferimento;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "veiculoOtt", orphanRemoval = true)
    private List<HistoricoSituacaoVeiculoOTT> historicoSituacoesVeiculo;
    @Etiqueta("Motivo do desativamento")
    private String motivoDesativamento;
    private Boolean alugado;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "veiculoOtt", orphanRemoval = true)
    private List<RenovacaoVeiculoOTT> renovacaoVeiculoOTTS;
    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Certificado OTT")
    private CertificadoRenovacaoOTT certificadoInscricao;

    public VeiculoOperadoraTecnologiaTransporte() {
        this.veiculoOperadoraDetentorArquivos = Lists.newArrayList();
        this.historicoSituacoesVeiculo = Lists.newArrayList();
        this.renovacaoVeiculoOTTS = Lists.newArrayList();
        this.alugado = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperadoraTecnologiaTransporte getOperadoraTransporte() {
        return operadoraTransporte;
    }

    public void setOperadoraTransporte(OperadoraTecnologiaTransporte operadoraTransporte) {
        this.operadoraTransporte = operadoraTransporte;
    }

    public SituacaoVeiculoOTT getSituacaoVeiculoOTT() {
        return situacaoVeiculoOTT;
    }

    public void setSituacaoVeiculoOTT(SituacaoVeiculoOTT situacaoVeiculoOTT) {
        this.situacaoVeiculoOTT = situacaoVeiculoOTT;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public String getModeloVeiculo() {
        return modeloVeiculo;
    }

    public void setModeloVeiculo(String modeloVeiculo) {
        this.modeloVeiculo = modeloVeiculo;
    }

    public Integer getAnoFabricacaoVeiculo() {
        return anoFabricacaoVeiculo;
    }

    public void setAnoFabricacaoVeiculo(Integer anoFabricacaoVeiculo) {
        this.anoFabricacaoVeiculo = anoFabricacaoVeiculo;
    }

    public Cor getCor() {
        return cor;
    }

    public void setCor(Cor cor) {
        this.cor = cor;
    }

    public String getCrlv() {
        return crlv;
    }

    public void setCrlv(String crlv) {
        this.crlv = crlv;
    }

    public Boolean getVeiculoAdaptado() {
        return veiculoAdaptado;
    }

    public void setVeiculoAdaptado(Boolean veiculoAdaptado) {
        this.veiculoAdaptado = veiculoAdaptado;
    }

    public VeiculoPoluente getVeiculoPoluente() {
        return veiculoPoluente;
    }

    public void setVeiculoPoluente(VeiculoPoluente veiculoPoluente) {
        this.veiculoPoluente = veiculoPoluente;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public List<VeiculoOperadoraDetentorArquivo> getVeiculoOperadoraDetentorArquivos() {
        return veiculoOperadoraDetentorArquivos;
    }

    public void setVeiculoOperadoraDetentorArquivos(List<VeiculoOperadoraDetentorArquivo> veiculoOperadoraDetentorArquivos) {
        this.veiculoOperadoraDetentorArquivos = veiculoOperadoraDetentorArquivos;
    }

    public String getMotivoIndeferimento() {
        return motivoIndeferimento;
    }

    public void setMotivoIndeferimento(String motivoIndeferimento) {
        this.motivoIndeferimento = motivoIndeferimento;
    }

    public Date getDataIndeferimento() {
        return dataIndeferimento;
    }

    public void setDataIndeferimento(Date dataIndeferimento) {
        this.dataIndeferimento = dataIndeferimento;
    }

    public String getMotivoDesativamento() {
        return motivoDesativamento;
    }

    public void setMotivoDesativamento(String motivoDesativamento) {
        this.motivoDesativamento = motivoDesativamento;
    }

    public UsuarioSistema getUsuarioIndeferimento() {
        return usuarioIndeferimento;
    }

    public void setUsuarioIndeferimento(UsuarioSistema usuarioIndeferimento) {
        this.usuarioIndeferimento = usuarioIndeferimento;
    }

    public List<HistoricoSituacaoVeiculoOTT> getHistoricoSituacoesVeiculo() {
        Collections.sort(historicoSituacoesVeiculo);
        return historicoSituacoesVeiculo;
    }

    public void setHistoricoSituacoesVeiculo(List<HistoricoSituacaoVeiculoOTT> historicoSituacoesVeiculo) {
        this.historicoSituacoesVeiculo = historicoSituacoesVeiculo;
    }

    @Override
    public String toString() {
        return placaVeiculo + (marca != null ? " " + marca.getDescricao() : "") + " " + modeloVeiculo;
    }

    public VeiculoOttDTO toDTO() {
        VeiculoOttDTO dto = new VeiculoOttDTO();
        dto.setId(this.id);
        dto.setOperadoraTransporte(this.operadoraTransporte.toDTO());
        dto.setPlacaVeiculo(this.placaVeiculo != null ? this.placaVeiculo.toUpperCase() : "");
        dto.setModeloVeiculo(this.modeloVeiculo != null ? this.modeloVeiculo.toUpperCase() : "");
        dto.setMarca(this.marca != null ? this.marca.toDTO() : null);
        dto.setAnoFabricacaoVeiculo(this.anoFabricacaoVeiculo);
        dto.setCor(this.cor != null ? this.cor.toDTO() : null);
        dto.setCrlv(this.crlv);
        dto.setVeiculoAdaptado(this.veiculoAdaptado);
        dto.setVeiculoPoluente(this.veiculoPoluente.getVeiculoPoluenteDTO());
        dto.setSituacaoVeiculo(this.situacaoVeiculoOTT.getSituacaoVeiculoDTO());
        dto.setDataCadastro(this.dataCadastro);
        dto.setMotivoIndeferimento(this.motivoIndeferimento);
        dto.setDataIndeferimento(this.dataIndeferimento);
        dto.setUsuarioIndeferimento(this.usuarioIndeferimento != null ? this.usuarioIndeferimento.getNome() : null);
        dto.setMotivoDesativamento(this.motivoDesativamento);
        dto.setAlugado(this.alugado);
        return dto;
    }

    public List<RenovacaoVeiculoOTT> getRenovacaoVeiculoOTTS() {
        if (renovacaoVeiculoOTTS == null) renovacaoVeiculoOTTS = Lists.newArrayList();
        return renovacaoVeiculoOTTS;
    }

    public void setRenovacaoVeiculoOTTS(List<RenovacaoVeiculoOTT> renovacaoVeiculoOTTS) {
        this.renovacaoVeiculoOTTS = renovacaoVeiculoOTTS;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Boolean getAlugado() {
        return alugado != null ? alugado : Boolean.FALSE;
    }

    public void setAlugado(Boolean alugado) {
        this.alugado = alugado;
    }

    public CertificadoRenovacaoOTT getCertificadoInscricao() {
        return certificadoInscricao;
    }

    public void setCertificadoInscricao(CertificadoRenovacaoOTT certificadoInscricao) {
        this.certificadoInscricao = certificadoInscricao;
    }

    @Override
    public void realizarValidacoes() {
        ValidacaoException ve = new ValidacaoException();
        if (operadoraTransporte == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Operadora de Tecnologia de Transporte!");
        }
        if (Strings.isNullOrEmpty(placaVeiculo)) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Placa do Veículo!");
        }
        if (Strings.isNullOrEmpty(modeloVeiculo)) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o modelo do veículo!");
        }
        if (marca == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a marca do veículo!");
        }
        if (anoFabricacaoVeiculo == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o ano de fabricação do veículo!");
        }
        if (cor == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a cor do veículo!");
        }
        if (Strings.isNullOrEmpty(crlv)) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o CRLV (Licenciamento) do veículo!");
        }
        for (VeiculoOperadoraDetentorArquivo veiculoOperadoraDetentorArquivo : veiculoOperadoraDetentorArquivos) {
            if (veiculoOperadoraDetentorArquivo.getObrigatorio()
                && veiculoOperadoraDetentorArquivo.getDetentorArquivoComposicao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O documento " +
                    veiculoOperadoraDetentorArquivo.getDescricaoDocumento() + " deve ser anexado.");
            }
        }
        ve.lancarException();
    }
}
