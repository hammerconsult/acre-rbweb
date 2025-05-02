package br.com.webpublico.entidades.comum;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
@Etiqueta("Fechamento Mensal")
public class FechamentoMensal extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @OneToMany(mappedBy = "fechamentoMensal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FechamentoMensalMes> meses;

    public FechamentoMensal() {
        meses = Lists.newArrayList();
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

    public List<FechamentoMensalMes> getMeses() {
        return meses;
    }

    public void setMeses(List<FechamentoMensalMes> meses) {
        this.meses = meses;
    }
}
