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
@Table(name = "ITEMSOLTRANSFGRUPOBEM")
@Etiqueta(value = "Item Solicitação de Transferência Grupo Patrimonial")
public class ItemSolicitacaoTransferenciaGrupoBem extends EventoBem {

    @ManyToOne
    @Etiqueta("Solicitação")
    private SolicitacaoTransferenciaGrupoBem solicitacao;

    public ItemSolicitacaoTransferenciaGrupoBem() {
        super(TipoEventoBem.SOLICITACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL, TipoOperacao.NENHUMA_OPERACAO);
    }

    public ItemSolicitacaoTransferenciaGrupoBem(Bem bem, SolicitacaoTransferenciaGrupoBem solicitacao, Date dataLancamento) {
        super(TipoEventoBem.SOLICITACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL, TipoOperacao.NENHUMA_OPERACAO, dataLancamento);
        this.setBem(bem);
        this.setSolicitacao(solicitacao);
        this.setSituacaoEventoBem(SituacaoEventoBem.EM_ELABORACAO);
    }

    public SolicitacaoTransferenciaGrupoBem getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoTransferenciaGrupoBem solicitacao) {
        this.solicitacao = solicitacao;
    }

}
