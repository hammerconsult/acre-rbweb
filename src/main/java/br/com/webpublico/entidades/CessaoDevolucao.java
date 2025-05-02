package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 29/01/15
 * Time: 16:43
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Devolução de Bem Cedido")
public class CessaoDevolucao extends EventoBem {

    @OneToOne
    @Etiqueta("Cessão")
    private Cessao cessao;

    @Etiqueta("Conservação do Bem")
    private String conservacaoBem;

    @ManyToOne
    @Etiqueta("Devolução da Cessão de Bens Móveis")
    private LoteCessaoDevolucao loteCessaoDevolucao;

    public CessaoDevolucao() {
        super(TipoEventoBem.CESSAODEVOLUCAO, TipoOperacao.NENHUMA_OPERACAO);
    }

    public LoteCessaoDevolucao getLoteCessaoDevolucao() {
        return loteCessaoDevolucao;
    }

    public void setLoteCessaoDevolucao(LoteCessaoDevolucao loteCessaoDevolucao) {
        this.loteCessaoDevolucao = loteCessaoDevolucao;
    }

    public Cessao getCessao() {
        return cessao;
    }

    public void setCessao(Cessao cessao) {
        this.cessao = cessao;
    }

    public String getConservacaoBem() {
        return conservacaoBem;
    }

    public void setConservacaoBem(String conservacaoBem) {
        this.conservacaoBem = conservacaoBem;
    }
}
