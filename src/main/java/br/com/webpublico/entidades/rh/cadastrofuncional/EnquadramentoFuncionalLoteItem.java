package br.com.webpublico.entidades.rh.cadastrofuncional;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.EnquadramentoFuncional;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by peixe on 07/02/18.
 */
@Entity
@Etiqueta("Enquadramento Funcional Por Lote")
@GrupoDiagrama(nome = "RecursosHumanos")
public class EnquadramentoFuncionalLoteItem implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EnquadramentoFuncionalLote enquadramentoFuncionalLote;
    @ManyToOne
    private ContratoFP contratoFP;
    @ManyToOne
    private EnquadramentoFuncional enquadramentoFuncAntigo;
    @ManyToOne
    private EnquadramentoFuncional enquadramentoFuncNovo;

    public EnquadramentoFuncionalLoteItem() {
    }

    public EnquadramentoFuncionalLoteItem(EnquadramentoFuncionalLote enquadramentoFuncionalLote, ContratoFP contratoFP, EnquadramentoFuncional enquadramentoFuncAntigo, EnquadramentoFuncional enquadramentoFuncNovo) {
        this.enquadramentoFuncionalLote = enquadramentoFuncionalLote;
        this.contratoFP = contratoFP;
        this.enquadramentoFuncAntigo = enquadramentoFuncAntigo;
        this.enquadramentoFuncNovo = enquadramentoFuncNovo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnquadramentoFuncionalLote getEnquadramentoFuncionalLote() {
        return enquadramentoFuncionalLote;
    }

    public void setEnquadramentoFuncionalLote(EnquadramentoFuncionalLote enquadramentoFuncionalLote) {
        this.enquadramentoFuncionalLote = enquadramentoFuncionalLote;
    }

    public EnquadramentoFuncional getEnquadramentoFuncAntigo() {
        return enquadramentoFuncAntigo;
    }

    public void setEnquadramentoFuncAntigo(EnquadramentoFuncional enquadramentoFuncAntigo) {
        this.enquadramentoFuncAntigo = enquadramentoFuncAntigo;
    }

    public EnquadramentoFuncional getEnquadramentoFuncNovo() {
        return enquadramentoFuncNovo;
    }

    public void setEnquadramentoFuncNovo(EnquadramentoFuncional enquadramentoFuncNovo) {
        this.enquadramentoFuncNovo = enquadramentoFuncNovo;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }
}
