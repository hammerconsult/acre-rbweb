package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Desenvolvimento on 30/08/2017.
 */

@Entity
@Audited
@Table(name = "ESTORTRANSFDEPRECREC")
public class EstornoTransferenciaDepreciacaoRecebida extends EventoBem{

    public EstornoTransferenciaDepreciacaoRecebida() {
        super(TipoEventoBem.ESTORNOTRANSFERENCIADEPRECIACAORECEBIDA, TipoOperacao.DEBITO);
    }

    public EstornoTransferenciaDepreciacaoRecebida(EstadoBem ultimoEstado, EstadoBem novoEstado, Bem bem) {
        super(TipoEventoBem.ESTORNOTRANSFERENCIADEPRECIACAORECEBIDA, TipoOperacao.DEBITO);
        this.setEstadoInicial(ultimoEstado);
        this.setEstadoResultante(novoEstado);
        this.setBem(bem);
        this.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        this.setValorDoLancamento(ultimoEstado.getValorAcumuladoDaDepreciacao());
    }
}
