package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoLevantamentoContabilMonitoramentoFiscal;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.*;

/**
 * @author octavio
 */
@GrupoDiagrama(nome = "Fiscalização")
@Etiqueta("Registro de Lançamento Contábil do Monitoramento Fiscal")
@Table(name = "REGISTROCONTABILMONFISCAL")
@Entity
@Audited
public class RegistroLancamentoContabilMonitoramentoFiscal extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "registroLancContabilMF", orphanRemoval = true)
    private List<LancamentoFiscalMonitoramentoFiscal> lancamentosFiscais;
    @ManyToOne
    private MonitoramentoFiscal monitoramentoFiscal;
    private Long numeroRegistro;
    private Integer ano;
    @Enumerated(EnumType.STRING)
    private TipoLevantamentoContabilMonitoramentoFiscal tipo;
    @Transient
    private Map<Integer, List<LancamentoFiscalMonitoramentoFiscal>> lancamentosPorAno;

    public RegistroLancamentoContabilMonitoramentoFiscal() {
        this.lancamentosFiscais = Lists.newArrayList();
        this.ano = Calendar.getInstance().get(Calendar.YEAR);
        this.numeroRegistro = 1L;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<LancamentoFiscalMonitoramentoFiscal> getLancamentosFiscais() {
        return lancamentosFiscais;
    }

    public void setLancamentosFiscais(List<LancamentoFiscalMonitoramentoFiscal> lancamentosFiscais) {
        this.lancamentosFiscais = lancamentosFiscais;
    }

    public MonitoramentoFiscal getMonitoramentoFiscal() {
        return monitoramentoFiscal;
    }

    public void setMonitoramentoFiscal(MonitoramentoFiscal monitoramentoFiscal) {
        this.monitoramentoFiscal = monitoramentoFiscal;
    }

    public Long getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(Long numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Map<Integer, List<LancamentoFiscalMonitoramentoFiscal>> getLancamentosPorAno() {
        if (lancamentosPorAno == null || lancamentosPorAno.isEmpty()) {
            lancamentosPorAno = new TreeMap<>();
            Collections.sort(lancamentosFiscais);
            for (LancamentoFiscalMonitoramentoFiscal lancamento : lancamentosFiscais) {
                if (lancamentosPorAno.containsKey(lancamento.getAno())) {
                    lancamentosPorAno.get(lancamento.getAno()).add(lancamento);
                } else {
                    lancamentosPorAno.put(lancamento.getAno(), new ArrayList<LancamentoFiscalMonitoramentoFiscal>());
                    lancamentosPorAno.get(lancamento.getAno()).add(lancamento);
                }
            }
        }
        return lancamentosPorAno;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return String.format("%d/%d", numeroRegistro, ano);
    }

    public String getNumeroAno() {
        return String.format("%d/%d", numeroRegistro, ano);
    }

    public TipoLevantamentoContabilMonitoramentoFiscal getTipo() {
        return tipo;
    }

    public void setTipo(TipoLevantamentoContabilMonitoramentoFiscal tipo) {
        this.tipo = tipo;
    }
}
