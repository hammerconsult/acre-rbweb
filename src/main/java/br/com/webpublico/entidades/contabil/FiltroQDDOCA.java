package br.com.webpublico.entidades.contabil;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.TipoOCA;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.beust.jcommander.internal.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Audited
@Etiqueta("Filtro de Quadro de Detalhamento das Despesas por Orçamento Criança e Adolescente - QDDOCA")
public class FiltroQDDOCA extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    private TipoOCA tipoOCA;
    @OneToMany(mappedBy = "filtroQDDOCA", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FiltroQDDOCAFuncao> funcoes;

    public FiltroQDDOCA() {
        super();
        funcoes = Lists.newArrayList();
    }

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

    public TipoOCA getTipoOCA() {
        return tipoOCA;
    }

    public void setTipoOCA(TipoOCA tipoOCA) {
        this.tipoOCA = tipoOCA;
    }

    public List<FiltroQDDOCAFuncao> getFuncoes() {
        return funcoes;
    }

    public void setFuncoes(List<FiltroQDDOCAFuncao> funcoes) {
        this.funcoes = funcoes;
    }
}
