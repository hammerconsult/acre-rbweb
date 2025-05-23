/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.entidades.ConfiguracaoHorarioExpediente;
import br.com.webpublico.entidades.Feriado;
import br.com.webpublico.entidadesauxiliares.NumeroExtenso;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoPrazo;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.negocios.FeriadoFacade;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.*;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.util.Calendar.*;

/**
 * @author daniel
 */
public class DataUtil {


    private static final Logger log = LoggerFactory.getLogger(DataUtil.class);
    public static final int ANO_1900 = 1900;
    //private static final FastDateFormat formatter = FastDateFormat.getInstance("dd/MM/yyyy", Util.localeBrasil);
    private static final int DIAS_NO_MES[] = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public static String getDataFormatada(Date data) {
        try {
            if (data == null) {
                return "";
            }
            return new SimpleDateFormat("dd/MM/yyyy").format(data);
        } catch (Exception e) {
            throw new RuntimeException("Erro:" + e);
        }

    }

    public static String getDataFormatada(java.time.LocalDate data) {
        try {
            if (data == null) {
                return "";
            }
            return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(data);
        } catch (Exception e) {
            throw new RuntimeException("Erro:" + e);
        }

    }

    public static String getDataFormatada(Date data, String mask) {
        try {
            if (data == null) {
                return "";
            }
            return new SimpleDateFormat(mask).format(data);
        } catch (Exception e) {
            throw new RuntimeException("Erro:" + e);
        }

    }

    public static String formatarHoraMinuto(Integer hh, Integer mm) {
        String hora = String.valueOf(hh != null ? hh : 0);
        String minuto = String.valueOf(mm != null ? mm : 0);
        if (hora.length() == 1) {
            hora = "0" + hora;
        }
        if (minuto.length() == 1) {
            minuto = "0" + minuto;
        }
        return hora + ":" + minuto;
    }

    public static String getDataFormatadaDiaHora(Date data) {
        try {
            return getDataFormatadaDiaHora(data, "dd/MM/yyyy HH:mm:ss");
        } catch (Exception e) {
            throw new RuntimeException("Erro:" + e);
        }
    }

    public static String getDataFormatadaDiaHora(Date data, String mask) {
        try {
            if (data == null) {
                return "";
            }
            return new SimpleDateFormat(mask).format(data);
        } catch (Exception e) {
            throw new RuntimeException("Erro:" + e);
        }
    }

    public static int diferencaMesesInteira(Date dataAnterior, Date dataPosterior) {
        if (dataAnterior != null && dataPosterior != null) {
            Calendar cInicial = Calendar.getInstance();
            Calendar cFinal = Calendar.getInstance();
            cInicial.setTime(dataAnterior);
            cFinal.setTime(dataPosterior);
            int diferencaMeses = cFinal.get(Calendar.MONTH) - cInicial.get(Calendar.MONTH);
            int diferencaAnos = (cFinal.get(Calendar.YEAR) - cInicial.get(Calendar.YEAR)) * 12;
            return diferencaAnos + diferencaMeses;
        }
        return 0;
    }

