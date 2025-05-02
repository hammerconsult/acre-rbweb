package br.com.webpublico.entidades;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class CdaProcessoDeProtesto extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoDeProtesto processoDeProtesto;
    @ManyToOne
    private CertidaoDividaAtiva cda;

    @Transient
    private ResultadoParcela resultadoParcela;

    public CdaProcessoDeProtesto() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoDeProtesto getProcessoDeProtesto() {
        return processoDeProtesto;
    }

    public void setProcessoDeProtesto(ProcessoDeProtesto processoDeProtesto) {
        this.processoDeProtesto = processoDeProtesto;
    }

    public CertidaoDividaAtiva getCda() {
        return cda;
    }

    public void setCda(CertidaoDividaAtiva cda) {
        this.cda = cda;
    }

    public ResultadoParcela getResultadoParcela() {
        return resultadoParcela;
    }

    public void setResultadoParcela(ResultadoParcela resultadoParcela) {
        this.resultadoParcela = resultadoParcela;
    }

    public Long getCriadoEm() {
        return this.criadoEm;
    }

}
