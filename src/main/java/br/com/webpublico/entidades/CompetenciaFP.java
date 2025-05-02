/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.StatusCompetencia;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Competência Para Folha de Pagamento")
public class CompetenciaFP extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Mês")
    @Enumerated(EnumType.ORDINAL)
    //@Pesquisavel - pesquisa feita no CompetenciaFPPesquisaGenericaControlador
    private Mes mes;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Status da Competência")
    private StatusCompetencia statusCompetencia;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo a Folha de Pagamento")
    private TipoFolhaDePagamento tipoFolhaDePagamento;

    public CompetenciaFP() {
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

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public StatusCompetencia getStatusCompetencia() {
        return statusCompetencia;
    }

    public void setStatusCompetencia(StatusCompetencia statusCompetencia) {
        this.statusCompetencia = statusCompetencia;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    @Override
    public String toString() {
        return mes + "/" + exercicio + " - " + statusCompetencia + " - " + tipoFolhaDePagamento.getDescricao();
    }

    public boolean isStatusCompetenciaAberta() {
        return StatusCompetencia.ABERTA.equals(statusCompetencia);
    }

    public boolean isStatusCompetenciaFechada() {
        return StatusCompetencia.FECHADA.equals(statusCompetencia);
    }

    public boolean isStatusCompetenciaEfetivada() {
        return StatusCompetencia.EFETIVADA.equals(statusCompetencia);
    }
}