    public static int getMes(Date dataAno) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataAno);
        return c.get(Calendar.MONTH) + 1;
    }

    public static int getMesIniciandoEmZero(Date dataAno) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataAno);
        return c.get(Calendar.MONTH);
    }

    public static int getDia(Date dataDia) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataDia);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static Date getDateParse(String data) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(data);
        } catch (Exception e) {
            throw new RuntimeException("Erro:" + e);
        }
    }

    public static Date getDateParse(String data, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(data);
        } catch (Exception e) {
            throw new RuntimeException("Erro:" + e);
        }

    }


    public static Date getDateTimeParse(String data) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(data);
        } catch (Exception e) {
            throw new RuntimeException("Erro:" + e);
        }

    }

    public static Integer getAno(Date dataAno) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataAno);
        return c.get(Calendar.YEAR);
    }

    public static double diferencaMesesFracionada(Date dataAnterior, Date dataPosterior) {
        if (dataAnterior.after(dataPosterior)) {
            return 0;
        }
        Calendar calMenor = Calendar.getInstance();
        calMenor.setTime(dataAnterior);
        Calendar calMaior = Calendar.getInstance();
        calMaior.setTime(dataPosterior);
        int dAno = calMaior.get(YEAR) - calMenor.get(YEAR);
        int dMes = calMaior.get(MONTH) - calMenor.get(MONTH);
        int dDia = calMaior.get(DAY_OF_MONTH) - calMenor.get(DAY_OF_MONTH);

        if (dMes < 0) {
            dMes += 12;
            dAno--;
        }
        if (dDia < 0) {
            dDia += quantidadeDiasMesAnterior(dataPosterior);
            dMes--;
        }
        return (dAno * 12) + dMes + (dDia / (double) quantidadeDiasMesAnterior(dataPosterior));
    }

    public static Calendar montaData(int dia, int mes, int ano) {
        Calendar c = Calendar.getInstance();
        c.set(ano, mes, dia);
        return c;
    }

    public static String montarDataFormatada(int dia, int mes, int ano) {
        Calendar c = Calendar.getInstance();
        c.set(ano, mes, dia);
        return getDataFormatada(c.getTime());
    }

    public static DateTime criarDataComMesEAno(int mes, int ano) {
        DateTime data = new DateTime(ano, mes, 1, 0, 0);
        return data;
    }


    public static int getDiasNoMes(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getDiasNoMes(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
    }

    public static int getDiasNoMes(int mes, int ano) {
        if (mes == 2 && isBissexto(ano)) {
            return 29;
        }
        return DIAS_NO_MES[mes];
    }

    public static int getDiasNoAno(int ano) {
        if (isBissexto(ano)) {
            return 366;
        }
        return 365;
    }

    public static boolean isBissexto(int ano) {
        if (ano % 400 == 0) {
            return true;
        }
        if (ano % 100 == 0) {
            return false;
        }
        if (ano % 4 == 0) {
            return true;
        }
        return false;
    }

    public static int quantidadeDiasMesAnterior(Date data) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        cal.set(DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, -1);
        return getDiasNoMes(cal.getTime());
    }

    public static int diferencaDiasInteira(Date dataAnterior, Date dataPosterior) {
        if (dataAnterior.after(dataPosterior)) {
            return 0;
        }
        int dias = Days.daysBetween(new DateTime(dataAnterior), new DateTime((dataPosterior))).getDays();
        return dias;
    }

    public static Long diferencaDiasInteiraComTimezone(Date dataAnterior, Date dataPosterior) {
        if (dataAnterior.after(dataPosterior)) {
            return 0L;
        }
        return DataUtil.dateToLocalDate(dataAnterior).until(DataUtil.dateToLocalDate(dataPosterior), ChronoUnit.DAYS);
    }

    public static DateTime zerarHorasMinutosSegundos(DateTime dateTime) {
        dateTime = dateTime.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        return dateTime;
    }

    public static double diferencaDiasFracionado(Date dataAnterior, Date dataPosterior) {
        Calendar calAnterior = Calendar.getInstance();
        calAnterior.setTime(dataAnterior);
        Date dataAnteriorComHora = calAnterior.getTime();

        Calendar calPosterior = Calendar.getInstance();
        calPosterior.setTime(dataPosterior);
        Date dataPosteriorComHora = calPosterior.getTime();
        if (dataAnteriorComHora.after(dataPosteriorComHora)) {
            return 0;
        }

        Long diferenca = (dataPosteriorComHora.getTime() - dataAnteriorComHora.getTime()) / (24 * 60 * 60 * 1000);
        return diferenca.doubleValue();
    }

    public static boolean ehDiaNaoUtil(Date data, FeriadoFacade feriadoFacade) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            return true;
        }
        if (!feriadoFacade.feriadosNoDia(data).isEmpty()) {
            return true;
        }
        return false;
    }

    public static Date ajustarDataUtil(Date data, FeriadoFacade feriadoFacade) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        while (ehDiaNaoUtil(cal.getTime(), feriadoFacade)) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return cal.getTime();
    }

    public static Date ajustarDataUtilPraTras(Date data, FeriadoFacade feriadoFacade, Boolean ajustaPraFrenteCasoNaoTenhaPraTrasMesmoMes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        while (ehDiaNaoUtil(cal.getTime(), feriadoFacade)) {
            if (ajustaPraFrenteCasoNaoTenhaPraTrasMesmoMes && cal.get(DAY_OF_MONTH) == 1) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
            } else {
                cal.add(Calendar.DAY_OF_MONTH, ajustaPraFrenteCasoNaoTenhaPraTrasMesmoMes ? 1 : -1);
            }
        }
        return cal.getTime();
    }

    public static boolean isDataValida(Date data) {
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        return c.get(YEAR) >= ANO_1900;
    }

    public static int horasEntre(Date begin, Date end) {
        return Hours.hoursBetween(new DateTime(begin), new DateTime(end)).getHours();
    }

    public static int diasEntreDate(Date begin, Date end) {
        return diasEntre(new DateTime(begin), new DateTime(end));
    }

    public static boolean isEqualYearAndMonth(DateTime begin, DateTime end) {
        return begin.getMonthOfYear() == end.getMonthOfYear() && begin.getYear() == end.getYear();
    }

    public static boolean isEqualYearAndMonth(java.time.LocalDate begin, java.time.LocalDate end) {
        return begin.getMonthValue() == end.getMonthValue() && begin.getYear() == end.getYear();
    }

    public static int diasEntre(DateTime begin, DateTime end) {
        return Days.daysBetween(begin, end).getDays() + 1;
    }

    public static int diasEntre(java.time.LocalDate begin, java.time.LocalDate end) {
        return (int) DAYS.between(begin, end) + 1;
    }


    public static int qtdDiasNaoUteis(Date dataInicial, Date dataFinal, FeriadoFacade feriadoFacade) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(dataInicial);
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(dataFinal);
        int qtdDiasNaoUteis = 0;

        //System.out.println("DATA INICIAL ---- " + dataInicial);
        //System.out.println("DATA FINAL   ---- " + dataFinal);

        while (((Date) cal1.getTime()).getTime() <= ((Date) cal2.getTime()).getTime()) {
            //System.out.println("entrou no while");
            if (ehDiaNaoUtil(cal1.getTime(), feriadoFacade)) {
                qtdDiasNaoUteis++;
            }
            cal1.add(Calendar.DAY_OF_YEAR, 1);
        }

        //System.out.println("qtd --- " + qtdDiasNaoUteis);
        return qtdDiasNaoUteis;
    }

    public static Date retornaMaiorData(Date inicio, Date fim) {
        if (inicio.getTime() > fim.getTime()) {
            return inicio;
        } else {
            return fim;
        }

    }

    public static Date retornaMenorData(Date inicio, Date fim) {
        if (inicio.getTime() < fim.getTime()) {
            return inicio;
        } else {
            return fim;
        }
    }

    public static Calendar primeiroDiaMes(Date data) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(data);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal;
    }

    public static Calendar primeiroDiaMesHorarioZerado(Date data) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(data);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Date primeiroDiaMes(Mes mes) {
        LocalDate localDate = LocalDate.now().withMonthOfYear(mes.getNumeroMes());
        return localDate.dayOfMonth().withMinimumValue().toDate();
    }

    public static Date primeiroDiaMes(Integer mes, Integer ano) {
        LocalDate localDate = LocalDate.now().withYear(ano).withMonthOfYear(mes);
        return localDate.dayOfMonth().withMinimumValue().toDate();
    }


    public static Date ultimoDiaMes(Mes mes) {
        LocalDate localDate = LocalDate.now().withMonthOfYear(mes.getNumeroMes());
        return localDate.dayOfMonth().withMaximumValue().toDate();
    }

    public static Calendar ultimoDiaMes(Date data) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(data);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal;
    }

    public static Date ultimoDiaDoMes(Mes mes) {
        LocalDate localDate = LocalDate.now().withMonthOfYear(mes.getNumeroMes());
        return localDate.dayOfMonth().withMaximumValue().toDate();
    }

    public static int ultimoDiaDoMes(int mes) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, mes - 1);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static Date ultimoDiaAnoAtual() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        return cal.getTime();
    }

    public static Date getPrimeiroDiaAno(Integer ano) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, ano);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        return cal.getTime();
    }

    public static Date getPrimeiroDiaMes(Integer ano, Integer mes) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, ano);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, mes);
        return cal.getTime();
    }

    public static Date getPrimeiroDiaAno(Date data) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        return cal.getTime();
    }


    public static Date getUltimoDiaAno(Integer ano) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, ano);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        return cal.getTime();
    }


    public static Calendar ultimoDiaUtil(Calendar cal, FeriadoFacade feriadoFacade) {
        if (ehDiaNaoUtil(cal.getTime(), feriadoFacade)) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
            ultimoDiaUtil(cal, feriadoFacade);
        }
        return cal;
    }

    public static Date primeiroDiaMes(java.time.LocalDate data) {
        return localDateToDate(data.withDayOfMonth(1));
    }

    public static Date ultimoDiaMes(java.time.LocalDate data) {
        return localDateToDate(data.withDayOfMonth(data.lengthOfMonth()));
    }

    @Deprecated
    public static ObjetoData getAnosMesesDias(Date dataInicial, Date dataFinal) {
        ObjetoData objetoData = new ObjetoData();

        Calendar inicio = Calendar.getInstance();
        Calendar fim = Calendar.getInstance();
        inicio.setTime(dataInicial);
        if (dataFinal != null) {
            fim.setTime(dataFinal);
        } else {
            fim.setTime(new Date());
        }

        Period period = new Period(new DateTime(dataInicial), new DateTime(dataFinal));
        objetoData.setAnos(period.getYears());
        objetoData.setMeses(period.getMonths());
        objetoData.setDias(period.getDays());
        return objetoData;
    }


    /**
     * Método para verificar o periodo entre datas, adicionando um dia no final da data para contar o dia de inicio de vigencia.
     */
    public static Period getDiferencaAnosMesesDias(Date dataInicial, Date dataFinal) {
        Calendar inicio = Calendar.getInstance();
        Calendar fim = Calendar.getInstance();
        inicio.setTime(dataInicial);
        if (dataFinal != null) {
            fim.setTime(dataFinal);
        } else {
            fim.setTime(new Date());
        }
        return new Period(new DateTime(dataInicial), new DateTime(dataFinal).plusDays(1), PeriodType.yearMonthDay());
    }

    public static Date getDataUltimoDiaAnoFromExercicioCorrente() {
        return getUltimoDiaAno(Util.getSistemaControlador().getExercicioCorrente().getAno());

    }

    public static boolean validaMes(Integer mes) {
        if (mes < 1 || mes > 12) {
            return false;
        }
        return true;
    }

    public static boolean validaDiaMes(Integer mes, Integer dia) {
        switch (mes) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (dia < 1 || dia > 31) {
                    return false;
                }
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if (dia < 1 || dia > 30) {
                    return false;
                }
                break;
            case 2:
                if (dia < 1 || dia > 28) {
                    return false;
                }
                break;
        }
        return true;
    }

    private static boolean beforeEquals(Date fixa, Date comparada) {
        return fixa.equals(comparada) || fixa.before(comparada);
    }

    private static boolean afterEquals(Date fixa, Date comparada) {
        return fixa.equals(comparada) || fixa.after(comparada);
    }

    //Rever
    public static boolean isVigenciasIguais(Date inicio1, Date fim1, Date inicio2, Date fim2) {
        if (inicio1 != null && inicio2 != null && fim1 == null && fim2 == null) {
            return true;
        }
        if (inicio1 != null && inicio2 != null && fim1 != null && fim2 != null) {
            if ((beforeEquals(inicio2, fim1) && afterEquals(inicio2, inicio1)) || (afterEquals(fim2, inicio1) && beforeEquals(fim2, fim1))) {
                return true;
            }
        }
        if (inicio1 != null && fim1 != null && inicio2 != null && fim2 == null) {
            if (beforeEquals(inicio2, inicio1)) {
                return true;
            }
            if (afterEquals(inicio2, inicio1) && beforeEquals(inicio2, fim1)) {
                return true;
            }
        }
        if (inicio1 != null && fim1 == null && inicio2 != null && fim2 != null) {
            if (beforeEquals(inicio1, inicio2)) {
                return true;
            }
            if (afterEquals(inicio1, inicio2) && beforeEquals(inicio1, fim2)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVigenciaValida(ValidadorVigencia novo, List<? extends ValidadorVigencia> adicionados) {
        return isVigenciaValida(novo, adicionados, true);
    }

    public static boolean isVigenciaValida(ValidadorVigencia novo, List<? extends ValidadorVigencia> adicionados, boolean validaSeTemVigente) {
        if (!CollectionUtils.isEmpty(adicionados)) {
            DateTime dtInicioDaVigenciaNovo = new DateTime(novo.getInicioVigencia());
            DateTime dtFinalDaVigenciaNovo = null;
            dtFinalDaVigenciaNovo = getDateTimeFinalDaVigenciaNovo(novo, dtFinalDaVigenciaNovo);

            if (dataInicialEhPosteriorOuIgualAdataFinal(dtInicioDaVigenciaNovo, dtFinalDaVigenciaNovo)) {
                FacesUtil.addOperacaoNaoPermitida("A data inicial da vigência deve ser anterior a data de final da vigência.");
                return false;
            }
            if (temRegistroAdicionadoVigente(novo, adicionados, validaSeTemVigente)) {
                return false;
            }
            if (!isDataInicioDataFinalValidas(novo, adicionados, dtInicioDaVigenciaNovo, dtFinalDaVigenciaNovo)) {
                return false;
            }
        }
        return true;
    }

    public static boolean dataInicialEhPosteriorOuIgualAdataFinal(DateTime dtInicioDaVigenciaNovo, DateTime dtFinalDaVigenciaNovo) {
        if (dtFinalDaVigenciaNovo == null) return false;
        return dtInicioDaVigenciaNovo.isAfter(dtFinalDaVigenciaNovo) || dtInicioDaVigenciaNovo.isEqual(dtFinalDaVigenciaNovo);
    }

    public static boolean isDataInicioDataFinalValidas(ValidadorVigencia novo, List<? extends ValidadorVigencia> adicionados, DateTime dtInicioDaVigenciaNovo, DateTime dtFinalDaVigenciaNovo) {
        for (ValidadorVigencia adicionado : adicionados) {

            if (!adicionado.equals(novo)) {
                DateTime dtInicioDaVigenciaAdicionado = new DateTime(adicionado.getInicioVigencia());
                DateTime dtFinalDaVigenciaAdicionado = new DateTime(adicionado.getFimVigencia());

                if (new Interval(dtInicioDaVigenciaAdicionado, dtFinalDaVigenciaAdicionado).contains(dtInicioDaVigenciaNovo)) {
                    FacesUtil.addOperacaoNaoPermitida(getMensagemJaExisteUmIntervaloDeVigenciaComAData() + getDataFormatada(dtInicioDaVigenciaNovo.toDate()));
                    return false;
                }
                if (dtInicioDaVigenciaNovo.equals(dtInicioDaVigenciaAdicionado) || dtInicioDaVigenciaNovo.equals(dtFinalDaVigenciaAdicionado)) {
                    FacesUtil.addOperacaoNaoPermitida(getMensagemJaExisteUmIntervaloDeVigenciaComAData() + getDataFormatada(dtInicioDaVigenciaNovo.toDate()));
                    return false;
                }

                if (dtFinalDaVigenciaNovo != null) {
                    if (new Interval(dtInicioDaVigenciaAdicionado, dtFinalDaVigenciaAdicionado).contains(dtFinalDaVigenciaNovo)) {
                        FacesUtil.addOperacaoNaoPermitida(getMensagemJaExisteUmIntervaloDeVigenciaComAData() + getDataFormatada(dtFinalDaVigenciaNovo.toDate()));
                        return false;
                    }
                    if (dtFinalDaVigenciaNovo.equals(dtInicioDaVigenciaAdicionado) || dtFinalDaVigenciaNovo.equals(dtFinalDaVigenciaAdicionado)) {
                        FacesUtil.addOperacaoNaoPermitida(getMensagemJaExisteUmIntervaloDeVigenciaComAData() + getDataFormatada(dtFinalDaVigenciaNovo.toDate()));
                        return false;
                    }
                    if (new Interval(dtInicioDaVigenciaNovo, dtFinalDaVigenciaNovo).contains(dtInicioDaVigenciaAdicionado)) {
                        FacesUtil.addOperacaoNaoPermitida(getMensagemJaExisteUmIntervaloDeVigenciaComAData() + getDataFormatada(dtInicioDaVigenciaAdicionado.toDate()));
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isPeriodoConcomitante(ValidadorVigencia novo, List<? extends ValidadorVigencia> adicionados) {
        for (ValidadorVigencia adicionado : adicionados) {
            if (!adicionado.equals(novo)) {
                Date adicionadoInicioVigencia = DataUtil.dataSemHorario(adicionado.getInicioVigencia());
                Date adicionadoFimVigencia = DataUtil.dataSemHorario(adicionado.getFimVigencia());
                Date novoInicioVigencia = DataUtil.dataSemHorario(novo.getInicioVigencia());
                Date novoFimVigencia = DataUtil.dataSemHorario(novo.getFimVigencia());
                if ((novoInicioVigencia.compareTo(adicionadoInicioVigencia) <= 0 &&
                    novoFimVigencia.compareTo(adicionadoFimVigencia) >= 0) ||
                    (novoInicioVigencia.compareTo(adicionadoInicioVigencia) >= 0 &&
                        novoInicioVigencia.compareTo(adicionadoFimVigencia) <= 0) ||
                    (novoFimVigencia.compareTo(adicionadoInicioVigencia) >= 0 &&
                        novoFimVigencia.compareTo(adicionadoFimVigencia) <= 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String getMensagemJaExisteUmIntervaloDeVigenciaComAData() {
        return "Já existe um intervalo de vigência com a data ";
    }

    private static boolean temRegistroAdicionadoVigente(ValidadorVigencia novo, List<? extends ValidadorVigencia> adicionados, boolean validaSeTemVigente) {
        for (ValidadorVigencia adicionado : adicionados) {

            if (!adicionado.equals(novo)) {
                DateTime dtInicioDaVigenciaNovo = new DateTime(adicionado.getInicioVigencia());
                DateTime dtFinalDaVigenciaNovo = null;
                dtFinalDaVigenciaNovo = getDateTimeFinalDaVigenciaNovo(adicionado, dtFinalDaVigenciaNovo);

                Date fimVigenciaNovoRegistro = novo.getFimVigencia();
                if (fimVigenciaNovoRegistro == null) {
                    fimVigenciaNovoRegistro = getDataUltimaHoraDia(new Date());
                }

                if (dtFinalDaVigenciaNovo == null && validaSeTemVigente && fimVigenciaNovoRegistro.after(dtInicioDaVigenciaNovo.toDate())) {
                    FacesUtil.addOperacaoNaoPermitida(getMensagemRegistroVigenteEncontrado(dtInicioDaVigenciaNovo, dtFinalDaVigenciaNovo));
                    return true;
                } else {
                    try {
                        if (new Interval(dtInicioDaVigenciaNovo, dtFinalDaVigenciaNovo).contains(new DateTime(novo.getInicioVigencia()))) {
                            FacesUtil.addOperacaoNaoPermitida(getMensagemRegistroVigenteEncontrado(dtInicioDaVigenciaNovo, dtFinalDaVigenciaNovo));
                            return true;
                        }
                    } catch (IllegalArgumentException iae) {
                        FacesUtil.addOperacaoNaoPermitida("Existe(m) registro(s) de Data Final (" + getDataFormatada(dtFinalDaVigenciaNovo.toDate()) + ") anterior à Data Inicial (" + getDataFormatada(dtInicioDaVigenciaNovo.toDate()) + ").");
                        return true;
                    }
                    if (novo.getFimVigencia() != null && new Interval(dtInicioDaVigenciaNovo, dtFinalDaVigenciaNovo).contains(new DateTime(novo.getFimVigencia()))) {
                        FacesUtil.addOperacaoNaoPermitida(getMensagemRegistroVigenteEncontrado(dtInicioDaVigenciaNovo, dtFinalDaVigenciaNovo));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static String getMensagemRegistroVigenteEncontrado() {
        return "Foi encontrado um registro adicionado vigente.";
    }

    private static String getMensagemRegistroVigenteEncontrado(DateTime inicio, DateTime fim) {
        return "O registro atual está entre a vigência " + getDataFormatada(inicio.toDate()) + " e " + (fim != null ? (getDataFormatada(fim.toDate())) : " Atualmente.");
    }


    public static DateTime getDateTimeFinalDaVigenciaNovo(ValidadorVigencia novo, DateTime dtFinalDaVigenciaNovo) {
        if (novo.getFimVigencia() != null) {
            dtFinalDaVigenciaNovo = new DateTime(novo.getFimVigencia());
        }
        return dtFinalDaVigenciaNovo;
    }

    public static Integer getDias(Date dataInicial, Date dataFinal) {
        Long data1 = dataInicial.getTime() / (24 * 60 * 60 * 1000);
        Long data2 = dataFinal.getTime() / (24 * 60 * 60 * 1000);
        Long diferenca = (data2 - data1) + 1;
        return diferenca.intValue();
    }

    //MUNIF
    public static int getPrazoDias(Date dataACeite, Date dataAtual, int prazoDias, List<Feriado> feriados) {
        ////System.out.println("Data de aceite " + formatterDiaMesAno.format(dataACeite) + " data atual " + formatterDiaMesAno.format(dataAtual));
        if (!dataACeite.before(dataAtual)) {
            throw new RuntimeException("A dada de aceite deve ser menor do que a data atual. Data de aceite " + dataACeite + " data atual " + dataAtual);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(dataACeite);
        while (cal.getTime().before(dataAtual)) {
            //System.out.print(formatterDiaMesAno.format(cal.getTime()) + " " + prazoDias);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            boolean conta = true;
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                //System.out.print(" Sábado ou Domingo");
                conta = false;
            } else if (estaNaListaDeFeriados(cal.getTime(), feriados)) {
                //System.out.print(" Feriado");
                conta = false;
            }
            if (conta) {
                ////System.out.println(" Conta");
                prazoDias--;
            } else {
                ////System.out.println(" Pula");
            }

        }
        return prazoDias;
    }

    public static boolean estaNaListaDeFeriados(Date dia, List<Feriado> lista) {
        dia = dataSemHorario(dia);
        for (Feriado f : lista) {
            if (dia.equals(dataSemHorario(f.getDataFeriado()))) {
                return true;
            }
        }
        return false;
    }

    public static int getPrazoHoras(Date dataACeite, Date dataAtual, int prazoHoras, List<Feriado> feriados, ConfiguracaoHorarioExpediente expediente) {
        return 10;
    }

    public static Date getHorario(int h, int m, int s) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, m);
        cal.set(Calendar.SECOND, s);
        return cal.getTime();
    }

    public static Date getDia(int dia, int mes, int ano) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, dia);
        cal.set(Calendar.MONTH, mes - 1);
        cal.set(Calendar.YEAR, ano);
        return cal.getTime();
    }

    public static Date dataSemHorario(Date data) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getDataUltimaHoraDia(Date data) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 59);
        return cal.getTime();
    }

    public static void main(String args[]) {
        ConfiguracaoHorarioExpediente che = new ConfiguracaoHorarioExpediente();
        che.setHoraInicialManha(getHorario(8, 0, 0));
        che.setHoraFinalManha(getHorario(12, 0, 0));
        che.setHoraInicialTarde(getHorario(14, 0, 0));
        che.setHoraFinalTarde(getHorario(17, 0, 0));
        List<Feriado> feriados = new ArrayList<>();
        feriados.add(new Feriado(getDia(18, 8, 2012), "Dia do Munif"));
        feriados.add(new Feriado(getDia(21, 7, 2012), "Dia Teste 1"));
        feriados.add(new Feriado(getDia(15, 7, 2012), "Dia Teste 2"));
        feriados.add(new Feriado(getDia(15, 7, 2012), "Dia Teste 2b"));
        feriados.add(new Feriado(getDia(10, 7, 2012), "Dia Teste 3"));
        feriados.add(new Feriado(getDia(23, 7, 2012), "Dia Teste 4"));

        long agora = System.currentTimeMillis();
        int n = 0;
        for (int i = 0; i < 1000; i++) {
            n += getPrazoDias(getDia(5, 7, 2012), new Date(), 30, feriados);
        }
        //System.out.println(System.currentTimeMillis() - agora + "ms");
        //System.out.println(getPrazoDias(getDia(5, 7, 2000), new Date(), 30, feriados));

    }

    public static Integer getMinutos(Date inicio, Date fim) {
        return getMinutos(new DateTime(inicio), new DateTime(fim));
    }

    public static Integer getMinutos(DateTime inicio, DateTime fim) {
        return Minutes.minutesBetween(inicio, fim).getMinutes();
    }

    public static int getQuantidadeDeDiasNoMes(Date data) {
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static boolean isVigente(Date dataInicio, Date dataFim) {
        boolean valida = false;
        Date dataAtual = new Date();
        if (dataInicio != null && dataFim != null) {
            if (dataAtual.getTime() >= dataInicio.getTime() && dataAtual.getTime() <= dataFim.getTime()) {
                valida = true;
            }
        }

        if (dataInicio != null && dataFim == null) {
            if (dataAtual.getTime() >= dataInicio.getTime()) {
                valida = true;
            }
        }
        return valida;
    }

    public static boolean isVigente(Date dataInicio, Date dataFim, Date dataReferencia) {
        if (dataInicio != null && dataFim != null) {
            if (dataReferencia.getTime() >= dataInicio.getTime() && dataReferencia.getTime() <= dataFim.getTime()) {
                return true;
            }
        }

        if (dataInicio != null && dataFim == null) {
            if (dataReferencia.getTime() >= dataInicio.getTime()) {
                return true;
            }
        }
        return false;
    }

    public static boolean verificaDataFinalMaiorQueDataInicial(Date dataInicial, Date dataFinal) {
        if (dataInicial != null && dataFinal != null) {
            return dataInicial.after(dataFinal);
        }
        return false;
    }

    public static Date getDataDiaAnterior(Date dataAtual) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataAtual);
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c.getTime();
    }

    public static Date ajustarData(Date data, int tipoCampo, int quantidade, FeriadoFacade feriadoFacade) {
        return ajustarData(data, tipoCampo, quantidade, feriadoFacade, 1);
    }

    public static Date ajustarData(Date data, int tipoCampo, int quantidade, FeriadoFacade feriadoFacade, int acrescimo) {
        Calendar retorno = Calendar.getInstance();
        retorno.setTime(data);
        retorno.add(tipoCampo, quantidade);
        while (ehDiaNaoUtil(retorno.getTime(), feriadoFacade)) {
            retorno.add(Calendar.DAY_OF_MONTH, acrescimo);
        }
        return retorno.getTime();
    }

    public static Date acrescentarUmAno(Date dataOriginal) {
        Calendar c = Calendar.getInstance();

        c.setTime(dataOriginal);
        c.add(Calendar.YEAR, 1);

        return c.getTime();
    }

    public static Date adicionarMeses(Date dataOriginal, int qtdeMes) {
        Calendar c = Calendar.getInstance();

        c.setTime(dataOriginal);
        c.add(Calendar.MONTH, qtdeMes);
        return c.getTime();
    }

    public static Date adicionarMinutos(Date dataOriginal, int qtdeMinutos) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataOriginal);
        c.add(MINUTE, qtdeMinutos);
        return c.getTime();
    }

    public static String getDescricaoMes(Integer indice) {
        for (Mes mes : Mes.values()) {
            if (mes.getNumeroMes().equals(indice)) {
                return mes.getDescricao();
            }
        }
        return "";
    }

    public static String recuperarDataEscritaPorExtenso(Date dataReferencia) {
        DateFormat df = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy.");
        dataReferencia = Calendar.getInstance(Locale.getDefault()).getTime();
        return df.format(dataReferencia);
    }

    public static String recuperarDataPorExtenso(Date dataReferencia) {
        DateFormat df = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy.");
        dataReferencia = Calendar.getInstance(Locale.getDefault()).getTime();
        return df.format(dataReferencia);
    }

    public static Date adicionaDias(Date dataAtual, Integer dias) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataAtual);
        c.add(Calendar.DAY_OF_MONTH, dias);
        return c.getTime();
    }

    public static Date adicionaAnos(Date dataAtual, Integer anos) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataAtual);
        c.add(Calendar.YEAR, anos);
        return c.getTime();
    }

    public static Date removerDias(Date dataAtual, Integer dias) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataAtual);
        c.add(Calendar.DAY_OF_MONTH, -dias);
        return c.getTime();
    }

    public static Date adicionaHoras(Date dataAtual, Integer horas) {
        DateTime hoje = new DateTime(dataAtual);
        hoje = hoje.plusHours(horas);
        return hoje.toDate();

//        Calendar c = Calendar.getInstance();
//        c.setTime(dataAtual);
//        c.add(Calendar.HOUR_OF_DAY, horas);
//        return c.getTime();
    }

    public static Date juntarDataEHora(Date dia, Date hora) {
        Calendar calData = Calendar.getInstance();
        calData.setTime(dia);

        Calendar calHora = Calendar.getInstance();
        calHora.setTime(hora);

        calData.set(Calendar.HOUR_OF_DAY, calHora.get(Calendar.HOUR_OF_DAY));
        calData.set(Calendar.MINUTE, calHora.get(Calendar.MINUTE));
        calData.set(Calendar.SECOND, calHora.get(Calendar.SECOND));

        return calData.getTime();
    }

    public static String totalDeDiasEmAnosMesesDias(int totalDias) {
        Double diasRestantes = Double.valueOf(totalDias);
        Double anos = diasRestantes / 365.25;
        Double mesesRestante = (anos - anos.intValue()) * 12;
        diasRestantes = ((((anos - anos.intValue()) * 12) - Double.valueOf(String.valueOf((anos - anos.intValue()) * 12)).intValue()) * 30);
        String retorno = "";
        if (anos > 0) {
            retorno += anos.intValue();
            if (anos > 1) {
                retorno += " ANOS, ";
            } else {
                retorno += " ANO, ";
            }
        }
        if (mesesRestante > 0) {
            retorno += Double.valueOf(String.valueOf((anos - anos.intValue()) * 12)).intValue();
            if (mesesRestante > 1) {
                retorno += " MESES e ";
            } else {
                retorno += " MÊS e ";
            }
        }
        if (diasRestantes > 0) {
            retorno += diasRestantes.intValue();
            if (diasRestantes > 1) {
                retorno += " DIAS";
            } else {
                retorno += " DIA";
            }
        }
        if ("".equals(retorno.trim())) {
            retorno = "-";
        }
        return retorno.trim();
    }

    /**
     * Retorna verdadeiro caso data informada seja um dia de sábado ou domingo
     *
     * @param dataParametro
     * @return boolean true para sábado ou domingo
     */
    public static boolean isSabadoDomingo(Date dataParametro) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataParametro);

        boolean result = false;
        if ((c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) || (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)) {
            result = true;
        }
        return result;
    }

    public static boolean isDomingo(Date dataParametro) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataParametro);

        boolean result = false;
        if ((c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
            result = true;
        }
        return result;
    }

    public static Date somaPeriodo(Date dataInicial, Integer prazo, TipoPrazo tipoPrazo) {
        if (dataInicial == null || prazo == null || tipoPrazo == null) {
            return null;
        }
        DateTime dt = somaTempo(dataInicial, prazo, tipoPrazo);
        return dt.toDate();
    }

    private static DateTime somaTempo(Date dataInicial, Integer prazo, TipoPrazo tipoPrazo) {
        DateTime dt = new DateTime(dataInicial);
        switch (tipoPrazo) {
            case ANOS:
                return dt.plusYears(prazo);
            case DIAS:
                return dt.plusDays(prazo);
            case HORAS:
                return dt.plusHours(prazo);
            case MESES:
                return dt.plusMonths(prazo);
            case MINUTOS:
                return dt.plusMinutes(prazo);
            case SEGUNDOS:
                return dt.plusSeconds(prazo);
            case SEMANAS:
                return dt.plusWeeks(prazo);
            default:
                new Exception("Nenhum tipo de prazo definido para a data: " + tipoPrazo.getDescricao());
                return null;
        }
    }

    public static String retornaDescricaoMes(String mes) {
        String toReturn = "";
        switch (mes) {
            case "01":
                toReturn = "Janeiro";
                break;
            case "02":
                toReturn = "Fevereiro";
                break;
            case "03":
                toReturn = "Março";
                break;
            case "04":
                toReturn = "Abril";
                break;
            case "05":
                toReturn = "Maio";
                break;
            case "06":
                toReturn = "junho";
                break;
            case "07":
                toReturn = "Julho";
                break;
            case "08":
                toReturn = "Agosto";
                break;
            case "09":
                toReturn = "Setembro";
                break;
            case "10":
                toReturn = "Outubro";
                break;
            case "11":
                toReturn = "Novembro";
                break;
            case "12":
                toReturn = "Dezembro";
                break;
        }
        return toReturn;
    }

    public static String diaExtenso(String dia) {
        NumeroExtenso ne = new NumeroExtenso();
        return ne.getExtenso(dia);
    }

    public static XMLGregorianCalendar convertDateToXMLGregorianCalendar(Date date) {
        try {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(date);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Não foi possível converter a data {} para XMLGregorianCalendar", date);
        }
        return null;
    }

    public static String getIdade(Date data) {
        if (data != null) {
            DateTime dateTime = new DateTime(data);
            DateTime hoje = new DateTime(new Date());
            return Years.yearsBetween(dateTime, hoje).getYears() + "";
        }
        return "";
    }

    public static List<Date> pegaDiasPorDiaDaSemana(Date dtInicio, Date dtFim, int... diasDaSemanaAVerificar) {
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(dtFim);
        Calendar diaAtual = Calendar.getInstance();
        diaAtual.setTime(dtInicio);
        List<Date> dias = new ArrayList<>();
        while (diaAtual.compareTo(calEnd) <= 0) {
            for (int diaDaSemana : diasDaSemanaAVerificar) {
                if (diaAtual.get(Calendar.DAY_OF_WEEK) == diaDaSemana) {
                    dias.add(diaAtual.getTime());
                    break;
                }
            }
            diaAtual.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dias;
    }

    public static boolean isBetween(Date inicio, Date fim, Date referencia) {
        return isBetween(new DateTime(inicio), fim != null ? new DateTime(fim) : null, new DateTime(referencia));
    }

    public static boolean isBetween(DateTime inicio, DateTime fim, DateTime referencia) {
        if (fim == null) {
            fim = referencia;
        }
        if (referencia.isEqual(inicio) || referencia.isEqual(fim)) return true;
        Interval interval = new Interval(inicio, fim);
        return interval.contains(referencia);
    }

    public static String getAnosAndDiasFormatadosPorPeriodo(DateTime dataInicial, DateTime dataFinal, int diasAMais) {
        PeriodFormatter formato = new PeriodFormatterBuilder()
            .printZeroNever()
            .appendYears()
            .appendSuffix(" ano e ", " anos e ")
            .appendDays()
            .appendSuffix(" dia ", " dias ")
            .toFormatter();

        Period periodo = new Period(dataInicial, dataFinal.plusDays(diasAMais), PeriodType.yearDay());
        return formato.print(periodo);
    }

    public static Date alterarHoras(Date data, int hora, int minutos, int segundo) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minutos);
        calendar.set(Calendar.SECOND, segundo);
        return calendar.getTime();
    }

    public static Date ultimaHoraDoDia(Date date) {
        return DataUtil.alterarHoras(date, 23, 59, 59);
    }


    public static String ultimaHoraDoDiaSintaxeTimestamp(Date date) {
        return " TO_TIMESTAMP ('" + DataUtil.getDataFormatadaDiaHora(ultimaHoraDoDia(date)) + ".999999999', 'dd/MM/yyyy HH24:MI:SS.FF') ";
    }

    public static List<DateTime> transformarPeriodoEmList(Date inicio, Date termino) {
        if (inicio == null || termino == null) {
            return Lists.newArrayList();
        }
        return transformarPeriodoEmList(new DateTime(inicio), new DateTime(termino));
    }

    public static List<DateTime> transformarPeriodoEmList(DateTime inicio, DateTime termino) {
        List<DateTime> datas = Lists.newLinkedList();
        if (inicio == null || termino == null) {
            return Lists.newArrayList();
        }
        while (inicio.isBefore(termino)) {
            datas.add(inicio);
            inicio = inicio.plusDays(1);
        }
        return datas;
    }

    public static String converterAnoMesDia(Date dataReferencia) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(dataReferencia);
    }

    public static Map<Integer, Set<Date>> getTodosOsDiasEntreDatasNoMap(Date dataInicial, Date dataFinal, Map<Integer, Set<Date>> anoDias) {
        if (anoDias == null) {
            anoDias = Maps.newHashMap();
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(dataInicial);
        while (dataSemHorario(calendar.getTime()).compareTo(dataSemHorario(dataFinal)) <= 0) {
            Set<Date> dates = new HashSet<>();
            Date result = dataSemHorario(calendar.getTime());
            dates.add(result);
            if (anoDias.isEmpty() || !anoDias.keySet().contains(calendar.get(Calendar.YEAR))) {
                anoDias.put(calendar.get(Calendar.YEAR), dates);
            } else {
                anoDias.get(calendar.get(Calendar.YEAR)).addAll(dates);
            }
            calendar.add(Calendar.DATE, 1);
        }
        return anoDias;
    }

    public static Date getDataComHoraAtual(Date dataOperacao) {
        LocalDateTime now = LocalDateTime.now();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataOperacao);
        calendar.set(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DATE),
            now.getHourOfDay(),
            now.getMinuteOfHour(),
            now.getSecondOfMinute());
        return calendar.getTime();
    }

    public static boolean compararDatas(Date dataUm, Date dataDois) {
        dataUm = Util.getDataHoraMinutoSegundoZerado(dataUm);
        dataDois = Util.getDataHoraMinutoSegundoZerado(dataDois);
        return dataUm.compareTo(dataDois) == 0;
    }

    public static DateTime criarDataUltimoDiaMes(Integer mes, Integer ano) {
        try {
            DateTime dataAtual = new DateTime();
            dataAtual = dataAtual.withMonthOfYear(mes);
            dataAtual = dataAtual.withYear(ano);
            dataAtual = dataAtual.dayOfMonth().withMaximumValue();
            return dataAtual;
        } catch (RuntimeException ex) {
            throw new RuntimeException("Problema na criação da data", ex);
        }
    }

    public static Date getUltimoDiaMes(Integer mes, Integer ano) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, ano);
        cal.set(Calendar.MONTH, mes - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static java.time.LocalDate criarDataPrimeiroDiaMes(Integer mes, Integer ano) {
        try {
            java.time.LocalDate primeiroDiaDoMes = java.time.LocalDate.now();
            primeiroDiaDoMes = primeiroDiaDoMes.withDayOfMonth(1);
            primeiroDiaDoMes = primeiroDiaDoMes.withMonth(mes);
            primeiroDiaDoMes = primeiroDiaDoMes.withYear(ano);
            return primeiroDiaDoMes;
        } catch (RuntimeException ex) {
            throw new RuntimeException("Problema na criação da data", ex);
        }
    }

    public static String montarDataFormatada(int dia, int mes, int ano, String mask) {
        Calendar c = Calendar.getInstance();
        c.set(ano, mes, dia);
        return getDataFormatada(c.getTime(), mask);
    }

    public static Date buscarUltimoDiaMesPeloDiaDaSemana(int month, int year, int diaSemana) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        cal.set(GregorianCalendar.DAY_OF_WEEK, diaSemana);
        cal.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    public static int montarDataCalendarioJuliano(Date data) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);

        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);
        int segundo = calendar.get(Calendar.SECOND);

        double base = (100.0 * ano) + mes - 190002.5;

        return (int) ((367.0 * ano) -
            (Math.floor(7.0 * (ano + Math.floor((mes + 9.0) / 12.0)) / 4.0)) +
            Math.floor((275.0 * mes) / 9.0) +
            dia + ((hora + ((minuto + (segundo / 60.0)) / 60.0)) / 24.0) +
            1721013.5 - ((0.5 * base) / Math.abs(base)) + 0.5);
    }

    public static Date getDataHoraMinutoSegundoZerado(Date valorData) {
        Calendar c = Calendar.getInstance();
        c.setTime(valorData);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date parseDateRFC3339(String data) {
        try {
            String timezone = "-";
            String timestamp = StringUtils.substringBeforeLast(data, "-");
            timezone += StringUtils.substringAfterLast(data, "-").replace(":", "");

            data = timestamp + timezone;

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", Locale.getDefault());//spec for RFC3339 (with fractional seconds)
            format.setLenient(true);

            return format.parse(data);
        } catch (Exception e) {
            log.error("Erro ao fazer parse do padrao RFC3339. ", e);
        }
        return null;
    }

    public static java.time.LocalDate dateToLocalDate(Date date) {
        return java.time.Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date localDateToDate(java.time.LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static boolean isWithinRange(java.time.LocalDate testDate, java.time.LocalDate start, java.time.LocalDate end) {
        return !(testDate.isBefore(start) || testDate.isAfter(end));
    }

    public static ZonedDateTime dateTimeToLocalDateTime(DateTime inicioPeriodo) {
        return java.time.Instant.ofEpochMilli(inicioPeriodo.getMillis())
            .atZone(ZoneId.systemDefault());
    }

    public static java.time.LocalDateTime dateToLocalDateTime(Date date) {
        return java.time.Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Integer daysBetween(java.time.LocalDate start, java.time.LocalDate end) {
        return ((Long) DAYS.between(start, end)).intValue();
    }

    public static Integer monthsBetween(java.time.LocalDate start, java.time.LocalDate end) {
        return ((Long) MONTHS.between(start, end)).intValue();
    }


}
