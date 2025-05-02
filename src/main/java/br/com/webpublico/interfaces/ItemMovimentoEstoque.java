package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoOperacao;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 12/04/14
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
public interface ItemMovimentoEstoque {

    public Integer getNumeroItem();

    public Long getIdOrigem();

    public Date getDataMovimento();

    public BigDecimal getValorTotal();

    public BigDecimal getQuantidade();

    public BigDecimal getValorUnitario();

    public LocalEstoqueOrcamentario getLocalEstoqueOrcamentario();

    public LoteMaterial getLoteMaterial();

    public Material getMaterial();

    public TipoOperacao getTipoOperacao();

    public TipoEstoque getTipoEstoque();

    public String getDescricaoDaOperacao();

    public Boolean ehEstorno();

    public UnidadeOrganizacional getUnidadeOrcamentaria();

    public TipoBaixaBens getTipoBaixaBens();

    public TipoIngresso getTipoIngressoBens();
}
