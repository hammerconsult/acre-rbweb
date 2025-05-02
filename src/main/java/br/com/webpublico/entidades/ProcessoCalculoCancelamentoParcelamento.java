/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;


@Entity
@Audited
@Table(name = "PROCESSOCALCCANCPARC")
public class ProcessoCalculoCancelamentoParcelamento extends ProcessoCalculo {

    @OneToMany(mappedBy = "processoCalculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CancelamentoParcelamento> cancelamentos;
    private Boolean dividaAtiva;
    private Boolean dividaAtivaAjuizada;
    private String referencia;

    public ProcessoCalculoCancelamentoParcelamento() {
        cancelamentos = Lists.newArrayList();
    }

    public List<CancelamentoParcelamento> getCancelamentos() {
        return cancelamentos;
    }

    public void setCancelamentos(List<CancelamentoParcelamento> cancelamentos) {
        this.cancelamentos = cancelamentos;
    }

    public Boolean getDividaAtivaAjuizada() {
        return dividaAtivaAjuizada;
    }

    public void setDividaAtivaAjuizada(Boolean dividaAtivaAjuizada) {
        this.dividaAtivaAjuizada = dividaAtivaAjuizada;
    }

    public Boolean getDividaAtiva() {
        return dividaAtiva;
    }

    public void setDividaAtiva(Boolean dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    @Override
    public List<? extends Calculo> getCalculos() {
        return getCancelamentos();
    }
}
