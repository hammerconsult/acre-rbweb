package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.tributario.CategoriasAssuntoLicenciamentoAmbiental;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited
public class CategoriaAssuntoLicenciamentoAmbiental extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Categoria")
    @Enumerated(EnumType.STRING)
    private CategoriasAssuntoLicenciamentoAmbiental categoria;

    @Obrigatorio
    @ManyToOne
    private AssuntoLicenciamentoAmbiental assunto;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;

    @Obrigatorio
    @Etiqueta("Valor (UFM)")
    private BigDecimal valorUFM;

    public CategoriaAssuntoLicenciamentoAmbiental() {
    }

    public CategoriaAssuntoLicenciamentoAmbiental(AssuntoLicenciamentoAmbiental assunto) {
        this.assunto = assunto;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CategoriasAssuntoLicenciamentoAmbiental getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriasAssuntoLicenciamentoAmbiental categoria) {
        this.categoria = categoria;
    }

    public AssuntoLicenciamentoAmbiental getAssunto() {
        return assunto;
    }

    public void setAssunto(AssuntoLicenciamentoAmbiental assunto) {
        this.assunto = assunto;
    }

    public BigDecimal getValorUFM() {
        return valorUFM;
    }

    public void setValorUFM(BigDecimal valorUFM) {
        this.valorUFM = valorUFM;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    @Override
    public String toString() {
        return categoria.getDescricao();
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        if (valorUFM == null || valorUFM.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("O campo Valor (UFM) deve ser informado, com um valor maior que 0 (zero).");
        }
    }
}
