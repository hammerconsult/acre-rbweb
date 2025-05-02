package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.SituacaoLoteBaixa;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.enums.TipoProcessoDebito;
import br.com.webpublico.enums.tributario.StatusSolicitacaoPIX;
import br.com.webpublico.negocios.tributario.dao.JdbcDamDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonArquivosInterface;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.DatabaseMetaData;
import java.util.LinkedList;
import java.util.List;

import static br.com.webpublico.enums.SituacaoParcela.*;

@Service
public class ServiceDAM {

    private Logger logger = LoggerFactory.getLogger(ServiceDAM.class);
    @PersistenceContext
    protected transient EntityManager em;
    private List<Long> naoPodemSerCancelados;
    @Autowired
    private JdbcDamDAO jdbcDamDAO;

    public void alterarSituacaoDamsDaParcela(SituacaoParcelaValorDivida situacao) {
        naoPodemSerCancelados = Lists.newArrayList();
        if (PAGO.equals(situacao.getSituacaoParcela())
                || PAGO_REFIS.equals(situacao.getSituacaoParcela())
                || PAGO_SUBVENCAO.equals(situacao.getSituacaoParcela())
                || BAIXADO.equals(situacao.getSituacaoParcela())) {
            pagaDamsQueEstaoNaArrecadacaoCancelaOsDemais(situacao.getParcela());
        } else {
            StringBuilder hql = new StringBuilder();
            hql.append("select dam from DAM dam join dam.itens item")
                .append(" join dam.exercicio ex ")
                .append(" where item.parcela = :parcela ")
                .append(" order by ex.ano desc, dam.numero desc");
            Query q = em.createQuery(hql.toString());
            q.setParameter("parcela", situacao.getParcela());
            List<DAM> dams = q.getResultList();
            for (DAM dam : dams) {
                if (SUBSTITUIDO.equals(situacao.getSituacaoParcela())) {
                    alterarSituacaoDAM(dam, DAM.Situacao.SUBISTITUIDO);
                    break;
                } else if ((EM_ABERTO.equals(situacao.getSituacaoParcela()) && DAM.Situacao.PAGO.equals(dam.getSituacao()))) {
                    alterarSituacaoDAM(dam, DAM.Situacao.ABERTO);
                    break;
                } else if ((EM_ABERTO.equals(situacao.getSituacaoParcela()) && DAM.Situacao.CANCELADO.equals(dam.getSituacao()))) {
                    alterarSituacaoDAM(dam, DAM.Situacao.ABERTO);
                    break;
                } else if (!EM_ABERTO.equals(situacao.getSituacaoParcela()) && !PAGO_BLOQUEIO_JUDICIAL.equals(situacao.getSituacaoParcela()) && !DAM.Tipo.SUBVENCAO.equals(dam.getTipo()) && !DAM.Situacao.PAGO.equals(dam.getSituacao())) {
                    alterarSituacaoDAM(dam, DAM.Situacao.CANCELADO);
                    break;
                }
            }
        }
    }

    private void alterarSituacaoDAM(DAM dam, DAM.Situacao situacao) {
        UsuarioSistema usuarioSistema = Util.recuperarUsuarioCorrente();
        jdbcDamDAO.atualizar(dam.getId(), situacao, usuarioSistema);
    }

    private void pagaDamsQueEstaoNaArrecadacaoCancelaOsDemais(ParcelaValorDivida parcela) {
        pagaDamsQueEstaoNaArrecadacao(parcela);
        pagaPrimeiroDamECancelaDemaisDamsQueEstaoNoProcessoDeBaixa(parcela);
        pagaPrimeiroDamECancelaDemaisDamsQueEstaoNoPagamentoAvulso(parcela);
        pagaPrimeiroDamECancelaDemaisDansQueEstaoNoPagamentoPorPix(parcela);
        pagaPrimeiroDamECancelaDemaisDansQueEstaoNoPagamentoInternetBanking(parcela);
        cancelaDamsAbertosQueNaoEstaoNaArrecadacao(parcela);
    }

