package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 05/05/14
 * Time: 10:11
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Cessão de Bem")
public class Cessao extends EventoBem{

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Lote de Cessão de Bem")
    @ManyToOne
    private LoteCessao loteCessao;

    @Invisivel
    @Transient
    private String conservacaoBem;

    public Cessao() {
        super(TipoEventoBem.CESSAO, TipoOperacao.NENHUMA_OPERACAO);
    }

    public Cessao(Bem bem, LoteCessao lote, EstadoBem estadoInicial, EstadoBem estadoResultante, Date dataLancamento) {
        super(TipoEventoBem.CESSAO, TipoOperacao.NENHUMA_OPERACAO);
        this.setBem(bem);
        this.setEstadoInicial(estadoInicial);
        this.setEstadoResultante(estadoResultante);
        this.setLoteCessao(lote);
        this.setDataLancamento(dataLancamento);
    }

    public LoteCessao getLoteCessao() {
        return loteCessao;
    }

    public void setLoteCessao(LoteCessao loteCessao) {
        this.loteCessao = loteCessao;
    }

    public String getConservacaoBem() {
        return conservacaoBem;
    }

    public void setConservacaoBem(String conservacaoBem) {
        this.conservacaoBem = conservacaoBem;
    }

    @Override
    public int compareTo(EventoBem o) {
        return this.getBem().getIdentificacao().compareTo(o.getBem().getIdentificacao());
    }
}
