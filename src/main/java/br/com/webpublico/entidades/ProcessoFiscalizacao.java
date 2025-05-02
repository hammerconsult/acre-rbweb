/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoLevantamentoFiscalizacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Claudio
 */
@Entity
@Audited
@Etiqueta("Processo de Fiscalização")
@GrupoDiagrama(nome = "fiscalizacaogeral")
public class ProcessoFiscalizacao implements Serializable, PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Código")
    @Pesquisavel
    @Tabelavel
    private String codigo;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Secretaria")
    private SecretariaFiscalizacao secretariaFiscalizacao;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Levantamento")
    @Tabelavel
    @Pesquisavel
    private TipoLevantamentoFiscalizacao tipoLevantamentoFiscalizacao;
    @Etiqueta("Pessoa")
    @ManyToOne
    private Pessoa pessoa;
    @Etiqueta("Cadastro")
    @ManyToOne
    private Cadastro cadastro;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "processoFiscalizacao", orphanRemoval = true)
    private List<SituacaoProcessoFiscal> situacoesProcessoFiscalizacao;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de cadastro")
    @Tabelavel
    private TipoCadastroTributario tipoCadastroFiscalizacao;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Pesquisavel
    private TermoAdvertencia termoAdvertencia;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private AutoInfracaoFiscalizacao autoInfracaoFiscalizacao;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data da Fiscalização")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private Date dataCadastro;
    @Transient
    private Long criadoEm;
    @OneToMany(mappedBy = "processoFiscalizacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecursoFiscalizacao> recursoFiscalizacaos;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "processoFiscalizacao")
    private List<TermoGeralFiscalizacao> termoGeralFiscalizacao;
    @ManyToOne
    private Denuncia denuncia;
    private String migracaoChave;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;
    @Etiqueta("Cadastro")
    @Tabelavel
    @Transient
    private String descricaoCadastroTabelavel;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private LocalOcorrencia localOcorrencia;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "processoFiscalizacao", orphanRemoval = true)
    private List<ItemProcessoFiscalizacao> itensProcessoFiscalizacao;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private CancelamentoProcessoFiscal cancelamentoProcessoFiscal;
    @Transient
    @Etiqueta("Situação")
    @Tabelavel
    private SituacaoProcessoFiscal situacaoProcessoFiscal;


    public ProcessoFiscalizacao() {
        this.criadoEm = System.nanoTime();
        this.situacoesProcessoFiscalizacao = new ArrayList<>();
        this.termoGeralFiscalizacao = Lists.newArrayList();
        this.localOcorrencia = new LocalOcorrencia();
    }

    public ProcessoFiscalizacao(ProcessoFiscalizacao processoFiscalizacao, SituacaoProcessoFiscal situacaoProcessoFiscal) {
        this.id = processoFiscalizacao.getId();
        this.codigo = processoFiscalizacao.getCodigo();
        this.secretariaFiscalizacao = processoFiscalizacao.getSecretariaFiscalizacao();
        this.tipoLevantamentoFiscalizacao = processoFiscalizacao.getTipoLevantamentoFiscalizacao();
        this.tipoCadastroFiscalizacao = processoFiscalizacao.getTipoCadastroFiscalizacao();
        this.dataCadastro = processoFiscalizacao.getDataCadastro();
        this.cadastro = processoFiscalizacao.getCadastro();
        this.pessoa = processoFiscalizacao.getPessoa();
        this.situacaoProcessoFiscal = situacaoProcessoFiscal;


    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public AutoInfracaoFiscalizacao getAutoInfracaoFiscalizacao() {
        return autoInfracaoFiscalizacao;
    }

    public void setAutoInfracaoFiscalizacao(AutoInfracaoFiscalizacao autoInfracaoFiscalizacao) {
        this.autoInfracaoFiscalizacao = autoInfracaoFiscalizacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<SituacaoProcessoFiscal> getSituacoesProcessoFiscalizacao() {
        return situacoesProcessoFiscalizacao;
    }

    public void setSituacoesProcessoFiscalizacao(List<SituacaoProcessoFiscal> situacoesProcessoFiscalizacao) {
        this.situacoesProcessoFiscalizacao = situacoesProcessoFiscalizacao;
    }

    public TipoCadastroTributario getTipoCadastroFiscalizacao() {
        return tipoCadastroFiscalizacao;
    }

    public void setTipoCadastroFiscalizacao(TipoCadastroTributario tipoCadastroFiscalizacao) {
        this.tipoCadastroFiscalizacao = tipoCadastroFiscalizacao;
    }

    public TipoLevantamentoFiscalizacao getTipoLevantamentoFiscalizacao() {
        return tipoLevantamentoFiscalizacao;
    }

    public void setTipoLevantamentoFiscalizacao(TipoLevantamentoFiscalizacao tipoLevantamentoFiscalizacao) {
        this.tipoLevantamentoFiscalizacao = tipoLevantamentoFiscalizacao;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public TermoAdvertencia getTermoAdvertencia() {
        return termoAdvertencia;
    }

    public void setTermoAdvertencia(TermoAdvertencia termoAdvertencia) {
        this.termoAdvertencia = termoAdvertencia;
    }

    public SecretariaFiscalizacao getSecretariaFiscalizacao() {
        return secretariaFiscalizacao;
    }

    public void setSecretariaFiscalizacao(SecretariaFiscalizacao secretariaFiscalizacao) {
        this.secretariaFiscalizacao = secretariaFiscalizacao;
    }

    public List<RecursoFiscalizacao> getRecursoFiscalizacaos() {
        if (recursoFiscalizacaos != null && recursoFiscalizacaos.size() > 0) {
            Collections.sort(recursoFiscalizacaos, new Comparator<RecursoFiscalizacao>() {
                @Override
                public int compare(RecursoFiscalizacao recurso1, RecursoFiscalizacao recurso2) {
                    return recurso1.getDataEntrada().compareTo(recurso2.getDataEntrada());
                }
            });
        }
        return recursoFiscalizacaos;
    }

    public void setRecursoFiscalizacaos(List<RecursoFiscalizacao> recursoFiscalizacaos) {
        this.recursoFiscalizacaos = recursoFiscalizacaos;
    }

    public List<TermoGeralFiscalizacao> getTermoGeralFiscalizacao() {
        return termoGeralFiscalizacao;
    }

    public void setTermoGeralFiscalizacao(List<TermoGeralFiscalizacao> termoGeralFiscalizacao) {
        this.termoGeralFiscalizacao = termoGeralFiscalizacao;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public Denuncia getDenuncia() {
        return denuncia;
    }

    public void setDenuncia(Denuncia denuncia) {
        this.denuncia = denuncia;
    }

    public String getDescricaoCadastroTabelavel() {
        return descricaoCadastroTabelavel;
    }

    public void setDescricaoCadastroTabelavel(String descricaoCadastroTabelavel) {
        this.descricaoCadastroTabelavel = descricaoCadastroTabelavel;
    }

    public LocalOcorrencia getLocalOcorrencia() {
        return localOcorrencia;
    }

    public void setLocalOcorrencia(LocalOcorrencia localOcorrencia) {
        this.localOcorrencia = localOcorrencia;
    }

    public List<ItemProcessoFiscalizacao> getItensProcessoFiscalizacao() {
        return itensProcessoFiscalizacao;
    }

    public void setItensProcessoFiscalizacao(List<ItemProcessoFiscalizacao> itensProcessoFiscalizacao) {
        this.itensProcessoFiscalizacao = itensProcessoFiscalizacao;
    }

    public CancelamentoProcessoFiscal getCancelamentoProcessoFiscal() {
        return cancelamentoProcessoFiscal;
    }

    public void setCancelamentoProcessoFiscal(CancelamentoProcessoFiscal cancelamentoProcessoFiscal) {
        this.cancelamentoProcessoFiscal = cancelamentoProcessoFiscal;
    }

    public SituacaoProcessoFiscal getSituacaoProcessoFiscal() {
        return situacaoProcessoFiscal;
    }

    public void setSituacaoProcessoFiscal(SituacaoProcessoFiscal situacaoProcessoFiscal) {
        this.situacaoProcessoFiscal = situacaoProcessoFiscal;
    }

    public BigDecimal somaValorTotalUFMItemProcesso() {
        BigDecimal retorno = BigDecimal.ZERO;
        if (this.getItensProcessoFiscalizacao() != null) {
            for (ItemProcessoFiscalizacao item : this.getItensProcessoFiscalizacao()) {
                retorno = retorno.add(item.valorTotalUFM());
            }
        }
        return retorno;
    }

    public BigDecimal somaValorTotalReaisItemProcesso(BigDecimal valorUFMVigente) {
        BigDecimal retorno = BigDecimal.ZERO;
        if (this.getItensProcessoFiscalizacao() != null) {
            for (ItemProcessoFiscalizacao item : this.getItensProcessoFiscalizacao()) {
                retorno = retorno.add(item.valorTotalUFMConvertido(valorUFMVigente));
            }
        }
        return retorno;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ProcessoFiscalizacao[ id=" + id + " ]";
    }

    public void defineDescricaoCadastroTabelavel() {
        try {
            switch (getTipoCadastroFiscalizacao()) {
                case IMOBILIARIO:
                    descricaoCadastroTabelavel = cadastro.getNumeroCadastro();
                    break;
                case ECONOMICO:
                    descricaoCadastroTabelavel = ((CadastroEconomico) cadastro).getDescricao();
                    break;
                case RURAL:
                    descricaoCadastroTabelavel = cadastro.toString();
                    break;
                case PESSOA:
                    descricaoCadastroTabelavel = pessoa.getNomeAutoComplete();
            }
        } catch (Exception e) {
            descricaoCadastroTabelavel = "";
        }
    }
}
