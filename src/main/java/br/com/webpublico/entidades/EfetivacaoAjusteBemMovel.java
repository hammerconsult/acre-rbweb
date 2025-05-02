package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoAjusteBemMovel;
import br.com.webpublico.enums.TipoAjusteBemMovel;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 08/03/2018.
 */

@Entity
@Audited
@Etiqueta("Efetivação de Ajuste de Bens Móveis")
public class EfetivacaoAjusteBemMovel extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Efetivação")
    @Temporal(TemporalType.DATE)
    private Date dataEfetivacao;

    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Etiqueta("Solicitação")
    private SolicitacaoAjusteBemMovel solicitacaoAjusteBemMovel;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    @Length(maximo = 3000)
    private String descricao;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Ajuste")
    private TipoAjusteBemMovel tipoAjusteBemMovel;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Responsável")
    private UsuarioSistema usuarioEfetivacao;

    @ManyToOne
    @Etiqueta("Unidade Administrativa")
    private UnidadeOrganizacional unidadeAdministrativa;

    @Invisivel
    @OneToMany(mappedBy = "efetivacaoAjusteBemMovel")
    private List<ItemEfetivacaoAjusteBemMovel> itensEfetivacao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public EfetivacaoAjusteBemMovel() {
        itensEfetivacao = Lists.newArrayList();
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Date dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    public SolicitacaoAjusteBemMovel getSolicitacaoAjusteBemMovel() {
        return solicitacaoAjusteBemMovel;
    }

    public void setSolicitacaoAjusteBemMovel(SolicitacaoAjusteBemMovel solicitacaoAjusteBemMovel) {
        this.solicitacaoAjusteBemMovel = solicitacaoAjusteBemMovel;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoAjusteBemMovel getTipoAjusteBemMovel() {
        return tipoAjusteBemMovel;
    }

    public void setTipoAjusteBemMovel(TipoAjusteBemMovel tipoAjusteBemMovel) {
        this.tipoAjusteBemMovel = tipoAjusteBemMovel;
    }

    public UsuarioSistema getUsuarioEfetivacao() {
        return usuarioEfetivacao;
    }

    public void setUsuarioEfetivacao(UsuarioSistema usuarioEfetivacao) {
        this.usuarioEfetivacao = usuarioEfetivacao;
    }

    public List<ItemEfetivacaoAjusteBemMovel> getItensEfetivacao() {
        return itensEfetivacao;
    }

    public void setItensEfetivacao(List<ItemEfetivacaoAjusteBemMovel> itensEfetivacao) {
        this.itensEfetivacao = itensEfetivacao;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public boolean isSolicitacaoRejeitada() {
        return solicitacaoAjusteBemMovel !=null && solicitacaoAjusteBemMovel.isSolicitacaoRejeitada();
    }

    public boolean isSolicitacaoAceita() {
        return solicitacaoAjusteBemMovel !=null && solicitacaoAjusteBemMovel.isSolicitacaoAceita();
    }

    @Override
    public String toString() {
        try {
            return "Nº " + codigo
                + " - " + descricao
                + " - " + DataUtil.getDataFormatada(dataEfetivacao);
        } catch (NullPointerException e) {
            return "";
        }
    }

    public TipoOperacao getTipoOperacao() {
        if (getSolicitacaoAjusteBemMovel() != null && getSolicitacaoAjusteBemMovel().getOperacaoAjusteBemMovel() != null) {
            if (OperacaoAjusteBemMovel.retornaOperacoesAumentativo().contains(getSolicitacaoAjusteBemMovel().getOperacaoAjusteBemMovel())) {
                return TipoOperacao.DEBITO;
            } else {
                return TipoOperacao.CREDITO;
            }
        }
        throw new ExcecaoNegocioGenerica("Operação de ajuste não encontrada.");
    }

    public TipoEventoBem getTipoEventoBem() {
        if (solicitacaoAjusteBemMovel != null) {
            switch (solicitacaoAjusteBemMovel.getOperacaoAjusteBemMovel()) {
                case AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO:
                    return TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO;

                case AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA:
                    return TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA;

                case AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO:
                    return TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO;

                case AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO_EMPRESA_PUBLICA:
                    return TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO_EMPRESA_PUBLICA;

                case AJUSTE_BEM_MOVEL_DEPRECIACAO_AUMENTATIVO:
                    return TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_DEPRECIACAO_AUMENTATIVO;

                case AJUSTE_BEM_MOVEL_DEPRECIACAO_DIMINUTIVO:
                    return TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_DEPRECIACAO_DIMINUTIVO;

                case AJUSTE_BEM_MOVEL_AMORTIZACAO_AUMENTATIVO:
                    return TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_AMORTIZACAO_AUMENTATIVO;

                case AJUSTE_BEM_MOVEL_AMORTIZACAO_DIMINUTIVO:
                    return TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_AMORTIZACAO_DIMINUTIVO;
                default:
                    throw new ExcecaoNegocioGenerica("Operação de ajuste não encontrada para definir o tipo de evento bem.");
            }
        }
        throw new ExcecaoNegocioGenerica("Solicitação de ajuste não encontrada para definir o tipo de evento bem.");
    }
}
