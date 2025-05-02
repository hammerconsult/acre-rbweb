package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoReajusteAposentadoria;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Regra de Aposentadoria")
public class RegraAposentadoria extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Descrição")
    private String descricao;
    @Etiqueta("Tipo Aposentadoria")
    @ManyToOne
    private TipoAposentadoria tipoAposentadoria;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Reajuste Aposentadoria")
    private TipoReajusteAposentadoria tipoReajusteAposentadoria;
    private Boolean decisaoJudicial = false;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoAposentadoria getTipoAposentadoria() {
        return tipoAposentadoria;
    }

    public void setTipoAposentadoria(TipoAposentadoria tipoAposentadoria) {
        this.tipoAposentadoria = tipoAposentadoria;
    }

    public TipoReajusteAposentadoria getTipoReajusteAposentadoria() {
        return tipoReajusteAposentadoria;
    }

    public void setTipoReajusteAposentadoria(TipoReajusteAposentadoria tipoReajusteAposentadoria) {
        this.tipoReajusteAposentadoria = tipoReajusteAposentadoria;
    }

    public Boolean getDecisaoJudicial() {
        return decisaoJudicial != null && decisaoJudicial;
    }

    public void setDecisaoJudicial(Boolean decisaoJudicial) {
        this.decisaoJudicial = decisaoJudicial;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
