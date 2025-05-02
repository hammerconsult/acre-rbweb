/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import br.com.webpublico.webreportdto.dto.rh.RecursoFPDTO;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Recurso Folha de Pagamento")
public class RecursoFP implements Serializable, Comparable<RecursoFP> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    @Obrigatorio
    private String codigo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código do Grupo de RecursoFP")
    @Obrigatorio
    private String codigoGrupo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código do Órgão")
    @Obrigatorio
    private Integer codigoOrgao;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição Reduzida")
    private String descricaoReduzida;
    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Inicio da Vigência")
    private Date inicioVigencia;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Final da Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @ManyToOne
    @Etiqueta("Despesa Orçamentaria")
    private DespesaORC despesaORC;
    @OneToMany(mappedBy = "recursoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Fontes de Recurso")
    private List<FontesRecursoFP> fontesRecursoFPs;
    @Transient
    @Invisivel
    private Date dataRegistro;
    @Tabelavel
    private Integer ordem;
    @Transient
    private Long criadoEm = System.nanoTime();
    @Transient
    @Invisivel
    private boolean selecionado;


    public RecursoFP() {
        fontesRecursoFPs = new ArrayList<FontesRecursoFP>();
        dataRegistro = new Date();
    }

    public RecursoFP(Long id, String codigo, String descricao) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static List<RecursoFPDTO> toDtos(List<RecursoFP> recursos) {
        List<RecursoFPDTO> recursoFPDtos = Lists.newArrayList();
        for (RecursoFP recurso : recursos) {
            recursoFPDtos.add(toDto(recurso));
        }
        return recursoFPDtos;
    }

    public static RecursoFPDTO toDto(RecursoFP recursoFP) {
        RecursoFPDTO recursoFPDTO = new RecursoFPDTO();
        recursoFPDTO.setCodigo(recursoFP.getCodigo());
        recursoFPDTO.setDescricao(recursoFP.getDescricao());
        return recursoFPDTO;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public RecursoFP(String descricao) {
        this.descricao = descricao;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<FontesRecursoFP> getFontesRecursoFPs() {
        return fontesRecursoFPs;
    }

    public void setFontesRecursoFPs(List<FontesRecursoFP> fontesRecursoFPs) {
        this.fontesRecursoFPs = fontesRecursoFPs;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public Long getId() {
        return id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
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

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public String getCodigoGrupo() {
        return codigoGrupo;
    }

    public void setCodigoGrupo(String codigoGrupo) {
        this.codigoGrupo = codigoGrupo;
    }

    public String getDescricaoReduzida() {
        return descricaoReduzida;
    }

    public void setDescricaoReduzida(String descricaoReduzida) {
        this.descricaoReduzida = descricaoReduzida;
    }

    public Integer getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(Integer codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }

    public String toStringAutoComplete() {
        String retorno = codigo + " - " + descricao;
        return retorno.length() > 68 ? retorno.substring(0, 65) + "..." : retorno;
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    @Override
    public int compareTo(RecursoFP o) {
        try {
            int resultado = this.getCodigo().compareTo(o.getCodigo());
            if (resultado == 0)
                return this.getDescricao().compareTo(o.getDescricao());
            return resultado;
        } catch (Exception e) {
            return 0;
        }
    }
}
