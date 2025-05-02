/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoUtilizacaoRendasPatrimoniais;
import br.com.webpublico.enums.TipoVinculoImobiliario;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gustavo
 */
@Entity

@Audited
@Etiqueta("Localização")
public class Localizacao implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @ManyToOne
    private PessoaFisica representanteSecretaria;
    private String cargoRepresentante;
    @ManyToOne
    private PessoaFisica procurador;
    private String cargoProcurador;
    @ManyToOne
    private UnidadeOrganizacional secretaria;
    private String decreto;
    private String portaria;
    @Enumerated(EnumType.STRING)
    private TipoUtilizacaoRendasPatrimoniais tipoOcupacaoLocalizacao;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    @ManyToOne
    private Bairro bairro;
    @ManyToOne
    private Logradouro logradouro;
    @OneToMany(mappedBy = "localizacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PontoComercial> pontosComerciais;
    private String numero;
    private String complemento;
    @Tabelavel
    @ManyToOne
    @Etiqueta("Lotação")
    private LotacaoVistoriadora lotacaoVistoriadora;
    @ManyToOne
    private IndiceEconomico indiceEconomico;
    private Boolean calculaRateio;

    @Enumerated(EnumType.STRING)
    private TipoVinculoImobiliario tipoVinculoImobiliario;

    public Localizacao() {
        pontosComerciais = new ArrayList<PontoComercial>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoVinculoImobiliario getTipoVinculoImobiliario() {
        return tipoVinculoImobiliario;
    }

    public void setTipoVinculoImobiliario(TipoVinculoImobiliario tipoVinculoImobiliario) {
        this.tipoVinculoImobiliario = tipoVinculoImobiliario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public String getPortaria() {
        return portaria;
    }

    public void setPortaria(String portaria) {
        this.portaria = portaria;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDecreto() {

        return decreto;
    }

    public void setDecreto(String decreto) {
        this.decreto = decreto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public PessoaFisica getProcurador() {
        return procurador;
    }

    public void setProcurador(PessoaFisica procurador) {
        this.procurador = procurador;
    }

    public String getCargoProcurador(){
        return cargoProcurador;
    }

    public void setCargoProcurador(String cargoProcurador){
        this.cargoProcurador = cargoProcurador;
    }

    public PessoaFisica getRepresentanteSecretaria() {
        return representanteSecretaria;
    }

    public void setRepresentanteSecretaria(PessoaFisica representanteSecretaria) {
        this.representanteSecretaria = representanteSecretaria;
    }

    public String getCargoRepresentante(){
        return cargoRepresentante;
    }

    public void setCargoRepresentante(String cargoRepresentante){
        this.cargoRepresentante = cargoRepresentante;
    }

    public UnidadeOrganizacional getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(UnidadeOrganizacional secretaria) {
        this.secretaria = secretaria;
    }

    public TipoUtilizacaoRendasPatrimoniais getTipoOcupacaoLocalizacao() {
        return tipoOcupacaoLocalizacao;
    }

    public void setTipoOcupacaoLocalizacao(TipoUtilizacaoRendasPatrimoniais tipoOcupacaoLocalizacao) {
        this.tipoOcupacaoLocalizacao = tipoOcupacaoLocalizacao;
    }

    public List<PontoComercial> getPontosComerciais() {
        return pontosComerciais;
    }

    public void setPontosComerciais(List<PontoComercial> pontosComerciais) {
        this.pontosComerciais = pontosComerciais;
    }

    public LotacaoVistoriadora getLotacaoVistoriadora() {
        return lotacaoVistoriadora;
    }

    public void setLotacaoVistoriadora(LotacaoVistoriadora lotacaoVistoriadora) {
        this.lotacaoVistoriadora = lotacaoVistoriadora;
    }

    public IndiceEconomico getIndiceEconomico() {
        return indiceEconomico;
    }

    public void setIndiceEconomico(IndiceEconomico indiceEconomico) {
        this.indiceEconomico = indiceEconomico;
    }

    public Boolean getCalculaRateio() {
        return calculaRateio;
    }

    public void setCalculaRateio(Boolean calculaRateio) {
        this.calculaRateio = calculaRateio;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Localizacao)) {
            return false;
        }
        Localizacao other = (Localizacao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descricao + "/" + bairro + " Logradouro: " + logradouro;
    }

    public String getToStringAutoComplete() {
        String s = "";

        if (this.codigo != null) {
            s += this.codigo + " - ";
        }

        if (this.descricao != null) {
            s += this.descricao.trim();
        }

        return s;
    }

    public boolean isIndividualizada() {
        return TipoVinculoImobiliario.VINCULACAO_INDIVIDUALIZADA.equals(tipoVinculoImobiliario);
    }
}
