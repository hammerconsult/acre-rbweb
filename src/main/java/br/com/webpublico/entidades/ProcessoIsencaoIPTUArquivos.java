package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Etiqueta("Arquivos da Solicitação de Processo de Isenção de IPTU")
@Table(name = "PROCESSOISENCAOIPTUARQUIVO")
public class ProcessoIsencaoIPTUArquivos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoIsencaoIPTU processoIsencaoIPTU;
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;
    @Transient
    private Long criadoEm;

    public ProcessoIsencaoIPTUArquivos() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoIsencaoIPTU getProcessoIsencaoIPTU() {
        return processoIsencaoIPTU;
    }

    public void setProcessoIsencaoIPTU(ProcessoIsencaoIPTU processoIsencaoIPTU) {
        this.processoIsencaoIPTU = processoIsencaoIPTU;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
