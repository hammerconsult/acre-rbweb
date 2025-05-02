package br.com.webpublico.entidades.tributario.procuradoria;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.ProcessoJudicial;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.tributario.procuradoria.PapelProcesso;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by tributario on 19/08/2015.
 */
@Audited
@Entity
@GrupoDiagrama(nome = "Procuradoria")
public class PessoaProcesso extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoJudicial processoJudicial;
    @ManyToOne
    private Pessoa pessoa;
    @Etiqueta("Papel no Processo")
    @Enumerated(EnumType.STRING)
    private PapelProcesso papelProcesso;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoJudicial getProcessoJudicial() {
        return processoJudicial;
    }

    public void setProcessoJudicial(ProcessoJudicial processoJudicial) {
        this.processoJudicial = processoJudicial;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public PapelProcesso getPapelProcesso() {
        return papelProcesso;
    }

    public void setPapelProcesso(PapelProcesso papelProcesso) {
        this.papelProcesso = papelProcesso;
    }
}
