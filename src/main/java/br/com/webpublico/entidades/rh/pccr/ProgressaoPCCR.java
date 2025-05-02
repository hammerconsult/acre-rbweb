package br.com.webpublico.entidades.rh.pccr;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 20/06/14
 * Time: 17:49
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Etiqueta("Progressão PCCR")
@Audited
public class ProgressaoPCCR implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @Tabelavel
    @ManyToOne
    private PlanoCargosSalariosPCCR planoCargosSalarios;
    @OneToMany(mappedBy = "progressaoPCCR", cascade = CascadeType.ALL)
    private List<ValorProgressaoPCCR> valorProgressaoPCCRs;
    @Transient
    private Long criadoEm = System.nanoTime();

    public Long getCriadoEm() {
        return criadoEm;
    }

    public ProgressaoPCCR() {
        valorProgressaoPCCRs = new LinkedList<>();
    }

    public List<ValorProgressaoPCCR> getValorProgressaoPCCRs() {
        return valorProgressaoPCCRs;
    }

    public List<ValorProgressaoPCCR> getValorProgressaoPCCRsVigentes() {
        List<ValorProgressaoPCCR> valores = new LinkedList<>();
        DateTime data = new DateTime(UtilRH.getDataOperacao());
        for (ValorProgressaoPCCR valor : valorProgressaoPCCRs) {
            DateTime inicioVigencia = new DateTime(valor.getInicioVigencia());
            DateTime finalVigencia = valor.getFinalVigencia() != null ? new DateTime(valor.getFinalVigencia()) : null;
            if (finalVigencia == null) {
                valores.add(valor);
            } else {
                if (data.isAfter(inicioVigencia) && data.isBefore(finalVigencia)) {
                    valores.add(valor);
                }
            }
        }
        return valores;
    }

    public void setValorProgressaoPCCRs(List<ValorProgressaoPCCR> valorProgressaoPCCRs) {
        this.valorProgressaoPCCRs = valorProgressaoPCCRs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public PlanoCargosSalariosPCCR getPlanoCargosSalarios() {
        return planoCargosSalarios;
    }

    public void setPlanoCargosSalarios(PlanoCargosSalariosPCCR planoCargosSalarios) {
        this.planoCargosSalarios = planoCargosSalarios;
    }

    @Override
    public String toString() {
        return descricao;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
