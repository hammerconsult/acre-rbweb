package br.com.webpublico.entidades.rh.portal.atualizacaocadastral;

import br.com.webpublico.entidades.CID;
import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.Dependente;
import br.com.webpublico.entidades.GrauDeParentesco;
import br.com.webpublico.enums.EstadoCivil;
import br.com.webpublico.enums.Sexo;
import br.com.webpublico.pessoa.dto.DependenteDTO;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class DependentePortal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PessoaFisicaPortal pessoaFisicaPortal;
    private String nome;
    private String nomeMae;
    private String nomePai;
    private String cpf;
    private Date dataNascimento;
    @Enumerated(EnumType.STRING)
    private Sexo sexo;
    @ManyToOne
    private GrauDeParentesco grauDeParentesco;
    private String numero;
    private Boolean inativarDependente;
    private Boolean portadorNecessidadeEspecial;
    private Boolean alterado;
    @ManyToOne
    private CID cid;
    private String codigoDaCid;
    private Date deficienteDesde;
    @Enumerated(EnumType.STRING)
    private EstadoCivil estadoCivil;
    @ManyToOne(cascade = CascadeType.ALL)
    private RGPortal rg;
    @ManyToOne(cascade = CascadeType.ALL)
    private TituloEleitorPortal tituloEleitor;
    @ManyToOne(cascade = CascadeType.ALL)
    private CarteiraTrabalhoPortal carteiraTrabalho;
    @ManyToOne(cascade = CascadeType.ALL)
    private CertidaoNascimentoPortal certidaoNascimento;
    @ManyToOne(cascade = CascadeType.ALL)
    private CertidaoCasamentoPortal certidaoCasamento;
    @ManyToOne(cascade = CascadeType.ALL)
    private SituacaoMilitarPortal situacaoMilitar;
    @OneToMany(mappedBy = "dependentePortal", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<TelefonePortal> telefones;
    @OneToMany(mappedBy = "dependentePortal", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<EnderecoCorreioPortal> enderecos;
    @OneToMany(mappedBy = "dependentePortal", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<DependenteVinculoPortal> dependentesVinculos;
    private Long idDependente;
    @ManyToOne
    private Dependente dependente;
    @ManyToOne
    private Cidade naturalidade;


    static public DependentePortal dtoToDependentePortal(DependenteDTO dependenteDTO, PessoaFisicaPortal pessoa) {

        DependentePortal dependentePortal = new DependentePortal();
        dependentePortal.setIdDependente(dependenteDTO.getId());
        dependentePortal.setPessoaFisicaPortal(pessoa);
        dependentePortal.setNome(dependenteDTO.getNome());
        dependentePortal.setNomeMae(dependenteDTO.getNomeMae());
        dependentePortal.setNomePai(dependenteDTO.getNomePai());
        dependentePortal.setCpf(dependenteDTO.getCpf());
        dependentePortal.setDataNascimento(dependenteDTO.getDataNascimento());
        dependentePortal.setEstadoCivil(dependenteDTO.getEstadoCivil() != null ? EstadoCivil.valueOf(dependenteDTO.getEstadoCivil().name()) : null);
        dependentePortal.setNumero(dependenteDTO.getNumero());
        dependentePortal.setPortadorNecessidadeEspecial(dependenteDTO.getPortadorNecessidadeEspecial());
        dependentePortal.setTelefones(TelefonePortal.dtoToTelefones(dependenteDTO.getTelefones(), null, dependentePortal));
        dependentePortal.setEnderecos(EnderecoCorreioPortal.dtoToEnderecos(dependenteDTO.getEnderecos(), null, dependentePortal));
        dependentePortal.setDependentesVinculos(new ArrayList<DependenteVinculoPortal>());
        dependentePortal.setAlterado(dependenteDTO.getAlterado());
        dependentePortal.setCodigoDaCid(dependenteDTO.getCodigoDaCid());
        dependentePortal.setDeficienteDesde(dependenteDTO.getDeficienteDesde());
        dependentePortal.setInativarDependente(dependenteDTO.getInativarDependente());
        dependentePortal.setNaturalidade(Cidade.dtoToCidade(dependenteDTO.getNaturalidade()));

        if (dependenteDTO.getTituloEleitor() != null && dependenteDTO.getTituloEleitor().isTituloPreenchido()) {
            dependentePortal.setTituloEleitor(TituloEleitorPortal.dtoToTituloEleitorPortal(dependenteDTO.getTituloEleitor()));
        }
        if (dependenteDTO.getRg() != null && dependenteDTO.getRg().isRgPreenchido()) {
            dependentePortal.setRg(RGPortal.dtoToRgPortal(dependenteDTO.getRg()));
        }
        if (dependenteDTO.getCarteiraTrabalho() != null && dependenteDTO.getCarteiraTrabalho().isCarteiraPreenchido()) {
            dependentePortal.setCarteiraTrabalho(CarteiraTrabalhoPortal.dtoToCarteiraTrabalhoPortal(dependenteDTO.getCarteiraTrabalho()));
        }
        if (dependenteDTO.getCertidaoNascimento() != null && dependenteDTO.getCertidaoNascimento().isCertidaoNascimentoPreenchido()) {
            dependentePortal.setCertidaoNascimento(CertidaoNascimentoPortal.dtoToCertidaoNascimentoPortal(dependenteDTO.getCertidaoNascimento()));
        }
        if (dependenteDTO.getCertidaoCasamento() != null && dependenteDTO.getCertidaoCasamento().isCertidaoCasamentoPreenchido()) {
            dependentePortal.setCertidaoCasamento(CertidaoCasamentoPortal.dtoToCertidaoCasamentoPortal(dependenteDTO.getCertidaoCasamento()));
        }
        if (dependenteDTO.getSituacaoMilitar() != null && dependenteDTO.getSituacaoMilitar().isSituacaoMilitarPreenchido()) {
            dependentePortal.setSituacaoMilitar(SituacaoMilitarPortal.dtoToSituacaoMilitarPortal(dependenteDTO.getSituacaoMilitar()));
        }
        if(dependenteDTO.getSexo() != null) {
            dependentePortal.setSexo(Sexo.valueOf(dependenteDTO.getSexo().value()));
        }
        if(dependenteDTO.getEstadoCivil() != null) {
            dependentePortal.setEstadoCivil(EstadoCivil.valueOf(dependenteDTO.getEstadoCivil().name()));
        }
        return dependentePortal;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public String getNomePai() {
        return nomePai;
    }

    public void setNomePai(String nomePai) {
        this.nomePai = nomePai;
    }

    public Dependente getDependente() {
        return dependente;
    }

    public void setDependente(Dependente dependente) {
        this.dependente = dependente;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
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

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public GrauDeParentesco getGrauDeParentesco() {
        return grauDeParentesco;
    }

    public void setGrauDeParentesco(GrauDeParentesco grauDeParentesco) {
        this.grauDeParentesco = grauDeParentesco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Boolean getInativarDependente() {
        return inativarDependente;
    }

    public void setInativarDependente(Boolean inativarDependente) {
        this.inativarDependente = inativarDependente;
    }

    public Boolean getPortadorNecessidadeEspecial() {
        return portadorNecessidadeEspecial;
    }

    public void setPortadorNecessidadeEspecial(Boolean portadorNecessidadeEspecial) {
        this.portadorNecessidadeEspecial = portadorNecessidadeEspecial;
    }

    public Boolean getAlterado() {
        return alterado == null ? false : alterado;
    }

    public void setAlterado(Boolean alterado) {
        this.alterado = alterado;
    }

    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public RGPortal getRg() {
        return rg;
    }

    public void setRg(RGPortal rg) {
        this.rg = rg;
    }

    public TituloEleitorPortal getTituloEleitor() {
        return tituloEleitor;
    }

    public void setTituloEleitor(TituloEleitorPortal tituloEleitor) {
        this.tituloEleitor = tituloEleitor;
    }

    public CarteiraTrabalhoPortal getCarteiraTrabalho() {
        return carteiraTrabalho;
    }

    public void setCarteiraTrabalho(CarteiraTrabalhoPortal carteiraTrabalho) {
        this.carteiraTrabalho = carteiraTrabalho;
    }

    public CertidaoNascimentoPortal getCertidaoNascimento() {
        return certidaoNascimento;
    }

    public void setCertidaoNascimento(CertidaoNascimentoPortal certidaoNascimento) {
        this.certidaoNascimento = certidaoNascimento;
    }

    public CertidaoCasamentoPortal getCertidaoCasamento() {
        return certidaoCasamento;
    }

    public void setCertidaoCasamento(CertidaoCasamentoPortal certidaoCasamento) {
        this.certidaoCasamento = certidaoCasamento;
    }

    public SituacaoMilitarPortal getSituacaoMilitar() {
        return situacaoMilitar;
    }

    public void setSituacaoMilitar(SituacaoMilitarPortal situacaoMilitar) {
        this.situacaoMilitar = situacaoMilitar;
    }

    public List<TelefonePortal> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<TelefonePortal> telefones) {
        this.telefones = telefones;
    }

    public List<EnderecoCorreioPortal> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<EnderecoCorreioPortal> enderecos) {
        this.enderecos = enderecos;
    }

    public List<DependenteVinculoPortal> getDependentesVinculos() {
        return dependentesVinculos;
    }

    public void setDependentesVinculos(List<DependenteVinculoPortal> dependentesVinculos) {
        this.dependentesVinculos = dependentesVinculos;
    }

    public CID getCid() {
        return cid;
    }

    public void setCid(CID cid) {
        this.cid = cid;
    }

    public String getCodigoDaCid() {
        return codigoDaCid;
    }

    public void setCodigoDaCid(String codigoDaCid) {
        this.codigoDaCid = codigoDaCid;
    }

    public Date getDeficienteDesde() {
        return deficienteDesde;
    }

    public void setDeficienteDesde(Date deficienteDesde) {
        this.deficienteDesde = deficienteDesde;
    }

    public Long getIdDependente() {
        return idDependente;
    }

    public void setIdDependente(Long idDependente) {
        this.idDependente = idDependente;
    }

    public Cidade getNaturalidade() {
        return naturalidade;
    }

    public void setNaturalidade(Cidade naturalidade) {
        this.naturalidade = naturalidade;
    }
}
