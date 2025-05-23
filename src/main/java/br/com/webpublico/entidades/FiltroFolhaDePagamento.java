package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Folha de Pagamento")
public class FiltroFolhaDePagamento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private FolhaDePagamento folhaDePagamento;
    @Temporal(TemporalType.DATE)
    private Date calculadaEm;
    private String filtroJSON;

    public FiltroFolhaDePagamento() {
    }

    public FiltroFolhaDePagamento(FolhaDePagamento folhaDePagamento, Date calculadaEm, String filtroJSON) {
        this.folhaDePagamento = folhaDePagamento;
        this.calculadaEm = calculadaEm;
        this.filtroJSON = filtroJSON;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FolhaDePagamento getFolhaDePagamento() {
        return folhaDePagamento;
    }

    public void setFolhaDePagamento(FolhaDePagamento folhaDePagamento) {
        this.folhaDePagamento = folhaDePagamento;
    }

    public Date getCalculadaEm() {
        return calculadaEm;
    }

    public void setCalculadaEm(Date calculadaEm) {
        this.calculadaEm = calculadaEm;
    }

    public String getFiltroJSON() {
        return filtroJSON;
    }

    public void setFiltroJSON(String filtroJSON) {
        this.filtroJSON = filtroJSON;
    }
}
