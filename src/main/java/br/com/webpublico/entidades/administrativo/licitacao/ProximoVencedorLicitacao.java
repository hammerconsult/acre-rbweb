package br.com.webpublico.entidades.administrativo.licitacao;

import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.PropostaFornecedor;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Próximo Vencedor da Licitação")
public class ProximoVencedorLicitacao extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    private Long numero;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data de Lançametno")
    private Date dataLancamento;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Licitação")
    private Licitacao licitacao;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Vencedor Atual")
    private PropostaFornecedor vencedorAtual;

    @Invisivel
    @Etiqueta("Itens Vencidos")
    @OneToMany(mappedBy = "proximoVencedorLicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProximoVencedorLicitacaoItem> itens;

    public ProximoVencedorLicitacao() {
        itens = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public List<ProximoVencedorLicitacaoItem> getItens() {
        return itens;
    }

    public void setItens(List<ProximoVencedorLicitacaoItem> itensProximoVencedor) {
        this.itens = itensProximoVencedor;
    }

    public PropostaFornecedor getVencedorAtual() {
        return vencedorAtual;
    }

    public void setVencedorAtual(PropostaFornecedor vencedorAtual) {
        this.vencedorAtual = vencedorAtual;
    }
}
