package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 02/07/15
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
public class ProcessoRevisao extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoRevisaoCalculoIPTU processoRevisaoCalculoIPTU;
    @ManyToOne
    private RevisaoCalculoIPTU revisaoCalculoIPTU;

    public Long getId() {
        return id;
    }

    public ProcessoRevisaoCalculoIPTU getProcessoRevisaoCalculoIPTU() {
        return processoRevisaoCalculoIPTU;
    }

    public void setProcessoRevisaoCalculoIPTU(ProcessoRevisaoCalculoIPTU processoRevisaoCalculoIPTU) {
        this.processoRevisaoCalculoIPTU = processoRevisaoCalculoIPTU;
    }

    public RevisaoCalculoIPTU getRevisaoCalculoIPTU() {
        return revisaoCalculoIPTU;
    }

    public void setRevisaoCalculoIPTU(RevisaoCalculoIPTU revisaoCalculoIPTU) {
        this.revisaoCalculoIPTU = revisaoCalculoIPTU;
    }
}
