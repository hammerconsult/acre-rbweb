package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by israeleriston on 11/11/15.
 */
@Entity
@Audited
@Etiqueta("Acidente")
public class Acidente extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Médico")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @OneToOne
    private Medico medico;
    @Etiqueta("Natureza da Lesão")
    @Obrigatorio
    private String naturezaLesao;
    @Obrigatorio
    @Etiqueta("Situação Geradora")
    private String situacaoGeradora;
    @Obrigatorio
    @Etiqueta("Diagnóstico")
    private String diagnostico;
    @Etiqueta("C.I.D")
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    private CID cid;
    @Etiqueta("Observações")
    @Obrigatorio
    private String observacao;


    public Acidente(Medico medico, String naturezaLesao, String situacaoGeradora, String diagnostico, CID cid, String observacao) {
        this.medico = new Medico();
        this.naturezaLesao = naturezaLesao;
        this.situacaoGeradora = situacaoGeradora;
        this.diagnostico = diagnostico;
        this.cid = new CID();
        this.observacao = observacao;
    }

    public Acidente() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public String getNaturezaLesao() {
        return naturezaLesao;
    }

    public void setNaturezaLesao(String naturezaLesao) {
        this.naturezaLesao = naturezaLesao;
    }

    public String getSituacaoGeradora() {
        return situacaoGeradora;
    }

    public void setSituacaoGeradora(String situacaoGeradora) {
        this.situacaoGeradora = situacaoGeradora;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public CID getCid() {
        return cid;
    }

    public void setCid(CID cid) {
        this.cid = cid;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public String toString() {
        return this.getCid().toString();
    }
}
