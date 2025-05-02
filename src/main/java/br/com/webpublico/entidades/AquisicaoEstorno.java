package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 23/04/2018.
 */

@Entity
@Audited
@Etiqueta("Estorno de Aquisição de Bens Móveis")
public class AquisicaoEstorno extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Long numero;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Estorno")
    private Date dataEstorno;

    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Etiqueta("Usuário")
    private UsuarioSistema usuario;

    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Aquisição de Bens")
    private Aquisicao aquisicao;

    @Obrigatorio
    @Pesquisavel
    @Length(maximo = 3000)
    @Etiqueta("Motivo")
    private String motivo;

    @Etiqueta("Tipo Bem")
    @Enumerated(EnumType.STRING)
    private TipoBem tipoBem;

    @Invisivel
    @OneToMany(mappedBy = "aquisicaoEstorno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemAquisicaoEstorno> itensEstornoAquisicao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public AquisicaoEstorno() {
        itensEstornoAquisicao = Lists.newArrayList();
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public Aquisicao getAquisicao() {
        return aquisicao;
    }

    public void setAquisicao(Aquisicao aquisicao) {
        this.aquisicao = aquisicao;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public List<ItemAquisicaoEstorno> getItensEstornoAquisicao() {
        return itensEstornoAquisicao;
    }

    public void setItensEstornoAquisicao(List<ItemAquisicaoEstorno> itensEstornoAquisicao) {
        this.itensEstornoAquisicao = itensEstornoAquisicao;
    }

    @Override
    public String toString() {
        try {
            return this.aquisicao.toString();
        } catch (NullPointerException ne) {
            return "";
        }
    }
}
