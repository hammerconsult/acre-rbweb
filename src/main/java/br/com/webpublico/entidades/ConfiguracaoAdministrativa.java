/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author lucas
 */
@GrupoDiagrama(nome = "Administrativo")
@Audited

@Entity
@Etiqueta("Configuração Administrativa")
/*
 *
 * Agrega o conjunto de parâmetros utilizados pelo módulo Administrativo. A cada mudança nos valores
 * dos parâmetros deverá ser gerado um novo registro com a data correspondente (desde) e o vínculo
 * com a configuração anterior.
 *
 */
public class ConfiguracaoAdministrativa extends ConfiguracaoModulo implements Serializable {
    @Etiqueta("Valido Até")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date validoAte;
    @Etiqueta("Parâmetro")
    @Tabelavel
    @Obrigatorio
    private String parametro;
    @Etiqueta("Valor")
    @Tabelavel
    @Obrigatorio
    private String valor;
    @Etiqueta("Observações")
    @Obrigatorio
    private String observacoes;
    @OneToOne
    @Etiqueta("Responsável")
    @Tabelavel
    private UsuarioSistema alteradoPor;
    @Invisivel
    @Transient
    private Long criadoEm;

    public ConfiguracaoAdministrativa() {
        this.criadoEm = System.nanoTime();
    }

    public Date getValidoAte() {
        return validoAte;
    }

    public void setValidoAte(Date validoAte) {
        this.validoAte = validoAte;
    }

    public UsuarioSistema getAlteradoPor() {
        return alteradoPor;
    }

    public void setAlteradoPor(UsuarioSistema alteradoPor) {
        this.alteradoPor = alteradoPor;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
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
        return this.parametro;
    }
}
