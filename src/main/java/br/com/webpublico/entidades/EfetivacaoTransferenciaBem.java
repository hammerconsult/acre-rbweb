package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 26/12/13
 * Time: 14:35
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta(value = "Efetivação da Transferência de Bem", genero = "M")
public class EfetivacaoTransferenciaBem extends EventoBem {

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @OneToOne
    @Etiqueta("Transferência")
    private TransferenciaBem transferenciaBem;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Efetivação de Transferência")
    private LoteEfetivacaoTransferenciaBem lote;

    public EfetivacaoTransferenciaBem() {
        super(TipoEventoBem.EFETIVACAOTRANSFERENCIABEM, TipoOperacao.DEBITO);
    }

    public EfetivacaoTransferenciaBem(Long id) {
        super(TipoEventoBem.EFETIVACAOTRANSFERENCIABEM, TipoOperacao.DEBITO);
        this.setId(id);
    }

    public TransferenciaBem getTransferenciaBem() {
        return transferenciaBem;
    }

    public void setTransferenciaBem(TransferenciaBem transferenciaBem) {
        this.transferenciaBem = transferenciaBem;
    }

    public LoteEfetivacaoTransferenciaBem getLote() {
        return lote;
    }

    public void setLote(LoteEfetivacaoTransferenciaBem lote) {
        this.lote = lote;
    }
}
