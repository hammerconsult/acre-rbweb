package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by Desenvolvimento on 24/09/2015.
 */

@Entity
@Audited
@Table(name = "ESTORNOEFETIVACAOLEVMOVEL")
public class EstornoEfetivacaoLevantamentoMovel extends EventoBem {

    @OneToOne
    private EfetivacaoLevantamentoBem efetivacaoLevantamentoBem;

    public EstornoEfetivacaoLevantamentoMovel() {
        super(TipoEventoBem.ESTORNOEFETIVACAOLEVMOVEL, TipoOperacao.CREDITO);
        this.setSituacaoEventoBem(SituacaoEventoBem.ESTORNADO);
    }

    public EfetivacaoLevantamentoBem getEfetivacaoLevantamentoBem() {
        return efetivacaoLevantamentoBem;
    }

    public void setEfetivacaoLevantamentoBem(EfetivacaoLevantamentoBem efetivacaoLevantamentoBem) {
        if (efetivacaoLevantamentoBem != null) {
            this.setValorDoLancamento(efetivacaoLevantamentoBem.getValorDoLancamento());
        }
        this.efetivacaoLevantamentoBem = efetivacaoLevantamentoBem;
    }
}
