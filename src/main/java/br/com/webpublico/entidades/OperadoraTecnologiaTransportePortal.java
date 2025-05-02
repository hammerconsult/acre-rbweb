package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoOTT;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.ott.OperadoraTecnologiaTransporteDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Table(name = "OPERADORATRANSPORTEPORTAL")
@Entity
@GrupoDiagrama(nome = "RBTrans")
@Audited
@Etiqueta("Operadora de Tecnologia de Transporte Portal")
public class OperadoraTecnologiaTransportePortal extends SuperEntidade{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nome;
    private String cnpj;
    private String inscricaoEstadual;
    private String inscricaoMunicipal;
    private String enderecoComercial;
    private String numeroEndereco;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
    private String telefoneComercial;
    private String celular;
    private String nomeResponsavel;
    private String cpfResponsavel;
    private String emailResponsavel;
    @Enumerated(EnumType.STRING)
    private SituacaoOTT situacaoOTT;
    private Long operadora_id;

    public OperadoraTecnologiaTransportePortal() {
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
        dto.setSituacaoOttDTO(this.situacaoOTT.getSituacaoOttDTO());
        return dto;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public SituacaoOTT getSituacaoOTT() {
        return situacaoOTT;
    }

    public void setSituacaoOTT(SituacaoOTT situacaoOTT) {
        this.situacaoOTT = situacaoOTT;
    }

    public Long getOperadora_id() {
        return operadora_id;
    }

    public void setOperadora_id(Long operadora_id) {
        this.operadora_id = operadora_id;
    }
}
