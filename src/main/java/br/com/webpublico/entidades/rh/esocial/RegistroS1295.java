package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoIndicativoPeriodoESocial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Etiqueta("Registo S1295 - Solicitação de Totalização para Pagamento em Contingência")
public class RegistroS1295 extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Mês")
    private Mes mes;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ano")
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Indicativo de Período de Apuração")
    private TipoIndicativoPeriodoESocial tipoIndicativoPeriodoESocial;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Empregador")
    private Entidade entidade;

    public RegistroS1295() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoIndicativoPeriodoESocial getTipoIndicativoPeriodoESocial() {
        return tipoIndicativoPeriodoESocial;
    }

    public void setTipoIndicativoPeriodoESocial(TipoIndicativoPeriodoESocial tipoIndicativoPeriodoESocial) {
        this.tipoIndicativoPeriodoESocial = tipoIndicativoPeriodoESocial;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }
}
