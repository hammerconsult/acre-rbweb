package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.DAMResultadoParcela;
import br.com.webpublico.enums.SituacaoMarcaFogo;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoEmissaoMarcaFogo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Audited
public class MarcaFogo extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date dataLancamento;

    private Long codigo;

    @Obrigatorio
    @Etiqueta("Exercício")
    @ManyToOne
    private Exercicio exercicio;

    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @Obrigatorio
    @Etiqueta("Tipo de Cadastro")
    @Enumerated(EnumType.STRING)
    private TipoCadastroTributario tipoCadastroTributario;

    @ManyToOne
    private Cadastro cadastro;

    @ManyToOne
    private Pessoa pessoa;

    @ManyToOne
    private Pessoa procurador;

    @Etiqueta("Sigla")
    private String sigla;

    @Obrigatorio
    @Etiqueta("Logo")
    @ManyToOne(cascade = CascadeType.ALL)
    private Arquivo logo;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ano do Protocolo")
    private String anoProtocolo;

    @Obrigatorio
    @Etiqueta("Descrição da Marca de Fogo")
    private String descricaoMarcaFogo;

    @Enumerated(EnumType.STRING)
    private SituacaoMarcaFogo situacao;

    @OneToMany(mappedBy = "marcaFogo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnexoMarcaFogo> anexos;

    @OneToMany(mappedBy = "marcaFogo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DebitoMarcaFogo> debitos;

    @OneToMany(mappedBy = "marcaFogo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CadastroRuralMarcaFogo> cadastrosRurais;

    @Transient
    private boolean temProcurador;

    public MarcaFogo() {
        super();
        temProcurador = false;
        this.anexos = Lists.newArrayList();
        this.debitos = Lists.newArrayList();
        this.cadastrosRurais = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Pessoa getProcurador() {
        return procurador;
    }

    public void setProcurador(Pessoa procurador) {
        this.procurador = procurador;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public Arquivo getLogo() {
        return logo;
    }

    public void setLogo(Arquivo logo) {
        this.logo = logo;
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

    public String getDescricaoMarcaFogo() {
        return descricaoMarcaFogo;
    }

    public void setDescricaoMarcaFogo(String descricaoMarcaFogo) {
        this.descricaoMarcaFogo = descricaoMarcaFogo;
    }

    public SituacaoMarcaFogo getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoMarcaFogo situacao) {
        this.situacao = situacao;
    }

    public List<AnexoMarcaFogo> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<AnexoMarcaFogo> anexos) {
        this.anexos = anexos;
    }

    public List<DebitoMarcaFogo> getDebitos() {
        return debitos;
    }

    public void setDebitos(List<DebitoMarcaFogo> debitos) {
        this.debitos = debitos;
    }

    public boolean getTemProcurador() {
        return temProcurador;
    }

    public void setTemProcurador(boolean temProcurador) {
        this.temProcurador = temProcurador;
    }

    public List<CadastroRuralMarcaFogo> getCadastrosRurais() {
        return cadastrosRurais;
    }

    public void setCadastrosRurais(List<CadastroRuralMarcaFogo> cadastrosRurais) {
        this.cadastrosRurais = cadastrosRurais;
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (tipoCadastroTributario != null) {
            if ((!tipoCadastroTributario.isPessoa() && cadastro == null)
                || (tipoCadastroTributario.isPessoa() && pessoa == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Cadastro deve ser informado.");
            }
        }
        if (temProcurador && procurador == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Procurador deve ser informado.");
        }
        if (cadastrosRurais.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Deve ser informado pelo menos um cadastro rural.");
        }
        validarAnexos(ve);
        ve.lancarException();
    }

    private void validarAnexos(ValidacaoException ve) {
        if (anexos.isEmpty()) return;
        for (AnexoMarcaFogo anexo : anexos) {
            if (anexo.getDocumentoMarcaFogo().getObrigatorio() && anexo.getArquivo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O documento " +
                    anexo.getDocumentoMarcaFogo().getDescricao() + " deve ser anexado.");
            }
        }
    }

    public boolean hasDebitosPorTipoEmissao(TipoEmissaoMarcaFogo tipoEmissao) {
        return debitos != null && debitos.stream().anyMatch(d -> tipoEmissao.equals(d.getTipoEmissao()));
    }

    public boolean hasDebitoEmAberto() {
        return debitos != null &&
            debitos.stream().anyMatch(d -> d.getListaDAMResultadoParcela().stream().anyMatch(dr -> dr.getResultadoParcela().isEmAberto()));
    }

    public List<DAMResultadoParcela> getListaDAMResultadoParcela() {
        List<DAMResultadoParcela> listaDAMResultadoParcela = Lists.newArrayList();
        for (DebitoMarcaFogo debitoMarcaFogo : debitos) {
            listaDAMResultadoParcela.addAll(debitoMarcaFogo.getListaDAMResultadoParcela());
        }
        return listaDAMResultadoParcela;
    }

    public List<DebitoMarcaFogo> getDebitoPorTipoEmissao(TipoEmissaoMarcaFogo tipoEmissao) {
        return debitos.stream().filter(d -> d.getTipoEmissao().equals(tipoEmissao)).collect(Collectors.toList());
    }

    public DocumentoOficial getDocumentoOficial() {
        List<DebitoMarcaFogo> debitosMarcaFogo = getDebitoPorTipoEmissao(TipoEmissaoMarcaFogo.SEGUNDA_VIA);
        if (!debitosMarcaFogo.isEmpty()) {
            return debitosMarcaFogo.get(0).getDocumentoOficial();
        } else {
            debitosMarcaFogo = getDebitoPorTipoEmissao(TipoEmissaoMarcaFogo.PRIMEIRA_VIA);
            if (debitosMarcaFogo.isEmpty()) return null;
            return debitosMarcaFogo.get(0).getDocumentoOficial();
        }
    }

    public boolean hasCadastroRural(CadastroRuralMarcaFogo novo) {
        if (!getCadastrosRurais().isEmpty()) {
            for (CadastroRuralMarcaFogo cadastroRuralMarcaFogo : getCadastrosRurais()) {
                if (cadastroRuralMarcaFogo.getCadastroRural().getId().equals(novo.getCadastroRural().getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void validarNovoCadastroRural(CadastroRuralMarcaFogo cadastroRuralMarcaFogo) {
        ValidacaoException ve = new ValidacaoException();
        if (ve.getMensagens() == null || ve.getMensagens().isEmpty()) {
            if (hasCadastroRural(cadastroRuralMarcaFogo)) {
                ve.adicionarMensagemDeCampoObrigatorio("Cadastro Rural já registrado.");
            }
        }
        ve.lancarException();
    }

    public boolean temDebitoComDocumentoAindaNaoImpresso(TipoEmissaoMarcaFogo tipoEmissao) {
        if (debitos == null) return false;
        for (DebitoMarcaFogo debito : debitos) {
            if (debito.getTipoEmissao().equals(tipoEmissao) && !debito.isImprimiuDocumento()) return true;
        }
        return false;
    }
}
