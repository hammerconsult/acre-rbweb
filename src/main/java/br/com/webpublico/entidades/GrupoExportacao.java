/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoGrupoExportacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Grupo Exportação")
public class GrupoExportacao implements Serializable {

    public static final Long OUTROS_VALOR_BRUTO = 1L;
    public static final Long OUTROS_BASE_PREVIDENCIA = 2L;



    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Etiqueta("Código")
    @Obrigatorio
    @Tabelavel(agrupavel = true)
    private Long codigo;
    @Pesquisavel
    @Etiqueta("Descrição")
    @Obrigatorio
    @Tabelavel
    private String descricao;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Módulo")
    @ManyToOne
    private ModuloExportacao moduloExportacao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Nome Reduzido")
    private String nomeReduzido;
    @Etiqueta("Itens do Grupo de Exportação")
    @Tabelavel
    @OneToMany(mappedBy = "grupoExportacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemGrupoExportacao> itensGruposExportacoes;
    @Etiqueta("Itens do Grupo de Exportação Contábil")
    @OneToMany(mappedBy = "grupoExportacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemGrupoExportacaoContabil> itensGrupoExportacaoContabil;
    @Obrigatorio
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoGrupoExportacao tipoGrupoExportacao;
    @Transient
    @Invisivel
    private Long criadoEm;

//    // TODOS SEFIP
//    public static final Long SALARIO_FAMILIA = 10l;
//    public static final Long SALARIO_MATERNIDADE = 11l;
//    public static final Long REMUNERACAO_SEM_13 = 1l;
//    public static final Long REMUNERACAO_13 = 21l;
//    public static final Long VALOR_DESCONTADO_DO_SEGURADO = 30l;
//    public static final Long REMUNERACAO_BASE_CALCULO_CONTRIBUICAO = 31l;
//    public static final Long BASE_CALCULO_13_SALARIO_PREVIDENCIA = 40l;
//    public static final Long BASE_CALCULO_13_SALARIO_PREVIDENCIA_REF_GPS_COMPETENCIA = 41l;

    public GrupoExportacao() {
        this.criadoEm = System.nanoTime();
        itensGrupoExportacaoContabil = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getNomeReduzido() {
        return nomeReduzido;
    }

    public void setNomeReduzido(String nomeReduzido) {
        this.nomeReduzido = nomeReduzido;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ModuloExportacao getModuloExportacao() {
        return moduloExportacao;
    }

    public void setModuloExportacao(ModuloExportacao moduloExportacao) {
        this.moduloExportacao = moduloExportacao;
    }

    public List<ItemGrupoExportacao> getItensGruposExportacoes() {
        if (itensGruposExportacoes != null){
            Collections.sort(itensGruposExportacoes, new Comparator<ItemGrupoExportacao>() {
                @Override
                public int compare(ItemGrupoExportacao o1, ItemGrupoExportacao o2) {
                    return o1.getEventoFP().getCodigo().compareTo(o2.getEventoFP().getCodigo());
                }
            });
        }
        return itensGruposExportacoes;
    }

    public void setItensGruposExportacoes(List<ItemGrupoExportacao> itensGruposExportacoes) {
        this.itensGruposExportacoes = itensGruposExportacoes;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<ItemGrupoExportacaoContabil> getItensGrupoExportacaoContabil() {
        return itensGrupoExportacaoContabil;
    }

    public void setItensGrupoExportacaoContabil(List<ItemGrupoExportacaoContabil> itensGrupoExportacaoContabil) {
        this.itensGrupoExportacaoContabil = itensGrupoExportacaoContabil;
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
        return codigo + " - " + descricao;
    }

    public TipoGrupoExportacao getTipoGrupoExportacao() {
        return tipoGrupoExportacao;
    }

    public void setTipoGrupoExportacao(TipoGrupoExportacao tipoGrupoExportacao) {
        this.tipoGrupoExportacao = tipoGrupoExportacao;
    }
}
