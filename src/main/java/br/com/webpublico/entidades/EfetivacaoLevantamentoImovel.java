package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by Desenvolvimento on 10/11/2015.
 */

@Entity
@Audited
@Etiqueta(value = "Efetivação do Levantamento de Bem Imóvel", genero = "M")
@Table(name = "EFETIVACAOLEVANTAMENTOIMOV")
public class EfetivacaoLevantamentoImovel extends EventoBem {

    @ManyToOne
    @Obrigatorio
    private LevantamentoBemImovel levantamentoBemImovel;

    @ManyToOne
    @Obrigatorio
    private LoteEfetivacaoLevantamentoImovel loteEfetivacaoImovel;

    public EfetivacaoLevantamentoImovel() {
        super(TipoEventoBem.EFETIVACAOLEVANTAMENTOIMOVEL, TipoOperacao.DEBITO);
    }

    public LevantamentoBemImovel getLevantamentoBemImovel() {
        return levantamentoBemImovel;
    }

    public void setLevantamentoBemImovel(LevantamentoBemImovel levantamentoBemImovel) {
        this.levantamentoBemImovel = levantamentoBemImovel;
    }

    public LoteEfetivacaoLevantamentoImovel getLoteEfetivacaoImovel() {
        return loteEfetivacaoImovel;
    }

    public void setLoteEfetivacaoImovel(LoteEfetivacaoLevantamentoImovel loteEfetivacaoImovel) {
        this.loteEfetivacaoImovel = loteEfetivacaoImovel;
    }
}
