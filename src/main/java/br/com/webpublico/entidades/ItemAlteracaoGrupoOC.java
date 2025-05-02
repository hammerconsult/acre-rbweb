package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.GrupoContaDespesa;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Etiqueta("Alteração do Grupo de Objeto de Compra do Objeto de Compra")
public class ItemAlteracaoGrupoOC extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private ObjetoCompra objetoCompra;
    @ManyToOne
    private GrupoObjetoCompra grupoObjetoCompraOrigem;
    @ManyToOne
    private GrupoObjetoCompra grupoObjetoCompraDestino;
    @ManyToOne
    private AlteracaoGrupoOC alteracaoGrupoOC;
    @Transient
    private GrupoContaDespesa grupoContaDespesaOrigem;
    @Transient
    private GrupoContaDespesa grupoContaDespesaDestino;

    public ItemAlteracaoGrupoOC() {
    }

    public ItemAlteracaoGrupoOC(ObjetoCompra objetoCompra, GrupoObjetoCompra grupoObjetoCompraOrigem, GrupoObjetoCompra grupoObjetoCompraDestino, AlteracaoGrupoOC alteracaoGrupoOC) {
        this.objetoCompra = objetoCompra;
        this.grupoObjetoCompraOrigem = grupoObjetoCompraOrigem;
        this.grupoObjetoCompraDestino = grupoObjetoCompraDestino;
        this.alteracaoGrupoOC = alteracaoGrupoOC;
    }

    public GrupoContaDespesa getGrupoContaDespesaOrigem() {
        return grupoContaDespesaOrigem;
    }

    public void setGrupoContaDespesaOrigem(GrupoContaDespesa grupoContaDespesaOrigem) {
        this.grupoContaDespesaOrigem = grupoContaDespesaOrigem;
    }

    public GrupoContaDespesa getGrupoContaDespesaDestino() {
        return grupoContaDespesaDestino;
    }

    public void setGrupoContaDespesaDestino(GrupoContaDespesa grupoContaDespesaDestino) {
        this.grupoContaDespesaDestino = grupoContaDespesaDestino;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public GrupoObjetoCompra getGrupoObjetoCompraOrigem() {
        return grupoObjetoCompraOrigem;
    }

    public void setGrupoObjetoCompraOrigem(GrupoObjetoCompra grupoObjetoCompraOrigem) {
        this.grupoObjetoCompraOrigem = grupoObjetoCompraOrigem;
    }

    public GrupoObjetoCompra getGrupoObjetoCompraDestino() {
        return grupoObjetoCompraDestino;
    }

    public void setGrupoObjetoCompraDestino(GrupoObjetoCompra grupoObjetoCompraDestino) {
        this.grupoObjetoCompraDestino = grupoObjetoCompraDestino;
    }

    public AlteracaoGrupoOC getAlteracaoGrupoOC() {
        return alteracaoGrupoOC;
    }

    public void setAlteracaoGrupoOC(AlteracaoGrupoOC alteracaoGrupoOC) {
        this.alteracaoGrupoOC = alteracaoGrupoOC;
    }

    @Override
    public String toString() {
        return "Objeto Alterado: " + objetoCompra;
    }
}
