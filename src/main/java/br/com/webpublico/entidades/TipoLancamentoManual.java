package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPrestacaoDeContas;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Etiqueta("Tipo de Prestação de Contas do Lançamento Contábil Manual")
public class TipoLancamentoManual extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private LancamentoContabilManual lancamento;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private TipoPrestacaoDeContas tipoPrestacaoDeContas;

    public TipoLancamentoManual() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LancamentoContabilManual getLancamento() {
        return lancamento;
    }

    public void setLancamento(LancamentoContabilManual lancamento) {
        this.lancamento = lancamento;
    }

    public TipoPrestacaoDeContas getTipoPrestacaoDeContas() {
        return tipoPrestacaoDeContas;
    }

    public void setTipoPrestacaoDeContas(TipoPrestacaoDeContas tipoPrestacaoDeContas) {
        this.tipoPrestacaoDeContas = tipoPrestacaoDeContas;
    }
}
