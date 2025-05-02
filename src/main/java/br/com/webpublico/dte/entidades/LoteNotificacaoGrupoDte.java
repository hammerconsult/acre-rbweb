package br.com.webpublico.dte.entidades;

import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class LoteNotificacaoGrupoDte extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private LoteNotificacaoDte lote;

    @ManyToOne
    private GrupoContribuinteDte grupo;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteNotificacaoDte getLote() {
        return lote;
    }

    public void setLote(LoteNotificacaoDte lote) {
        this.lote = lote;
    }

    public GrupoContribuinteDte getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoContribuinteDte grupo) {
        this.grupo = grupo;
    }
}
