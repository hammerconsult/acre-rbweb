package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Audited
@Entity
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Solicitação Estorno Entrada Compra")
@Table(name = "SOLICITACAOESTORNOENTCOMP")
public class SolicitacaoEstornoEntradaMaterial extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private EntradaCompraMaterial entradaCompraMaterial;

    @ManyToOne(cascade = CascadeType.ALL)
    private SolicitacaoLiquidacaoEstorno solicitacaoLiquidacaoEst;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntradaCompraMaterial getEntradaCompraMaterial() {
        return entradaCompraMaterial;
    }

    public void setEntradaCompraMaterial(EntradaCompraMaterial entradaCompraMaterial) {
        this.entradaCompraMaterial = entradaCompraMaterial;
    }

    public SolicitacaoLiquidacaoEstorno getSolicitacaoLiquidacaoEst() {
        return solicitacaoLiquidacaoEst;
    }

    public void setSolicitacaoLiquidacaoEst(SolicitacaoLiquidacaoEstorno solicitacaoLiquidacaoEst) {
        this.solicitacaoLiquidacaoEst = solicitacaoLiquidacaoEst;
    }
}
