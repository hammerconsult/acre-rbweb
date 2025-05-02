package br.com.webpublico.entidades.rh.censo;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.EstadoCivil;
import br.com.webpublico.util.UtilEntidades;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class VinculoFPCenso extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String cpf;
    private String nome;
    @Temporal(TemporalType.DATE)
    private Date dataNascimento;
    private String email;
    @Enumerated(EnumType.STRING)
    private EstadoCivil estadoCivil;
    private String mae;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private TelefoneCenso telefone;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private EnderecoCenso endereco;
    @ManyToOne(cascade = CascadeType.ALL)
    private AnaliseAtualizacaoCenso analiseAtualizacaoCenso;
    @ManyToOne
    private TipoAposentadoriaCenso tipoAposentadoria;
    @OneToMany(mappedBy = "vinculoFPCenso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DependenteCenso> dependentes;
    private String codigo;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return UtilEntidades.formatarCpf(cpf);
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }


    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public TelefoneCenso getTelefone() {
        return telefone;
    }

    public void setTelefone(TelefoneCenso telefone) {
        this.telefone = telefone;
    }

    public EnderecoCenso getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoCenso endereco) {
        this.endereco = endereco;
    }

    public List<DependenteCenso> getDependentes() {
        return dependentes;
    }

    public void setDependentes(List<DependenteCenso> dependentes) {
        this.dependentes = dependentes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getMae() {
        return mae;
    }

    public void setMae(String mae) {
        this.mae = mae;
    }

    public AnaliseAtualizacaoCenso getAnaliseAtualizacaoCenso() {
        return analiseAtualizacaoCenso;
    }

    public void setAnaliseAtualizacaoCenso(AnaliseAtualizacaoCenso analiseAtualizacaoCenso) {
        this.analiseAtualizacaoCenso = analiseAtualizacaoCenso;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoAposentadoriaCenso getTipoAposentadoria() {
        return tipoAposentadoria;
    }

    public void setTipoAposentadoria(TipoAposentadoriaCenso tipoAposentadoria) {
        this.tipoAposentadoria = tipoAposentadoria;
    }

    public String getCodigo() {
        return codigo;
    }
}
