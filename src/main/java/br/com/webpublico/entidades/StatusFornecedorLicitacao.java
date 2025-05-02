package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoSituacaoFornecedorLicitacao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 20/02/14
 * Time: 18:01
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta(value = "Adjudicação e Homologação", genero = "M")
public class StatusFornecedorLicitacao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Operação")
    private TipoSituacaoFornecedorLicitacao tipoSituacao;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Número")
    private Long numero;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Situação")
    private Date dataSituacao;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Fornecedor")
    private LicitacaoFornecedor licitacaoFornecedor;

    @Obrigatorio
    @Length(maximo = 255)
    @Etiqueta("Motivo")
    private String motivo;

    @OneToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @OneToMany(mappedBy = "statusFornecedorLicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemStatusFornecedorLicitacao> itens;

    public StatusFornecedorLicitacao() {
        super();
        itens = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoSituacaoFornecedorLicitacao getTipoSituacao() {
        return tipoSituacao;
    }

    public void setTipoSituacao(TipoSituacaoFornecedorLicitacao tipoSituacao) {
        this.tipoSituacao = tipoSituacao;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataSituacao() {
        return dataSituacao;
    }

    public void setDataSituacao(Date dataSituacao) {
        this.dataSituacao = dataSituacao;
    }

    public LicitacaoFornecedor getLicitacaoFornecedor() {
        return licitacaoFornecedor;
    }

    public void setLicitacaoFornecedor(LicitacaoFornecedor licitacaoFornecedor) {
        this.licitacaoFornecedor = licitacaoFornecedor;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public List<ItemStatusFornecedorLicitacao> getItens() {
        return itens;
    }

    public void setItens(List<ItemStatusFornecedorLicitacao> itens) {
        this.itens = itens;
    }

    public boolean isHomologada() {
        return TipoSituacaoFornecedorLicitacao.HOMOLOGADA.equals(tipoSituacao);
    }

    public boolean isAdjudicada() {
        return TipoSituacaoFornecedorLicitacao.ADJUDICADA.equals(tipoSituacao);
    }

    @Override
    public String toString() {
        try {
            return numero + " - " + DataUtil.getDataFormatada(dataSituacao) + " - " + tipoSituacao.getDescricao() + " - " + motivo;
        } catch (NullPointerException e) {
            return "";
        }
    }
}
