package br.com.webpublico.negocios.administrativo.licitacao;

import br.com.webpublico.entidadesauxiliares.administrativo.AgendaLicitacaoVO;
import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.enums.TipoSituacaoLicitacao;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class AgendaLicitacaoFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public List<AgendaLicitacaoVO> buscarDadosAgenda(Date dataInicial, Date dataFinal) {
        List<AgendaLicitacaoVO> licitacoesNoPeriodo = buscarLicitacoes(dataInicial, dataFinal);
        List<AgendaLicitacaoVO> retorno = Lists.newArrayList();
        Integer diasNoMes = DataUtil.getDiasNoMes(dataFinal);
        Integer mes = DataUtil.getMesIniciandoEmZero(dataFinal);
        Integer ano = DataUtil.getAno(dataFinal);
        Integer posicaoNaSemana = null;
        for (int dia = 1; dia <= diasNoMes; dia++) {
            AgendaLicitacaoVO diaAgenda = new AgendaLicitacaoVO();
            Calendar c = DataUtil.montaData(dia, mes, ano);
            diaAgenda.setDia(dia);
            diaAgenda.setDiaDaSemana(getDiaDaSemana(c));
            posicaoNaSemana = posicaoNaSemana == null ? c.get(Calendar.DAY_OF_WEEK) - 1 : posicaoNaSemana + 1;
            diaAgenda.setPosicao(posicaoNaSemana);
            for (AgendaLicitacaoVO licitacao : licitacoesNoPeriodo) {
                if (licitacao.getDia().compareTo(dia) == 0) {
                    diaAgenda.getLicitacoes().add(licitacao);
                }
            }
            retorno.add(diaAgenda);
        }
        return retorno;
    }

    private List<AgendaLicitacaoVO> buscarLicitacoes(Date dataInicial, Date dataFinal) {
        String sql = " select distinct " +
            "  lic.id as idLicitacao, " +
            "  lic.ABERTAEM as abertaEm, " +
            "  lic.NUMEROLICITACAO || '/' || exLic.ano as processoLicitacao, " +
            "  lic.numero || '/' || exModLic.ANO as numeroAnoModalidadeLic, " +
            "  lic.modalidadeLicitacao as modalidadeLicitacao, " +
            "  pfPregoeiro.nome as pregoeiroResponsavel, " +
            "  status.tiposituacaolicitacao as statusAtual, " +
            "  lic.RESUMODOOBJETO as resumoObjeto " +
            " from licitacao lic " +
            "  inner join exercicio exLic on exLic.id = lic.EXERCICIO_ID " +
            "  inner join exercicio exModLic on exModLic.id = lic.EXERCICIOMODALIDADE_ID " +
            "  inner join MembroComissao mcPregoeiro on mcPregoeiro.id = lic.PREGOEIRORESPONSAVEL_ID " +
            "  inner join pessoafisica pfPregoeiro on pfPregoeiro.id = mcPregoeiro.PESSOAFISICA_ID " +
            "  inner join statuslicitacao status ON status.LICITACAO_ID = lic.ID " +
            " where trunc(lic.ABERTAEM) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " +
            "   and status.DATASTATUS = (SELECT MAX(st.DATASTATUS) FROM statuslicitacao st WHERE st.licitacao_id = lic.ID ) " +
            " order by lic.ABERTAEM asc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        List<Object[]> resultado = q.getResultList();
        List<AgendaLicitacaoVO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                AgendaLicitacaoVO licitacao = new AgendaLicitacaoVO();
                TipoSituacaoLicitacao tipoSituacaoLicitacao = obj[6] != null ? TipoSituacaoLicitacao.valueOf((String) obj[6]) : null;
                Date abertaEm = (Date) obj[1];
                licitacao.setIdLicitacao(((BigDecimal) obj[0]).longValue());
                licitacao.setDia(DataUtil.getDia(abertaEm));
                licitacao.setLicitacao(getDescricaoLicitacao(obj, tipoSituacaoLicitacao));
                licitacao.setResumoObjeto((String) obj[7]);
                licitacao.setTipoSituacaoLicitacao(tipoSituacaoLicitacao);
                retorno.add(licitacao);
            }
        }
        return retorno;
    }

    private String getDescricaoLicitacao(Object[] obj, TipoSituacaoLicitacao tipoSituacaoLicitacao) {
        String descricaoModalidadeLicitacao = obj[4] != null ? ModalidadeLicitacao.valueOf((String) obj[4]).getDescricao() : "";
        String descricaoStatusAtual = tipoSituacaoLicitacao != null ? tipoSituacaoLicitacao.getDescricao() : "";
        return "Processo de Licitação " + obj[2] +
            " - Modalidade " + descricaoModalidadeLicitacao + " " + obj[3] +
            " - " + DataUtil.getDataFormatadaDiaHora((Date) obj[1]) +
            " - " + obj[5] +
            " - " + descricaoStatusAtual;
    }

    private String getDiaDaSemana(Calendar cal) {
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                return "Domingo";
            case Calendar.MONDAY:
                return "Segunda-Feira";
            case Calendar.TUESDAY:
                return "Terça-Feira";
            case Calendar.WEDNESDAY:
                return "Quarta-Feira";
            case Calendar.THURSDAY:
                return "Quinta-Feira";
            case Calendar.FRIDAY:
                return "Sexta-Feira";
            case Calendar.SATURDAY:
                return "Sábado";
            default:
                return "";
        }
    }
}
