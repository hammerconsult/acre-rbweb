/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Configuração de Faltas Injustificadas.
 *
 * @author peixe
 */
@Entity

@Etiqueta("Configuração da Tabela de Faltas Injustificadas")
@Table(name = "ConfigFaltaInjustificadas")
@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
public class ConfiguracaoFaltasInjustificadas extends SuperEntidade implements Serializable, ValidadorVigencia, Comparable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Inicio Vigência")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Final Vigência")
    private Date finalVigencia;
    @Etiqueta("Referência FP")
    @Obrigatorio
    @Tabelavel
    @ManyToOne
    @Pesquisavel
    private ReferenciaFP referenciaFP;
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    @Etiqueta("Configuração")
    @ManyToOne
    private ConfiguracaoRH configuracaoRH;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Órgão")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Transient
    private Operacoes operacao;

    public ConfiguracaoFaltasInjustificadas() {
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

    @Override
    public Date getFimVigencia() {
        return getFinalVigencia();
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        setFinalVigencia(data);
    }

    public ReferenciaFP getReferenciaFP() {
        return referenciaFP;
    }

    public void setReferenciaFP(ReferenciaFP referenciaFP) {
        this.referenciaFP = referenciaFP;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoRH getConfiguracaoRH() {
        return configuracaoRH;
    }

    public void setConfiguracaoRH(ConfiguracaoRH configuracaoRH) {
        this.configuracaoRH = configuracaoRH;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    @Override
    public String toString() {
        return unidadeOrganizacional + " - " + referenciaFP;
    }

    public String getObservacoes() {
        Date hoje = UtilRH.getDataOperacao();
        if (hoje.compareTo(getInicioVigencia()) >= 0 && getFinalVigencia() == null) {
            return "<b>Vigente Atualmente</b>";
        }

        if (hoje.compareTo(getInicioVigencia()) >= 0 && hoje.compareTo(getFinalVigencia()) <= 0 && getFinalVigencia() != null) {
            return "<b>Vigente Atualmente</b>";
        }

        return "";
    }

    @Override
    public int compareTo(Object o) {
        return getInicioVigencia().compareTo(((ConfiguracaoFaltasInjustificadas) o).getInicioVigencia());
    }

    public boolean isEditando() {
        return Operacoes.EDITAR.equals(operacao);
    }
}
