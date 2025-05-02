package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Item Solicitação Alteração de Conservação do Bem")
@Table(name = "ITEMSOLICITACAOALTCONSBEM")
public class ItemSolicitacaoAlteracaoConservacaoBem extends EventoBem{

    @ManyToOne
    private SolicitacaoAlteracaoConservacaoBem solicitacaoAlteracaoConsBem;

    public ItemSolicitacaoAlteracaoConservacaoBem() {
        super(TipoEventoBem.SOLICITACAO_ALTERACAO_CONSERVACAO_BEM, TipoOperacao.NENHUMA_OPERACAO);
    }

    public ItemSolicitacaoAlteracaoConservacaoBem(Bem bem, EstadoBem estadoBem, EstadoBem estadoResultante, SolicitacaoAlteracaoConservacaoBem solicitacao, Date dataLancamento) {
        super(TipoEventoBem.SOLICITACAO_ALTERACAO_CONSERVACAO_BEM, TipoOperacao.NENHUMA_OPERACAO, dataLancamento);
        this.setBem(bem);
        this.setEstadoInicial(estadoBem);
        this.setEstadoResultante(estadoResultante);
        this.setSolicitacaoAlteracaoConsBem(solicitacao);
        this.setSituacaoEventoBem(SituacaoEventoBem.EM_ELABORACAO);
    }

    public SolicitacaoAlteracaoConservacaoBem getSolicitacaoAlteracaoConsBem() {
        return solicitacaoAlteracaoConsBem;
    }

    public void setSolicitacaoAlteracaoConsBem(SolicitacaoAlteracaoConservacaoBem solicitacaoAlteracaoConsBem) {
        this.solicitacaoAlteracaoConsBem = solicitacaoAlteracaoConsBem;
    }
}
