package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Solicitação de Compra Dotação")
public class SolicitacaoCompraDotacao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Solicitação de Compra")
    private SolicitacaoMaterial solicitacaoCompra;

    @ManyToOne
    @Etiqueta("Fonte Despesa Orc")
    private FonteDespesaORC fonteDespesaORC;

    @OneToMany(mappedBy = "solicitacaoCompraDotacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolicitacaoCompraDotacaoItem> itens;

    public SolicitacaoCompraDotacao() {
        itens = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoMaterial getSolicitacaoCompra() {
        return solicitacaoCompra;
    }

    public void setSolicitacaoCompra(SolicitacaoMaterial solicitacaoCompra) {
        this.solicitacaoCompra = solicitacaoCompra;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public List<SolicitacaoCompraDotacaoItem> getItens() {
        return itens;
    }

    public void setItens(List<SolicitacaoCompraDotacaoItem> itens) {
        this.itens = itens;
    }
}
