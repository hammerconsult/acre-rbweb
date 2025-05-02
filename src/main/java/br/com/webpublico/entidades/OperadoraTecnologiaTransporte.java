package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoOTT;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.ott.OperadoraTecnologiaTransporteDTO;
import br.com.webpublico.util.Util;
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

@Table(name = "OPERADORATRANSPORTE")
@Entity
@GrupoDiagrama(nome = "RBTrans")
@Audited
@Etiqueta("Operadora de Tecnologia de Transporte (OTT)")
public class OperadoraTecnologiaTransporte extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Nome")
    private String nome;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("CNPJ")
    private String cnpj;
    @Etiqueta("Inscrição Estadual")
    private String inscricaoEstadual;
    @Etiqueta("Inscrição Municipal")
    private String inscricaoMunicipal;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    private SituacaoOTT situacao;
    @Etiqueta("Endereço comercial")
    private String enderecoComercial;
    @Etiqueta("Número endereço")
    private String numeroEndereco;
    @Etiqueta("Complemento")
    private String complemento;
    @Etiqueta("Bairro")
    private String bairro;
    @Etiqueta("Cidade")
    private String cidade;
    @Etiqueta("UF")
    private String uf;
    @Etiqueta("CEP")
    private String cep;
    @Etiqueta("Telefone comercial")
    private String telefoneComercial;
    @Etiqueta("Celular")
    private String celular;
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @Etiqueta("Nome do responsável")
    private String nomeResponsavel;
    @Etiqueta("Cpf do responsável")
    private String cpfResponsavel;
    @Etiqueta("E-mail do responsável")
    private String emailResponsavel;
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "operadoraTecTransporte", orphanRemoval = true)
    private List<OperadoraTransporteDetentorArquivo> detentorArquivoComposicao;
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "operadoraTecTransporte", orphanRemoval = true)
    private List<CertificadoAnualOTT> certificados;
    @ManyToOne
    private PessoaJuridica pessoaJuridica;
    @Etiqueta("Motivo do indeferimento")
    private String motivoIndeferimento;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do indeferimento")
    private Date dataIndeferimento;
    @ManyToOne
    @Etiqueta("Usuário do indeferimento")
    private UsuarioSistema usuarioIndeferimento;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "operadoraTecTransporte", orphanRemoval = true)
    private List<HistoricoSituacaoOTT> historicoSituacoesOtt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "operadora", orphanRemoval = true)
    private List<RenovacaoOperadoraOTT> renovacaoOperadoraOTTS;

    public OperadoraTecnologiaTransporte() {
        this.dataCadastro = new Date();
        this.detentorArquivoComposicao = Lists.newArrayList();
        this.certificados = Lists.newArrayList();
        this.historicoSituacoesOtt = Lists.newArrayList();
        this.renovacaoOperadoraOTTS = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<OperadoraTransporteDetentorArquivo> getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(List<OperadoraTransporteDetentorArquivo> detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }

    public void setInscricaoMunicipal(String inscricaoMunicipal) {
        this.inscricaoMunicipal = inscricaoMunicipal;
    }

    public SituacaoOTT getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoOTT situacao) {
        this.situacao = situacao;
    }

    public String getEnderecoComercial() {
        return enderecoComercial;
    }

    public void setEnderecoComercial(String enderecoComercial) {
        this.enderecoComercial = enderecoComercial;
    }

    public String getNumeroEndereco() {
        return numeroEndereco;
    }

    public void setNumeroEndereco(String numeroEndereco) {
        this.numeroEndereco = numeroEndereco;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getTelefoneComercial() {
        return telefoneComercial;
    }

    public void setTelefoneComercial(String telefoneComercial) {
        this.telefoneComercial = telefoneComercial;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public String getCpfResponsavel() {
        return cpfResponsavel;
    }

    public void setCpfResponsavel(String cpfResponsavel) {
        this.cpfResponsavel = cpfResponsavel;
    }

    public String getEmailResponsavel() {
        return emailResponsavel;
    }

    public void setEmailResponsavel(String emailResponsavel) {
        this.emailResponsavel = emailResponsavel;
    }

    public List<CertificadoAnualOTT> getCertificados() {
        return certificados;
    }

    public void setCertificados(List<CertificadoAnualOTT> certificados) {
        this.certificados = certificados;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
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

    public UsuarioSistema getUsuarioIndeferimento() {
        return usuarioIndeferimento;
    }

    public void setUsuarioIndeferimento(UsuarioSistema usuarioIndeferimento) {
        this.usuarioIndeferimento = usuarioIndeferimento;
    }

    public List<HistoricoSituacaoOTT> getHistoricoSituacoesOtt() {
        Collections.sort(historicoSituacoesOtt);
        return historicoSituacoesOtt;
    }

    public void setHistoricoSituacoesOtt(List<HistoricoSituacaoOTT> historicoSituacoesOtt) {
        this.historicoSituacoesOtt = historicoSituacoesOtt;
    }

    @Override
    public String toString() {
        return nome;
    }

    public String toStringAutoComplete() {
        if (!Strings.isNullOrEmpty(nome) || !Strings.isNullOrEmpty(cnpj)) {
            return nome + " - " + cnpj;
        }
        return "";
    }

    public OperadoraTecnologiaTransporteDTO toDTO() {
        OperadoraTecnologiaTransporteDTO dto = new OperadoraTecnologiaTransporteDTO();
        dto.setId(this.id);
        dto.setCnpj(this.cnpj);
        dto.setNome(this.nome);
        dto.setInscricaoEstadual(this.inscricaoEstadual);
        dto.setInscricaoMunicipal(this.inscricaoMunicipal);
        dto.setCep(this.cep);
        dto.setEnderecoComercial(this.enderecoComercial);
        dto.setNumeroEndereco(this.numeroEndereco);
        dto.setCidade(this.cidade);
        dto.setUf(this.uf);
        dto.setBairro(this.bairro);
        dto.setComplemento(this.complemento);
        dto.setNomeResponsavel(this.nomeResponsavel);
        dto.setEmailResponsavel(this.emailResponsavel);
        dto.setCelular(this.celular);
        dto.setTelefoneComercial(this.telefoneComercial);
        dto.setCpfResponsavel(this.cpfResponsavel);
        dto.setSituacaoOttDTO(this.situacao.getSituacaoOttDTO());
        return dto;
    }

    public List<RenovacaoOperadoraOTT> getRenovacaoOperadoraOTTS() {
        return renovacaoOperadoraOTTS;
    }

    public void setRenovacaoOperadoraOTTS(List<RenovacaoOperadoraOTT> renovacaoOperadoraOTTS) {
        this.renovacaoOperadoraOTTS = renovacaoOperadoraOTTS;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Override
    public void realizarValidacoes() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(nome)) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o nome!");
        }
        if (Strings.isNullOrEmpty(cnpj) || !Util.valida_CpfCnpj(cnpj)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O CNPJ informado não é válido!");
        }
        if (Strings.isNullOrEmpty(nomeResponsavel)) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o nome do responsável!");
        }
        if (Strings.isNullOrEmpty(cpfResponsavel) || !Util.valida_CpfCnpj(cpfResponsavel)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O CPF do responsável informado não é válido!");
        }
        if (Strings.isNullOrEmpty(emailResponsavel) || !Util.validarEmail(emailResponsavel)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O email do responsável não é válido!");
        }
        for (OperadoraTransporteDetentorArquivo operadoraTransporteDetentorArquivo : detentorArquivoComposicao) {
            if (operadoraTransporteDetentorArquivo.getDescricaoDocumento() != null
                && operadoraTransporteDetentorArquivo.getObrigatorio()
                && operadoraTransporteDetentorArquivo.getDetentorArquivoComposicao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O documento " +
                    operadoraTransporteDetentorArquivo.getDescricaoDocumento() +
                    " deve ser anexado.");
            }
        }
        ve.lancarException();
    }
}
