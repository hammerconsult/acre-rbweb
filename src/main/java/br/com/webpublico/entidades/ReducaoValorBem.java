package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.enums.TipoReducaoValorBem;
import br.com.webpublico.interfaces.RedutorValorBem;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 07/10/14
 * Time: 14:09
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Redução de Valor do Bem")
public class ReducaoValorBem extends EventoBem implements RedutorValorBem {

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Lote de Redução de Valor do Bem")
    private LoteReducaoValorBem loteReducaoValorBem;

    public ReducaoValorBem() {
        super(TipoEventoBem.REDUCAOVALORBEM, TipoOperacao.CREDITO);
    }

    public ReducaoValorBem(Long id, BigDecimal valorDoLancamento) {
        super(TipoEventoBem.REDUCAOVALORBEM, TipoOperacao.CREDITO);
        this.setId(id);
        this.setValorDoLancamento(valorDoLancamento);
    }

    public ReducaoValorBem(TipoEventoBem tipoEventoBem) {
        super(tipoEventoBem, TipoOperacao.CREDITO);
    }

    public ReducaoValorBem(TipoEventoBem tipoEventoBem, LoteReducaoValorBem loteReducaoValorBem, Bem bem, Date dataLancamento) {
        super(tipoEventoBem, TipoOperacao.CREDITO);
        this.loteReducaoValorBem = loteReducaoValorBem;
        this.setDataLancamento(dataLancamento);
        this.setBem(bem);
    }

    public LoteReducaoValorBem getLoteReducaoValorBem() {
        return loteReducaoValorBem;
    }

    public void setLoteReducaoValorBem(LoteReducaoValorBem loteReducaoValorBem) {
        this.loteReducaoValorBem = loteReducaoValorBem;
    }

    @Override
    public Boolean ehEstorno() {
        return false;
    }

    @Override
    public TipoReducaoValorBem getTipoReducaoValorBem() {
        return this.loteReducaoValorBem.getTipoReducao();
    }

    @Override
    public LogReducaoOuEstorno getLogReducaoOuEstorno() {
        return this.loteReducaoValorBem.getLogReducaoOuEstorno();
    }

    @Override
    public String getMensagemSucesso() {
        return "O processo de " + getTipoReducaoValorBem().getDescricao() + " foi realizado com sucesso ";
    }

    @Override
    public BigDecimal getValorDoLancamento() {
        return valorDoLancamento;
    }

    @Override
    public String getMensagemErro() {
        return "Não foi possível realizar o processo de " + getTipoReducaoValorBem().getDescricao() + ". ";
    }
}
