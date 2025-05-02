package br.com.webpublico.negocios;

import br.com.webpublico.entidades.PropostaConcessaoDiaria;
import br.com.webpublico.entidades.ViagemDiaria;
import br.com.webpublico.entidadesauxiliares.contabil.ProcessamentoPropostaConcessaoDiariaVo;
import br.com.webpublico.enums.TipoProposta;
import br.com.webpublico.enums.TipoViagem;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ProcessamentoPropostaConcessaoDiariaFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    private static final int ZERO_HORA = 0;
    private static final int SEIS_HORAS = 6;
    private static final int DEZOITO_HORAS = 18;
    private static final int ONE_DAY = 24;

    @TransactionTimeout(value = 20, unit = TimeUnit.MINUTES)
    @Asynchronous
    public Future recalcularDiarias(Date dataInicial, Date dataFinal, AssistenteBarraProgresso assistenteBarraProgresso) {
        try {
            List<ViagemDiaria> viagens = buscarViagens(dataInicial, dataFinal);
            assistenteBarraProgresso.setDescricaoProcesso("Recalculando e salvando as viagens das diárias.");
            assistenteBarraProgresso.setTotal(viagens.size());
            for (ViagemDiaria viagem : viagens) {
                calcularValorDiaria(viagem);
                assistenteBarraProgresso.conta();
            }
        } catch (Exception ex) {
            assistenteBarraProgresso.setExecutando(false);
            assistenteBarraProgresso.getMensagens().add("Ocorreu um erro ao recalcular e salvar: " + ex.getMessage());
        }
        return new AsyncResult<>(null);
    }

    private void calcularValorDiaria(ViagemDiaria viagemDiaria) {
        ProcessamentoPropostaConcessaoDiariaVo vo = new ProcessamentoPropostaConcessaoDiariaVo();
        valorDaDiaria(viagemDiaria, vo);
        calcularDiaria(viagemDiaria, vo);
        calcularTotalMeiaDiaria(viagemDiaria, vo);
        viagemDiaria.setTotalIntegral(vo.getTotalIntegral());
        viagemDiaria.setTotalValorMeia(vo.getTotalValorMeia());
        viagemDiaria.setQuantidadeDiarias(vo.getQuantidadeDiarias());
        salvarViagem(viagemDiaria);
    }

    private void valorDaDiaria(ViagemDiaria viagemDiaria, ProcessamentoPropostaConcessaoDiariaVo vo) {
        if (viagemDiaria.getPropostaConcessaoDiaria().getPropostaConcessaoDiaria() != null) {
            valorDaDiaria(viagemDiaria, viagemDiaria.getPropostaConcessaoDiaria().getPropostaConcessaoDiaria(), vo);
        } else {
            valorDaDiaria(viagemDiaria, viagemDiaria.getPropostaConcessaoDiaria(), vo);
        }
    }

    private void calcularOneDiariaIntegral(ProcessamentoPropostaConcessaoDiariaVo vo) {
        vo.setDiferenca(0);
        vo.setTotalIntegral(vo.getValorDaDiaria());
        vo.setQuantidadeDiarias(1D);
        vo.setDiferenca(vo.getDiferenca() + 1);
    }

    private void valorDaDiaria(ViagemDiaria viagemDiaria, PropostaConcessaoDiaria selecionado, ProcessamentoPropostaConcessaoDiariaVo vo) {
        if (TipoProposta.CONCESSAO_DIARIACAMPO.equals(selecionado.getTipoProposta())) {
            vo.setValorDaDiaria(selecionado.getClasseDiaria().getValorEstadual());
        } else {
            if (TipoViagem.ESTADUAL.equals(viagemDiaria.getTipoViagem())) {
                vo.setValorDaDiaria(selecionado.getClasseDiaria().getValorEstadual());
            } else if (TipoViagem.INTERNACIONAL.equals(viagemDiaria.getTipoViagem())) {
                vo.setValorDaDiaria(vo.getValorDaDiaria().add(selecionado.getClasseDiaria().getValorInternacional()));
                BigDecimal valorIndicador = buscarValorIndicador(selecionado);
                if (valorIndicador != null) {
                    vo.setValorDaDiaria(vo.getValorDaDiaria().multiply(valorIndicador));
                }
            } else {
                vo.setValorDaDiaria(selecionado.getClasseDiaria().getValorNacional());
            }
        }
        if (selecionado.getTipoDespesaCusteadaTerceiro() != null) {
            vo.setValorDaDiaria(vo.getValorDaDiaria().divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP));
        }
    }

    private void calcularDiaria(ViagemDiaria viagemDiaria, ProcessamentoPropostaConcessaoDiariaVo vo) {
        if (isDataInicioAndFimNotEquals(viagemDiaria)) {
            Date saida = criarDataSaida(viagemDiaria);
            Date chegada = criarDataChegada(viagemDiaria);
            Integer diffHours = DataUtil.horasEntre(saida, chegada);
            if (diffHours < Integer.parseInt("24")) {
                calcularOneDiariaIntegral(vo);
            } else {
                calcularTotalMeiaDiaria(viagemDiaria, vo);
                totalIntegral(viagemDiaria, vo);
            }
        } else if (isInicioAndFimEqualAndTipoViagemInterestadual(viagemDiaria)) {
            if (isPartidaAndRetornoEntreHoras(viagemDiaria)) {
                calcularOneDiariaIntegral(vo);
            } else {
                vo.setTotalValorMeia(vo.getValorDaDiaria().multiply(new BigDecimal((calcularMeiaDiaria(viagemDiaria) % 1D))));
                totalIntegral(viagemDiaria, vo);
            }
        }
        if (!viagemDiaria.getTransladoComPernoite()) {
            vo.setQuantidadeDiarias(vo.getQuantidadeDiarias() + calcularMeiaDiaria(viagemDiaria));
        }
    }

    private void totalIntegral(ViagemDiaria viagemDiaria, ProcessamentoPropostaConcessaoDiariaVo vo) {
        vo.setQuantidadeDiarias(Integer.valueOf(getDias(viagemDiaria, vo)).doubleValue());
        if (viagemDiaria.getTransladoComPernoite() && calcularMeiaDiaria(viagemDiaria) != 0) {
            vo.setQuantidadeDiarias(vo.getQuantidadeDiarias() + 1D);
        }
        vo.setTotalIntegral(vo.getValorDaDiaria().multiply(new BigDecimal(vo.getQuantidadeDiarias())));
    }

    private Date criarDataChegada(ViagemDiaria viagemDiaria) {
        Calendar chegada = Calendar.getInstance();
        chegada.setTime(viagemDiaria.getDataInicialRetorno());
        chegada.set(Calendar.HOUR_OF_DAY, Integer.parseInt(viagemDiaria.getHoraInicialRetorno()));
        chegada.set(Calendar.MINUTE, Integer.parseInt(viagemDiaria.getMinutoInicialRetorno()));
        return chegada.getTime();
    }

    private Date criarDataSaida(ViagemDiaria viagemDiaria) {
        Calendar saida = Calendar.getInstance();
        saida.setTime(viagemDiaria.getDataInicialSaida());
        saida.set(Calendar.HOUR_OF_DAY, Integer.parseInt(viagemDiaria.getHoraInicialSaida()));
        saida.set(Calendar.MINUTE, Integer.parseInt(viagemDiaria.getMinutoInicialSaida()));
        return saida.getTime();
    }

    private void calcularTotalMeiaDiaria(ViagemDiaria viagem, ProcessamentoPropostaConcessaoDiariaVo vo) {
        if (!viagem.getTransladoComPernoite()) {
            vo.setTotalValorMeia(vo.getValorDaDiaria().multiply(new BigDecimal((calcularMeiaDiaria(viagem) % 1D))));
        }
    }

    private Double calcularMeiaDiaria(ViagemDiaria viagemDiaria) {
        if (isDataInicialDiferenteDataFinal(viagemDiaria)) {
            Date saida = criarDataSaida(viagemDiaria);
            Date chegada = criarDataChegada(viagemDiaria);
            Integer diffHours = DataUtil.horasEntre(saida, chegada);
            if (diffHours < ONE_DAY) {
                return 0D;
            }
        } else if (viagemDiaria.getHoraInicialSaida() != null && viagemDiaria.getHoraInicialRetorno() != null && viagemDiaria.getDataInicialSaida() != null && viagemDiaria.getDataInicialRetorno() != null) {
            if (viagemDiaria.getDataInicialSaida().compareTo(viagemDiaria.getDataInicialRetorno()) == 0 && viagemDiaria.isDiariaInterestadual()) {
                Integer partida = Integer.parseInt(viagemDiaria.getHoraInicialSaida());
                Integer retorno = Integer.parseInt(viagemDiaria.getHoraInicialRetorno());
                if (partida.compareTo(ZERO_HORA) >= 0 && partida.compareTo(SEIS_HORAS) <= 0 && retorno.compareTo(DEZOITO_HORAS) >= 0) {
                    return 0D;
                } else {
                    return 0.5D;
                }
            } else if (viagemDiaria.getHoraInicialSaida().compareTo(viagemDiaria.getHoraInicialRetorno()) > 0 && viagemDiaria.getDataInicialSaida().compareTo(viagemDiaria.getDataInicialRetorno()) == 0) {
                return 0D;
            }
        }
        if (Integer.parseInt(viagemDiaria.getHoraInicialSaida()) <= 12 && Integer.parseInt(viagemDiaria.getHoraInicialRetorno()) <= 12) {
            return 0D;
        }
        if (Integer.parseInt(viagemDiaria.getHoraInicialSaida()) > 12 && Integer.parseInt(viagemDiaria.getHoraInicialRetorno()) > 12) {
            return 0D;
        }
        if (Integer.parseInt(viagemDiaria.getHoraInicialSaida()) <= 12 && Integer.parseInt(viagemDiaria.getHoraInicialRetorno()) > 12) {
            return 0.5D;
        }
        if (Integer.parseInt(viagemDiaria.getHoraInicialSaida()) >= 12 && Integer.parseInt(viagemDiaria.getHoraInicialRetorno()) < 12) {
            return 0.5D;
        }
        return 0D;
    }

    private boolean isDataInicialDiferenteDataFinal(ViagemDiaria viagemDiaria) {
        return viagemDiaria.getDataInicialSaida() != null && viagemDiaria.getDataInicialRetorno() != null
            && viagemDiaria.getDataInicialSaida().compareTo(viagemDiaria.getDataInicialRetorno()) != 0;
    }


    private boolean isDataInicioAndFimNotEquals(ViagemDiaria viagemDiaria) {
        return viagemDiaria.getDataInicialSaida() != null && viagemDiaria.getDataInicialRetorno() != null &&
            viagemDiaria.getDataInicialSaida().compareTo(viagemDiaria.getDataInicialRetorno()) != 0;
    }

    private boolean isInicioAndFimEqualAndTipoViagemInterestadual(ViagemDiaria viagemDiaria) {
        return viagemDiaria.getDataInicialSaida() != null &&
            viagemDiaria.getDataInicialRetorno() != null &&
            viagemDiaria.getDataInicialSaida().compareTo(viagemDiaria.getDataInicialRetorno()) == 0 &&
            viagemDiaria.isDiariaInterestadual();
    }

    private boolean isPartidaAndRetornoEntreHoras(ViagemDiaria viagemDiaria) {
        Integer partida = Integer.parseInt(viagemDiaria.getHoraInicialSaida());
        Integer chegada = Integer.parseInt(viagemDiaria.getHoraInicialRetorno());
        return partida.compareTo(ZERO_HORA) >= 0 && partida.compareTo(SEIS_HORAS) <= 0 && chegada.compareTo(DEZOITO_HORAS) >= 0;
    }

    private int getDias(ViagemDiaria viagem, ProcessamentoPropostaConcessaoDiariaVo vo) {
        if (viagem.getDataInicialSaida() != null && viagem.getDataInicialRetorno() != null) {
            Calendar saida = Calendar.getInstance();
            saida.setTime(viagem.getDataInicialSaida());
            Calendar chegada = Calendar.getInstance();
            chegada.setTime(viagem.getDataInicialRetorno());
            int tempoDia = 1000 * 60 * 60 * 24;
            long diff = chegada.getTimeInMillis() - saida.getTimeInMillis();
            diff = diff / tempoDia;
            if (diff < 0) return 0;
            vo.setDiferenca(vo.getDiferenca() == null ? 0 : vo.getDiferenca());
            if (vo.getDiferenca() == 1) {
                return vo.getDiferenca();
            }
            diff += vo.getDiferenca();
            return (int) diff;
        } else {
            return 0;
        }
    }


    private List<ViagemDiaria> buscarViagens(Date dataInicial, Date dataFinal) {
        String sql = " select v.* from viagemdiaria v " +
            " inner join PROPOSTACONCESSAODIARIA prop on v.PROPOSTACONCESSAODIARIA_ID = prop.id " +
            " where trunc(prop.DATALANCAMENTO) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ";
        Query q = em.createNativeQuery(sql, ViagemDiaria.class);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        return q.getResultList();
    }

    private BigDecimal buscarValorIndicador(PropostaConcessaoDiaria selecionado) {
        String sql = " select vie.valor from ValorIndicadorEconomico vie " +
            " where trunc(vie.INICIOVIGENCIA) = (select max(trunc(v.INICIOVIGENCIA)) " +
            " from ValorIndicadorEconomico v where trunc(v.INICIOVIGENCIA) <= to_date(:dataProposta, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataProposta", DataUtil.getDataFormatada(selecionado.getDataLancamento()));
        q.setMaxResults(1);
        List<BigDecimal> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }

    private void salvarViagem(ViagemDiaria viagemDiaria) {
        em.merge(viagemDiaria);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
