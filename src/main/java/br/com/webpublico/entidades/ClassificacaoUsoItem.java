package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
@Audited
@Etiqueta("Classificação de Uso - Item")
public class ClassificacaoUsoItem extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ClassificacaoUso classificacaoUso;
    @Obrigatorio
    @Etiqueta("Código")
    private Long codigo;
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    private BigDecimal areaTotalMaxima;

    public ClassificacaoUsoItem() {
        super();
        areaTotalMaxima = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClassificacaoUso getClassificacaoUso() {
        return classificacaoUso;
    }

    public void setClassificacaoUso(ClassificacaoUso classificacaoUso) {
        this.classificacaoUso = classificacaoUso;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getAreaTotalMaxima() {
        return areaTotalMaxima;
    }

    public void setAreaTotalMaxima(BigDecimal areaTotalMaxima) {
        this.areaTotalMaxima = areaTotalMaxima;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
