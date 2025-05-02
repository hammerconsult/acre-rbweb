/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoITBI;
import br.com.webpublico.enums.TipoITBI;
import br.com.webpublico.interfaces.PossuidorArquivo;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class ProcessoCalculoITBI extends ProcessoCalculo implements Serializable, PossuidorArquivo {
    private Long codigo;
    private Boolean isentoImune;
    @Enumerated(EnumType.STRING)
    private TipoITBI tipoITBI;
    @Enumerated(EnumType.STRING)
    private SituacaoITBI situacao;
    @ManyToOne
    private Pessoa auditorFiscal;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    @ManyToOne
    private CadastroRural cadastroRural;
    @ManyToOne
    private TipoIsencaoITBI tipoIsencaoITBI;
    @ManyToOne
    private UsuarioSistema usuarioCancelamento;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCancelamento;
    private String motivoCancelamento;
    private String codigoVerificacao;
    private String observacao;
    @ManyToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @OneToMany(mappedBy = "processoCalculoITBI", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoITBI> calculos;
    @OneToMany(mappedBy = "processoCalculoITBI", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private List<RetificacaoCalculoITBI> retificacoes;
    @ManyToOne
    private SolicitacaoItbiOnline solicitacaoItbiOnline;

    public ProcessoCalculoITBI() {
        super();
        this.calculos = Lists.newArrayList();
        this.retificacoes = Lists.newArrayList();
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Boolean getIsentoImune() {
        return isentoImune != null ? isentoImune : Boolean.FALSE;
    }

    public void setIsentoImune(Boolean isentoImune) {
        this.isentoImune = isentoImune;
    }

    public TipoITBI getTipoITBI() {
        return tipoITBI;
    }

    public void setTipoITBI(TipoITBI tipoITBI) {
        this.tipoITBI = tipoITBI;
    }

    public SituacaoITBI getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoITBI situacao) {
        this.situacao = situacao;
    }

    public Pessoa getAuditorFiscal() {
        return auditorFiscal;
    }

    public void setAuditorFiscal(Pessoa auditorFiscal) {
        this.auditorFiscal = auditorFiscal;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public CadastroRural getCadastroRural() {
        return cadastroRural;
    }

    public void setCadastroRural(CadastroRural cadastroRural) {
        this.cadastroRural = cadastroRural;
    }

    public TipoIsencaoITBI getTipoIsencaoITBI() {
        return tipoIsencaoITBI;
    }

    public void setTipoIsencaoITBI(TipoIsencaoITBI tipoIsencaoITBI) {
        this.tipoIsencaoITBI = tipoIsencaoITBI;
    }

    public UsuarioSistema getUsuarioCancelamento() {
        return usuarioCancelamento;
    }

    public void setUsuarioCancelamento(UsuarioSistema usuarioCancelamento) {
        this.usuarioCancelamento = usuarioCancelamento;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    @Override
    public List<CalculoITBI> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoITBI> calculos) {
        this.calculos = calculos;
    }

    public List<CalculoITBI> getCalculosOrdenados(Boolean desc) {
        ordenarCalculos(desc);
        return calculos;
    }

    public List<RetificacaoCalculoITBI> getRetificacoes() {
        return retificacoes;
    }

    public void setRetificacoes(List<RetificacaoCalculoITBI> retificacoes) {
        this.retificacoes = retificacoes;
    }

    public Cadastro getCadastro() {
        return cadastroImobiliario != null ? cadastroImobiliario : (cadastroRural != null ? cadastroRural : null);
    }

    public Long getIdCadastro() {
        return getCadastro() != null ? getCadastro().getId() : null;
    }

    public String getInscricao() {
        return cadastroImobiliario != null ? cadastroImobiliario.getInscricaoCadastral() :
            (cadastroRural != null ? cadastroRural.getNumeroCadastro() : "");
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean isImobiliario() {
        return cadastroImobiliario != null;
    }

    public CalculoITBI getUltimoCalculo() {
        if (calculos != null && !calculos.isEmpty()) {
            ordenarCalculos(true);
            return calculos.get(0);
        }
        return null;
    }

    private void ordenarCalculos(Boolean desc) {
        calculos.sort(new Comparator<CalculoITBI>() {
            @Override
            public int compare(CalculoITBI c1, CalculoITBI c2) {
                return desc
                    ? c2.getOrdemCalculo().compareTo(c1.getOrdemCalculo())
                    : c1.getOrdemCalculo().compareTo(c2.getOrdemCalculo());
            }
        });
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public SolicitacaoItbiOnline getSolicitacaoItbiOnline() {
        return solicitacaoItbiOnline;
    }

    public void setSolicitacaoItbiOnline(SolicitacaoItbiOnline solicitacaoItbiOnline) {
        this.solicitacaoItbiOnline = solicitacaoItbiOnline;
    }
}
