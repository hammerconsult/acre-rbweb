package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Audited
@Entity
@Etiqueta("Processo Bloqueio Outorga")
@GrupoDiagrama(nome = "RBTrans")
public class BloqueioOutorga extends SuperEntidade implements PossuidorArquivo {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Etiqueta("Data de Lançamento")
    private Date lancamento;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEstorno;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private Situacao situacao;
    @ManyToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    private String motivo;
    private String motivoEstorno;
    @Obrigatorio
    @ManyToOne
    private UsuarioSistema usuarioIncluiu;
    @Etiqueta("CMC")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @OneToMany(mappedBy = "bloqueioOutorga", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ParametroBloqueioOutorga> parametros;
    @OneToMany(mappedBy = "bloqueioOutorga", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<DadoBloqueioOutorga> dadosBloqueioOutorgas;
    @Transient
    private List<DadoBloqueioOutorga> dadosBloqueioOutorgasTemporaria;
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getLancamento() {
        return lancamento;
    }

    public void setLancamento(Date lancamento) {
        this.lancamento = lancamento;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public UsuarioSistema getUsuarioIncluiu() {
        return usuarioIncluiu;
    }

    public void setUsuarioIncluiu(UsuarioSistema usuarioIncluiu) {
        this.usuarioIncluiu = usuarioIncluiu;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public List<ParametroBloqueioOutorga> getParametros() {
        if (this.parametros == null) {
            this.parametros = Lists.newArrayList();
        }
        return parametros;
    }

    public void setParametros(List<ParametroBloqueioOutorga> parametros) {
        this.parametros = parametros;
    }

    public List<DadoBloqueioOutorga> getDadosBloqueioOutorgas() {
        if (this.dadosBloqueioOutorgas == null) {
            this.dadosBloqueioOutorgas = Lists.newArrayList();
        }
        return dadosBloqueioOutorgas;
    }

    public void setDadosBloqueioOutorgas(List<DadoBloqueioOutorga> dadosBloqueioOutorgas) {
        this.dadosBloqueioOutorgas = dadosBloqueioOutorgas;
    }

    public List<DadoBloqueioOutorga> getDadosBloqueioOutorgasTemporaria() {
        if (this.dadosBloqueioOutorgasTemporaria == null) {
            this.dadosBloqueioOutorgasTemporaria = Lists.newArrayList();
        }
        return dadosBloqueioOutorgasTemporaria;
    }

    public void setDadosBloqueioOutorgasTemporaria(List<DadoBloqueioOutorga> dadosBloqueioOutorgasTemporaria) {
        this.dadosBloqueioOutorgasTemporaria = dadosBloqueioOutorgasTemporaria;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }

    public void setMotivoEstorno(String motivoEstorno) {
        this.motivoEstorno = motivoEstorno;
    }

    public BigDecimal totalBloqueado() {
        BigDecimal total = BigDecimal.ZERO;
        for (DadoBloqueioOutorga dadoBloqueioOutorga : getDadosBloqueioOutorgas()) {
            total = total.add(dadoBloqueioOutorga.getMontanteBloqueado());
        }
        return total;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public enum Situacao {
        EM_ABERTO("Em Aberto"),
        FINALIZADO("Finalizado"),
        ESTORNADO("Estornado");

        private String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
