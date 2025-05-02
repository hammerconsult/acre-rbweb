/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.RelatoriosItemDemonstDTO;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author juggernaut
 */
@Audited
@Entity
@Etiqueta("Relatórios Item Demonstrativo")
public class RelatoriosItemDemonst extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Relatório")
    private TipoRelatorioItemDemonstrativo tipoRelatorioItemDemonstrativo;
    @Etiqueta("Utiliza Conta?")
    private Boolean usaConta;
    @Etiqueta("Utiliza Programa?")
    private Boolean usaPrograma;
    @Etiqueta("Utiliza Ação?")
    private Boolean usaAcao;
    @Etiqueta("Utiliza Subação?")
    private Boolean usaSubAcao;
    @Etiqueta("Utiliza Função?")
    private Boolean usaFuncao;
    @Etiqueta("Utiliza Subfunção?")
    private Boolean usaSubFuncao;
    @Etiqueta("Utiliza Unidade Organizacional?")
    private Boolean usaUnidadeOrganizacional;
    @Etiqueta("Utiliza Fonte de Recurso?")
    private Boolean usaFonteRecurso;
    @Etiqueta("Utiliza Tipo Despesa?")
    private Boolean usaTipoDespesa;
    @Etiqueta("Utiliza Conta Financeira?")
    private Boolean usaContaFinanceira;
    @Obrigatorio
    @ManyToOne
    private Exercicio exercicio;
    @Obrigatorio
    @Etiqueta("Grupos")
    private Integer grupos;
    @OneToMany(mappedBy = "relatoriosItemDemonst", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDemonstrativo> itensDemonstrativos;
    @Etiqueta("Nota Explicativa")
    private String notaExplicativa;
    @Etiqueta("Utiliza Valor Padrão")
    private Boolean usaValorPadrao;

    public RelatoriosItemDemonst() {
        super();
        itensDemonstrativos = Lists.newArrayList();
        usaConta = false;
        usaPrograma = false;
        usaAcao = false;
        usaFuncao = false;
        usaSubFuncao = false;
        usaUnidadeOrganizacional = false;
        usaFonteRecurso = false;
        usaTipoDespesa = false;
        usaContaFinanceira = false;
        usaValorPadrao = false;
    }

    public RelatoriosItemDemonstDTO toDto() {
        RelatoriosItemDemonstDTO relatoriosItemDemonstDTO = new RelatoriosItemDemonstDTO();
        relatoriosItemDemonstDTO.setId(id);
        relatoriosItemDemonstDTO.setUsaConta(usaConta);
        relatoriosItemDemonstDTO.setUsaPrograma(usaPrograma);
        relatoriosItemDemonstDTO.setUsaAcao(usaAcao);
        relatoriosItemDemonstDTO.setUsaSubAcao(usaSubAcao);
        relatoriosItemDemonstDTO.setUsaFuncao(usaFuncao);
        relatoriosItemDemonstDTO.setUsaSubFuncao(usaSubFuncao);
        relatoriosItemDemonstDTO.setUsaUnidadeOrganizacional(usaUnidadeOrganizacional);
        relatoriosItemDemonstDTO.setUsaFonteRecurso(usaFonteRecurso);
        relatoriosItemDemonstDTO.setUsaTipoDespesa(usaTipoDespesa);
        relatoriosItemDemonstDTO.setUsaContaFinanceira(usaContaFinanceira);
        relatoriosItemDemonstDTO.setUsaValorPadrao(usaValorPadrao);
        relatoriosItemDemonstDTO.setAnoRelatorio(exercicio.getAno());
        return relatoriosItemDemonstDTO;
    }

    public Boolean getUsaConta() {
        return usaConta;
    }

    public void setUsaConta(Boolean usaConta) {
        this.usaConta = usaConta;
    }

    public Boolean getUsaPrograma() {
        return usaPrograma;
    }

    public void setUsaPrograma(Boolean usaPrograma) {
        this.usaPrograma = usaPrograma;
    }

    public Boolean getUsaAcao() {
        return usaAcao;
    }

    public void setUsaAcao(Boolean usaAcao) {
        this.usaAcao = usaAcao;
    }

    public Boolean getUsaFuncao() {
        return usaFuncao;
    }

    public void setUsaFuncao(Boolean usaFuncao) {
        this.usaFuncao = usaFuncao;
    }

    public Boolean getUsaSubFuncao() {
        return usaSubFuncao;
    }

    public void setUsaSubFuncao(Boolean usaSubFuncao) {
        this.usaSubFuncao = usaSubFuncao;
    }

    public Boolean getUsaUnidadeOrganizacional() {
        return usaUnidadeOrganizacional;
    }

    public void setUsaUnidadeOrganizacional(Boolean usaUnidadeOrganizacional) {
        this.usaUnidadeOrganizacional = usaUnidadeOrganizacional;
    }

    public Boolean getUsaFonteRecurso() {
        return usaFonteRecurso;
    }

    public void setUsaFonteRecurso(Boolean usaFonteRecurso) {
        this.usaFonteRecurso = usaFonteRecurso;
    }

    public Boolean getUsaTipoDespesa() {
        return usaTipoDespesa;
    }

    public void setUsaTipoDespesa(Boolean usaTipoDespesa) {
        this.usaTipoDespesa = usaTipoDespesa;
    }

    public Boolean getUsaSubAcao() {
        return usaSubAcao == null ? Boolean.FALSE : usaSubAcao;
    }

    public void setUsaSubAcao(Boolean usaSubAcao) {
        this.usaSubAcao = usaSubAcao;
    }

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

    public TipoRelatorioItemDemonstrativo getTipoRelatorioItemDemonstrativo() {
        return tipoRelatorioItemDemonstrativo;
    }

    public void setTipoRelatorioItemDemonstrativo(TipoRelatorioItemDemonstrativo tipoRelatorioItemDemonstrativo) {
        this.tipoRelatorioItemDemonstrativo = tipoRelatorioItemDemonstrativo;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    @Override
    public String toString() {
        return descricao +
            (tipoRelatorioItemDemonstrativo != null ?  " - " + tipoRelatorioItemDemonstrativo.getDescricao() : "") +
            (exercicio != null ? " de " + exercicio.getAno().toString() : "");
    }

    public Integer getGrupos() {
        return grupos;
    }

    public void setGrupos(Integer grupos) {
        this.grupos = grupos;
    }

    public String getNotaExplicativa() {
        return notaExplicativa;
    }

    public void setNotaExplicativa(String notaExplicativa) {
        this.notaExplicativa = notaExplicativa;
    }

    public Boolean getUsaContaFinanceira() {
        return usaContaFinanceira;
    }

    public void setUsaContaFinanceira(Boolean usaContaFinanceira) {
        this.usaContaFinanceira = usaContaFinanceira;
    }

    public List<ItemDemonstrativo> getItensDemonstrativos() {
        return itensDemonstrativos;
    }

    public void setItensDemonstrativos(List<ItemDemonstrativo> itensDemonstrativos) {
        this.itensDemonstrativos = itensDemonstrativos;
    }

    public Boolean getUsaValorPadrao() {
        return usaValorPadrao == null ? Boolean.FALSE : usaValorPadrao;
    }

    public void setUsaValorPadrao(Boolean usaValorPadrao) {
        this.usaValorPadrao = usaValorPadrao;
    }
}
