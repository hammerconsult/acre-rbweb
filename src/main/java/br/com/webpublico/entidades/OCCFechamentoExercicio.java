package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoDuplicarConfiguracaoFechamentoExercicio;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 20/11/14
 * Time: 14:38
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Configuração de Evento de Fechamento do Exercício")
public class OCCFechamentoExercicio extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Origem Conta Contábil")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private OrigemContaContabil origemContaContabil;
    @Etiqueta("Abertura e Fechamento de Exercicio")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AberturaFechamentoExercicio aberturaFechamentoExercicio;
    @Etiqueta("Tipo")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoDuplicarConfiguracaoFechamentoExercicio tipo;

    public OCCFechamentoExercicio() {
    }

    public OCCFechamentoExercicio(OrigemContaContabil origemContaContabil, AberturaFechamentoExercicio aberturaFechamentoExercicio, TipoDuplicarConfiguracaoFechamentoExercicio tipo) {
        this.origemContaContabil = origemContaContabil;
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
        this.tipo = tipo;
    }

    public TipoDuplicarConfiguracaoFechamentoExercicio getTipo() {
        return tipo;
    }

    public void setTipo(TipoDuplicarConfiguracaoFechamentoExercicio tipo) {
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrigemContaContabil getOrigemContaContabil() {
        return origemContaContabil;
    }

    public void setOrigemContaContabil(OrigemContaContabil origemContaContabil) {
        this.origemContaContabil = origemContaContabil;
    }

    public AberturaFechamentoExercicio getAberturaFechamentoExercicio() {
        return aberturaFechamentoExercicio;
    }

    public void setAberturaFechamentoExercicio(AberturaFechamentoExercicio aberturaFechamentoExercicio) {
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
    }
}
