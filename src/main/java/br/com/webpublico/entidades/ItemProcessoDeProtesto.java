package br.com.webpublico.entidades;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class ItemProcessoDeProtesto extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoDeProtesto processoDeProtesto;
    @ManyToOne
    private ParcelaValorDivida parcela;
    @ManyToOne
    private CertidaoDividaAtiva cda;
    private String referencia;

    @Transient
    private ResultadoParcela resultadoParcela;

    public ItemProcessoDeProtesto() {
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

    public ParcelaValorDivida getParcela() {
        return parcela;
    }

    public void setParcela(ParcelaValorDivida parcela) {
        this.parcela = parcela;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
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

    public CertidaoDividaAtiva getCda() {
        return cda;
    }

    public void setCda(CertidaoDividaAtiva cda) {
        this.cda = cda;
    }
}
