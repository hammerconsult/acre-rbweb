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
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Configuração de Evento de Fechamento do Exercício")
@Table(name = "CONFIGFECHAMENTOEXERCICIO")
public class ConfiguracaoEventoFechamentoExercicio extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
//    @Etiqueta("Configuração de Evento Contábil")
//    @Tabelavel
//    @Pesquisavel
//    @ManyToOne
//    private ConfiguracaoEvento configuracaoEvento;
    @Etiqueta("Evento Contábil")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private EventoContabil eventoContabil;
    @Etiqueta("Abertura e Fechamento de Exercicio")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AberturaFechamentoExercicio aberturaFechamentoExercicio;
    @Etiqueta("Tipo")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoDuplicarConfiguracaoFechamentoExercicio tipo;

    public ConfiguracaoEventoFechamentoExercicio() {
    }

    public ConfiguracaoEventoFechamentoExercicio(ConfiguracaoEvento configuracaoEvento, EventoContabil eventoContabil, AberturaFechamentoExercicio aberturaFechamentoExercicio, TipoDuplicarConfiguracaoFechamentoExercicio tipo) {
//        this.configuracaoEvento = configuracaoEvento;
        this.eventoContabil = eventoContabil;
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public ConfiguracaoEvento getConfiguracaoEvento() {
//        return configuracaoEvento;
//    }
//
//    public void setConfiguracaoEvento(ConfiguracaoEvento configuracaoEvento) {
//        this.configuracaoEvento = configuracaoEvento;
//    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public AberturaFechamentoExercicio getAberturaFechamentoExercicio() {
        return aberturaFechamentoExercicio;
    }

    public void setAberturaFechamentoExercicio(AberturaFechamentoExercicio aberturaFechamentoExercicio) {
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
    }

    public TipoDuplicarConfiguracaoFechamentoExercicio getTipo() {
        return tipo;
    }

    public void setTipo(TipoDuplicarConfiguracaoFechamentoExercicio tipo) {
        this.tipo = tipo;
    }
}
