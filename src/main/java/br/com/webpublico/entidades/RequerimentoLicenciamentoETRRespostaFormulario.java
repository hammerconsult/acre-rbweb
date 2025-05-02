package br.com.webpublico.entidades;

import br.com.webpublico.entidades.comum.RespostaFormulario;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class RequerimentoLicenciamentoETRRespostaFormulario extends SuperEntidade implements Comparable<RequerimentoLicenciamentoETRRespostaFormulario> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private RequerimentoLicenciamentoETR requerimentoLicenciamentoETR;
    private Integer ordem;
    @OneToOne
    private RespostaFormulario respostaFormulario;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequerimentoLicenciamentoETR getRequerimentoLicenciamentoETR() {
        return requerimentoLicenciamentoETR;
    }

    public void setRequerimentoLicenciamentoETR(RequerimentoLicenciamentoETR requerimentoLicenciamentoETR) {
        this.requerimentoLicenciamentoETR = requerimentoLicenciamentoETR;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public RespostaFormulario getRespostaFormulario() {
        return respostaFormulario;
    }

    public void setRespostaFormulario(RespostaFormulario respostaFormulario) {
        this.respostaFormulario = respostaFormulario;
    }

    @Override
    public int compareTo(RequerimentoLicenciamentoETRRespostaFormulario o) {
        return ordem.compareTo(o.getOrdem());
    }
}
