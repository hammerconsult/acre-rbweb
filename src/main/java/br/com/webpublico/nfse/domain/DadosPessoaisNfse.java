package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.RegimeTributario;
import br.com.webpublico.enums.TipoIssqn;
import br.com.webpublico.nfse.domain.dtos.DadosPessoaisNfseDTO;
import br.com.webpublico.nfse.domain.dtos.MunicipioNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.domain.dtos.PaisNfseDTO;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;


/**
 * A DadosPessoais.
 */
@Entity
@Audited
public class

DadosPessoaisNfse extends SuperEntidade implements Cloneable, NfseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta(value = "CPF/CNPJ")
    @Pesquisavel
    private String cpfCnpj;
    @Tabelavel
    @Etiqueta(value = "Inscrição Municipal")
    @Pesquisavel
    private String inscricaoMunicipal;
    @Tabelavel
    @Etiqueta(value = "Inscrição Estadual")
    @Pesquisavel
    private String inscricaoEstadualRg;
    @Obrigatorio
    @Tabelavel
    @Etiqueta(value = "Nome/Razão Social")
    @Pesquisavel
    private String nomeRazaoSocial;
    @Tabelavel
    @Etiqueta(value = "Nome Fantasia")
    @Pesquisavel
    private String nomeFantasia;
    private String apelido;
    @Obrigatorio
    @Etiqueta(value = "E-Mail")
    @Tabelavel
    @Pesquisavel
    private String email;
    private String telefone;
    private String celular;
    @Obrigatorio
    @Etiqueta(value = "CEP")
    private String cep;
    private String numero;
    @Obrigatorio
    @Etiqueta(value = "Logradouro")
    private String logradouro;
    @Obrigatorio
    @Etiqueta(value = "Bairro")
    private String bairro;
    private String complemento;
    private String codigoMunicipio;
    private String municipio;
    private String codigoPais;
    private String pais;
    private String numeroIdentificacao;
    private String uf;
    @Enumerated(EnumType.STRING)
    private RegimeTributario regimeTributario;
    @Enumerated(EnumType.STRING)
    private TipoIssqn tipoIssqn;
    @Temporal(TemporalType.DATE)
    private Date dataNascimento;


    public DadosPessoaisNfse() {
    }

    public DadosPessoaisNfse(DadosPessoaisNfseDTO dto) {
        this.setCpfCnpj(dto.getCpfCnpj());
        this.setInscricaoEstadualRg(dto.getInscricaoEstadualRg());
        this.setNomeRazaoSocial(dto.getNomeRazaoSocial());
        this.setEmail(dto.getEmail());
        this.setApelido(dto.getApelido());
        this.setTelefone(dto.getTelefone());
        this.setCelular(dto.getCelular());
        this.setCep(dto.getCep());
        this.setNumero(dto.getNumero());
        this.setLogradouro(dto.getLogradouro());
        this.setBairro(dto.getBairro());
        this.setComplemento(dto.getComplemento());
        this.setNumeroIdentificacao(dto.getNumeroIdentificacao());
        this.setInscricaoMunicipal(dto.getInscricaoMunicipal());
        if (dto.getMunicipio() != null) {
            this.setCodigoMunicipio(dto.getMunicipio().getCodigo());
            this.setMunicipio(dto.getMunicipio().getNome());
            this.setUf(dto.getMunicipio().getEstado());
        }
        if (dto.getPais() != null) {
            this.setCodigoPais(dto.getPais().getCodigo());
            this.setPais(dto.getPais().getNome());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }

    public void setInscricaoMunicipal(String inscricaoMunicipal) {
        this.inscricaoMunicipal = inscricaoMunicipal;
    }

    public String getInscricaoEstadualRg() {
        return inscricaoEstadualRg;
    }

    public void setInscricaoEstadualRg(String inscricaoEstadualRg) {
        this.inscricaoEstadualRg = inscricaoEstadualRg;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getNumeroIdentificacao() {
        return numeroIdentificacao;
    }

    public void setNumeroIdentificacao(String numeroIdentificacao) {
        this.numeroIdentificacao = numeroIdentificacao;
    }

    @Override
    public String toString() {
        return "(" + cpfCnpj + ") - " + nomeRazaoSocial;
    }

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(String codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public String getCodigoPais() {
        return codigoPais;
    }

    public void setCodigoPais(String codigoPais) {
        this.codigoPais = codigoPais;
    }

    public RegimeTributario getRegimeTributario() {
        return regimeTributario;
    }

    public void setRegimeTributario(RegimeTributario regimeTributario) {
        this.regimeTributario = regimeTributario;
    }

    public TipoIssqn getTipoIssqn() {
        return tipoIssqn;
    }

    public void setTipoIssqn(TipoIssqn tipoIssqn) {
        this.tipoIssqn = tipoIssqn;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEnderecoCompleto() {
        String sb = "";
        String juncaoVirgula = "";
        String juncaoTraco = "";
        if (!Strings.isNullOrEmpty(logradouro)) {
            sb += juncaoVirgula + logradouro.trim();
            juncaoVirgula = ", ";

        }
        if (!Strings.isNullOrEmpty(numero)) {
            sb += juncaoVirgula + numero.trim();
            juncaoVirgula = ", ";
        }
        if (!Strings.isNullOrEmpty(complemento)) {
            sb += juncaoVirgula + complemento.trim();
            juncaoVirgula = ", ";
        }
        if (!Strings.isNullOrEmpty(bairro)) {
            sb += juncaoVirgula + bairro.trim();
            juncaoVirgula = ", ";
        }
        if (!Strings.isNullOrEmpty(cep)) {
            sb += juncaoVirgula + "CEP " + (cep.trim());
            juncaoVirgula = ", ";
        }
        if (!Strings.isNullOrEmpty(municipio)) {
            sb += juncaoTraco + municipio;
            juncaoTraco = " - ";
        }
        if (!Strings.isNullOrEmpty(uf)) {
            sb += juncaoTraco + uf;
            juncaoTraco = " - ";
        }
        return sb;
    }


    @Override
    public DadosPessoaisNfseDTO toNfseDto() {
        DadosPessoaisNfseDTO dto = new DadosPessoaisNfseDTO();
        dto.setId(this.id);
        dto.setCpfCnpj(this.cpfCnpj);
        dto.setInscricaoEstadualRg(this.inscricaoEstadualRg);
        dto.setNomeRazaoSocial(this.nomeRazaoSocial);
        dto.setEmail(this.email);
        dto.setTelefone(this.telefone);
        dto.setCelular(this.celular);
        dto.setCep(StringUtil.retornaApenasNumeros(this.cep));
        dto.setNumero(this.numero);
        dto.setLogradouro(this.logradouro);
        dto.setBairro(this.bairro);
        dto.setComplemento(this.complemento);
        dto.setApelido(this.apelido);
        dto.setNumeroIdentificacao(this.numeroIdentificacao);
        if (this.municipio != null) {
            dto.setMunicipio(new MunicipioNfseDTO(null, this.codigoMunicipio, this.municipio, this.uf));
        }
        if (this.pais != null) {
            PaisNfseDTO pais = new PaisNfseDTO();
            pais.setNome(this.pais);
            pais.setCodigo(this.codigoPais);
            dto.setPais(pais);
        }
        dto.setTipoIssqn(this.tipoIssqn != null ? this.tipoIssqn.toDto() : null);
        dto.setRegimeTributario(this.regimeTributario != null ? this.regimeTributario.toDto() : null);
        return dto;
    }

    @Override
    public DadosPessoaisNfse clone() {
        DadosPessoaisNfse clone = new DadosPessoaisNfse();
        clone.setCpfCnpj(this.cpfCnpj);
        clone.setInscricaoEstadualRg(this.inscricaoEstadualRg);
        clone.setNomeRazaoSocial(this.nomeRazaoSocial);
        clone.setApelido(this.apelido);
        clone.setEmail(this.email);
        clone.setTelefone(this.telefone);
        clone.setCelular(this.celular);
        clone.setCep(this.cep);
        clone.setNumero(this.numero);
        clone.setLogradouro(this.logradouro);
        clone.setBairro(this.bairro);
        clone.setComplemento(this.complemento);
        clone.setNumeroIdentificacao(this.numeroIdentificacao);
        if (this.municipio != null) {
            clone.setCodigoMunicipio(this.codigoMunicipio);
            clone.setMunicipio(this.municipio);
        }
        if (this.pais != null) {
            clone.setCodigoPais(this.codigoPais);
            clone.setPais(this.pais);
        }
        clone.setRegimeTributario(this.regimeTributario);
        clone.setTipoIssqn(this.tipoIssqn);
        clone.setDataNascimento(this.dataNascimento);
        return clone;
    }
}
