package br.com.webpublico.entidades.rh.saudeservidor;


import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UF;
import br.com.webpublico.enums.rh.esocial.TipoResponsavelAmbiental;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class RegistroAmbiental  extends SuperEntidade  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private RiscoOcupacional riscoOcupacional;

    @ManyToOne
    private PessoaFisica responsavel;

    @Enumerated(EnumType.STRING)
    private TipoResponsavelAmbiental tipoResponsavelAmbiental;

    private String descricaoClasse;

    private String numeroInscricaoClasse;

    @ManyToOne
    private UF uf;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RiscoOcupacional getRiscoOcupacional() {
        return riscoOcupacional;
    }

    public void setRiscoOcupacional(RiscoOcupacional riscoOcupacional) {
        this.riscoOcupacional = riscoOcupacional;
    }

    public PessoaFisica getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaFisica responsavel) {
        this.responsavel = responsavel;
    }

    public TipoResponsavelAmbiental getTipoResponsavelAmbiental() {
        return tipoResponsavelAmbiental;
    }

    public void setTipoResponsavelAmbiental(TipoResponsavelAmbiental tipoResponsavelAmbiental) {
        this.tipoResponsavelAmbiental = tipoResponsavelAmbiental;
    }

    public String getDescricaoClasse() {
        return descricaoClasse;
    }

    public void setDescricaoClasse(String descricaoClasse) {
        this.descricaoClasse = descricaoClasse;
    }

    public String getNumeroInscricaoClasse() {
        return numeroInscricaoClasse;
    }

    public void setNumeroInscricaoClasse(String numeroInscricaoClasse) {
        this.numeroInscricaoClasse = numeroInscricaoClasse;
    }

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }
}
