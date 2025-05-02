package br.com.webpublico.entidades;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import br.com.webpublico.enums.EquipamentoCondutor;
import br.com.webpublico.enums.Sexo;
import br.com.webpublico.enums.SituacaoCondutorOTT;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.ott.CondutorOttDTO;
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

@Table(name = "CONDUTOROPERATRANSPORTE")
@Entity
@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Condutor da Operadora de Tecnologia de Transporte (OTT)")
public class CondutorOperadoraTecnologiaTransporte extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Operadora de Transporte")
    @ManyToOne
    private OperadoraTecnologiaTransporte operadoraTecTransporte;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Nome do Condutor")
    private String nomeCondutor;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("CPF")
    private String cpf;
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoCondutorOTT situacaoCondutorOTT;
    @Etiqueta("Equipamento do Condutor")
    @Enumerated(EnumType.STRING)
    private EquipamentoCondutor equipamentoCondutor;
    @Etiqueta("Gênero")
    @Enumerated(EnumType.STRING)
    private Sexo genero;
    @Etiqueta("RG")
    private String rg;
    @Etiqueta("CNH - Carteira Nacional de Habilitação")
    private String cnh;
    @Etiqueta("Endereço")
    private String enderecoLogradouro;
    @Etiqueta("Numero endereço")
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
    @Etiqueta("Telefone Comercial")
    private String telefoneComercial;
    @Etiqueta("Celular")
    private String celular;
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "condutorOperaTransporte", orphanRemoval = true)
    private List<CondutorOperadoraVeiculoOperadora> condutorOperadoraVeiculoOperadoras;
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "condutorOperaTransporte", orphanRemoval = true)
    private List<CertificadoCondutorOTT> certificados;
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "condutorOtt", orphanRemoval = true)
    private List<CondutorOperadoraDetentorArquivo> condutorOperadoraDetentorArquivos;
    @Etiqueta("Motivo do indeferimento")
    private String motivoIndeferimento;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do indeferimento")
    private Date dataIndeferimento;
    @ManyToOne
    @Etiqueta("Usuário do indeferimento")
    private UsuarioSistema usuarioIndeferimento;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "condutorOtt", orphanRemoval = true)
    private List<HistoricoSituacaoCondutorOTT> historicoSituacoesCondutor;
    @Etiqueta("Motivo do desativamento")
    private String motivoDesativamento;
    private Boolean servidorPublico;
    @OneToOne(cascade = CascadeType.ALL)
    private Arquivo foto;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "condutorOtt", orphanRemoval = true)
    private List<RenovacaoCondutorOTT> renovacaoCondutorOTTS;

    public CondutorOperadoraTecnologiaTransporte() {
        this.condutorOperadoraVeiculoOperadoras = Lists.newArrayList();
        this.certificados = Lists.newArrayList();
        this.condutorOperadoraDetentorArquivos = Lists.newArrayList();
        this.historicoSituacoesCondutor = Lists.newArrayList();
        this.renovacaoCondutorOTTS = Lists.newArrayList();
        this.servidorPublico = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CondutorOperadoraVeiculoOperadora> getCondutorOperadoraVeiculoOperadoras() {
        if (condutorOperadoraVeiculoOperadoras == null) condutorOperadoraVeiculoOperadoras = Lists.newArrayList();
        return condutorOperadoraVeiculoOperadoras;
    }

    public void setCondutorOperadoraVeiculoOperadoras(List<CondutorOperadoraVeiculoOperadora> condutorOperadoraVeiculoOperadoras) {
        this.condutorOperadoraVeiculoOperadoras = condutorOperadoraVeiculoOperadoras;
    }

    public OperadoraTecnologiaTransporte getOperadoraTecTransporte() {

        return operadoraTecTransporte;
    }

    public void setOperadoraTecTransporte(OperadoraTecnologiaTransporte operadoraTecTransporte) {
        this.operadoraTecTransporte = operadoraTecTransporte;
    }

    public String getMotivoDesativamento() {
        return motivoDesativamento;
    }

    public void setMotivoDesativamento(String motivoDesativamento) {
        this.motivoDesativamento = motivoDesativamento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNomeCondutor() {
        return nomeCondutor;
    }

    public void setNomeCondutor(String nomeCondutor) {
        this.nomeCondutor = nomeCondutor;
    }

    public SituacaoCondutorOTT getSituacaoCondutorOTT() {
        return situacaoCondutorOTT;
    }

    public void setSituacaoCondutorOTT(SituacaoCondutorOTT situacaoCondutorOTT) {
        this.situacaoCondutorOTT = situacaoCondutorOTT;
    }

    public EquipamentoCondutor getEquipamentoCondutor() {
        return equipamentoCondutor;
    }

    public void setEquipamentoCondutor(EquipamentoCondutor equipamentoCondutor) {
        this.equipamentoCondutor = equipamentoCondutor;
    }

    public Sexo getGenero() {
        return genero;
    }

    public void setGenero(Sexo genero) {
        this.genero = genero;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getCnh() {
        return cnh;
    }

    public void setCnh(String cnh) {
        this.cnh = cnh;
    }

    public String getEnderecoLogradouro() {
        return enderecoLogradouro;
    }

    public void setEnderecoLogradouro(String enderecoLogradouro) {
        this.enderecoLogradouro = enderecoLogradouro;
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

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public List<CertificadoCondutorOTT> getCertificados() {
        if (certificados == null) certificados = Lists.newArrayList();
        return certificados;
    }

    public void setCertificados(List<CertificadoCondutorOTT> certificados) {
        this.certificados = certificados;
    }

    public List<CondutorOperadoraDetentorArquivo> getCondutorOperadoraDetentorArquivos() {
        return condutorOperadoraDetentorArquivos;
    }

    public void setCondutorOperadoraDetentorArquivos(List<CondutorOperadoraDetentorArquivo> condutorOperadoraDetentorArquivos) {
        this.condutorOperadoraDetentorArquivos = condutorOperadoraDetentorArquivos;
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

    public List<HistoricoSituacaoCondutorOTT> getHistoricoSituacoesCondutor() {
        Collections.sort(historicoSituacoesCondutor);
        return historicoSituacoesCondutor;
    }

    public void setHistoricoSituacoesCondutor(List<HistoricoSituacaoCondutorOTT> historicoSituacoesCondutor) {
        this.historicoSituacoesCondutor = historicoSituacoesCondutor;
    }

    @Override
    public String toString() {
        return nomeCondutor + " - " + cpf;
    }

    public CondutorOttDTO toDTO() {
        CondutorOttDTO dto = new CondutorOttDTO();
        dto.setId(this.id);
        dto.setOperadoraTransporte(this.operadoraTecTransporte.toDTO());
        dto.setCpf(this.cpf);
        dto.setNomeCondutor(this.nomeCondutor.toUpperCase());
        dto.setRg(this.rg);
        dto.setCnh(this.cnh);
        dto.setGenero(this.genero.getSexoDTO());
        dto.setCep(this.cep);
        dto.setEnderecoLogradouro(this.enderecoLogradouro);
        dto.setNumeroEndereco(this.numeroEndereco);
        dto.setBairro(this.bairro);
        dto.setCidade(this.cidade);
        dto.setUf(this.uf);
        dto.setComplemento(this.complemento);
        dto.setCelular(this.celular);
        dto.setTelefoneComercial(this.telefoneComercial);
        dto.setEquipamentoCondutor(this.equipamentoCondutor != null ? this.equipamentoCondutor.getEquipamentoCondutorDTO() : null);
        dto.setDataCadastro(this.dataCadastro);
        dto.setSituacaoCondutor(this.situacaoCondutorOTT.getSituacaoCondutorDTO());
        dto.setMotivoIndeferimento(this.motivoIndeferimento);
        dto.setDataIndeferimento(this.dataIndeferimento);
        dto.setUsuarioIndeferimento(this.usuarioIndeferimento != null ? this.usuarioIndeferimento.getNome() : null);
        dto.setMotivoDesativamento(this.motivoDesativamento);
        dto.setServidorPublico(this.servidorPublico);
        if (this.foto != null) {
            ArquivoDTO arquivoDTO = this.foto.toArquivoDTO();
            br.com.webpublico.ott.ArquivoDTO arquivoDTOOtt = new br.com.webpublico.ott.ArquivoDTO();
            arquivoDTOOtt.setId(arquivoDTO.getId());
            arquivoDTOOtt.setNome(arquivoDTO.getNome());
            arquivoDTOOtt.setDescricao(arquivoDTO.getDescricao());
            arquivoDTOOtt.setMimeType(arquivoDTO.getMimeType());
            arquivoDTOOtt.setTamanho(arquivoDTO.getTamanho());
            arquivoDTOOtt.setConteudo(arquivoDTO.getConteudo());
            dto.setFoto(arquivoDTOOtt);
        }
        if (this.condutorOperadoraVeiculoOperadoras != null) {
            for (CondutorOperadoraVeiculoOperadora condutorOperadoraVeiculoOperadora : this.condutorOperadoraVeiculoOperadoras) {
                dto.getVeiculos().add(condutorOperadoraVeiculoOperadora.getVeiculoOTTransporte().toDTO());
            }
        }

        return dto;
    }

    public List<RenovacaoCondutorOTT> getRenovacaoCondutorOTTS() {
        return renovacaoCondutorOTTS;
    }

    public void setRenovacaoCondutorOTTS(List<RenovacaoCondutorOTT> renovacaoCondutorOTTS) {
        this.renovacaoCondutorOTTS = renovacaoCondutorOTTS;
    }

    public Boolean getServidorPublico() {
        return servidorPublico;
    }

    public void setServidorPublico(Boolean servidorPublico) {
        this.servidorPublico = servidorPublico;
    }

    public Arquivo getFoto() {
        return foto;
    }

    public void setFoto(Arquivo foto) {
        this.foto = foto;
    }

    @Override
    public void realizarValidacoes() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(nomeCondutor)) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o nome do Condutor!");
        }
        if (Strings.isNullOrEmpty(cpf)) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o CPF do Condutor!");
        } else if (!Util.valida_CpfCnpj(cpf)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O CPF digitado é inválido!");
        }
        if (Strings.isNullOrEmpty(cnh)) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a CNH do Condutor!");
        }
        for (CondutorOperadoraDetentorArquivo condutorOperadoraDetentorArquivo : condutorOperadoraDetentorArquivos) {
            if (condutorOperadoraDetentorArquivo.getObrigatorio()
                && condutorOperadoraDetentorArquivo.getDetentorArquivoComposicao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O documento " +
                    condutorOperadoraDetentorArquivo.getDescricaoDocumento() + " deve ser anexado.");
            }
        }
        ve.lancarException();
    }
}
