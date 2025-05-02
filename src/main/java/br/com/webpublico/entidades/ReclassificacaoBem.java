package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 30/01/14
 * Time: 16:16
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Cacheable
@Audited
@Etiqueta(value = "Reclassificação de Grupo", genero = "F")
public class ReclassificacaoBem extends EventoBem {
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Lote de Reclassificação de Grupo")
    @ManyToOne
    private LoteReclassificacaoBem loteReclassificacaoBem;

    public ReclassificacaoBem() {
        super(TipoEventoBem.RECLASSIFICACAOBEM, TipoOperacao.NENHUMA_OPERACAO);
    }

    public ReclassificacaoBem(Bem bem, EstadoBem estadoBem, LoteReclassificacaoBem lote, Date dataLancamento) {
        super(TipoEventoBem.RECLASSIFICACAOBEM, TipoOperacao.NENHUMA_OPERACAO);
        this.setBem(bem);
        this.setEstadoInicial(estadoBem);
        this.setEstadoResultante(estadoBem);
        this.setLoteReclassificacaoBem(lote);
        this.setSituacaoEventoBem(SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
        this.setDataLancamento(dataLancamento);
    }

    public LoteReclassificacaoBem getLoteReclassificacaoBem() {
        return loteReclassificacaoBem;
    }

    public void setLoteReclassificacaoBem(LoteReclassificacaoBem loteReclassificacaoBem) {
        this.loteReclassificacaoBem = loteReclassificacaoBem;
    }
}
