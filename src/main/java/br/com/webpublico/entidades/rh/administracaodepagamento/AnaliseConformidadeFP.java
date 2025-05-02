package br.com.webpublico.entidades.rh.administracaodepagamento;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import com.beust.jcommander.internal.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
public class AnaliseConformidadeFP extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(mappedBy = "analiseConformidadeFP", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VinculoAnaliseConformidadeFP> vinculosAnalise;
    private Integer ano;
    private Integer mes;
    private BigDecimal percentualAmostra;

    public AnaliseConformidadeFP() {
        vinculosAnalise = Lists.newArrayList();
    }

    public List<VinculoAnaliseConformidadeFP> getVinculosAnalise() {
        return vinculosAnalise;
    }

    public void setVinculosAnalise(List<VinculoAnaliseConformidadeFP> vinculosAnalise) {
        this.vinculosAnalise = vinculosAnalise;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public BigDecimal getPercentualAmostra() {
        return percentualAmostra;
    }

    public void setPercentualAmostra(BigDecimal percentualAmostra) {
        this.percentualAmostra = percentualAmostra;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
