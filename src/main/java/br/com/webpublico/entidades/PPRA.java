package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by carlos on 11/08/15.
 */
@Entity
@Audited
@Etiqueta("PPRA - Programa de Prevenção de Riscos Ambientais")
public class PPRA extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @Obrigatorio
    @Etiqueta("Identificação dos Riscos")
    @OneToMany(mappedBy = "ppra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IdentificacaoRisco> identificacaoRiscos;
    @Obrigatorio
    @Etiqueta("Reconhecimento dos Riscos")
    @OneToMany(mappedBy = "ppra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReconhecimentoRisco> reconhecimentoRiscos;
    @Obrigatorio
    @Etiqueta("Avaliação dos Riscos")
    @OneToMany(mappedBy = "ppra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvaliacaoQuantitativaPPRA> avaliacaoQuantitativaPPRAs;
    @Obrigatorio
    @Etiqueta("Medidas de Controle")
    @OneToMany(mappedBy = "ppra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedidaDeControlePPRA> medidaDeControlePPRAs;
    @Obrigatorio
    @Etiqueta("Programa PPRA")
    @OneToMany(mappedBy = "ppra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramaPPRA> programaPPRAs;

    public PPRA() {
        identificacaoRiscos = new ArrayList<IdentificacaoRisco>();
        reconhecimentoRiscos = new ArrayList<ReconhecimentoRisco>();
        avaliacaoQuantitativaPPRAs = new ArrayList<AvaliacaoQuantitativaPPRA>();
        medidaDeControlePPRAs = new ArrayList<MedidaDeControlePPRA>();
        programaPPRAs = new ArrayList<ProgramaPPRA>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<IdentificacaoRisco> getIdentificacaoRiscos() {
        return identificacaoRiscos;
    }

    public void setIdentificacaoRiscos(List<IdentificacaoRisco> identificacaoRiscos) {
        this.identificacaoRiscos = identificacaoRiscos;
    }

    public List<ReconhecimentoRisco> getReconhecimentoRiscos() {
        return reconhecimentoRiscos;
    }

    public void setReconhecimentoRiscos(List<ReconhecimentoRisco> reconhecimentoRiscos) {
        this.reconhecimentoRiscos = reconhecimentoRiscos;
    }

    public List<AvaliacaoQuantitativaPPRA> getAvaliacaoQuantitativaPPRAs() {
        return avaliacaoQuantitativaPPRAs;
    }

    public void setAvaliacaoQuantitativaPPRAs(List<AvaliacaoQuantitativaPPRA> avaliacaoQuantitativaPPRAs) {
        this.avaliacaoQuantitativaPPRAs = avaliacaoQuantitativaPPRAs;
    }

    public List<MedidaDeControlePPRA> getMedidaDeControlePPRAs() {
        return medidaDeControlePPRAs;
    }

    public void setMedidaDeControlePPRAs(List<MedidaDeControlePPRA> medidaDeControlePPRAs) {
        this.medidaDeControlePPRAs = medidaDeControlePPRAs;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public List<ProgramaPPRA> getProgramaPPRAs() {
        return programaPPRAs;
    }

    public void setProgramaPPRAs(List<ProgramaPPRA> programaPPRAs) {
        this.programaPPRAs = programaPPRAs;
    }

    @Override
    public String toString() {
        return unidadeOrganizacional.getDescricao();
    }
}
