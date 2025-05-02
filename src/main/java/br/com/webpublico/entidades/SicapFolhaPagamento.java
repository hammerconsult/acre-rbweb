package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Criado por Camila
 * Data: 14/09/2020.
 */
@GrupoDiagrama(nome = "Recursos Humanos")
@Entity
@Audited
@Etiqueta(value = "SicapFolhaPagamento")
public class SicapFolhaPagamento extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Sicap")
    @ManyToOne
    private Sicap sicap;
    @Etiqueta("Folha de Pagamento")
    @ManyToOne
    private FolhaDePagamento folhaDePagamento;

    public SicapFolhaPagamento() {

    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sicap getSicap() {
        return sicap;
    }

    public void setSicap(Sicap sicap) {
        this.sicap = sicap;
    }

    public FolhaDePagamento getFolhaDePagamento() {
        return folhaDePagamento;
    }

    public void setFolhaDePagamento(FolhaDePagamento folhaDePagamento) {
        this.folhaDePagamento = folhaDePagamento;
    }

}
