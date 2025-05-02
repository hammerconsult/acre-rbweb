/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoDespesaORC;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author venon
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Despesa Orçamentária")
public class DespesaORC implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Código")
    private String codigo;
    @Tabelavel
    @Etiqueta("Código Reduzido")
    private String codigoReduzido;
    @ManyToOne
    @Tabelavel
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Despesa Orçamentária")
    @Tabelavel
    private TipoDespesaORC tipoDespesaORC;
    @OneToMany(mappedBy = "despesaORC", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FonteDespesaORC> fonteDespesaORCs;
    @ManyToOne
    @Tabelavel
    private ProvisaoPPADespesa provisaoPPADespesa;
    @Transient
    private String descricaoContaDespesaPPA;

    public DespesaORC() {
    }

    public DespesaORC(Long id, String codigo, String codigoReduzido, String descricao) {
        this.id = id;
        this.codigo = codigo;
        this.codigoReduzido = codigoReduzido;
        this.descricaoContaDespesaPPA = descricao;
    }

    public String getDescricaoContaDespesaPPA() {
        if (descricaoContaDespesaPPA == null) {
            descricaoContaDespesaPPA = this.getProvisaoPPADespesa().getContaDeDespesa().toString();
        }
        return descricaoContaDespesaPPA;
    }

    public void setDescricaoContaDespesaPPA(String descricaoContaDespesaPPA) {
        this.descricaoContaDespesaPPA = descricaoContaDespesaPPA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoReduzido() {
        return codigoReduzido;
    }

    public void setCodigoReduzido(String codigoReduzido) {
        this.codigoReduzido = codigoReduzido;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoDespesaORC getTipoDespesaORC() {
        return tipoDespesaORC;
    }

    public void setTipoDespesaORC(TipoDespesaORC tipoDespesaORC) {
        this.tipoDespesaORC = tipoDespesaORC;
    }

    public List<FonteDespesaORC> getFonteDespesaORCs() {
        return fonteDespesaORCs;
    }

    public void setFonteDespesaORCs(List<FonteDespesaORC> fonteDespesaORCs) {
        this.fonteDespesaORCs = fonteDespesaORCs;
    }

    public ProvisaoPPADespesa getProvisaoPPADespesa() {
        return provisaoPPADespesa;
    }

    public void setProvisaoPPADespesa(ProvisaoPPADespesa provisaoPPADespesa) {
        this.provisaoPPADespesa = provisaoPPADespesa;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DespesaORC)) {
            return false;
        }
        DespesaORC other = (DespesaORC) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        try {
            return codigo + " - " + this.getProvisaoPPADespesa().getContaDeDespesa().getCodigo();
        } catch (Exception e) {
            return codigo;
        }
    }

    public String getFuncionalProgramatica() {
        try {
            Funcao funcao = getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getSubFuncao().getFuncao();
            SubFuncao subFuncao = getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getSubFuncao();
            ProgramaPPA programa = getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getPrograma();
            AcaoPPA acaoPPA = getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
            SubAcaoPPA subAcaoPPA = getProvisaoPPADespesa().getSubAcaoPPA();
            return funcao.getCodigo() + "." + subFuncao.getCodigo() + "." + programa.getCodigo() + "." + acaoPPA.getTipoAcaoPPA().getCodigo() + acaoPPA.getCodigo() + "." + subAcaoPPA.getCodigo() + " - " + subAcaoPPA.getDescricao();
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public String getProjetoAtividade() {
        return this.getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().toString();
    }

    public String getHistoricoPadrao() {
        return this.getCodigo() + " - " + this.getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getDescricao();
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return this.getProvisaoPPADespesa().getTipoContaDespesa();
    }

    public Conta getContaDeDespesa() {
        try {
            return this.getProvisaoPPADespesa().getContaDeDespesa();
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar provisão ppa despesa da despesa orçamentária.");
        }
    }

    public String getCodigoAdaptado() {
        String codigoAdaptado = "";
        if (this.getCodigo() == null) {
            return null;
        }
        String codigoAdaptadoFim = this.codigo.substring(0, this.codigo.length() - 4);
        codigoAdaptado = codigoAdaptadoFim.substring(3);
        return codigoAdaptado;
    }

    public FonteDespesaORC getFonteDespesaORCDaFonte(FonteDeRecursos fonteDeRecursos) {
        for (FonteDespesaORC fonteDespesaORC : getFonteDespesaORCs()) {
            ContaDeDestinacao destinacaoDeRecursos = (ContaDeDestinacao) fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursos();
            if (destinacaoDeRecursos != null && destinacaoDeRecursos.getFonteDeRecursos() != null) {
                if (destinacaoDeRecursos.getFonteDeRecursos().equals(fonteDeRecursos)) {
                    return fonteDespesaORC;
                }
            }
        }
        return null;
    }
}
