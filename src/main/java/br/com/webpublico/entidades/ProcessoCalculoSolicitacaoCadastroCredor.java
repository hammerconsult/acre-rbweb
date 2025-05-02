package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Entity
@Audited
@Table(name = "PROCESSOCALCULOSOLCADCRED")
public class ProcessoCalculoSolicitacaoCadastroCredor extends ProcessoCalculo implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "processoCalculoSolicitacao")
    private List<CalculoSolicitacaoCadastroCredor> calculos;

    @Override
    public List<CalculoSolicitacaoCadastroCredor> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoSolicitacaoCadastroCredor> calculos) {
        this.calculos = calculos;
    }
}
