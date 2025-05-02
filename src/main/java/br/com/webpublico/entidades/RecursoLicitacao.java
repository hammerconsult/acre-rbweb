/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoJulgamentoRecurso;
import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.enums.TipoRecursoLicitacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * @author lucas
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Recurso Licitacao")

public class RecursoLicitacao implements Serializable, EntidadeDetendorDocumentoLicitacao {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data do Recurso")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRecurso;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Licitação")
    private Licitacao licitacao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Integer numero;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Recurso")
    private TipoRecursoLicitacao tipoRecursoLicitacao;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Interponente")
    private Pessoa interponente;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Motivo")
    private String motivo;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Observação do Recurso")
    private String observacaoRecurso;

    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data do Julgamento")
    private Date dataJulgamento;

    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Julgador")
    private PessoaFisica julgador;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Resultado")
    private TipoJulgamentoRecurso tipoJulgamentoRecurso;

    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Publicação do Recurso")
    private Date dataPublicacaoRecurso;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Meio de Comunicação")
    private String meioDeComunicacao;

    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Observação do Julgamento")
    private String observacaoJulgamento;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;

    @OneToMany(mappedBy = "recursoLicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecursoStatus> status;

    @Invisivel
    @Transient
    private RecursoStatus statusAtual;

    @Transient
    private Long criadoEm;

    public RecursoLicitacao() {
        this.dataRecurso = new Date();
        criadoEm = System.nanoTime();
        status = new ArrayList<>();
    }

    public TipoRecursoLicitacao getTipoRecursoLicitacao() {
        return tipoRecursoLicitacao;
    }

    public void setTipoRecursoLicitacao(TipoRecursoLicitacao tipoRecursoLicitacao) {
        this.tipoRecursoLicitacao = tipoRecursoLicitacao;
    }

    public Pessoa getInterponente() {
        return interponente;
    }

    public void setInterponente(Pessoa interponente) {
        this.interponente = interponente;
    }

    public Date getDataJulgamento() {
        return dataJulgamento;
    }

    public void setDataJulgamento(Date dataJulgamento) {
        this.dataJulgamento = dataJulgamento;
    }

    public Date getDataRecurso() {
        return dataRecurso;
    }

    public void setDataRecurso(Date dataRecurso) {
        this.dataRecurso = dataRecurso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservacaoJulgamento() {
        return observacaoJulgamento;
    }

    public void setObservacaoJulgamento(String observacaoJulgamento) {
        this.observacaoJulgamento = observacaoJulgamento;
    }

    @Override
    public DetentorDocumentoLicitacao getDetentorDocumentoLicitacao() {
        return detentorDocumentoLicitacao;
    }

    @Override
    public void setDetentorDocumentoLicitacao(DetentorDocumentoLicitacao detentorDocumentoLicitacao) {
        this.detentorDocumentoLicitacao = detentorDocumentoLicitacao;
    }

    @Override
    public TipoMovimentoProcessoLicitatorio getTipoAnexo() {
        return TipoMovimentoProcessoLicitatorio.RECURSO;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public TipoJulgamentoRecurso getTipoJulgamentoRecurso() {
        return tipoJulgamentoRecurso;
    }

    public void setTipoJulgamentoRecurso(TipoJulgamentoRecurso tipoJulgamentoRecurso) {
        this.tipoJulgamentoRecurso = tipoJulgamentoRecurso;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getObservacaoRecurso() {
        return observacaoRecurso;
    }

    public void setObservacaoRecurso(String observacaoRecurso) {
        this.observacaoRecurso = observacaoRecurso;
    }

    public PessoaFisica getJulgador() {
        return julgador;
    }

    public void setJulgador(PessoaFisica julgador) {
        this.julgador = julgador;
    }

    public Date getDataPublicacaoRecurso() {
        return dataPublicacaoRecurso;
    }

    public void setDataPublicacaoRecurso(Date dataPublicacaoRecurso) {
        this.dataPublicacaoRecurso = dataPublicacaoRecurso;
    }

    public String getMeioDeComunicacao() {
        return meioDeComunicacao;
    }

    public void setMeioDeComunicacao(String meioDeComunicacao) {
        this.meioDeComunicacao = meioDeComunicacao;
    }

    public List<RecursoStatus> getStatus() {
        return status;
    }

    public void setStatus(List<RecursoStatus> status) {
        this.status = status;
    }

    public RecursoStatus getStatusAtual() {
        return statusAtual;
    }

    public void setStatusAtual(RecursoStatus statusAtual) {
        this.statusAtual = statusAtual;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return this.tipoRecursoLicitacao.getDescricao();
    }

    public boolean isTipoRecursoRecurso() {
        return TipoRecursoLicitacao.RECURSO.equals(this.getTipoRecursoLicitacao());
    }

    public boolean isTipoRecursoPedidoDeImpugnacao() {
        return TipoRecursoLicitacao.PEDIDO_DE_IMPUGNACAO.equals(this.getTipoRecursoLicitacao());
    }

    public boolean isTipoRecursoEsclarecimento() {
        return TipoRecursoLicitacao.ESCLARECIMENTO.equals(this.getTipoRecursoLicitacao());
    }

    public boolean isTipoJulgamentoIndeferido() {
        return TipoJulgamentoRecurso.INDEFERIDO.equals(this.getTipoJulgamentoRecurso());
    }

    public boolean isTipoJulgamentoDeferido() {
        return TipoJulgamentoRecurso.DEFERIDO.equals(this.getTipoJulgamentoRecurso());
    }

    public boolean temTipoJulgamentoInformado() {
        return getTipoJulgamentoRecurso() != null;
    }

    public RecursoStatus getStatusAtualLicitacao() {
        try {
            Collections.sort(this.status, new Comparator<RecursoStatus>() {
                @Override
                public int compare(RecursoStatus sl1, RecursoStatus sl2) {
                    return sl1.getStatusLicitacao().getNumero().compareTo(sl2.getStatusLicitacao().getNumero());
                }
            });

            if (this.status != null && !this.status.isEmpty()) {
                setStatusAtual(this.status.get(this.status.size() - 1));
                return getStatusAtual();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isRecursoSemStatus() {
        return this.getStatus() == null || this.getStatus().isEmpty();
    }
}
