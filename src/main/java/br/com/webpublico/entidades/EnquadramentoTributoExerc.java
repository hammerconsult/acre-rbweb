package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author daniel
 */
@Entity

@Audited
@Etiqueta("Enquadramento de Exercício de Referência de Tributos com Contas de Receita")
public class EnquadramentoTributoExerc implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício Vigente")
    private Exercicio exercicioVigente;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício Inicial da Dívida")
    private Exercicio exercicioDividaInicial;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício Final da Dívida")
    private Exercicio exercicioDividaFinal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "enquadramento", orphanRemoval = true)
    private List<ContaTributoReceita> contas;
    @Transient
    private Long criadoEm;

    public EnquadramentoTributoExerc() {
        this.criadoEm = System.nanoTime();
        this.contas = Lists.newArrayList();
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public List<ContaTributoReceita> getContas() {
        return contas;
    }

    public void setContas(List<ContaTributoReceita> contas) {
        this.contas = contas;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Exercicio getExercicioDividaFinal() {
        return exercicioDividaFinal;
    }

    public void setExercicioDividaFinal(Exercicio exercicioDividaFinal) {
        this.exercicioDividaFinal = exercicioDividaFinal;
    }

    public Exercicio getExercicioDividaInicial() {
        return exercicioDividaInicial;
    }

    public void setExercicioDividaInicial(Exercicio exercicioDividaInicial) {
        this.exercicioDividaInicial = exercicioDividaInicial;
    }

    public Exercicio getExercicioVigente() {
        return exercicioVigente;
    }

    public void setExercicioVigente(Exercicio exercicioVigente) {
        this.exercicioVigente = exercicioVigente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }


}