    private void pagaDamsQueEstaoNaArrecadacao(ParcelaValorDivida parcela) {
        StringBuilder sql = new StringBuilder();
        sql.append("select dam.* from DAM dam ")
                .append(" inner join ItemDAM item on item.dam_id = dam.id ")
                .append(" where item.parcela_id = :idParcela ")
                .append(" and exists (select itemBaixa.id from ItemLoteBaixa itemBaixa ")
                .append(" inner join LoteBaixa lote on lote.id = itemBaixa.loteBaixa_id ")
                .append(" where itemBaixa.dam_id = dam.id ")
                .append(" and lote.situacaoLoteBaixa in (:situacoes))");
        Query q = em.createNativeQuery(sql.toString(), DAM.class);
        q.setParameter("idParcela", parcela.getId());
        q.setParameter("situacoes", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO.name(), SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()));
        List<DAM> dams = q.getResultList();
        for (DAM dam : dams) {
            alterarSituacaoDAM(dam, DAM.Situacao.PAGO);
            naoPodemSerCancelados.add(dam.getId());
        }
    }

    private void pagaPrimeiroDamECancelaDemaisDamsQueEstaoNoProcessoDeBaixa(ParcelaValorDivida parcela) {
        StringBuilder sql = new StringBuilder();
        sql.append("select dam.* from DAM dam ")
                .append(" inner join ItemDAM item on item.dam_id = dam.id ")
                .append(" where item.parcela_id = :idParcela ")
                .append(" and exists (select itemProcesso.id from ItemProcessoDebito itemProcesso ")
                .append(" inner join ProcessoDebito processo on processo.id = itemProcesso.processoDebito_id ")
                .append(" where itemProcesso.parcela_id = item.parcela_id ")
                .append(" and processo.tipo = :tipoProcesso and processo.situacao in (:situacoesProcesso))")
                .append(" order by dam.situacao asc, dam.emissao desc ");
        Query q = em.createNativeQuery(sql.toString(), DAM.class);
        q.setParameter("idParcela", parcela.getId());
        q.setParameter("tipoProcesso", TipoProcessoDebito.BAIXA.name());

        q.setParameter("situacoesProcesso", Lists.newArrayList(SituacaoProcessoDebito.FINALIZADO.name(), SituacaoProcessoDebito.EM_ABERTO.name()));
        LinkedList<DAM> dams = Lists.newLinkedList(q.getResultList());
        pagarPrimeiroDamCancelarOsDemais(dams, Boolean.TRUE);
    }

    private void pagaPrimeiroDamECancelaDemaisDansQueEstaoNoPagamentoPorPix(ParcelaValorDivida parcela) {
        String sql = " select dam.* " +
            " from dam " +
            " inner join itemdam item on dam.id = item.dam_id " +
            " inner join parcelavalordivida pvd on item.parcela_id = pvd.id " +
            " where pvd.id = :idParcela " +
            " and (select status.statussolicitacao from pix " +
            "     inner join statuspix status on pix.id = status.pix_id " +
            "     where pix.id = dam.pix_id " +
            "     order by status.datasituacao desc fetch first 1 rows only " +
            "     ) = :status " +
            " order by dam.situacao, dam.emissao desc ";

        Query q = em.createNativeQuery(sql, DAM.class);
        q.setParameter("idParcela", parcela.getId());
        q.setParameter("status", StatusSolicitacaoPIX.ATIVA.name());

        LinkedList<DAM> dams = Lists.newLinkedList(q.getResultList());
        pagarPrimeiroDamCancelarOsDemais(dams, Boolean.FALSE);
    }

    private void pagarPrimeiroDamCancelarOsDemais(LinkedList<DAM> dams, Boolean apenasTipoUnico) {
        int i = 0;
        for (DAM dam : dams) {
            DAM.Situacao situacaoDam = null;
            if (i == 0 && (!apenasTipoUnico || DAM.Tipo.UNICO.equals(dam.getTipo()))) {
                situacaoDam = DAM.Situacao.PAGO;
                i = 1;
                naoPodemSerCancelados.add(dam.getId());
            } else {
                if (!DAM.Tipo.SUBVENCAO.equals(dam.getTipo())) {
                    situacaoDam = DAM.Situacao.CANCELADO;
                }
            }
            if (situacaoDam != null) {
                alterarSituacaoDAM(dam, situacaoDam);
            }
        }
    }

    private void pagaPrimeiroDamECancelaDemaisDansQueEstaoNoPagamentoInternetBanking(ParcelaValorDivida parcela) {
        String sql = " select dam.* " +
            " from dam " +
            " inner join itemdam item on dam.id = item.dam_id " +
            " inner join parcelavalordivida pvd on item.parcela_id = pvd.id " +
            " inner join pagamentointernetbanking pib on pib.dam_id = dam.id " +
            " where pvd.id = :idParcela " +
            " order by dam.situacao, dam.emissao desc ";

        Query q = em.createNativeQuery(sql, DAM.class);
        q.setParameter("idParcela", parcela.getId());

        LinkedList<DAM> dams = Lists.newLinkedList(q.getResultList());
        pagarPrimeiroDamCancelarOsDemais(dams, Boolean.FALSE);
    }

    private void pagaPrimeiroDamECancelaDemaisDamsQueEstaoNoPagamentoAvulso(ParcelaValorDivida parcela) {
        StringBuilder sql = new StringBuilder();
        sql.append("select dam.* from DAM dam ")
                .append(" inner join ItemDAM item on item.dam_id = dam.id ")
                .append(" where item.parcela_id = :idParcela ")
                .append(" and exists (select avulso.id from PagamentoAvulso avulso ")
                .append(" where avulso.parcelaValorDivida_id = item.parcela_id ")
                .append(" and coalesce(avulso.ativo,1) = 1) ")
                .append(" order by dam.situacao asc, dam.emissao desc ");
        Query q = em.createNativeQuery(sql.toString(), DAM.class);
        q.setParameter("idParcela", parcela.getId());
        LinkedList<DAM> dams = Lists.newLinkedList(q.getResultList());
        pagarPrimeiroDamCancelarOsDemais(dams, Boolean.TRUE);
    }

    private void cancelaDamsAbertosQueNaoEstaoNaArrecadacao(ParcelaValorDivida parcela) {
        StringBuilder sql = new StringBuilder();
        sql.append("select dam.* from DAM dam ")
                .append(" inner join ItemDAM item on item.dam_id = dam.id ")
                .append(" where item.parcela_id = :idParcela ")
                .append(" and dam.situacao = :situacaoDam ")
                .append(" and not exists (select itemBaixa.id from ItemLoteBaixa itemBaixa ")
                .append(" inner join LoteBaixa lote on lote.id = itemBaixa.loteBaixa_id ")
                .append(" where itemBaixa.dam_id = dam.id ")
                .append(" and lote.situacaoLoteBaixa in (:situacoes)) ");
        if (!naoPodemSerCancelados.isEmpty()) {
            sql.append(" and dam.id not in (:dams) ");
        }
        Query q = em.createNativeQuery(sql.toString(), DAM.class);
        q.setParameter("idParcela", parcela);
        if (!naoPodemSerCancelados.isEmpty()) {
            q.setParameter("dams", naoPodemSerCancelados);
        }
        q.setParameter("situacaoDam", DAM.Situacao.ABERTO.name());
        q.setParameter("situacoes", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO.name(), SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()));
        List<DAM> dams = q.getResultList();
        for (DAM dam : dams) {
            if (!DAM.Tipo.SUBVENCAO.equals(dam.getTipo())) {
                alterarSituacaoDAM(dam, DAM.Situacao.CANCELADO);
            }
        }
    }

    public boolean isAmbienteHomologacao() {
        SingletonArquivosInterface singletonInterface = (SingletonArquivosInterface) Util.getFacadeViaLookup("java:module/SingletonArquivosInterface");
        String usuarioBD = buscarUsuarioBD();

        return !usuarioBD.equals(singletonInterface.getConfiguracaoPerfil().getNomeBaseProducao());
    }

    private String buscarUsuarioBD() {
        String usuarioDB;
        Session session = null;
        try {
            session = ((SessionImplementor) em.getDelegate()).getFactory().openSession();
            DatabaseMetaData metaData = ((SessionImplementor) session).connection().getMetaData();
            usuarioDB = metaData.getUserName();
        } catch (Exception e) {
            usuarioDB = "Não Encontrado";
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return usuarioDB;
    }
}
