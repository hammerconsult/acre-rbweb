package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Servico;
import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Table(name = "PGCC")
public class PlanoGeralContasComentado extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    private Date fimVigencia;
    private String conta;
    private String desdobramento;
    private String nome;
    private String descricaoDetalhada;
    @ManyToOne
    private PlanoGeralContasComentado contaSuperior;
    @ManyToOne
    private Cosif cosif;
    @ManyToOne
    private Servico servico;
    @ManyToOne
    private CodigoTributacao codigoTributacao;
    @OneToOne
    private PlanoGeralContasComentadoTarifaBancaria tarifaBancaria;
    @OneToOne
    private PlanoGeralContasComentadoProdutoServico produtoServico;
    private Boolean contaTributavel;

    @Transient
    private List<PlanoGeralContasComentado> contasFilhas;

    @Override
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

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(String desdobramento) {
        this.desdobramento = desdobramento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricaoDetalhada() {
        return descricaoDetalhada;
    }

    public void setDescricaoDetalhada(String descricaoDetalhada) {
        this.descricaoDetalhada = descricaoDetalhada;
    }

    public PlanoGeralContasComentado getContaSuperior() {
        return contaSuperior;
    }

    public void setContaSuperior(PlanoGeralContasComentado contaSuperior) {
        this.contaSuperior = contaSuperior;
    }

    public Cosif getCosif() {
        return cosif;
    }

    public void setCosif(Cosif cosif) {
        this.cosif = cosif;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public CodigoTributacao getCodigoTributacao() {
        return codigoTributacao;
    }

    public void setCodigoTributacao(CodigoTributacao codigoTributacao) {
        this.codigoTributacao = codigoTributacao;
    }

    public PlanoGeralContasComentadoTarifaBancaria getTarifaBancaria() {
        return tarifaBancaria;
    }

    public void setTarifaBancaria(PlanoGeralContasComentadoTarifaBancaria tarifaBancaria) {
        this.tarifaBancaria = tarifaBancaria;
    }

    public PlanoGeralContasComentadoProdutoServico getProdutoServico() {
        return produtoServico;
    }

    public void setProdutoServico(PlanoGeralContasComentadoProdutoServico produtoServico) {
        this.produtoServico = produtoServico;
    }

    public Boolean getContaTributavel() {
        return contaTributavel != null ? contaTributavel : Boolean.FALSE;
    }

    public void setContaTributavel(Boolean contaTributavel) {
        this.contaTributavel = contaTributavel;
    }

    public List<PlanoGeralContasComentado> getContasFilhas() {
        return contasFilhas;
    }

    public void setContasFilhas(List<PlanoGeralContasComentado> contasFilhas) {
        this.contasFilhas = contasFilhas;
    }
}
