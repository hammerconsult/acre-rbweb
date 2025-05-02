package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@GrupoDiagrama(nome = "Tributario")
@Audited
@Etiqueta("Processo de Revisão de Dívida Ativa - Exercício Débito")
public class ProcessoRevisaoDividaAtivaExercicio extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoRevisaoDividaAtiva processoRevisaoDividaAtiva;
    @ManyToOne
    private Exercicio exercicio;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoRevisaoDividaAtiva getProcessoRevisaoDividaAtiva() {
        return processoRevisaoDividaAtiva;
    }

    public void setProcessoRevisaoDividaAtiva(ProcessoRevisaoDividaAtiva processoRevisaoDividaAtiva) {
        this.processoRevisaoDividaAtiva = processoRevisaoDividaAtiva;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }
}
