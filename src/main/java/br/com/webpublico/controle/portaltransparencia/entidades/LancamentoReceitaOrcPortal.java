package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.LancamentoReceitaOrc;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 13/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class LancamentoReceitaOrcPortal extends EntidadePortalTransparencia {

    @ManyToOne
    private LancamentoReceitaOrc lancamentoReceitaOrc;

    public LancamentoReceitaOrcPortal() {
    }

    public LancamentoReceitaOrcPortal(LancamentoReceitaOrc lancamentoReceitaOrc) {
        this.lancamentoReceitaOrc = lancamentoReceitaOrc;
        super.setTipo(TipoObjetoPortalTransparencia.RECEITA_REALIZADA);
    }

    public LancamentoReceitaOrc getLancamentoReceitaOrc() {
        return lancamentoReceitaOrc;
    }

    public void setLancamentoReceitaOrc(LancamentoReceitaOrc lancamentoReceitaOrc) {
        this.lancamentoReceitaOrc = lancamentoReceitaOrc;
    }

    @Override
    public String toString() {
        return lancamentoReceitaOrc.toString();
    }
}
