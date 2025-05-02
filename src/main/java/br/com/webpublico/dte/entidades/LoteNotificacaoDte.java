package br.com.webpublico.dte.entidades;

import br.com.webpublico.dte.enums.SituacaoLoteNotificacaoDte;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com. webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import br.com.webpublico.util.anotacoes.*;
import com.beust.jcommander.internal.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Etiqueta("Emissão de Notificação")
@Entity
@Audited
public class LoteNotificacaoDte extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Registrado em")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.TIMESTAMP)
    private Date registradoEm;

    @Etiqueta("Registrado por")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private UsuarioSistema registradoPor;

    @Etiqueta("Situação")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private SituacaoLoteNotificacaoDte situacao;

    @Etiqueta("Título")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String titulo;

    @Etiqueta("Conteúdo")
    @Obrigatorio
    private String conteudo;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoteNotificacaoDocDte> documentos;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoteNotificacaoGrupoDte> grupos;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoteNotificacaoCmcDte> cadastros;

    @Etiqueta("Ciência Automática Em")
    @Pesquisavel
    @Obrigatorio
    @Positivo(permiteZero = false)
    private Integer cienciaAutomaticaEm;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public LoteNotificacaoDte() {
        super();
        documentos = Lists.newArrayList();
        grupos = Lists.newArrayList();
        cadastros = Lists.newArrayList();
        situacao = SituacaoLoteNotificacaoDte.ABERTO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getRegistradoEm() {
        return registradoEm;
    }

    public void setRegistradoEm(Date registradoEm) {
        this.registradoEm = registradoEm;
    }

    public UsuarioSistema getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(UsuarioSistema registradoPor) {
        this.registradoPor = registradoPor;
    }

    public SituacaoLoteNotificacaoDte getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoLoteNotificacaoDte situacao) {
        this.situacao = situacao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public List<LoteNotificacaoDocDte> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<LoteNotificacaoDocDte> documentos) {
        this.documentos = documentos;
    }

    public List<LoteNotificacaoGrupoDte> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<LoteNotificacaoGrupoDte> grupos) {
        this.grupos = grupos;
    }

    public List<LoteNotificacaoCmcDte> getCadastros() {
        return cadastros;
    }

    public void setCadastros(List<LoteNotificacaoCmcDte> cadastros) {
        this.cadastros = cadastros;
    }

    public Integer getCienciaAutomaticaEm() {
        return cienciaAutomaticaEm;
    }

    public void setCienciaAutomaticaEm(Integer cienciaAutomaticaEm) {
        this.cienciaAutomaticaEm = cienciaAutomaticaEm;
    }

    public boolean hasModelo(ModeloDocumentoDte modeloDocumento) {
        if (documentos != null && documentos.isEmpty()) {
            for (LoteNotificacaoDocDte documento : documentos) {
                if (documento.getModeloDocumento().equals(modeloDocumento))
                    return true;
            }
        }
        return false;
    }

    public boolean hasGrupo(GrupoContribuinteDte grupoContribuinte) {
        if (grupos != null && grupos.isEmpty()) {
            for (LoteNotificacaoGrupoDte grupo : grupos) {
                if (grupo.getGrupo().equals(grupoContribuinte))
                    return true;
            }
        }
        return false;
    }

    public boolean hasCadastro(CadastroEconomico cadastroEconomico) {
        if (cadastros != null && cadastros.isEmpty()) {
            for (LoteNotificacaoCmcDte cadastro : cadastros) {
                if (cadastro.getCadastroEconomico().equals(cadastroEconomico))
                    return true;
            }
        }
        return false;
    }

    public boolean isAberto() {
        return SituacaoLoteNotificacaoDte.ABERTO.equals(situacao);
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
