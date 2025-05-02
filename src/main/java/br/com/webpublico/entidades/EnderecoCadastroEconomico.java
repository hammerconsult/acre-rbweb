package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@GrupoDiagrama(nome = "CadastroEconomico")
@Entity
@Audited
@Etiqueta("Endereço do Cadastro Econômico")
public class EnderecoCadastroEconomico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String numero;
    private String complemento;
    private String cep;
    @Enumerated(EnumType.STRING)
    private TipoEndereco tipoEndereco;
    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Etiqueta("Área de Utulização")
    private Double areaUtilizacao;
    private Integer sequencial;

    public EnderecoCadastroEconomico() {
        this.criadoEm = System.nanoTime();
    }

    public EnderecoCadastroEconomico(TipoEndereco tipoEndereco) {
        this.criadoEm = System.nanoTime();
        this.tipoEndereco = tipoEndereco;
    }

    public EnderecoCadastroEconomico(TipoEndereco tipoEndereco, CadastroEconomico cadastroEconomico) {
        this.criadoEm = System.nanoTime();
        this.tipoEndereco = tipoEndereco;
        this.cadastroEconomico = cadastroEconomico;
    }

    public EnderecoCadastroEconomico(EnderecoCadastroEconomico enderecoCadastroEconomico) {
        this.criadoEm = System.nanoTime();
        this.tipoEndereco = enderecoCadastroEconomico.getTipoEndereco();
        this.numero = enderecoCadastroEconomico.getNumero();
        this.complemento = enderecoCadastroEconomico.getComplemento();
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

    public TipoEndereco getTipoEndereco() {
        return tipoEndereco;
    }

    public void setTipoEndereco(TipoEndereco tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Double getAreaUtilizacao() {
        if (areaUtilizacao == null) areaUtilizacao = 0.0;
        return areaUtilizacao;
    }

    public Integer getSequencial() {
        return sequencial;
    }

    public void setSequencial(Integer sequencial) {
        this.sequencial = sequencial;
    }

    public void setAreaUtilizacao(Double areaUtilizacao) {
        this.areaUtilizacao = areaUtilizacao;
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

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        String endereco = logradouro + ", " + numero + ", " + complemento + ", " + bairro + ", " + localidade + ", " + uf + ", " + cep;
        endereco = endereco.replace(", null", " ");
        endereco = endereco.replace("null", " ");
        return endereco;
    }

    public EnderecoCorreio converterEnderecoCadastroEconomicoPrincipalParaEnderecoCorreio() {
        EnderecoCorreio enderecoCorreio = new EnderecoCorreio();
        enderecoCorreio.setCep(cep);
        enderecoCorreio.setLogradouro(logradouro);
        enderecoCorreio.setComplemento(complemento);
        enderecoCorreio.setBairro(bairro);
        enderecoCorreio.setLocalidade(localidade);
        enderecoCorreio.setUf(uf);
        enderecoCorreio.setNumero(numero);
        enderecoCorreio.setTipoEndereco(tipoEndereco);
        enderecoCorreio.setPrincipal(TipoEndereco.COMERCIAL.equals(tipoEndereco));
        return enderecoCorreio;
    }

    public void definirSequencial() {
        if (tipoEndereco.equals(TipoEndereco.COMERCIAL)) {
            sequencial = 0;
        } else {
            sequencial = 1;
            List<EnderecoCadastroEconomico> enderecosSecundarios = cadastroEconomico.getEnderecosSecundarios();
            if (enderecosSecundarios != null && !enderecosSecundarios.isEmpty()) {
                sequencial = enderecosSecundarios.stream()
                    .map(EnderecoCadastroEconomico::getSequencial)
                    .max(Integer::compare).get() + 1;
            }
        }
    }
}
