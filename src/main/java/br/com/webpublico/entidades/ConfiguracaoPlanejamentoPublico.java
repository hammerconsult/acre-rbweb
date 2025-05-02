/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author gustavo
 */
@Entity

@Table(name = "ConfPlanejamentoPublico")
@Audited
@Etiqueta("Configuração do Planejamento Público")
public class ConfiguracaoPlanejamentoPublico extends ConfiguracaoModulo implements Serializable {

    /*
     *
     * Atributo que determina até qual nível devem ser mostradas as contas nas
     * consultas do PPA.
     *
     */
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Nivel da Conta de Despesa PPA")
    private String nivelContaDespesaPPA;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Máscara do Código do Programa")
    //4 Dígitos
    private String mascaraCodigoPrograma;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Máscara do Código da Ação")
    //3 Dígitos
    private String mascaraCodigoAcao;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Máscara do Código da Subação")
    //4 Dígitos
    private String mascaraCodigoSubAcao;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Máscara do Código da Receita LOA")
    private String mascaraCodigoReceitaLOA;

    public String getNivelContaDespesaPPA() {
        return nivelContaDespesaPPA;
    }

    public void setNivelContaDespesaPPA(String nivelContaDespesaPPA) {
        this.nivelContaDespesaPPA = nivelContaDespesaPPA;
    }

    public String getMascaraCodigoAcao() {
        return mascaraCodigoAcao;
    }

    public void setMascaraCodigoAcao(String mascaraCodigoAcao) {
        this.mascaraCodigoAcao = mascaraCodigoAcao;
    }

    public String getMascaraCodigoPrograma() {
        return mascaraCodigoPrograma;
    }

    public void setMascaraCodigoPrograma(String mascaraCodigoPrograma) {
        this.mascaraCodigoPrograma = mascaraCodigoPrograma;
    }

    public String getMascaraCodigoSubAcao() {
        return mascaraCodigoSubAcao;
    }

    public void setMascaraCodigoSubAcao(String mascaraCodigoSubAcao) {
        this.mascaraCodigoSubAcao = mascaraCodigoSubAcao;
    }

    public String getMascaraCodigoReceitaLOA() {
        return mascaraCodigoReceitaLOA;
    }

    public void setMascaraCodigoReceitaLOA(String mascaraCodigoReceitaLOA) {
        this.mascaraCodigoReceitaLOA = mascaraCodigoReceitaLOA;
    }
}
