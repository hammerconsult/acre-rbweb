package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by Fabio on 09/09/2016.
 */
@GrupoDiagrama(nome = "CadastroEconomico")
@Entity
@Audited
@Etiqueta("Endereço do Alvará")
public class EnderecoAlvara extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Alvara alvara;
    @Enumerated(EnumType.STRING)
    private TipoEndereco tipoEndereco;
    private String bairro;
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private Integer sequencial;
    private Double areaUtilizacao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Alvara getAlvara() {
        return alvara;
    }

    public void setAlvara(Alvara alvara) {
        this.alvara = alvara;
    }

    public Integer getSequencial() {
        return sequencial;
    }

    public void setSequencial(Integer sequencial) {
        this.sequencial = sequencial;
    }

    public TipoEndereco getTipoEndereco() {
        return tipoEndereco;
    }

    public void setTipoEndereco(TipoEndereco tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Double getAreaUtilizacao() {
        return areaUtilizacao;
    }

    public void setAreaUtilizacao(Double areaUtilizacao) {
        this.areaUtilizacao = areaUtilizacao;
    }

    @Override
    public String toString() {
        String endereco = logradouro + ", " + numero + ", " + complemento + ", " + bairro + ", " + cep;
        endereco = endereco.replace(", null", " ");
        endereco = endereco.replace("null", " ");
        return endereco;
    }
}
