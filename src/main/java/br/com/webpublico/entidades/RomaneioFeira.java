package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoRomaneioFeira;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Romaneio Feira")
public class RomaneioFeira extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Data Inicial")
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    private Date dataInicial;

    @Etiqueta("Data Final")
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    private Date dataFinal;

    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoRomaneioFeira situacao;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Feira")
    private Feira feira;

    @Etiqueta("Feirantes")
    @OneToMany(mappedBy = "romaneioFeira", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RomaneioFeiraFeirante> feirantes;

    public RomaneioFeira() {
        feirantes = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicio) {
        this.dataInicial = dataInicio;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFim) {
        this.dataFinal = dataFim;
    }

    public SituacaoRomaneioFeira getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoRomaneioFeira situacao) {
        this.situacao = situacao;
    }

    public Feira getFeira() {
        return feira;
    }

    public void setFeira(Feira feira) {
        this.feira = feira;
    }

    public List<RomaneioFeiraFeirante> getFeirantes() {
        return feirantes;
    }

    public void setFeirantes(List<RomaneioFeiraFeirante> feiras) {
        this.feirantes = feiras;
    }
}
