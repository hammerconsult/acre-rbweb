package br.com.webpublico.entidades.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributario")
public class VinculoServicoTributo extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParametroRegularizacao parametroRegularizacao;
    @ManyToOne
    private ServicoConstrucao servicoConstrucao;
    @ManyToOne
    private Tributo tributo;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParametroRegularizacao getParametroRegularizacao() {
        return parametroRegularizacao;
    }

    public void setParametroRegularizacao(ParametroRegularizacao parametroRegularizacao) {
        this.parametroRegularizacao = parametroRegularizacao;
    }

    public ServicoConstrucao getServicoConstrucao() {
        return servicoConstrucao;
    }

    public void setServicoConstrucao(ServicoConstrucao servicoConstrucao) {
        this.servicoConstrucao = servicoConstrucao;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }
}
