package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoRepresentatividadeSiprev;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by William on 12/04/2016.
 */
@Entity
@Audited
public class Siprev extends SuperEntidade implements Serializable, PossuidorArquivo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Código do SIAFIl")
    @Pesquisavel
    @Obrigatorio
    private String codigo;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataGeracao;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Representante Legal")
    @Pesquisavel
    @Obrigatorio
    private Pessoa representante;
    @Tabelavel
    @Etiqueta("Início da representatividade")
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    private Date inicioRepresentatividade;
    @Tabelavel
    @Etiqueta("Final da representatividade")
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    private Date fimRepresentatividade;
    @Tabelavel
    @Etiqueta("Tipo da representatividade")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private TipoRepresentatividadeSiprev tipoRepresentatividade;
    @Tabelavel
    @Etiqueta("Mês")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private Mes mes;
    @Tabelavel
    @Etiqueta("Ano")
    @Pesquisavel
    @ManyToOne
    @Obrigatorio
    private Exercicio exercicio;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "siprev")
    private List<SiprevErro> erros;

    public Siprev() {
        detentorArquivoComposicao = new DetentorArquivoComposicao();
        erros = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getRepresentante() {
        return representante;
    }

    public void setRepresentante(Pessoa representante) {
        this.representante = representante;
    }

    public Date getInicioRepresentatividade() {
        return inicioRepresentatividade;
    }

    public void setInicioRepresentatividade(Date inicioRepresentatividade) {
        this.inicioRepresentatividade = inicioRepresentatividade;
    }

    public Date getFimRepresentatividade() {
        return fimRepresentatividade;
    }

    public void setFimRepresentatividade(Date fimRepresentatividade) {
        this.fimRepresentatividade = fimRepresentatividade;
    }

    public TipoRepresentatividadeSiprev getTipoRepresentatividade() {
        return tipoRepresentatividade;
    }

    public void setTipoRepresentatividade(TipoRepresentatividadeSiprev tipoRepresentatividade) {
        this.tipoRepresentatividade = tipoRepresentatividade;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<SiprevErro> getErros() {
        return erros;
    }

    public void setErros(List<SiprevErro> erros) {
        this.erros = erros;
    }
}
