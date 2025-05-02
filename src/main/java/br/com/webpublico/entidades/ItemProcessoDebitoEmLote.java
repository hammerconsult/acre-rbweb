package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoProcessoDebito;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity

@GrupoDiagrama(nome = "Tributario")
@Audited
public class ItemProcessoDebitoEmLote implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    private Long criadoEm;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    private ProcessoDebito processoDebito;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    private ProcessoDebitoEmLote processoDebitoEmLote;

    public ItemProcessoDebitoEmLote(ProcessoDebito processo) {
        criadoEm = System.nanoTime();
        processoDebito = processo;

    }

    public ItemProcessoDebitoEmLote() {
        criadoEm = System.nanoTime();
    }

    public ProcessoDebito getProcessoDebito() {
        return processoDebito;
    }

    public void setProcessoDebito(ProcessoDebito processoDebito) {
        this.processoDebito = processoDebito;
    }

    public ProcessoDebitoEmLote getProcessoDebitoEmLote() {
        return processoDebitoEmLote;
    }

    public void setProcessoDebitoEmLote(ProcessoDebitoEmLote processoDebitoEmLote) {
        this.processoDebitoEmLote = processoDebitoEmLote;
    }
}
