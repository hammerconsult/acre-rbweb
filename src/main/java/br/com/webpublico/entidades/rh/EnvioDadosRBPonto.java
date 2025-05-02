package br.com.webpublico.entidades.rh;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.rh.TipoInformacaoEnvioRBPonto;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.beust.jcommander.internal.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
@Entity
@Etiqueta("Envio Dados RBPonto")
public class EnvioDadosRBPonto extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Unidade Organizacional")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    @Etiqueta("Servidor")
    private ContratoFP contratoFP;
    @Etiqueta("Data Inicial")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataInicial;
    @Etiqueta("Data Final")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataFinal;
    @Etiqueta("Data de Envio")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEnvio;
    @Etiqueta("Responsável")
    @ManyToOne
    private UsuarioSistema usuario;
    @Enumerated(EnumType.STRING)
    private TipoInformacaoEnvioRBPonto tipoInformacaoEnvioRBPonto;
    @OneToMany(mappedBy = "envioDadosRBPonto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEnvioDadosRBPonto> itensEnvioDadosRBPontos;
    private Boolean apenasNaoEnviados;

    public EnvioDadosRBPonto() {
        this.itensEnvioDadosRBPontos = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public TipoInformacaoEnvioRBPonto getTipoInformacaoEnvioRBPonto() {
        return tipoInformacaoEnvioRBPonto;
    }

    public void setTipoInformacaoEnvioRBPonto(TipoInformacaoEnvioRBPonto tipoInformacaoEnvioRBPonto) {
        this.tipoInformacaoEnvioRBPonto = tipoInformacaoEnvioRBPonto;
    }

    public List<ItemEnvioDadosRBPonto> getItensEnvioDadosRBPontos() {
        return itensEnvioDadosRBPontos;
    }

    public void setItensEnvioDadosRBPontos(List<ItemEnvioDadosRBPonto> itensEnvioDadosRBPontos) {
        this.itensEnvioDadosRBPontos = itensEnvioDadosRBPontos;
    }

    public Boolean getApenasNaoEnviados() {
        return apenasNaoEnviados != null ? apenasNaoEnviados : false;
    }

    public void setApenasNaoEnviados(Boolean apenasNaoEnviados) {
        this.apenasNaoEnviados = apenasNaoEnviados;
    }
}
