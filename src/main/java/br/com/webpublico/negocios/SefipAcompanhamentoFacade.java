/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.rh.ParametrosRelatorioConferenciaSefip;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SefipAcompanhamentoFacade implements Serializable {

    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @Resource
    SessionContext context;
    @Resource
    private UserTransaction userTransaction;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void salvarNovo(Object obj) {
        abrirTransacao();
        em.persist(obj);
        fecharTransacao();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Object salvar(Object obj) {
        abrirTransacao();
        obj = em.merge(obj);
        fecharTransacao();
        return obj;
    }


    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void deleteSefip(Sefip s) {
        abrirTransacao();
        Query q = em.createQuery("delete from Sefip s where s.mes = :mes and s.ano = :ano and s.entidade = :entidade");
        q.setParameter("mes", s.getMes());
        q.setParameter("ano", s.getAno());
        q.setParameter("entidade", s.getEntidade());
        q.executeUpdate();
        fecharTransacao();
    }

    private boolean mesmasFolhasDePagamento(Sefip s1, Sefip s2) {
        if (s1.getSefipFolhasDePagamento().size() != s2.getSefipFolhasDePagamento().size()) {
            return false;
        }

        for (SefipFolhaDePagamento sfp1 : s1.getSefipFolhasDePagamento()) {
            boolean contem = false;
            for (SefipFolhaDePagamento sfp2 : s2.getSefipFolhasDePagamento()) {
                if (sfp1.getFolhaDePagamento().equals(sfp2.getFolhaDePagamento())) {
                    contem = true;
                }
            }
            if (!contem) {
                return false;
            }
        }
        return true;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void abrirTransacao() {
        userTransaction = context.getUserTransaction();
        try {
            userTransaction.begin();
        } catch (NotSupportedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            context.setRollbackOnly();
        } catch (SystemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            context.setRollbackOnly();
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void fecharTransacao() {
        em.flush();
        try {
            userTransaction.commit();
        } catch (RollbackException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            context.setRollbackOnly();
        } catch (HeuristicMixedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            context.setRollbackOnly();
        } catch (HeuristicRollbackException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            context.setRollbackOnly();
        } catch (SystemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            context.setRollbackOnly();
        }
    }


    public void jaExisteSefipGeradoMesmosParametros(Sefip sefip) {
        abrirTransacao();

        String hql = "select s from Sefip s where s.mes = :mes and s.ano = :ano and s.entidade = :entidade";
        Query q = em.createQuery(hql);
        q.setParameter("mes", sefip.getMes());
        q.setParameter("ano", sefip.getAno());
        q.setParameter("entidade", sefip.getEntidade());
        q.setMaxResults(1);
        try {
            Sefip s = (Sefip) q.getSingleResult();
            s.getSefipFolhasDePagamento().size();
            fecharTransacao();
            if (mesmasFolhasDePagamento(s, sefip)) {
                throw new ExcecaoNegocioGenerica("Já existe um arquivo gerado para entidade = " + sefip.getEntidade() + " em " + sefip.getMes() + "/" + sefip.getAno() + " e as mesmas folhas de pagamento (" + sefip.getSefipFolhasDePagamento() + ").");
            }
            return;
        } catch (NoResultException nre) {
        }
        fecharTransacao();
    }

    public byte[] gerarRelatoriArrayBytes(List<ParametrosRelatorioConferenciaSefip> relatorioConferenciaSefip, Sefip selecionado, UsuarioSistema usuarioLogado) {
        RelatorioDTO dto = montarRelatorioDTO(relatorioConferenciaSefip, selecionado, usuarioLogado);
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        dto.setApi("rh/relatorio-conferencia-sefip/sincrono/");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ResponseEntity<byte[]> exchange = new RestTemplate().exchange(configuracao.getUrlCompleta(dto.getApi() + "gerar"), HttpMethod.POST, request, byte[].class);
        return exchange.getBody();
    }

    private RelatorioDTO montarRelatorioDTO(List<ParametrosRelatorioConferenciaSefip> relatorioConferenciaSefip, Sefip selecionado, UsuarioSistema usuarioLogado) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO DE CONFERÊNCIA DO SEFIP");
        dto.adicionarParametro("USUARIO", usuarioLogado.getLogin(), false);
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "Departamento de Recursos Humanos");
        dto.adicionarParametro("NOMERELATORIO", "Conferência do Arquivo Sefip");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("REFERENCIA", selecionado.getMes() + " / " + selecionado.getAno());
        dto.adicionarParametro("ALIQUOTARAT", BigDecimal.ZERO);
        dto.adicionarParametro("TIPODEFOLHA", getDescricaoFolhasUtilizadasGeracaoArquivo(selecionado));

        dto.adicionarParametro("CONFERENCIA_SEFIP_ITENS_SERVIDOR", relatorioConferenciaSefip);
        dto.adicionarParametro("CONFERENCIA_SEFIP_ITENS", relatorioConferenciaSefip);
        return dto;
    }


    private String getDescricaoFolhasUtilizadasGeracaoArquivo(Sefip selecionado) {
        String retorno = "";
        for (SefipFolhaDePagamento folha : selecionado.getSefipFolhasDePagamento()) {
            retorno += folha.getFolhaDePagamento().getTipoFolhaDePagamento().getDescricao() + " ("+ folha.getFolhaDePagamento().getVersao() +"), ";
        }
        return retorno.substring(0, retorno.length() - 2);
    }

}
