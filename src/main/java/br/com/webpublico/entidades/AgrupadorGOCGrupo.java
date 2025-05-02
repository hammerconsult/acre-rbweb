package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Agrupador Grupo Objeto de Compra - Grupos")
public class AgrupadorGOCGrupo extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @ManyToOne
    private AgrupadorGOC agrupadorGOC;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Grupo Objeto de Compra")
    private GrupoObjetoCompra grupoObjetoCompra;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public AgrupadorGOC getAgrupadorGOC() {
        return agrupadorGOC;
    }

    public void setAgrupadorGOC(AgrupadorGOC agrupadorGOC) {
        this.agrupadorGOC = agrupadorGOC;
    }
}
