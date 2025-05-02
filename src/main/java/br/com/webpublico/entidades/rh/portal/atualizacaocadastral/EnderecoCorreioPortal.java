package br.com.webpublico.entidades.rh.portal.atualizacaocadastral;


import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.pessoa.dto.EnderecoCorreioDTO;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by peixe on 29/08/17.
 */
@Entity
public class EnderecoCorreioPortal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PessoaFisicaPortal pessoaFisicaPortal;
    @ManyToOne
    private DependentePortal dependentePortal;
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;
    private String numero;
    @Enumerated(EnumType.STRING)
    private TipoEndereco tipoEndereco;
    private Boolean principal;

    public EnderecoCorreioPortal() {
    }

    public static List<EnderecoCorreioPortal> dtoToEnderecos(List<EnderecoCorreioDTO> dtos, PessoaFisicaPortal pessoa, DependentePortal dependentePortal) {
        List<EnderecoCorreioPortal> enderecos = Lists.newLinkedList();
        if (dtos != null && !dtos.isEmpty()) {
            for (EnderecoCorreioDTO dto : dtos) {
                EnderecoCorreioPortal f = dtoToEnderecoCorreioPortal(dto, pessoa, dependentePortal);
                if (f != null) {
                    enderecos.add(f);
                }
            }
        }
        return enderecos;
    }

    private static EnderecoCorreioPortal dtoToEnderecoCorreioPortal(EnderecoCorreioDTO dto, PessoaFisicaPortal pessoa, DependentePortal dependentePortal) {
        EnderecoCorreioPortal e = new EnderecoCorreioPortal();
        e.setCep(dto.getCep());
        e.setBairro(dto.getBairro());
        e.setComplemento(dto.getComplemento());
        e.setLocalidade(dto.getLocalidade());
        e.setLogradouro(dto.getLogradouro());
        e.setNumero(dto.getNumero());
        e.setPessoaFisicaPortal(pessoa);
        e.setDependentePortal(dependentePortal);
        e.setPrincipal(dto.getPrincipal());
        e.setTipoEndereco(dto.getTipoEndereco() != null ? TipoEndereco.valueOf(dto.getTipoEndereco().name()) : null);
        e.setUf(dto.getUf());
        return e;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaFisicaPortal getPessoaFisicaPortal() {
        return pessoaFisicaPortal;
    }

    public void setPessoaFisicaPortal(PessoaFisicaPortal pessoaFisicaPortal) {
        this.pessoaFisicaPortal = pessoaFisicaPortal;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
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

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoEndereco getTipoEndereco() {
        return tipoEndereco;
    }

    public void setTipoEndereco(TipoEndereco tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

    public DependentePortal getDependentePortal() {
        return dependentePortal;
    }

    public void setDependentePortal(DependentePortal dependentePortal) {
        this.dependentePortal = dependentePortal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnderecoCorreioPortal that = (EnderecoCorreioPortal) o;
        return Objects.equals(cep, that.cep) &&
            Objects.equals(logradouro != null ? logradouro.toUpperCase().trim() : null, that.logradouro != null ? that.logradouro.toUpperCase().trim() : null) &&
            Objects.equals(complemento != null ? complemento.toUpperCase().trim() : null, that.complemento != null ? that.complemento.toUpperCase().trim() : null) &&
            Objects.equals(bairro != null ? bairro.toUpperCase().trim() : null, that.bairro != null ? that.bairro.toUpperCase().trim() : null) &&
            Objects.equals(localidade != null ? localidade.toUpperCase().trim() : null, that.localidade != null ? that.localidade.toUpperCase().trim() : null) &&
            Objects.equals(uf != null ? uf.toUpperCase().trim() : null, that.uf != null ? that.uf.toUpperCase().trim() : null) &&
            Objects.equals(numero, that.numero) &&
            tipoEndereco == that.tipoEndereco &&
            Objects.equals(principal, that.principal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cep, logradouro, complemento, bairro, localidade, uf, numero, tipoEndereco, principal);
    }
}
