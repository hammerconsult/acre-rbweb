/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import java.io.Serializable;

@GrupoDiagrama(nome = "Patrimonial")
@Audited
@Entity

/*
 *
 * Agrega o conjunto de parâmetros utilizados pelo módulo Patrimonial. A cada mudança nos valores
 * dos parâmetros deverá ser gerado um novo registro com a data correspondente (desde) e o vínculo
 * com a configuração anterior.
 *
 */
public class ConfiguracaoPatrimonial extends ConfiguracaoModulo implements Serializable {

    /**
     * Atributo utilizado para definir o formato do código dos Grupos de Bens
     * <p/>
     * Exemplo de máscara que permite 5 níveis: 999.999.9.9.999
     */
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Mascara do Grupo Bem")
    private String mascaraGrupoBem;

    public ConfiguracaoPatrimonial() {
    }

    public ConfiguracaoPatrimonial(String mascaraGrupoBem) {
        this.mascaraGrupoBem = mascaraGrupoBem;
    }

    public String getMascaraGrupoBem() {
        return mascaraGrupoBem;
    }

    public void setMascaraGrupoBem(String mascaraGrupoBem) {
        this.mascaraGrupoBem = mascaraGrupoBem;
    }

    @Override
    public String toString() {
        return mascaraGrupoBem;
    }

}
