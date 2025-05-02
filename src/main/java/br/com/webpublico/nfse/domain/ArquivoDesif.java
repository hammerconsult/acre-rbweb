package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.nfse.enums.*;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ArquivoDesif extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private UsuarioWeb enviadoPor;
    @Temporal(TemporalType.TIMESTAMP)
    private Date enviadoEm;
    @Enumerated(EnumType.STRING)
    private SituacaoArquivoDesif situacao;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @ManyToOne
    private TipoInstituicaoFinanceira tipoInstituicaoFinanceira;
    @ManyToOne
    private Cidade cidade;
    @Temporal(TemporalType.DATE)
    private Date inicioCompetencia;
    @Temporal(TemporalType.DATE)
    private Date fimCompetencia;
    @Enumerated(EnumType.STRING)
    private ModuloDesif modulo;
    @Enumerated(EnumType.STRING)
    private TipoDesif tipo;
    private String protocolo;
    @Enumerated(EnumType.STRING)
    private TipoConsolidacaoDesif tipoConsolidacao;
    private String cnpjResponsavel;
    private String versao;
    @Enumerated(EnumType.STRING)
    private TipoArredondamentoDesif tipoArredondamento;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioWeb getEnviadoPor() {
        return enviadoPor;
    }

    public void setEnviadoPor(UsuarioWeb enviadoPor) {
        this.enviadoPor = enviadoPor;
    }

    public Date getEnviadoEm() {
        return enviadoEm;
    }

    public void setEnviadoEm(Date enviadoEm) {
        this.enviadoEm = enviadoEm;
    }

    public SituacaoArquivoDesif getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoArquivoDesif situacao) {
        this.situacao = situacao;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public TipoInstituicaoFinanceira getTipoInstituicaoFinanceira() {
        return tipoInstituicaoFinanceira;
    }

    public void setTipoInstituicaoFinanceira(TipoInstituicaoFinanceira tipoInstituicaoFinanceira) {
        this.tipoInstituicaoFinanceira = tipoInstituicaoFinanceira;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public Date getInicioCompetencia() {
        return inicioCompetencia;
    }

    public void setInicioCompetencia(Date inicioCompetencia) {
        this.inicioCompetencia = inicioCompetencia;
    }

    public Date getFimCompetencia() {
        return fimCompetencia;
    }

    public void setFimCompetencia(Date fimCompetencia) {
        this.fimCompetencia = fimCompetencia;
    }

    public ModuloDesif getModulo() {
        return modulo;
    }

    public void setModulo(ModuloDesif modulo) {
        this.modulo = modulo;
    }

    public TipoDesif getTipo() {
        return tipo;
    }

    public void setTipo(TipoDesif tipo) {
        this.tipo = tipo;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public TipoConsolidacaoDesif getTipoConsolidacao() {
        return tipoConsolidacao;
    }

    public void setTipoConsolidacao(TipoConsolidacaoDesif tipoConsolidacao) {
        this.tipoConsolidacao = tipoConsolidacao;
    }

    public String getCnpjResponsavel() {
        return cnpjResponsavel;
    }

    public void setCnpjResponsavel(String cnpjResponsavel) {
        this.cnpjResponsavel = cnpjResponsavel;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public TipoArredondamentoDesif getTipoArredondamento() {
        return tipoArredondamento;
    }

    public void setTipoArredondamento(TipoArredondamentoDesif tipoArredondamento) {
        this.tipoArredondamento = tipoArredondamento;
    }
}
