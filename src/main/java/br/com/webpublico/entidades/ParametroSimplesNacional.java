package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import br.com.webpublico.util.anotacoes.UFM;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributario")
@Etiqueta("Parâmetro Simples Nacional")
public class ParametroSimplesNacional extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Valor UFMRB")
    @UFM
    private BigDecimal valorUFMRB;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parametroSimplesNacional")
    private List<ParamSimplesNacionalDivida> dividas;


    public ParametroSimplesNacional() {
        this.dividas = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public BigDecimal getValorUFMRB() {
        return valorUFMRB;
    }

    public void setValorUFMRB(BigDecimal valorUFMRB) {
        this.valorUFMRB = valorUFMRB;
    }

    public List<ParamSimplesNacionalDivida> getDividas() {
        return dividas;
    }

    public void setDividas(List<ParamSimplesNacionalDivida> dividas) {
        this.dividas = dividas;
    }
}
