package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author octavio
 */
@GrupoDiagrama(nome = "Fiscalização")
@Etiqueta("Mala Direta do Monitoramento Fiscal")
@Entity
@Audited
@Table(name = "MALADIRETAMONFISCAL")
public class MalaDiretaMonitoramentoFiscal extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ItemMalaDiretaGeral itemMalaDiretaGeral;
    @ManyToOne
    private MonitoramentoFiscal monitoramentoFiscal;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataResposta;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegularizacao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemMalaDiretaGeral getItemMalaDiretaGeral() {
        return itemMalaDiretaGeral;
    }

    public void setItemMalaDiretaGeral(ItemMalaDiretaGeral itemMalaDiretaGeral) {
        this.itemMalaDiretaGeral = itemMalaDiretaGeral;
    }

    public MonitoramentoFiscal getMonitoramentoFiscal() {
        return monitoramentoFiscal;
    }

    public void setMonitoramentoFiscal(MonitoramentoFiscal monitoramentoFiscal) {
        this.monitoramentoFiscal = monitoramentoFiscal;
    }

    public Date getDataResposta() {
        return dataResposta;
    }

    public void setDataResposta(Date dataResposta) {
        this.dataResposta = dataResposta;
    }

    public Date getDataRegularizacao() {
        return dataRegularizacao;
    }

    public void setDataRegularizacao(Date dataRegularizacao) {
        this.dataRegularizacao = dataRegularizacao;
    }

}
