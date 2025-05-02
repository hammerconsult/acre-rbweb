package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author octavio
 */
@GrupoDiagrama(nome = "Fiscalização")
@Etiqueta(value = "Fiscal de Monitoramento")
@Entity
@Audited
public class FiscalMonitoramento extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta(value = "Monitoramento Fiscal")
    @ManyToOne
    private MonitoramentoFiscal monitoramentoFiscal;
    @Etiqueta(value = "Auditores Fiscais")
    @ManyToOne
    private UsuarioSistema auditorFiscal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MonitoramentoFiscal getMonitoramentoFiscal() {
        return monitoramentoFiscal;
    }

    public void setMonitoramentoFiscal(MonitoramentoFiscal monitoramentoFiscal) {
        this.monitoramentoFiscal = monitoramentoFiscal;
    }

    public UsuarioSistema getAuditorFiscal() {
        return auditorFiscal;
    }

    public void setAuditorFiscal(UsuarioSistema auditorFiscal) {
        this.auditorFiscal = auditorFiscal;
    }

    @Override
    public String toString() {
        if (auditorFiscal.getPessoaFisica() != null) {
            if (auditorFiscal.getPessoaFisica().getNome() != null) {
                return auditorFiscal.getPessoaFisica().getNome();
            } else {
                return auditorFiscal.getLogin();
            }

        }
        return auditorFiscal.getLogin();
    }
}
