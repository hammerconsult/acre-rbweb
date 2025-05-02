package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@GrupoDiagrama(nome = "Contábil")
@Audited
@Entity
@Etiqueta("Configuração de Alienação de Ativos")
public class ConfigAlienacaoAtivos extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;

    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Evento Contábil")
    private EventoContabil evento;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Grupo")
    private TipoGrupo tipoGrupo;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Grupo Patrimonial")
    private GrupoBem grupo;

    public ConfigAlienacaoAtivos() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public EventoContabil getEvento() {
        return evento;
    }

    public void setEvento(EventoContabil evento) {
        this.evento = evento;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public GrupoBem getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoBem grupo) {
        this.grupo = grupo;
    }
}
