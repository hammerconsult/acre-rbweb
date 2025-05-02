package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.SituacaoRecurso;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Buzatto on 31/08/2015.
 */
@Entity
@Audited
@Etiqueta(value = "Recurso Concurso")
@GrupoDiagrama(nome = "Concursos")
public class RecursoConcurso extends SuperEntidade implements Serializable, Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Código")
    private Integer codigo;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "Concurso")
    @ManyToOne
    private Concurso concurso;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "Data")
    @Temporal(TemporalType.DATE)
    private Date data;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "Situação Recurso")
    @Enumerated(EnumType.STRING)
    private SituacaoRecurso situacaoRecurso;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "Candidato")
    @ManyToOne
    private InscricaoConcurso inscricaoConcurso;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "Cargo")
    @ManyToOne
    private CargoConcurso cargoConcurso;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "Fase")
    @ManyToOne
    private FaseConcurso faseConcurso;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "Prova")
    @ManyToOne
    private ProvaConcurso provaConcurso;

    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Anexo")
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "Observação")
    private String observacao;

    public RecursoConcurso() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Concurso getConcurso() {
        return concurso;
    }

    public void setConcurso(Concurso concurso) {
        this.concurso = concurso;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public SituacaoRecurso getSituacaoRecurso() {
        return situacaoRecurso;
    }

    public void setSituacaoRecurso(SituacaoRecurso situacaoRecurso) {
        this.situacaoRecurso = situacaoRecurso;
    }

    public InscricaoConcurso getInscricaoConcurso() {
        return inscricaoConcurso;
    }

    public void setInscricaoConcurso(InscricaoConcurso inscricaoConcurso) {
        this.inscricaoConcurso = inscricaoConcurso;
    }

    public CargoConcurso getCargoConcurso() {
        return cargoConcurso;
    }

    public void setCargoConcurso(CargoConcurso cargoConcurso) {
        this.cargoConcurso = cargoConcurso;
    }

    public FaseConcurso getFaseConcurso() {
        return faseConcurso;
    }

    public void setFaseConcurso(FaseConcurso faseConcurso) {
        this.faseConcurso = faseConcurso;
    }

    public ProvaConcurso getProvaConcurso() {
        return provaConcurso;
    }

    public void setProvaConcurso(ProvaConcurso provaConcurso) {
        this.provaConcurso = provaConcurso;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean temConcurso() {
        return this.concurso != null;
    }

    public boolean temCargo() {
        return this.cargoConcurso != null;
    }

    public boolean temArquivo() {
        return this.arquivo != null;
    }

    public boolean temFase() {
        return this.faseConcurso != null;
    }

    public boolean temProva() {
        return this.provaConcurso != null;
    }

    public String getCodigoComZeros() {
        return StringUtil.cortaOuCompletaEsquerda("" + codigo, 3, "0");
    }

    @Override
    public String toString() {
        String descricao = getCodigoComZeros() + "/" + getAno() + " - " + getObservacao();
        if (descricao.length() > 60) {
            descricao = descricao.substring(0, 60);
            descricao += "...";
        }
        return descricao;
    }

    public int getAno() {
        return DataUtil.getAno(data);
    }

    public void cancelarCandidato() {
        this.inscricaoConcurso = null;
    }

    public void cancelarCargo() {
        this.cargoConcurso = null;
    }

    public void cancelarFase() {
        this.faseConcurso = null;
    }

    public void cancelarProva() {
        this.provaConcurso = null;
    }

    public void cancelarConcurso() {
        this.concurso = null;
    }

    public boolean isSituacaoAceito() {
        return SituacaoRecurso.ACEITO.equals(this.situacaoRecurso);
    }

    public boolean isSituacaoNaoAceito() {
        return SituacaoRecurso.NAO_ACEITO.equals(this.situacaoRecurso);
    }

    public boolean isSituacaoEmAndamento() {
        return SituacaoRecurso.EM_ANDAMENTO.equals(this.situacaoRecurso);
    }

    @Override
    public int compareTo(Object o) {
        return this.getData().compareTo(((RecursoConcurso) o).getData());
    }
}
