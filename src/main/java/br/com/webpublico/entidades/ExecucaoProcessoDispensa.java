package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity

@Audited
@GrupoDiagrama(nome = "Licitações")
@Etiqueta("Execução Processo Ata")
public class ExecucaoProcessoDispensa extends SuperEntidade {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @Etiqueta("Execução Processo")
    private ExecucaoProcesso execucaoProcesso;

    @ManyToOne
    @Etiqueta("Dispensa de Licitação")
    private DispensaDeLicitacao dispensaLicitacao;

    public ExecucaoProcessoDispensa() {
    }

    public ExecucaoProcessoDispensa(ExecucaoProcesso execucaoProcesso) {
        this.execucaoProcesso = execucaoProcesso;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExecucaoProcesso getExecucaoProcessoCompra() {
        return execucaoProcesso;
    }

    public void setExecucaoProcessoCompra(ExecucaoProcesso execucaoProcesso) {
        this.execucaoProcesso = execucaoProcesso;
    }

    public DispensaDeLicitacao getDispensaLicitacao() {
        return dispensaLicitacao;
    }

    public void setDispensaLicitacao(DispensaDeLicitacao dispensaLicitacao) {
        this.dispensaLicitacao = dispensaLicitacao;
    }
}
