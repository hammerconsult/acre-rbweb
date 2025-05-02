/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Leonardo
 */
@Entity

@Audited
@Etiqueta("CAGED")
public class Caged implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistemaLogado;
    @Invisivel
    private String conteudoArquivo;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Etiqueta("Data de Criação")
    private Date geradoEm;
    @Etiqueta(value = "Entidade")
    @ManyToOne
    @Tabelavel
    private Entidade entidade;
    @Etiqueta("Mês")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    private Mes mes;
    @Tabelavel
    @Etiqueta("Ano")
    @ManyToOne
    private Exercicio exercicio;
    @Etiqueta(value = "Responsável")
    @ManyToOne
    @Tabelavel
    private PessoaFisica pessoaFisica;
    @Transient
    @Invisivel
    private Long criadoEm;
//    @Transient
//    @Invisivel
//    private HierarquiaOrganizacional hierarquiaOrganizacional;
//    @Transient
//    @Invisivel
//    private ItemEntidadeDPContas itemEntidadeDPContas;

    public Caged() {
        this.criadoEm = System.nanoTime();
    }

//    public ItemEntidadeDPContas getItemEntidadeDPContas() {
//        return itemEntidadeDPContas;
//    }
//
//    public void setItemEntidadeDPContas(ItemEntidadeDPContas itemEntidadeDPContas) {
//        this.itemEntidadeDPContas = itemEntidadeDPContas;
//    }
//
//    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
//        return hierarquiaOrganizacional;
//    }
//
//    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
//        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getUsuarioSistemaLogado() {
        return usuarioSistemaLogado;
    }

    public void setUsuarioSistemaLogado(UsuarioSistema usuarioSistemaLogado) {
        this.usuarioSistemaLogado = usuarioSistemaLogado;
    }

    public String getConteudoArquivo() {
        return conteudoArquivo;
    }

    public void setConteudoArquivo(String conteudoArquivo) {
        this.conteudoArquivo = conteudoArquivo;
    }

    public Date getGeradoEm() {
        return geradoEm;
    }

    public void setGeradoEm(Date geradoEm) {
        this.geradoEm = geradoEm;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return entidade + " - "+mes+"/"+exercicio.getAno();
    }

    public Date getPrimeiroDiaDoMes() {
        Calendar pd = Calendar.getInstance();
        pd.set(Calendar.DAY_OF_MONTH, 1);
        pd.set(Calendar.MONTH, this.getMes().getNumeroMesIniciandoEmZero());
        pd.set(Calendar.YEAR, this.getExercicio().getAno());
        pd.setTime(Util.getDataHoraMinutoSegundoZerado(pd.getTime()));
        return pd.getTime();
    }

    public Date getUltimoDiaDoMes() {
        Calendar ud = Calendar.getInstance();
        ud.set(Calendar.MONTH, this.getMes().getNumeroMesIniciandoEmZero());
        ud.set(Calendar.DAY_OF_MONTH, ud.getActualMaximum(Calendar.DAY_OF_MONTH));
        ud.set(Calendar.YEAR, this.getExercicio().getAno());
        ud.setTime(Util.getDataHoraMinutoSegundoZerado(ud.getTime()));
        return ud.getTime();
    }
}
