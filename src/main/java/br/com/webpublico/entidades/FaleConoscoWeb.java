package br.com.webpublico.entidades;

import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.enums.TipoFaleConosco;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Fale Conosco")
public class FaleConoscoWeb extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Length(maximo = 255)
    @Etiqueta("Página")
    private String pagina;

    @Pesquisavel
    @Length(maximo = 255)
    @Etiqueta("Recusrso")
    private String recurso;

    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Módulo")
    private ModuloSistema modulo;

    @Temporal(TemporalType.TIMESTAMP)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data de Envio")
    private Date dataEnvio;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Tipo")
    private TipoFaleConosco tipo;

    @Length(maximo = 70)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Título")
    private String titulo;

    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Usuário")
    private UsuarioSistema usuario;

    @Temporal(TemporalType.TIMESTAMP)
    @Pesquisavel
    @Etiqueta("Data Lançamento")
    private Date dataLancamento;

    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Conteúdo")
    private String conteudo;

    @ManyToOne
    @Etiqueta("Unidade Administrativa")
    private UnidadeOrganizacional unidadeAdministrativa;

    @Transient
    @Tabelavel
    @Etiqueta("Unidade Administrativa")
    private HierarquiaOrganizacional hierarquiaAdministrativa;

    @ManyToOne
    @Pesquisavel
    @Etiqueta("Unidade Orçamentária")
    private UnidadeOrganizacional unidadeOrcamentaria;

    @Transient
    @Tabelavel
    @Etiqueta("Unidade Orçamentária")
    private HierarquiaOrganizacional hierarquiaOrcametaria;

    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Visualizado Em")
    private Date visualizadoEm;

    @Invisivel
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Invisivel
    @OneToMany(mappedBy = "faleConoscoWeb", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FaleConoscoWebResposta> respostas;

    @Invisivel
    @OneToMany(mappedBy = "faleConoscoWeb", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FaleConoscoWebOcorrencia> ocorrencias;

    public FaleConoscoWeb() {
        respostas = Lists.newArrayList();
        ocorrencias = Lists.newArrayList();
    }

    public FaleConoscoWeb(FaleConoscoWeb obj, HierarquiaOrganizacional hoAdm, HierarquiaOrganizacional hoOrc) {
        setId(obj.getId());
        setDataEnvio(obj.getDataEnvio());
        setUsuario(obj.getUsuario());
        setTitulo(obj.getTitulo());
        setTipo(obj.getTipo());
        setHierarquiaAdministrativa(hoAdm);
        setHierarquiaOrcametaria(hoOrc);
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrcametaria() {
        return hierarquiaOrcametaria;
    }

    public void setHierarquiaOrcametaria(HierarquiaOrganizacional hierarquiaOrcametaria) {
        this.hierarquiaOrcametaria = hierarquiaOrcametaria;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPagina() {
        return pagina;
    }

    public void setPagina(String pagina) {
        this.pagina = pagina;
    }

    public String getRecurso() {
        return recurso;
    }

    public void setRecurso(String recurso) {
        this.recurso = recurso;
    }

    public Date getVisualizadoEm() {
        return visualizadoEm;
    }

    public void setVisualizadoEm(Date visualizadaEm) {
        this.visualizadoEm = visualizadaEm;
    }

    public ModuloSistema getModulo() {
        return modulo;
    }

    public void setModulo(ModuloSistema modulo) {
        this.modulo = modulo;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(UnidadeOrganizacional unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public TipoFaleConosco getTipo() {
        return tipo;
    }

    public void setTipo(TipoFaleConosco tipo) {
        this.tipo = tipo;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public List<FaleConoscoWebResposta> getRespostas() {
        return respostas;
    }

    public void setRespostas(List<FaleConoscoWebResposta> respostas) {
        this.respostas = respostas;
    }

    public List<FaleConoscoWebOcorrencia> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(List<FaleConoscoWebOcorrencia> ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    @Override
    public String toString() {
        return DataUtil.getDataFormatada(dataLancamento) + " - " + tipo.getDescricao();
    }

    public void atribuirTitulo() {
        if (!Strings.isNullOrEmpty(pagina) && tipo != null) {
            if (Strings.isNullOrEmpty(titulo)) {
                titulo = tipo.getDescricao() + " para a página: " + pagina;
            }
        }
    }

    public String getConteudoReduzido() {
        int tamanho = 67;
        return conteudo.length() > tamanho ? conteudo.substring(0, tamanho) + "..." : conteudo;
    }

    public FaleConoscoWebResposta getRespostaAtual() {
        if (this.respostas != null && !this.respostas.isEmpty()) {
            Collections.sort(this.respostas);
            return this.respostas.get(this.respostas.size() - 1);
        }
        return null;
    }

    public FaleConoscoWebOcorrencia getOcorrenciaAtual() {
        if (this.ocorrencias != null && !this.ocorrencias.isEmpty()) {
            Collections.sort(this.ocorrencias);
            return this.ocorrencias.get(this.ocorrencias.size() - 1);
        }
        return null;
    }

    public Boolean isVisualizado() {
        return visualizadoEm != null;
    }
}
