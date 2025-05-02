package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wanderley
 * Date: 05/11/13
 * Time: 10:51
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class CalculoMultaAcessoria extends Calculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @ManyToOne
    private ProcessoCalculoMultaAcessoria procCalcMultaAcessoria;
    @ManyToOne
    private UsuarioSistema usuarioLancamento;
    @OneToMany(mappedBy = "calculoMultaAcessoria" ,cascade = CascadeType.ALL , orphanRemoval = true)
    private List<ItemCalculoMultaAcessoria> listaItemCalculoMultaAcessoria;
    private Boolean issComMovimento;

    public ProcessoCalculoMultaAcessoria getProcessoCalculoMultaAcessoria() {
        return procCalcMultaAcessoria;
    }

    public void setProcessoCalculoMultaAcessoria(ProcessoCalculoMultaAcessoria processoCalculoMultaAcessoria) {
        super.setProcessoCalculo(processoCalculoMultaAcessoria);
        this.procCalcMultaAcessoria = processoCalculoMultaAcessoria;
    }

    public UsuarioSistema getUsuarioLancamento() {
        return usuarioLancamento;
    }

    public void setUsuarioLancamento(UsuarioSistema usuarioLancamento) {
        this.usuarioLancamento = usuarioLancamento;
    }

    public List<ItemCalculoMultaAcessoria> getListaItemCalculoMultaAcessoria() {
        return listaItemCalculoMultaAcessoria;
    }

    public void setListaItemCalculoMultaAcessoria(List<ItemCalculoMultaAcessoria> listaItemCalculoMultaAcessoria) {
        this.listaItemCalculoMultaAcessoria = listaItemCalculoMultaAcessoria;
    }

    public Boolean getIssComMovimento() {
        return issComMovimento;
    }

    public void setIssComMovimento(Boolean issComMovimento) {
        this.issComMovimento = issComMovimento;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return procCalcMultaAcessoria;
    }

}
