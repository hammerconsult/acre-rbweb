package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Audited
@Table(name = "ITEMEFETIVTRANSFGRUPOBEM")
@Etiqueta(value = "Item Efetivação de Transferência Grupo Patrimonial")
public class ItemEfetivacaoTransferenciaGrupoBem extends EventoBem {

    @ManyToOne
    @Etiqueta("Efetivação")
    private EfetivacaoTransferenciaGrupoBem efetivacao;

    public ItemEfetivacaoTransferenciaGrupoBem() {
        super(TipoEventoBem.TRANSFERENCIA_GRUPO_PATRIMONIAL_ORIGINAL_CONCEDIDA, TipoOperacao.NENHUMA_OPERACAO);
    }

    public ItemEfetivacaoTransferenciaGrupoBem(TipoEventoBem tipoEventoBem, TipoOperacao tipoOperacao, EfetivacaoTransferenciaGrupoBem efetivacao) {
        super(tipoEventoBem, tipoOperacao);
        this.setEfetivacao(efetivacao);
    }

    public EfetivacaoTransferenciaGrupoBem getEfetivacao() {
        return efetivacao;
    }

    public void setEfetivacao(EfetivacaoTransferenciaGrupoBem efetivacao) {
        this.efetivacao = efetivacao;
    }
}
