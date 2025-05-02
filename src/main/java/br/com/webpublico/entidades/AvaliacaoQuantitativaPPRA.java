package br.com.webpublico.entidades;

import br.com.webpublico.enums.IntensidadeAvaliacaoQuantitativaPPRA;
import br.com.webpublico.enums.TecnicaAvaliacaoQuantitativaPPRA;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by carlos on 04/09/15.
 */
@Entity
@Audited
@Etiqueta("Avaliação Quantitativa PPRA")
public class AvaliacaoQuantitativaPPRA extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Intensidade")
    @Enumerated(EnumType.STRING)
    private IntensidadeAvaliacaoQuantitativaPPRA intensidade;
    @Obrigatorio
    @Etiqueta("Técnica")
    @Enumerated(EnumType.STRING)
    private TecnicaAvaliacaoQuantitativaPPRA tecnica;
    @Obrigatorio
    @Etiqueta("Tempo de Exposição")
    @Temporal(TemporalType.TIME)
    private Date exposicao;
    @Obrigatorio
    @Etiqueta("Tolerância")
    private String tolerancia;
    @Obrigatorio
    @Etiqueta("Quantidade de Servidores")
    private Integer quantidade;
    @ManyToOne
    private PPRA ppra;

    public AvaliacaoQuantitativaPPRA() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IntensidadeAvaliacaoQuantitativaPPRA getIntensidade() {
        return intensidade;
    }

    public void setIntensidade(IntensidadeAvaliacaoQuantitativaPPRA intensidade) {
        this.intensidade = intensidade;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public TecnicaAvaliacaoQuantitativaPPRA getTecnica() {
        return tecnica;
    }

    public void setTecnica(TecnicaAvaliacaoQuantitativaPPRA tecnica) {
        this.tecnica = tecnica;
    }

    public Date getExposicao() {
        return exposicao;
    }

    public void setExposicao(Date exposicao) {
        this.exposicao = exposicao;
    }

    public String getTolerancia() {
        return tolerancia;
    }

    public void setTolerancia(String tolerancia) {
        this.tolerancia = tolerancia;
    }

    public PPRA getPpra() {
        return ppra;
    }

    public void setPpra(PPRA ppra) {
        this.ppra = ppra;
    }

    @Override
    public String toString() {
        return tecnica.toString();
    }
}
