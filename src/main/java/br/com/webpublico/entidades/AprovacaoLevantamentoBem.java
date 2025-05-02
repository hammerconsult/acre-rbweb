package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Julio Cesar
 * Date: 23/04/14
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Aprovação de Levantamento de Bens Patrimoniais")
public class AprovacaoLevantamentoBem extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "aprovacaoLevantamentoBem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemAprovacaoLevantamento> listaItemAprovacaoLevantamentos;

    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data da Aprovação")
    private Date dataAprovacao;

    @ManyToOne
    @Etiqueta("Aprovador")
    private UsuarioSistema aprovador;

    @Tabelavel
    @Transient
    @Etiqueta("Aprovador")
    private String descricaoAprovador;

    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Tabelavel
    @Transient
    @Etiqueta("Unidade Organizacional")
    private String descricaoUnidade;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoEventoBem situacao;

    public AprovacaoLevantamentoBem() {
        super();
        listaItemAprovacaoLevantamentos = Lists.newArrayList();
        situacao = SituacaoEventoBem.CONCLUIDO;
    }

    public String getDescricaoAprovador() {
        return descricaoAprovador;
    }

    public void setDescricaoAprovador(String descricaoAprovador) {
        this.descricaoAprovador = descricaoAprovador;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemAprovacaoLevantamento> getListaItemAprovacaoLevantamentos() {
        return listaItemAprovacaoLevantamentos;
    }

    public void setListaItemAprovacaoLevantamentos(List<ItemAprovacaoLevantamento> listaItemAprovacaoLevantamentos) {
        this.listaItemAprovacaoLevantamentos = listaItemAprovacaoLevantamentos;
    }

    public Date getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(Date dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    public UsuarioSistema getAprovador() {
        return aprovador;
    }

    public void setAprovador(UsuarioSistema aprovador) {
        this.aprovador = aprovador;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public SituacaoEventoBem getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoEventoBem situacao) {
        this.situacao = situacao;
    }

    public void aprovarLevantamentos(List<LevantamentoBemPatrimonial> levantamentosSelecionados, List<LevantamentoBemPatrimonial> levantamentosInconsistentes, Date dataDeCorte) {
        ValidacaoException ve = new ValidacaoException();
        levantamentosInconsistentes.clear();
        String erro;

        if (levantamentosSelecionados == null || levantamentosSelecionados.isEmpty()) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Não existem levantamentos para serem aprovados.");
            throw ve;
        }

        if(requerDataDeCorte(levantamentosSelecionados) && dataDeCorte == null){
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Para realizar a aprovação do(s) levantamento(s), a data de corte deve ser definida nos parâmetros do patrimônio.");
            throw ve;
        }

        for (LevantamentoBemPatrimonial levantamento : levantamentosSelecionados) {
            if (levantamento.requerVerificacaoDataDeCorteParaAprovacao()
                    && (DataUtil.dataSemHorario(levantamento.getDataAquisicao()).compareTo(DataUtil.dataSemHorario(dataDeCorte)) > 0)) {
                erro = "A data de aquisição do bem de código " + levantamento.getCodigoPatrimonio() + " é posterior a data de corte definida nos parâmetros do patrimônio.";
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, erro);
                levantamento.setErro(erro);
                levantamentosInconsistentes.add(levantamento);
                continue;
            } else {
                listaItemAprovacaoLevantamentos.add(new ItemAprovacaoLevantamento(this, levantamento));
            }
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private boolean requerDataDeCorte(List<LevantamentoBemPatrimonial> levantamentosSelecionados) {
        for (LevantamentoBemPatrimonial ls : levantamentosSelecionados) {
            if(ls.requerVerificacaoDataDeCorteParaAprovacao()){
                return true;
            }
        }

        return false;
    }

    public Boolean getEstornado(){
        return SituacaoEventoBem.ESTORNADO.equals(this.situacao);
    }
}
