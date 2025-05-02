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
 * Date: 08/05/14
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Efetivação da Cessão")
public class EfetivacaoCessao extends EventoBem {

    @OneToOne
    @Etiqueta("Cessão")
    private Cessao cessao;

    @Etiqueta("Efetivação")
    @ManyToOne
    private LoteEfetivacaoCessao loteEfetivacaoCessao;

    public EfetivacaoCessao() {
        super(TipoEventoBem.EFETIVACAOCESSAO, TipoOperacao.NENHUMA_OPERACAO);
    }

    public Cessao getCessao() {
        return cessao;
    }

    public void setCessao(Cessao cessao) {
        this.cessao = cessao;
    }

    public LoteEfetivacaoCessao getLoteEfetivacaoCessao() {
        return loteEfetivacaoCessao;
    }

    public void setLoteEfetivacaoCessao(LoteEfetivacaoCessao loteEfetivacaoCessao) {
        this.loteEfetivacaoCessao = loteEfetivacaoCessao;
    }
}
