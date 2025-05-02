package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 29/10/14
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Alteração de Conservação do Bem")
public class AlteracaoConservacaoBem extends EventoBem {

    @ManyToOne
    private EfetivacaoAlteracaoConservacaoBem efetivacaoAlteracaoConsBem;

    public AlteracaoConservacaoBem() {
        super(TipoEventoBem.EFETIVACAO_ALTERACAO_CONSERVACAO_BEM, TipoOperacao.NENHUMA_OPERACAO, new Date());
    }

    public AlteracaoConservacaoBem(Bem bem, EstadoBem estadoBem, EfetivacaoAlteracaoConservacaoBem efetivacao, Date dataLancamento) {
        super(TipoEventoBem.EFETIVACAO_ALTERACAO_CONSERVACAO_BEM, TipoOperacao.NENHUMA_OPERACAO, dataLancamento);
        this.setBem(bem);
        this.setEstadoInicial(estadoBem);
        this.setEfetivacaoAlteracaoConsBem(efetivacao);
    }

    public EfetivacaoAlteracaoConservacaoBem getEfetivacaoAlteracaoConsBem() {
        return efetivacaoAlteracaoConsBem;
    }

    public void setEfetivacaoAlteracaoConsBem(EfetivacaoAlteracaoConservacaoBem efetivacaoAlteracaoConservacaoBem) {
        this.efetivacaoAlteracaoConsBem = efetivacaoAlteracaoConservacaoBem;
    }
}
