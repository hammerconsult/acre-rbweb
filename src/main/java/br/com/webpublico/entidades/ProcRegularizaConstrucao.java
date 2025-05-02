package br.com.webpublico.entidades;

import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ResponsavelServico;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Tributario")
@Entity
@Audited
@Etiqueta("Processo de Regularização de Construção")
public class ProcRegularizaConstrucao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data de Criação do Processo")
    private Date dataCriacao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ano do Protocolo")
    private String anoProtocolo;
    @Etiqueta("Usuário Responsável")
    @ManyToOne
    private UsuarioSistema usuarioIncluiu;
    private String observacao;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Imóvel")
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    @Obrigatorio
    @Etiqueta("Data de Início da Obra")
    @Temporal(TemporalType.DATE)
    private Date dataInicioObra;
    @Etiqueta("Data de Conclusão da Obra")
    @Temporal(TemporalType.DATE)
    private Date dataFimObra;
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("CEI")
    private String matriculaINSS;
    @Obrigatorio
    @Etiqueta("Responsável Pelo Projeto")
    @ManyToOne
    private ResponsavelServico responsavelProjeto;
    @Obrigatorio
    @Etiqueta("ART/RRT do Projeto")
    private String artRrtAutorProjeto;
    @Obrigatorio
    @Etiqueta("Responsável Pela Execução")
    @ManyToOne
    private ResponsavelServico responsavelExecucao;
    @Obrigatorio
    @Etiqueta("ART/RRT de Execução")
    private String artRrtResponsavelExecucao;
    @Etiqueta("Situação")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private Situacao situacao;
    @Etiqueta("Alvaras de Construção")
    @OneToMany(mappedBy = "procRegularizaConstrucao", cascade = CascadeType.ALL)
    @org.hibernate.annotations.OrderBy(clause = "dataExpedicao desc")
    private List<AlvaraConstrucao> alvarasDeConstrucao;
    private BigDecimal areaExistente;
    private BigDecimal aConstruir;
    private BigDecimal aDemolir;

    public ProcRegularizaConstrucao() {
        this.situacao = Situacao.EM_ABERTO;
    }

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

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public UsuarioSistema getUsuarioIncluiu() {
        return usuarioIncluiu;
    }

    public void setUsuarioIncluiu(UsuarioSistema usuarioIncluiu) {
        this.usuarioIncluiu = usuarioIncluiu;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public Date getDataInicioObra() {
        return dataInicioObra;
    }

    public void setDataInicioObra(Date dataInicioObra) {
        this.dataInicioObra = dataInicioObra;
    }

    public Date getDataFimObra() {
        return dataFimObra;
    }

    public void setDataFimObra(Date dataFimObra) {
        this.dataFimObra = dataFimObra;
    }

    public String getMatriculaINSS() {
        return matriculaINSS;
    }

    public void setMatriculaINSS(String matriculaINSS) {
        this.matriculaINSS = matriculaINSS;
    }

    public ResponsavelServico getResponsavelProjeto() {
        return responsavelProjeto;
    }

    public void setResponsavelProjeto(ResponsavelServico responsavelProjeto) {
        this.responsavelProjeto = responsavelProjeto;
    }

    public String getArtRrtAutorProjeto() {
        return artRrtAutorProjeto;
    }

    public void setArtRrtAutorProjeto(String artRrtAutorProjeto) {
        this.artRrtAutorProjeto = artRrtAutorProjeto;
    }

    public ResponsavelServico getResponsavelExecucao() {
        return responsavelExecucao;
    }

    public void setResponsavelExecucao(ResponsavelServico responsavelExecucao) {
        this.responsavelExecucao = responsavelExecucao;
    }

    public String getArtRrtResponsavelExecucao() {
        return artRrtResponsavelExecucao;
    }

    public void setArtRrtResponsavelExecucao(String artRrtResponsavelExecucao) {
        this.artRrtResponsavelExecucao = artRrtResponsavelExecucao;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public BigDecimal getaConstruir() {
        return aConstruir;
    }

    public void setaConstruir(BigDecimal aConstruir) {
        this.aConstruir = aConstruir;
    }

    public BigDecimal getaDemolir() {
        return aDemolir;
    }

    public void setaDemolir(BigDecimal aDemolir) {
        this.aDemolir = aDemolir;
    }

    public List<AlvaraConstrucao> getAlvarasDeConstrucao() {
        if (alvarasDeConstrucao == null) {
            alvarasDeConstrucao = Lists.newArrayList();
        }
        return alvarasDeConstrucao;
    }

    public void setAlvarasDeConstrucao(List<AlvaraConstrucao> alvarasDeConstrucao) {
        this.alvarasDeConstrucao = alvarasDeConstrucao;
    }

    public BigDecimal getAreaExistente() {
        return areaExistente;
    }

    public void setAreaExistente(BigDecimal areaExistente) {
        this.areaExistente = areaExistente;
    }

    public AlvaraConstrucao getUltimoAlvara() {
        return getUltimoAlvara(false);
    }

    public AlvaraConstrucao getUltimoAlvara(boolean considerarVencidos, AlvaraConstrucao.Situacao... situacoes) {
        for (AlvaraConstrucao alvara : alvarasDeConstrucao) {
            if (situacoes != null && situacoes.length > 0) {
                boolean situacaoValida = true;
                for (AlvaraConstrucao.Situacao situacoe : situacoes) {
                    if (!alvara.getSituacao().equals(situacoe)) {
                        situacaoValida = false;
                        break;
                    }
                }
                if (!situacaoValida) {
                    continue;
                }
            }
            boolean vencido = alvara.getDataVencimentoCartaz() != null && (DataUtil.dataSemHorario(alvara.getDataVencimentoCartaz()).compareTo(new Date()) <= 0 && !alvara.getSituacao().equals(AlvaraConstrucao.Situacao.FINALIZADO));
            if (vencido && !considerarVencidos) {
                continue;
            }
            return alvara;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(codigo);
        if (!Strings.isNullOrEmpty(anoProtocolo) && !Strings.isNullOrEmpty(numeroProtocolo)) {
            builder
                .append(" - ")
                .append(numeroProtocolo)
                .append("/")
                .append(anoProtocolo);
        }
        builder.append(" - ").append(cadastroImobiliario.toString());
        builder.append(" - ").append(situacao.getDescricao());
        return builder.toString();
    }

    public enum Situacao implements EnumComDescricao {
        EM_ABERTO("Em Aberto"),
        AGUARDANDO_ALVARA_CONSTRUCAO("Aguardando Alvará de Construção"),
        ALVARA_CONSTRUCAO("Alvará de Construção"),
        AGUARDANDO_TAXA_VISTORIA("Aguardando Taxa de Vistoria"),
        TAXA_VISTORIA("Taxa de Vistoria"),
        AGUARDANDO_HABITESE("Aguardando Habite-se"),
        HABITESE("Habite-se"),
        FINALIZADO("Finalizado");

        private final String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }
}
