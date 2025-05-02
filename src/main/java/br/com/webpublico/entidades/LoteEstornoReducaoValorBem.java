package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 31/10/14
 * Time: 14:12
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Lote de Estorno da Redução de Valor do Bem")
public class LoteEstornoReducaoValorBem extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private LogReducaoOuEstorno logReducaoOuEstorno;

    @OneToOne
    private LoteReducaoValorBem loteReducaoValorBem;

    @Temporal(TemporalType.DATE)
    private Date dataEstorno;

    @ManyToOne
    private UsuarioSistema usuarioDoEstorno;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "loteEstornoReducaoValorBem", fetch = FetchType.LAZY)
    private List<EstornoReducaoValorBem> estornos;

    @Transient
    private List<ReducaoValorBemEstornoContabil> reducoesValorBemEstornoContabil;

    public LoteEstornoReducaoValorBem() {
        reducoesValorBemEstornoContabil = Lists.newArrayList();
    }

    public LoteEstornoReducaoValorBem(LoteReducaoValorBem loteReducaoValorBem, Date dataEstorno, UsuarioSistema usuarioDoEstorno) {
        this.loteReducaoValorBem = loteReducaoValorBem;
        this.dataEstorno = dataEstorno;
        this.usuarioDoEstorno = usuarioDoEstorno;
        this.logReducaoOuEstorno = new LogReducaoOuEstorno();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<EstornoReducaoValorBem> getEstornos() {
        return estornos;
    }

    public void setEstornos(List<EstornoReducaoValorBem> estornos) {
        this.estornos = estornos;
    }

    public LogReducaoOuEstorno getLogReducaoOuEstorno() {
        return logReducaoOuEstorno;
    }

    public void setLogReducaoOuEstorno(LogReducaoOuEstorno logReducaoOuEstorno) {
        this.logReducaoOuEstorno = logReducaoOuEstorno;
    }

    public LoteReducaoValorBem getLoteReducaoValorBem() {
        return loteReducaoValorBem;
    }

    public void setLoteReducaoValorBem(LoteReducaoValorBem loteReducaoValorBem) {
        this.loteReducaoValorBem = loteReducaoValorBem;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public UsuarioSistema getUsuarioDoEstorno() {
        return usuarioDoEstorno;
    }

    public void setUsuarioDoEstorno(UsuarioSistema usuarioDoEstorno) {
        this.usuarioDoEstorno = usuarioDoEstorno;
    }

    public List<MsgLogReducaoOuEstorno> getMensagens() {
        return this.logReducaoOuEstorno.getMensagens();
    }

    public List<ReducaoValorBemEstornoContabil> getReducoesValorBemEstornoContabil() {
        return reducoesValorBemEstornoContabil;
    }

    public void setReducoesValorBemEstornoContabil(List<ReducaoValorBemEstornoContabil> reducoesValorBemEstornoContabil) {
        this.reducoesValorBemEstornoContabil = reducoesValorBemEstornoContabil;
    }
}
