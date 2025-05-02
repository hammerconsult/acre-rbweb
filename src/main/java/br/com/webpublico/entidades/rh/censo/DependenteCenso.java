package br.com.webpublico.entidades.rh.censo;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.TipoDeficiencia;
import br.com.webpublico.util.UtilEntidades;

import javax.persistence.*;
import java.util.Date;

@Entity
public class DependenteCenso extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private VinculoFPCenso vinculoFPCenso;
    private Long idResponsavel;
    private String nome;
    @ManyToOne
    private GrauParentescoCenso grauParentescoCenso;
    @Temporal(TemporalType.DATE)
    private Date dataNascimento;
    private String cpf;
    private String sexo;
    @Temporal(TemporalType.DATE)
    private Date dataCasamento;
    @Enumerated(EnumType.STRING)
    private TipoDeficiencia tipoDeficiencia;
    @Transient
    private boolean isNovoDependente;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public VinculoFPCenso getVinculoFPCenso() {
        return vinculoFPCenso;
    }

    public void setVinculoFPCenso(VinculoFPCenso vinculoFPCenso) {
        this.vinculoFPCenso = vinculoFPCenso;
    }

    public Long getIdResponsavel() {
        return idResponsavel;
    }

    public void setIdResponsavel(Long idResponsavel) {
        this.idResponsavel = idResponsavel;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public GrauParentescoCenso getGrauParentescoCenso() {
        return grauParentescoCenso;
    }

    public void setGrauParentescoCenso(GrauParentescoCenso grauParentescoCenso) {
        this.grauParentescoCenso = grauParentescoCenso;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return UtilEntidades.formatarCpf(cpf);
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Date getDataCasamento() {
        return dataCasamento;
    }

    public void setDataCasamento(Date dataCasamento) {
        this.dataCasamento = dataCasamento;
    }

    public TipoDeficiencia getTipoDeficiencia() {
        return tipoDeficiencia;
    }

    public void setTipoDeficiencia(TipoDeficiencia tipoDeficiencia) {
        this.tipoDeficiencia = tipoDeficiencia;
    }

    public boolean isNovoDependente() {
        return isNovoDependente;
    }

    public void setNovoDependente(boolean novoDependente) {
        isNovoDependente = novoDependente;
    }
}
