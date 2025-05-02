package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.enums.TipoReducaoValorBem;
import br.com.webpublico.interfaces.RedutorValorBem;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 30/10/14
 * Time: 09:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Estorno Redução Valor Bem")
public class EstornoReducaoValorBem extends EventoBem implements RedutorValorBem {

    @ManyToOne
    private LoteEstornoReducaoValorBem loteEstornoReducaoValorBem;
    @OneToOne
    private ReducaoValorBem reducaoValorBem;

    public EstornoReducaoValorBem() {
        super(TipoEventoBem.ESTORNOREDUCAOVALORBEM, TipoOperacao.DEBITO);
    }

    public LoteEstornoReducaoValorBem getLoteEstornoReducaoValorBem() {
        return loteEstornoReducaoValorBem;
    }

    public void setLoteEstornoReducaoValorBem(LoteEstornoReducaoValorBem loteEstornoReducaoValorBem) {
        this.loteEstornoReducaoValorBem = loteEstornoReducaoValorBem;
    }

    public ReducaoValorBem getReducaoValorBem() {
        return reducaoValorBem;
    }

    public void setReducaoValorBem(ReducaoValorBem reducaoValorBem) {
        this.reducaoValorBem = reducaoValorBem;
    }

    @Override
    public Boolean ehEstorno() {
        return true;
    }

    @Override
    public BigDecimal getValorDoLancamento() {
        setValorDoLancamento(this.reducaoValorBem.getValorDoLancamento());
        return this.reducaoValorBem.getValorDoLancamento();
    }

    @Override
    public TipoReducaoValorBem getTipoReducaoValorBem() {
        return this.reducaoValorBem.getTipoReducaoValorBem();
    }

    @Override
    public LogReducaoOuEstorno getLogReducaoOuEstorno() {
        return this.loteEstornoReducaoValorBem.getLogReducaoOuEstorno();
    }

    @Override
    public String getMensagemSucesso() {
        return "O estorno do processo de " + getTipoReducaoValorBem().getDescricao() + " foi realizado com sucesso para o bem " + getBem();
    }

    @Override
    public String getMensagemErro() {
        return "Não foi possível realizar o estorno do processo de " + getTipoReducaoValorBem().getDescricao() + " para o bem " + getBem();
    }
}
