package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import com.beust.jcommander.internal.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class AlteracaoCMC extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data de Alteração")
    private Date dataAlteracao;
    @Etiqueta("Calcular Alvará")
    private Boolean calcularAlvara;
    @ManyToOne
    @Etiqueta("Cadastro Econômico")
    private CadastroEconomico cadastroEconomico;
    @ManyToOne
    @Etiqueta("Processo de Cálculo de Alvará")
    private ProcessoCalculoAlvara processoCalculoAlvara;

    @OneToMany(mappedBy = "alteracaoCMC", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Cnaes Atuais do CMC")
    private List<AlteracaoCnaeCMC> cnaesAtuais;
    @OneToMany(mappedBy = "alteracaoCMC", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Cnaes Anteriores do CMC")
    private List<AlteracaoCnaeCMCAnterior> cnaesAnteriores;

    @OneToMany(mappedBy = "alteracaoCMC", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Endereços Atuais do CMC")
    private List<AlteracaoEnderecoCMC> enderecosAtuais;
    @OneToMany(mappedBy = "alteracaoCMC", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Endereços Anteriores do CMC")
    private List<AlterEnderecoCMCAnterior> enderecosAnteriores;
    @Transient
    private String razaoSocial;

    public AlteracaoCMC() {
        this.calcularAlvara = false;
        inicializarCnaes();
        inicializarEnderecos();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public Boolean isCalcularAlvara() {
        return calcularAlvara != null ? calcularAlvara : false;
    }

    public void setCalcularAlvara(Boolean calcularAlvara) {
        this.calcularAlvara = calcularAlvara;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public ProcessoCalculoAlvara getProcessoCalculoAlvara() {
        return processoCalculoAlvara;
    }

    public void setProcessoCalculoAlvara(ProcessoCalculoAlvara processoCalculoAlvara) {
        this.processoCalculoAlvara = processoCalculoAlvara;
    }

    public List<AlteracaoCnaeCMC> getCnaesAtuais() {
        return cnaesAtuais;
    }

    public void setCnaesAtuais(List<AlteracaoCnaeCMC> cnaesAtuais) {
        this.cnaesAtuais = cnaesAtuais;
    }

    public List<AlteracaoCnaeCMCAnterior> getCnaesAnteriores() {
        return cnaesAnteriores;
    }

    public void setCnaesAnteriores(List<AlteracaoCnaeCMCAnterior> cnaesAnteriores) {
        this.cnaesAnteriores = cnaesAnteriores;
    }

    public List<AlteracaoEnderecoCMC> getEnderecosAtuais() {
        return enderecosAtuais;
    }

    public void setEnderecosAtuais(List<AlteracaoEnderecoCMC> enderecosAtuais) {
        this.enderecosAtuais = enderecosAtuais;
    }

    public List<AlterEnderecoCMCAnterior> getEnderecosAnteriores() {
        return enderecosAnteriores;
    }

    public void setEnderecosAnteriores(List<AlterEnderecoCMCAnterior> enderecosAnteriores) {
        this.enderecosAnteriores = enderecosAnteriores;
    }

    public void inicializarCnaes() {
        this.cnaesAtuais = Lists.newArrayList();
        this.cnaesAnteriores = Lists.newArrayList();
    }

    public void inicializarEnderecos() {
        this.enderecosAtuais = Lists.newArrayList();
        this.enderecosAnteriores = Lists.newArrayList();
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getRazaoSocial() {
        razaoSocial = "";
        if(cadastroEconomico != null && cadastroEconomico.getPessoa() != null) {
            if(cadastroEconomico.getPessoa() instanceof PessoaFisica) {
                razaoSocial += cadastroEconomico.getPessoa().getNome() + " - " + cadastroEconomico.getPessoa().getCpf_Cnpj();
            } else if (cadastroEconomico.getPessoa() instanceof PessoaJuridica) {
                razaoSocial += ((PessoaJuridica) cadastroEconomico.getPessoa()).getRazaoSocial() + " - " + cadastroEconomico.getPessoa().getCpf_Cnpj();
            }
        }
        return razaoSocial;
    }
}
