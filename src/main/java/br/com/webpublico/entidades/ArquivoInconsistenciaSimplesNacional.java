package br.com.webpublico.entidades;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.LazyInitializationException;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Desenvolvimento on 19/07/2016.
 */
@Entity
@Audited
@Table(name = "ARQUIVOINCOSSIMPLESNACIONA")
@Etiqueta("Simples Nacional")
public class ArquivoInconsistenciaSimplesNacional extends SuperEntidade implements PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Código")
    @Tabelavel
    @Pesquisavel
    private Long codigo;
    @Etiqueta("Data da Operação")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @Etiqueta("Usuário")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Etiqueta("Arquivo de Importação")
    @Obrigatorio
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoImportacao;
    @OneToMany(mappedBy = "arquivo")
    private List<ItemArquivoSimplesNacional> listaItensArquivoSimplesNacional;
    @Transient
    @Invisivel
    private ArquivoComposicao arquivoProcessado;
    @OneToMany(mappedBy = "arquivoInconsistenciaSimplesNacional", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy(value = "id")
    private List<ArquivoExportadoSimplesNacional> arquivosExportados;

    public ArquivoInconsistenciaSimplesNacional() {
        this.detentorArquivoImportacao = new DetentorArquivoComposicao();
        this.listaItensArquivoSimplesNacional = Lists.newArrayList();
        this.arquivosExportados = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public DetentorArquivoComposicao getDetentorArquivoImportacao() {
        return detentorArquivoImportacao;
    }

    public void setDetentorArquivoImportacao(DetentorArquivoComposicao detentorArquivoImportacao) {
        this.detentorArquivoImportacao = detentorArquivoImportacao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public List<ItemArquivoSimplesNacional> getListaItensArquivoSimplesNacional() {
        return listaItensArquivoSimplesNacional;
    }

    public void setListaItensArquivoSimplesNacional(List<ItemArquivoSimplesNacional> listaItensArquivoSimplesNacional) {
        this.listaItensArquivoSimplesNacional = listaItensArquivoSimplesNacional;
    }

    public Boolean isCodigoVazio() {
        return this.codigo == null;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoImportacao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.setDetentorArquivoImportacao(detentorArquivoComposicao);
    }

    public Boolean isArquivoImportacaoSelecionado() {
        try {
            return !detentorArquivoImportacao.getArquivosComposicao().isEmpty();
        } catch (Exception ex) {
            return Boolean.FALSE;
        }
    }

    public ArquivoComposicao getArquivoProcessado() {
        return arquivoProcessado;
    }

    public void setArquivoProcessado(ArquivoComposicao arquivoProcessado) {
        this.arquivoProcessado = arquivoProcessado;
    }

    public List<ArquivoExportadoSimplesNacional> getArquivosExportados() {
        return arquivosExportados;
    }

    public void setArquivosExportados(List<ArquivoExportadoSimplesNacional> arquivosExportados) {
        this.arquivosExportados = arquivosExportados;
    }

    public Boolean isArquivoDoProcessoOriginal() {
        if (arquivoProcessado != null && isArquivoImportacaoSelecionado()) {
            return arquivoProcessado.equals(detentorArquivoImportacao.getArquivosComposicao().get(0));
        }
        return Boolean.FALSE;
    }

    public boolean hasArquivoExportado(ArquivoExportadoSimplesNacional.TipoExportacao tipoExportacao) {
        return arquivosExportados.stream().anyMatch(a -> tipoExportacao.equals(a.getTipoExportacao()));
    }
}
