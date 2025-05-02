package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 09/10/13
 * Time: 17:58
 * To change this template use File | Settings | File Templates.
 */

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
public class ArquivoEconsigItens implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String linha;
    @ManyToOne
    private LancamentoFP lancamentoFP;
    @ManyToOne
    private ArquivoEconsig arquivoEconsig;

    public ArquivoEconsigItens() {
    }

    public ArquivoEconsigItens(String linha, LancamentoFP lancamentoFP, ArquivoEconsig arquivoEconsig) {
        this.linha = linha;
        this.lancamentoFP = lancamentoFP;
        this.arquivoEconsig = arquivoEconsig;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public LancamentoFP getLancamentoFP() {
        return lancamentoFP;
    }

    public void setLancamentoFP(LancamentoFP lancamentoFP) {
        this.lancamentoFP = lancamentoFP;
    }

    public ArquivoEconsig getArquivoEconsig() {
        return arquivoEconsig;
    }

    public void setArquivoEconsig(ArquivoEconsig arquivoEconsig) {
        this.arquivoEconsig = arquivoEconsig;
    }
}
