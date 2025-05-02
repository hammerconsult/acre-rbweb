package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.VOAlvara;
import br.com.webpublico.enums.SituacaoProcessoSuspensaoCassacaoAlvara;
import br.com.webpublico.enums.TipoProcessoSuspensaoCassacaoAlvara;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class AlvaraProcessoSuspensaoCassacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Alvara alvara;
    @ManyToOne
    private ProcessoSuspensaoCassacaoAlvara processo;
    @Transient
    private VOAlvara voAlvara;


    public AlvaraProcessoSuspensaoCassacao(ProcessoSuspensaoCassacaoAlvara processo) {
        this.processo = processo;
    }

    public AlvaraProcessoSuspensaoCassacao() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Alvara getAlvara() {
        return alvara;
    }

    public void setAlvara(Alvara alvara) {
        this.alvara = alvara;
    }

    public ProcessoSuspensaoCassacaoAlvara getProcesso() {
        return processo;
    }

    public void setProcesso(ProcessoSuspensaoCassacaoAlvara processo) {
        this.processo = processo;
    }

    public VOAlvara getVoAlvara() {
        return voAlvara;
    }

    public void setVoAlvara(VOAlvara voAlvara) {
        this.voAlvara = voAlvara;
    }
}
