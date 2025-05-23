/*
 * Codigo gerado automaticamente em Thu Dec 08 11:23:00 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.StatusLancePregao;
import br.com.webpublico.enums.TipoAvaliacaoLicitacao;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.administrativo.RepresentanteLicitacaoFacade;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Stateless
public class PropostaFornecedorFacade extends AbstractFacade<PropostaFornecedor> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CertameFacade certameFacade;
    @EJB
    private RepresentanteLicitacaoFacade representanteLicitacaoFacade;
    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;

    public PropostaFornecedorFacade() {
        super(PropostaFornecedor.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public PropostaFornecedor recuperar(Object id) {
        PropostaFornecedor entity = super.recuperar(id);
        entity.getListaDeItensPropostaFornecedor().size();
        entity.getLotes().size();
        if (entity.getLotes() != null) {
            for (LotePropostaFornecedor lote : entity.getLotes()) {
                lote.getItens().size();
            }
        }
        if (entity.getLicitacao() != null) {
            entity.getLicitacao().getListaDeStatusLicitacao().size();
        }
        if (entity.getDetentorDocumentoLicitacao() != null) {
            Hibernate.initialize(entity.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }

        return entity;
    }

    public PropostaFornecedor recuperarComDependenciasItens(Object id) {
        PropostaFornecedor pf = super.recuperar(id);
        Hibernate.initialize(pf.getLotes());
        for (LotePropostaFornecedor lote : pf.getLotes()) {
            Hibernate.initialize(lote.getItens());
        }
        return pf;
    }

    @Override
    public PropostaFornecedor salvarRetornando(PropostaFornecedor entity) {
        Util.validarCampos(entity);
        validarCamposObrigarios(entity);
        validarPrazos(entity);
        validarPercentualDesconto(entity);
        return super.salvarRetornando(entity);
    }

    private void validarCamposObrigarios(PropostaFornecedor selecionado) {
        ValidacaoException exception = new ValidacaoException();
        if (selecionado.getFornecedor().isPessoaJuridica()) {
            if (selecionado.getRepresentante() == null) {
                exception.adicionarMensagemDeCampoObrigatorio("o campo representante deve ser informado para fornecedor pessoa jurídica.");
            }
            if (selecionado.getInstrumentoRepresentacao() == null || selecionado.getInstrumentoRepresentacao().isEmpty()) {
                exception.adicionarMensagemDeCampoObrigatorio("O campo instrumento de representação deve ser informado para fornecedor pessoa jurídica.");
            }
        }
        exception.lancarException();
    }

    private void validarPrazos(PropostaFornecedor selecionado) {
        ValidacaoException exception = new ValidacaoException();
        if (selecionado.getValidadeDaProposta() < 0) {
            exception.adicionarMensagemDeOperacaoNaoPermitida("O campo Prazo de Validade não pode ser menor que zero.");
        }
        if (selecionado.getPrazoDeExecucao() < 0) {
            exception.adicionarMensagemDeOperacaoNaoPermitida("O campo Prazo de Execução não pode ser menor que zero.");
        }
        exception.lancarException();
    }

    private void validarPercentualDesconto(PropostaFornecedor selecionado) {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
            for (LotePropostaFornecedor lote : selecionado.getLotes()) {
                if (selecionado.getLicitacao().getTipoApuracao().isPorLote()) {
                    if (lote.getPercentualDesconto() != null && lote.getPercentualDesconto().compareTo(BigDecimal.ZERO) < 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Desconto (%)' do lote " + lote + " não pode ser menor que zero.");
                    }
                } else {
                    for (ItemPropostaFornecedor item : lote.getItens()) {
                        if (item.getPercentualDesconto() != null && item.getPercentualDesconto().compareTo(BigDecimal.ZERO) < 0) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Desconto (%)' do item " + item + " não pode ser menor que zero.");
                        }
                    }
                }
            }
            ve.lancarException();
        }
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public CertameFacade getCertameFacade() {
        return certameFacade;
    }

    public RepresentanteLicitacaoFacade getRepresentanteLicitacaoFacade() {
        return representanteLicitacaoFacade;
    }

    public List<ItemPropostaFornecedor> recuperaItensDaPropostaFornecedor(PropostaFornecedor propostaFornecedor) {
        String hql = "from ItemPropostaFornecedor item where item.propostaFornecedor = :proposta";
        Query consulta = em.createQuery(hql);
        consulta.setParameter("proposta", propostaFornecedor);
        try {
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<ItemPropostaFornecedor>();
        }

    }

    public BigDecimal buscarValorPropostaFornecedorPorLoteProcessoCompra(Pessoa fornecedor, Licitacao
        licitacao, LoteProcessoDeCompra loteProcessoDeCompra) {
        String sql = " select coalesce(lote.percentualdesconto,0) as valor from lotepropfornec lote  " +
            "  inner join propostafornecedor pf on pf.id = lote.propostafornecedor_id  " +
            "  inner join licitacao lic on lic.id = pf.licitacao_id  " +
            " where lote.loteprocessodecompra_id = :idLote " +
            "  and pf.licitacao_id = :idLicitacao " +
            "  and pf.fornecedor_id = :idFornecedor " +
            "  and lic.tipoavaliacao = :maiorDesconto " +
            " union all " +
            " select coalesce(lote.valor ,0) as valor from lotepropfornec lote  " +
            "  inner join propostafornecedor pf on pf.id = lote.propostafornecedor_id  " +
            "  inner join licitacao lic on lic.id = pf.licitacao_id  " +
            " where lote.loteprocessodecompra_id = :idLote " +
            "  and pf.licitacao_id = :idLicitacao " +
            "  and pf.fornecedor_id = :idFornecedor " +
            "  and lic.tipoavaliacao <> :maiorDesconto ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("idLote", loteProcessoDeCompra.getId());
        consulta.setParameter("idLicitacao", licitacao.getId());
        consulta.setParameter("idFornecedor", fornecedor.getId());
        consulta.setParameter("maiorDesconto", TipoAvaliacaoLicitacao.MAIOR_DESCONTO.name());
        consulta.setMaxResults(1);
        if (consulta.getResultList() != null && !consulta.getResultList().isEmpty()) {
            return (BigDecimal) consulta.getResultList().get(0);
        }
        return BigDecimal.ZERO;
    }

    public List<PropostaFornecedor> buscarPropostaFornecedorPorLicitacao(Licitacao licitacao) {
        Query q = em.createNativeQuery("select * from propostafornecedor where licitacao_id = :param", PropostaFornecedor.class).setParameter("param", licitacao.getId());
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            for (Object obj : q.getResultList()) {
                PropostaFornecedor pf = (PropostaFornecedor) obj;
                pf.getLotes().size();
            }
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<ItemPropostaFornecedor> recuperarItemPropostaFornecedorPorItemProcessoCompra(ItemProcessoDeCompra
                                                                                                 itemProcessoDeCompra) {

        String sql = " select ipf.* from itempropfornec ipf " +
            "           inner join propostafornecedor pf on pf.id = ipf.propostafornecedor_id " +
            "           inner join licitacao lic on lic.id = pf.licitacao_id " +
            "          where ipf.itemprocessodecompra_id = :idItemProcesso " +
            "           and (case " +
            "               when lic.tipoavaliacao = :maiorDesconto and  ipf.percentualdesconto is not null and  ipf.percentualdesconto > 0 then ipf.percentualdesconto " +
            "               when lic.tipoavaliacao <> :maiorDesconto and ipf.preco is not null and ipf.preco > 0 then ipf.preco else 0 end) <> 0 " +
            "         order by (case " +
            "               when lic.tipoavaliacao = :maiorDesconto and  ipf.percentualdesconto is not null and ipf.percentualdesconto > 0 then ipf.percentualdesconto " +
            "               when lic.tipoavaliacao <> :maiorDesconto and ipf.preco is not null and ipf.preco > 0 then ipf.preco end)  ";
        Query q = em.createNativeQuery(sql, ItemPropostaFornecedor.class);
        q.setParameter("idItemProcesso", itemProcessoDeCompra.getId());
        q.setParameter("maiorDesconto", TipoAvaliacaoLicitacao.MAIOR_DESCONTO.name());
        if (!q.getResultList().isEmpty()) {
            List<ItemPropostaFornecedor> list = q.getResultList();
            for (ItemPropostaFornecedor itemProposta : list) {
                itemProposta.getPropostaFornecedor().getListaDeItensPropostaFornecedor().size();
            }

            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public void atualizaValorDoLote(LotePropostaFornecedor lote) {
        BigDecimal valor = BigDecimal.ZERO;
        for (ItemPropostaFornecedor ipf : lote.getItens()) {
            if (ipf.getPreco() != null) {
                valor = valor.add(ipf.getPrecoTotal());
            }
        }
        lote.setValor(valor);
    }

    public boolean fornecedorJaFezPropostaParaEstaLicitacao(Pessoa fornecedor, Licitacao licitacao) {
        String hql = "  select pf "
            + "       from PropostaFornecedor pf "
            + "      where pf.fornecedor = :forn "
            + "        and pf.licitacao = :lic";

        Query q = em.createQuery(hql);
        q.setParameter("forn", fornecedor);
        q.setParameter("lic", licitacao);

        if (q.getResultList().isEmpty()) {
            return false;
        }

        return true;
    }

    public void criarLotesItensProposta(PropostaFornecedor entity) {
        entity.setLotes(Lists.newArrayList());
        List<LoteProcessoDeCompra> lotesProcesso = processoDeCompraFacade.buscarLotesProcessoCompra(entity.getLicitacao().getProcessoDeCompra());

        for (LoteProcessoDeCompra loteProcessoDeCompra : lotesProcesso) {
            LotePropostaFornecedor lpf = new LotePropostaFornecedor();
            lpf.setPropostaFornecedor(entity);
            lpf.setLoteProcessoDeCompra(loteProcessoDeCompra);
            lpf.setPercentualDesconto(BigDecimal.ZERO);
            lpf.setValor(BigDecimal.ZERO);

            List<ItemProcessoDeCompra> itens = processoDeCompraFacade.buscarItensProcessoCompraPorLote(loteProcessoDeCompra);
            for (ItemProcessoDeCompra itemProcessoDeCompra : itens) {
                ItemPropostaFornecedor ipf = new ItemPropostaFornecedor();
                ipf.setPropostaFornecedor(entity);
                ipf.setItemProcessoDeCompra(itemProcessoDeCompra);
                ipf.setLotePropostaFornecedor(lpf);
                ipf.setPercentualDesconto(BigDecimal.ZERO);
                lpf.setItens(Util.adicionarObjetoEmLista(lpf.getItens(), ipf));
                entity.setListaDeItensPropostaFornecedor(Util.adicionarObjetoEmLista(entity.getListaDeItensPropostaFornecedor(), ipf));
            }
            entity.setLotes(Util.adicionarObjetoEmLista(entity.getLotes(), lpf));
        }
    }

    public void validarPropostas(Licitacao licitacao, LotePropostaFornecedor lote) {
        ValidacaoException ve = new ValidacaoException();
        if (licitacao.getTipoApuracao().isPorItem()) {
            for (ItemPropostaFornecedor item : lote.getItens()) {
                validarConfirmacaoItemProposta(licitacao, item, ve);
            }
        }
        ve.lancarException();
    }

    public void validarConfirmacaoItemProposta(Licitacao licitacao, ItemPropostaFornecedor item, ValidacaoException ve) {
        validarMarca(ve, item);
        validarPreco(ve, item, licitacao);
        validarDesconto(ve, item, licitacao);
    }

    private void validarMarca(ValidacaoException ve, ItemPropostaFornecedor ipf) {
        TipoObjetoCompra tipoObjetoCompra = ipf.getItemProcessoDeCompra().getObjetoCompra().getTipoObjetoCompra();
        if (((ipf.getPreco() != null && ipf.getPreco().compareTo(BigDecimal.ZERO) > 0) || (ipf.getPercentualDesconto() != null && ipf.getPercentualDesconto().compareTo(BigDecimal.ZERO) > 0))
            && (ipf.getMarca() == null || ipf.getMarca().isEmpty() && !TipoObjetoCompra.SERVICO.equals(tipoObjetoCompra))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O item " + ipf.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getNumero() + " está com a marca não informada.");
        }
    }

    private void validarPreco(ValidacaoException ve, ItemPropostaFornecedor ipf, Licitacao licitacao) {
        if (!licitacao.getTipoAvaliacao().isMaiorDesconto()) {
            if (ipf.getPreco() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O item " + ipf.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getNumero() + " está com o preço não informado.");
            }
            if (ipf.getPreco().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O item " + ipf.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getNumero() + " não pode ser um valor negativo.");
            }
            if (!StringUtils.isEmpty(ipf.getMarca())) {
                if (ipf.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O preço do item " + ipf.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getNumero() + " está negativo ou zerado, o preço deve ser maior que 0(zero).");
                }
            }
        }
    }

    private void validarDesconto(ValidacaoException ve, ItemPropostaFornecedor ipf, Licitacao licitacao) {
        if (licitacao.getTipoAvaliacao().isMaiorDesconto()) {
            if (ipf.getPercentualDesconto().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Desconto (%)' do item " + ipf + " não pode ser menor que zero.");
            }
        }
    }

    public PropostaFornecedor recuperarPropostaDoFornecedorPorLicitacao(Pessoa fornecedor, Licitacao licitacao) {
        String sql = "select pf.* from propostafornecedor pf where pf.fornecedor_id = :idForn and pf.licitacao_id = :idLic";
        Query q = em.createNativeQuery(sql, PropostaFornecedor.class);
        q.setParameter("idForn", fornecedor.getId());
        q.setParameter("idLic", licitacao.getId());
        try {
            return (PropostaFornecedor) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<PropostaFornecedor> buscarPorLicitacao(Licitacao licitacao) {
        Query q = em.createQuery("from PropostaFornecedor where licitacao = :param").setParameter("param", licitacao);

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            for (Object obj : q.getResultList()) {
                PropostaFornecedor pf = (PropostaFornecedor) obj;
                pf.getListaDeItensPropostaFornecedor().size();
                pf.getLotes().size();
            }
        }

        return q.getResultList();
    }

    public List<PropostaFornecedor> buscarPorLicitacao(Long idLicitacao, String filtro) {
        String sql =
            "  select prop.* from propostafornecedor prop " +
                " inner join pessoa p on p.id = prop.fornecedor_id " +
                " left join pessoajuridica pj on pj.id = p.id" +
                " left join pessoafisica pf on pf.id = p.id" +
                " where (lower(translate(pj.razaoSocial,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
                "    or lower(translate(pj.nomeFantasia,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
                "    or replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :filtro" +
                "    or (lower(translate(pf.nome,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
                "    or replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like :filtro))" +
                " and prop.licitacao_id = :idLicitacao ";
        Query q = em.createNativeQuery(sql, PropostaFornecedor.class);
        q.setParameter("idLicitacao", idLicitacao);
        q.setParameter("filtro", "%" + filtro.trim() + "%");
        return q.getResultList();
    }

    public List<Pessoa> buscarFornecedorSemPropostaParaEstaLicitacao(String filtro, Licitacao licitacao) {
        String sql =
            "  SELECT  F.* FROM LICITACAOFORNECEDOR F" +
                " inner join pessoa p on p.id = F.EMPRESA_ID" +
                " left join pessoajuridica pj on pj.id = p.id" +
                " left join pessoafisica pf on pf.id = p.id" +
                " WHERE NOT EXISTS(SELECT 1 " +
                "                        FROM PROPOSTAFORNECEDOR prop " +
                "                      WHERE prop.FORNECEDOR_id = p.id " +
                "                        and prop.licitacao_id = :lic)" +
                " and (lower(translate(pj.razaoSocial,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
                "    or lower(translate(pj.nomeFantasia,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
                "    or replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :filtro" +
                "    or (lower(translate(pf.nome,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
                "    or replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like :filtro))" +
                " and p.SITUACAOCADASTRALPESSOA = :ativo" +
                " and f.licitacao_id = :lic";
        Query q = em.createNativeQuery(sql, LicitacaoFornecedor.class);
        q.setParameter("lic", licitacao.getId());
        q.setParameter("filtro", "%" + filtro.trim() + "%");
        q.setParameter("ativo", SituacaoCadastralPessoa.ATIVO.name());
        Set<Pessoa> listaPessoa = new HashSet<>();
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            List<LicitacaoFornecedor> lista = q.getResultList();
            for (LicitacaoFornecedor licitacaoFornecedor : lista) {
                listaPessoa.add(licitacaoFornecedor.getEmpresa());
            }
        }
        return Lists.newArrayList(listaPessoa);
    }

    public List<PropostaFornecedor> buscarPropostaVencedorasLancePregaoPorLicitacao(String parte, Licitacao
        licitacao) {
        if (licitacao == null) {
            return Lists.newArrayList();
        }
        String sql = " " +
            " select distinct proposta.* from pregao pre " +
            "   inner join itempregao item on item.pregao_id = pre.id " +
            "   left join ITPREITPRO itemPro on itemPro.itempregao_id = item.id " +
            "   left join ITPRELOTPRO lotePro on lotePro.itempregao_id = item.id " +
            "   inner join itempregaolancevencedor iplv on iplv.id = item.ITEMPREGAOLANCEVENCEDOR_ID " +
            "   inner join LANCEPREGAO lance on lance.id = iplv.LANCEPREGAO_ID " +
            "   inner join propostafornecedor proposta on proposta.id = lance.propostafornecedor_id " +
            "   inner join pessoa p on p.id = proposta.FORNECEDOR_ID " +
            "   left join pessoajuridica pj on pj.id = p.id " +
            "   left join pessoafisica pf on pf.id = p.id " +
            " where pre.licitacao_id = :idLicitacao " +
            "   and iplv.STATUS = :vencedor " +
            "   and (lower(translate(pj.razaoSocial,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
            "    or lower(translate(pj.nomeFantasia,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
            "    or replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :filtro" +
            "    or (lower(translate(pf.nome,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
            "    or replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like :filtro))";
        Query q = em.createNativeQuery(sql, PropostaFornecedor.class);
        q.setParameter("idLicitacao", licitacao.getId());
        q.setParameter("vencedor", StatusLancePregao.VENCEDOR.name());
        q.setParameter("filtro", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public BigDecimal buscarValorItemProposta(PropostaFornecedor propostaFornecedor, ItemProcessoDeCompra itemProcessoCompra) {
        String sql = " " +
            " select coalesce(item.preco, item.percentualdesconto) " +
            "   from itempropfornec item " +
            "         inner join lotepropfornec lote on lote.id = item.lotepropostafornecedor_id " +
            "         inner join propostafornecedor prop on prop.id = lote.propostafornecedor_id " +
            "   where item.itemprocessodecompra_id = :idItemProcesso " +
            "   and prop.id = :idProposta ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("idItemProcesso", itemProcessoCompra.getId());
        consulta.setParameter("idProposta", propostaFornecedor.getId());
        consulta.setMaxResults(1);
        if (consulta.getResultList() != null && !consulta.getResultList().isEmpty()) {
            return (BigDecimal) consulta.getResultList().get(0);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal buscarValorLoteProposta(PropostaFornecedor propostaFornecedor, LoteProcessoDeCompra loteProcessoDeCompra) {
        String sql = " " +
            "   select coalesce(lote.valor, lote.percentualdesconto) " +
            "   from itempropfornec item " +
            "     inner join lotepropfornec lote on lote.id = item.lotepropostafornecedor_id " +
            "     inner join propostafornecedor prop on prop.id = lote.propostafornecedor_id " +
            "   where lote.loteprocessodecompra_id = :idLoteProcesso " +
            "   and prop.id = :idProposta ";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("idLoteProcesso", loteProcessoDeCompra.getId());
        consulta.setParameter("idProposta", propostaFornecedor.getId());
        consulta.setMaxResults(1);
        if (consulta.getResultList() != null && !consulta.getResultList().isEmpty()) {
            return (BigDecimal) consulta.getResultList().get(0);
        }
        return BigDecimal.ZERO;
    }
}
