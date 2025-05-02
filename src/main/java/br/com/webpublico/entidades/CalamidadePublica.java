package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCalamidadePublica;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Calamidade Pública")
public class CalamidadePublica extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Descrição")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private String descricao;
    @Etiqueta("Abreviação")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoCalamidadePublica abreviacao;
    @Etiqueta("Início de Vigência")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Etiqueta("Final de Vigência")
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @OneToMany(mappedBy = "calamidadePublica", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CalamidadePublicaAtoLegal> atosLegais;
    @OneToMany(mappedBy = "calamidadePublica", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CalamidadePublicaContrato> contratos;
    @OneToMany(mappedBy = "calamidadePublica", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CalamidadePublicaRecurso> recursos;
    @OneToMany(mappedBy = "calamidadePublica", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CalamidadePublicaBemServico> bensServicosRecebidos;
    @OneToMany(mappedBy = "calamidadePublica", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CalamidadePublicaBemDoado> bensDoados;

    public CalamidadePublica() {
        super();
        contratos = Lists.newArrayList();
        atosLegais = Lists.newArrayList();
        recursos = Lists.newArrayList();
        bensServicosRecebidos = Lists.newArrayList();
        bensDoados = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public List<CalamidadePublicaAtoLegal> getAtosLegais() {
        return atosLegais;
    }

    public void setAtosLegais(List<CalamidadePublicaAtoLegal> atosLegais) {
        this.atosLegais = atosLegais;
    }

    public List<CalamidadePublicaContrato> getContratos() {
        return contratos;
    }

    public void setContratos(List<CalamidadePublicaContrato> contratos) {
        this.contratos = contratos;
    }

    public TipoCalamidadePublica getAbreviacao() {
        return abreviacao;
    }

    public void setAbreviacao(TipoCalamidadePublica abreviacao) {
        this.abreviacao = abreviacao;
    }

    public List<CalamidadePublicaRecurso> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<CalamidadePublicaRecurso> recursos) {
        this.recursos = recursos;
    }

    public List<CalamidadePublicaBemServico> getBensServicosRecebidos() {
        return bensServicosRecebidos;
    }

    public void setBensServicosRecebidos(List<CalamidadePublicaBemServico> bensServicosRecebidos) {
        this.bensServicosRecebidos = bensServicosRecebidos;
    }

    public List<CalamidadePublicaBemDoado> getBensDoados() {
        return bensDoados;
    }

    public void setBensDoados(List<CalamidadePublicaBemDoado> bensDoados) {
        this.bensDoados = bensDoados;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
