package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Licitações")
public class ExecucaoProcessoFonte extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Execução Processo Reserva")
    private ExecucaoProcessoReserva execucaoProcessoReserva;

    @ManyToOne
    @Etiqueta("Fonte de Despesa")
    private FonteDespesaORC fonteDespesaORC;

    @Etiqueta("Valor (R$)")
    private BigDecimal valor;

    @Etiqueta("Itens")
    @OneToMany(mappedBy = "execucaoProcessoFonte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExecucaoProcessoFonteItem> itens;

    private Boolean geraReserva;

    public ExecucaoProcessoFonte() {
        valor = BigDecimal.ZERO;
        geraReserva = Boolean.FALSE;
        itens = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExecucaoProcessoReserva getExecucaoProcessoReserva() {
        return execucaoProcessoReserva;
    }

    public void setExecucaoProcessoReserva(ExecucaoProcessoReserva execucaoProcessoReserva) {
        this.execucaoProcessoReserva = execucaoProcessoReserva;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<ExecucaoProcessoFonteItem> getItens() {
        return itens;
    }

    public void setItens(List<ExecucaoProcessoFonteItem> itens) {
        this.itens = itens;
    }

    public Boolean getGeraReserva() {
        return geraReserva;
    }

    public void setGeraReserva(Boolean geraReserva) {
        this.geraReserva = geraReserva;
    }
}
