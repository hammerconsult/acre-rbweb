/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.BarCode;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.controle.Web;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoParte;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.esocial.domain.OcorrenciaESocial;
import br.com.webpublico.esocial.dto.OcorrenciaESocialDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.interfaces.InjetaSistemaService;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.UploadArquivo;
import br.com.webpublico.nfse.domain.dtos.ParametrosFiltroNfseDTO;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Limpavel;
import br.com.webpublico.util.anotacoes.Positivo;
import br.com.webpublico.util.tratamentoerros.BuscaCausa;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.catalina.connector.RequestFacade;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.xml.sax.SAXException;

import javax.ejb.EJBException;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.net.ssl.SSLContext;
import javax.persistence.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Munif
 */
public class Util extends UtilEntidades {

    public static final Locale localeBrasil = new Locale("pt_BR_", "pt", "BR");
    public static final FastDateFormat formatterDiaMesAno = FastDateFormat.getInstance("dd/MM/yyyy", localeBrasil);
    public static final FastDateFormat formatterHoraMinuto = FastDateFormat.getInstance("HH:mm", localeBrasil);
    public static final FastDateFormat formatterDataHora = FastDateFormat.getInstance("dd/MM/yyyy HH:mm:ss", localeBrasil);
    public static final FastDateFormat formatterDataSemBarra = FastDateFormat.getInstance("ddMMyyyy", localeBrasil);
    public static final FastDateFormat formatterHoraMinutoSegundo = FastDateFormat.getInstance("HH:mm:ss", localeBrasil);
    public static final FastDateFormat formatterAno = FastDateFormat.getInstance("yyyy", localeBrasil);
    public static final FastDateFormat formatterMes = FastDateFormat.getInstance("MM", localeBrasil);
    public static final FastDateFormat formatterMesAno = FastDateFormat.getInstance("MM/yyyy", localeBrasil);
    public static final String MASCARA_DECIMAL_DOIS_DIGITOS = "#,##0.00";
    public static final String MASCARA_DECIMAL_QUATRO_DIGITOS = "#,####0.0000";
    protected static final Logger logger = LoggerFactory.getLogger(Util.class);
    private static final int DIAS_NO_MES[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}; //Janeiro é mes 0 igual a calendar
    private static final Map<HttpServletRequest, EntityManager> entitysManagerOpenViewReadOnly = Maps.newHashMap();
    private static final Object criarEntityManagerOpenViewReadOnly = new Object();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String JAVA_MODULE_PATH = "java:module/";

    public static String parseMonthYear(Date data) {
        return formatterMesAno.format(data);
    }


    public static String dateToString(Date data) {
        return formatterDiaMesAno.format(data);
    }

    public static String dateToString(LocalDate data) {
        return formatterDiaMesAno.format(data);
    }

    public static String hourToString(Date data) {
        return formatterHoraMinutoSegundo.format(data);
    }

    public static String dateHourToString(Date data) {
        return formatterDataHora.format(data);
    }

    public static String reaisToString(BigDecimal valor) {
        return new DecimalFormat(MASCARA_DECIMAL_DOIS_DIGITOS).format(valor);
    }

    /**
     * Retorna o número de dias de um determinado mes mes 0 é janeiro
     *
     * @param numeroMes número do mês no calendário, de 0 a 11
     * @param ano       ano em que se deseja calcular, necessário para o cálculo de ano bissexto
     * @return o número de dias no mês/ano informados
     */
    public static int getDiasMes(int numeroMes, int ano) {
        DateTime dateTime = new DateTime(ano, numeroMes, 1, 0, 0);
        return dateTime.dayOfMonth().getMaximumValue();
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

    public static String removerCaracteresEspeciais(String value) {
        return value.replaceAll("[-+.^:,/_()]", "");
    }

    public static String getAnoDaData(Date data) {
        return formatterAno.format(data);
    }

    public static String getMesDaData(Date data) {
        return formatterMes.format(data);
    }

    public static Boolean validaCodigoAlfaNumerico(String codigo) {
        return codigo.matches("[a-zA-Z0-9]*");
    }

    public static int getDiferencaMeses(Date inicio, Date fim) {
        int lMultiplier = 1;
        if (inicio.after(fim)) {
            lMultiplier = -1;
            java.util.Date pTemp = fim;
            fim = inicio;
            inicio = pTemp;
        }

        Calendar lFrom = new GregorianCalendar();
        lFrom.setTime(inicio);
        Calendar lTo = new GregorianCalendar();
        lTo.setTime(fim);

        int lFromYear = lFrom.get(Calendar.YEAR);
        int lFromMonth = lFrom.get(Calendar.MONTH);
        int lFromDay = lFrom.get(Calendar.DAY_OF_MONTH);

        int lToYear = lTo.get(Calendar.YEAR);
        int lToMonth = lTo.get(Calendar.MONTH);
        int lToDay = lTo.get(Calendar.DAY_OF_MONTH);

        int lYearDiff = lToYear - lFromYear;
        int lMonthDiff = lToMonth - lFromMonth;
        int lDayDiff = lToDay - lFromDay;

        if (lDayDiff < 0) {
            lMonthDiff--;
            Calendar lTemp = new GregorianCalendar();
            lTemp.setTime(fim);
            lTemp.add(lTemp.MONTH, -1);
            lDayDiff = lTemp.getActualMaximum(lTemp.DAY_OF_MONTH) + lDayDiff;
        }

        if (lMonthDiff < 0) {
            lYearDiff--;
            lMonthDiff = 12 + lMonthDiff;
        }
        return lMonthDiff;
    }

    public static long diferencaDeDiasEntreData(Date inicio, Date fim) {
        long diferenca = 0;
        if (inicio.after(fim)) {
            java.util.Date pTemp = fim;
            fim = inicio;
            inicio = pTemp;
        }

        Calendar lFrom = new GregorianCalendar();
        lFrom.setTime(inicio);
        Calendar lTo = new GregorianCalendar();
        lTo.setTime(fim);

        diferenca = (lTo.getTimeInMillis() - lFrom.getTimeInMillis()) / (1000 * 60 * 60 * 24);
        return diferenca;
    }

    public static long diferencaDeMesesEntreData(Date inicio, Date fim) {
        long diferenca = 0;
        if (inicio.after(fim)) {
            java.util.Date pTemp = fim;
            fim = inicio;
            inicio = pTemp;
        }

        Calendar lFrom = new GregorianCalendar();
        lFrom.setTime(inicio);
        Calendar lTo = new GregorianCalendar();
        lTo.setTime(fim);

        diferenca = (lTo.getTimeInMillis() - lFrom.getTimeInMillis()) / (1000 * 60 * 60 * 24) / 30;
        return diferenca;
    }

    /*
     * Retorna uma data com o primeiro dia do mes passado por parâmetro
     */
    public static Date criaDataPrimeiroDiaDoMes(Integer mes, Integer ano) {
        try {
            Calendar dataAtual = new GregorianCalendar();
            dataAtual.setTime(new Date());
            Calendar data = new GregorianCalendar(ano, mes, dataAtual.get(Calendar.DAY_OF_MONTH));
            return data.getTime();
        } catch (RuntimeException ex) {
            throw new RuntimeException("Problema na criação da data", ex);
        }
    }

    public static DateTime criaDataPrimeiroDiaMesJoda(Integer mes, Integer ano) {

        try {
            DateTime dataAtual = new DateTime();
            dataAtual = dataAtual.withDayOfMonth(1);
            dataAtual = dataAtual.withMonthOfYear(mes);
            dataAtual = dataAtual.withYear(ano);

            return dataAtual;
        } catch (RuntimeException ex) {
            throw new RuntimeException("Problema na criação da data", ex);
        }
    }

    public static Date criaDataPrimeiroDiaDoMesFP(Integer mes, Integer ano) {
        try {
            Calendar dataAtual = Calendar.getInstance();
            dataAtual.set(Calendar.YEAR, ano);
            dataAtual.set(Calendar.MONTH, mes);
            dataAtual.set(Calendar.DAY_OF_MONTH, dataAtual.getMinimum(Calendar.DAY_OF_MONTH));
            return dataAtual.getTime();
        } catch (RuntimeException ex) {
            throw new RuntimeException("Problema na criação da data", ex);
        }
    }

    public static Date criaDataUltimoDiaDoMesFP(Integer mes, Integer ano) {
        try {
            Calendar dataAtual = Calendar.getInstance();
            dataAtual.set(Calendar.YEAR, ano);
            dataAtual.set(Calendar.MONTH, mes);
            dataAtual.set(Calendar.DAY_OF_MONTH, dataAtual.getMaximum(Calendar.DAY_OF_MONTH));
            return dataAtual.getTime();
        } catch (RuntimeException ex) {
            throw new RuntimeException("Problema na criação da data", ex);
        }
    }

    public static Date recuperaDataUltimoDiaDoMes(Date data) {
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        Integer ultimoDia = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar c2 = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), ultimoDia);
        return c2.getTime();
    }

    public static Date recuperaDataUltimoDiaMesSeguinte(Date data) {
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        c.add(Calendar.MONTH, Calendar.MONTH - 1);
        Integer ultimoDia = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar c2 = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), ultimoDia);
        return c2.getTime();
    }

    public static boolean validarEmail(String email) {
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean valida_CpfCnpj(String s_aux) {
        if (s_aux == null) {
            return false;
        }
        s_aux = s_aux.replace(".", "");
        s_aux = s_aux.replace("-", "");
        s_aux = s_aux.replace("/", "");
//------- Rotina para CPF
        if (s_aux.length() == 11) {
            if (Sets.newHashSet("00000000000", "11111111111", "999999999").contains(s_aux)) {
                return false;
            }

            int d1, d2;
            int digito1, digito2, resto;
            int digitoCPF;
            String nDigResult;
            d1 = d2 = 0;
            digito1 = digito2 = resto = 0;
            for (int n_Count = 1; n_Count < s_aux.length() - 1; n_Count++) {
                digitoCPF = Integer.valueOf(s_aux.substring(n_Count - 1, n_Count)).intValue();
//--------- Multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.
                d1 = d1 + (11 - n_Count) * digitoCPF;
//--------- Para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.
                d2 = d2 + (12 - n_Count) * digitoCPF;
            }
            ;
//--------- Primeiro resto da divisão por 11.
            resto = (d1 % 11);
//--------- Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
            if (resto < 2) {
                digito1 = 0;
            } else {
                digito1 = 11 - resto;
            }
            d2 += 2 * digito1;
//--------- Segundo resto da divisão por 11.
            resto = (d2 % 11);
//--------- Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
            if (resto < 2) {
                digito2 = 0;
            } else {
                digito2 = 11 - resto;
            }
//--------- Digito verificador do CPF que está sendo validado.
            String nDigVerific = s_aux.substring(s_aux.length() - 2, s_aux.length());
//--------- Concatenando o primeiro resto com o segundo.
            nDigResult = String.valueOf(digito1) + String.valueOf(digito2);
//--------- Comparar o digito verificador do cpf com o primeiro resto + o segundo resto.
            return nDigVerific.equals(nDigResult);
        } //-------- Rotina para CNPJ
        else if (s_aux.length() == 14) {
            if (Sets.newHashSet("00000000000000", "11111111111111", "999999999999").contains(s_aux)) {
                return false;
            }

            int soma = 0, aux, dig;
            String cnpj_calc = s_aux.substring(0, 12);
            char[] chr_cnpj = s_aux.toCharArray();
//--------- Primeira parte
            for (int i = 0; i < 4; i++) {
                if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                    soma += (chr_cnpj[i] - 48) * (6 - (i + 1));
                }
            }
            for (int i = 0; i < 8; i++) {
                if (chr_cnpj[i + 4] - 48 >= 0 && chr_cnpj[i + 4] - 48 <= 9) {
                    soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));
                }
            }
            dig = 11 - (soma % 11);
            cnpj_calc += (dig == 10 || dig == 11)
                ? "0" : Integer.toString(dig);
//--------- Segunda parte
            soma = 0;
            for (int i = 0; i < 5; i++) {
                if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                    soma += (chr_cnpj[i] - 48) * (7 - (i + 1));
                }
            }
            for (int i = 0; i < 8; i++) {
                if (chr_cnpj[i + 5] - 48 >= 0 && chr_cnpj[i + 5] - 48 <= 9) {
                    soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));
                }
            }
            dig = 11 - (soma % 11);
            cnpj_calc += (dig == 10 || dig == 11)
                ? "0" : Integer.toString(dig);
            return s_aux.equals(cnpj_calc);
        } else {
            return false;
        }
    }

    public static Object clonarObjeto(Object obj) {
        try {
            return BeanUtils.cloneBean(obj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static Object clonarEmNiveis(Object obj, int nivel) {
        try {
            if (obj == null) {
                return null;
            }
            Object retorno = BeanUtils.cloneBean(obj);
            if (nivel <= 0) {
                return retorno;
            }
            nivel--;
            for (Field field : Persistencia.getAtributos(retorno.getClass())) {
                field.setAccessible(true);
                Class c = field.getType();


                if (c.isAnnotationPresent(Entity.class)) {
                    field.set(retorno, clonarEmNiveis(field.get(retorno), nivel));
                }

                if (Collection.class.isAssignableFrom(c)) {
                    try {
                        List l = (List) field.get(retorno);
                        if (l != null) {
                            ArrayList<Object> novaLista = new ArrayList<>();
                            for (Object o : l) {
                                novaLista.add(BeanUtils.cloneBean(o));
                            }
                            field.set(retorno, novaLista);
                        }
                    } catch (LazyInitializationException lie) {

                    }
                }
            }

            return retorno;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static boolean validaVigencia(Date inicioVigencia, Date finalVigencia) {
        Date dataAtual = Util.getDataHoraMinutoSegundoZerado(new Date());
        if (finalVigencia != null) {
            return (dataAtual.getTime() <= finalVigencia.getTime());
        } else {
            finalVigencia = dataAtual;
            return ((dataAtual.getTime() >= inicioVigencia.getTime())
                && (dataAtual.getTime() <= finalVigencia.getTime()));
        }
    }

    /**
     * <b> Método implementado pela área Administrativa do sistema WebPúblico
     * </b> Este método é utilizado para adicionar ou substituir um objeto
     * contido em uma lista visando diminuir o grande número de verificações
     * desnecessárias nas classes. <p> <b>Obs:</b> Para que este método funcione
     * perfeitamente,principalmente no quesito de substituição é necessário que
     * os métodos 'Equals' e 'HashCode' estejam implementados nas classes
     * participantes de acordo com o padrão encontrado na classe
     * IdentidadeDaEntidade.
     * <p/>
     * </p>
     *
     * @param lista  indica a lista que receberá o objeto
     * @param objeto indica o objeto que será adicionado ou sobrescrito caso já
     *               esteja na lista
     * @see IdentidadeDaEntidade
     */
    public static <T> List<T> adicionarObjetoEmLista(List<T> lista, T objeto) {
        if (lista == null) {
            lista = new ArrayList<>();
        }

        if (lista.contains(objeto)) {
            lista.set(lista.indexOf(objeto), objeto);
        } else {
            lista.add(objeto);
        }
        return lista;
    }

    public static void limparCampos(Object obj) {

        for (Field f : obj.getClass().getDeclaredFields()) {

            if (f.isAnnotationPresent(Limpavel.class)) {
                f.setAccessible(true);
                Limpavel l = f.getAnnotation(Limpavel.class);
                if (l.value() == Limpavel.STRING_VAZIA) {
                    trataStringVazia(obj, f);
                } else if (l.value() == Limpavel.NULO) {
                    trataNulo(obj, f);

                } else if (l.value() == Limpavel.ZERO) {
                    trataZero(obj, f);

                } else if (l.value() == Limpavel.FALSO) {
                    trataFalso(obj, f);

                } else if (l.value() == Limpavel.VERDADEIRO) {
                    trataVerdadeiro(obj, f);

                } else if (l.value() == Limpavel.NOVO_OBJETO) {
                    trataNovoObjeto(obj, f);
                }
                f.setAccessible(false);

            }
        }
    }

    private static void trataStringVazia(Object obj, Field f) {
        if (f.getType().equals(String.class) || f.getType().equals(char.class)
            || f.getType().equals(Character.class)) {
            try {
                f.set(obj, "");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } /*else {
         //logger.debug("Não é possível setar uma String vazia para um campo do tipo " + f.getType());
         }*/
    }

    private static void trataZero(Object obj, Field f) {
        if (f.getType().equals(Integer.class) || f.getType().equals(int.class)
            || f.getType().equals(Double.class) || f.getType().equals(double.class)) {
            try {
                f.set(obj, 0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (f.getType().equals(BigDecimal.class)) {
            try {
                f.set(obj, BigDecimal.ZERO);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (f.getType().equals(Long.class) || (f.getType().equals(long.class))) {
            try {
                f.set(obj, 0L);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } /*else {
         //logger.debug("Não é possível setar um valor numérico para um campo do tipo " + f.getType());
         } */
    }

    private static void trataFalso(Object obj, Field f) {
        if (f.getType().equals(Boolean.class) || f.getType().equals(boolean.class)) {
            try {
                f.set(obj, false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } /*else {
         //logger.debug("Não é possível setar um valor Boolean para um campo do tipo " + f.getType());
         }*/
    }

    private static void trataVerdadeiro(Object obj, Field f) {

        if (f.getType().equals(Boolean.class) || f.getType().equals(boolean.class)) {
            try {
                f.set(obj, true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } /*else {
         //logger.debug("Não é possível setar um valor Boolean para um campo do tipo " + f.getType());
         }*/
    }

    private static void trataNovoObjeto(Object obj, Field f) {

        try {
            f.set(obj, obj.getClass().newInstance());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void trataNulo(Object obj, Field f) {
        if (!isPrimitivo(f)) {
            try {
                f.set(obj, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static boolean isPrimitivo(Field f) {
        return (f.getType().equals(boolean.class)
            || f.getType().equals(char.class)
            || f.getType().equals(int.class)
            || f.getType().equals(long.class)
            || f.getType().equals(float.class));
    }

    public static boolean validaPeriodo(Date inicioVigencia, Date fimVigencia) {
        if (fimVigencia == null) {
            return false;
        } else {
            if (inicioVigencia.after(fimVigencia)) {
                return true;
            }
        }
        return false;
    }

    public static String getNumeroDoMes(Date date) {
        Calendar mes = Calendar.getInstance();
        mes.setTime(date);
        return "" + (mes.get(Calendar.MONTH) + 1);
    }

    public static String montaHQLQuebrandoEspacos(String campo, String filtro) {
        String vetorString[] = filtro.trim().split(" ");

        String retorno = " (lower(" + campo + ") like :filtro0) or ";

        if (vetorString.length > 1) {
            for (int i = 0; i < vetorString.length; i++) {
                retorno += " (lower(" + campo + ") like :filtro" + (i + 1) + ") or ";
            }
        }

        retorno = retorno.substring(0, retorno.length() - 3);
        return " (" + retorno + ") ";
    }

    public static Object[] montaValorParametros(String filtro) {
        List<String> lista = new ArrayList<>();
        String vetorString[] = filtro.split(" ");
        lista.add("%" + filtro.toLowerCase().trim() + "%");

        if (vetorString.length > 1) {
            for (int i = 0; i < vetorString.length; i++) {
                lista.add("%" + vetorString[i].toLowerCase().trim() + "%");
            }
        }

        return lista.toArray();
    }

    /**
     * @param dia referente ao dia
     * @param mes referente ao mês iniciado em 0 (Janeiro = 0)
     * @param ano referente ao ano
     * @return data conforme parâmetros
     */
    public static Date montarData(Integer dia, Integer mes, Integer ano) {
        Calendar dataRetorno = Calendar.getInstance();
        dataRetorno.set(Calendar.DAY_OF_MONTH, dia);
        dataRetorno.set(Calendar.MONTH, mes);
        dataRetorno.set(Calendar.YEAR, ano);
        dataRetorno.setTime(Util.getDataHoraMinutoSegundoZerado(dataRetorno.getTime()));
        return dataRetorno.getTime();
    }

    public static void imprime(String texto) {
        logger.debug(texto);
    }

    public static void geraPDF(String nome, String conteudo, FacesContext facesContext) {
        try {
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "inline;filename=" + nome + ".pdf");
            response.setCharacterEncoding("iso-8859-1");
            response.setLocale(new Locale("pt_BR_", "pt", "BR"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert(conteudo, baos);
            byte[] bytes = baos.toByteArray();
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();
            facesContext.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void gerarPDFNoDialog(String nome, String conteudo) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert(conteudo, baos);
            byte[] bytes = baos.toByteArray();
            InputStream is = new ByteArrayInputStream(bytes);
            StreamedContent pdf = new DefaultStreamedContent(is, "application/pdf", nome);
            Web.getSessionMap().put("relatorio", pdf);
            FacesUtil.atualizarComponente("FormularioRelatorio");
            FacesUtil.executaJavaScript("mostraRelatorio()");
            FacesUtil.executaJavaScript("ajustaImpressaoRelatorio()");
        } catch (Exception e) {
            logger.error("geraPDFNoDialog {}", e);
        }
    }

    public static void geraPDF(String nome, List<String> conteudos, FacesContext facesContext) {
        try {
            Rectangle tamanhoPagina = new Rectangle(800f, 900f);
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "inline;filename=" + nome + ".pdf");
            response.setCharacterEncoding("iso-8859-1");
            response.setLocale(new Locale("pt_BR_", "pt", "BR"));
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            for (String conteudo : conteudos) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Html2Pdf.convert(conteudo, baos);
                byte[] bytes = baos.toByteArray();
                PdfReader reader = new PdfReader(new ByteArrayInputStream(bytes));
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    document.setPageSize(tamanhoPagina);
                    document.newPage();
                    PdfImportedPage page = writer.getImportedPage(reader, i);
                    cb.addTemplate(page, 0, 0);
                }
            }
            response.getOutputStream().flush();
            document.close();
            facesContext.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void downloadPDF(String nome, String conteudo, FacesContext facesContext) {
        //logger.debug("NOME..: " + nome);
        try {
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "attachment;filename=" + nome + ".pdf");
            response.setCharacterEncoding("iso-8859-1");
            response.setLocale(new Locale("pt_BR_", "pt", "BR"));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert(conteudo, baos);
            byte[] bytes = baos.toByteArray();

            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();

            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();

            facesContext.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getStackTraceDaException(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }

    public static Object getControladorPeloNome(String nome) {
        ELContext el = FacesContext.getCurrentInstance().getELContext();
        ELResolver er = FacesContext.getCurrentInstance().getApplication().getELResolver();
        return er.getValue(el, null, nome);
    }

    public static SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public static Object getSpringBeanPeloNome(String nome) {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        return ap.getBean(nome);
    }

    public static String formataNumero(Object valor) {
        try {
            String formato = "#,##0.00";
            DecimalFormat d = new DecimalFormat(formato);
            return d.format(valor);
        } catch (Exception e) {
            return String.valueOf(valor);
        }
    }

    public static String formataNumeroInteiro(Long valor) {
        try {
            String formato = "#,###,###,###";
            DecimalFormat d = new DecimalFormat(formato);
            return d.format(valor);
        } catch (Exception e) {
            return String.valueOf(valor);
        }
    }

    public static String formataValor(BigDecimal valor) {
        DecimalFormat df = new DecimalFormat("###,##0.00");

        if (valor != null) {
            return "R$ " + df.format(valor);
        }
        return "";
    }

    public static String formatarValor(BigDecimal valor) {
        return formatarValor(valor, null, true);
    }

    public static String formatarValor(BigDecimal valor, String mascara, boolean mostrarCifrao) {
        if (valor != null) {
            DecimalFormat df = new DecimalFormat(mascara == null || mascara.isEmpty() ? "###,##0.00" : mascara);
            return (mostrarCifrao ? "R$ " : "") + df.format(valor);
        }
        return "";
    }

    public static String formataValorSemCifrao(BigDecimal valor) {
        DecimalFormat df = new DecimalFormat(MASCARA_DECIMAL_DOIS_DIGITOS);
        if (valor != null) {
            return df.format(valor);
        }
        return "";
    }

    public static String formataValorQuatroCasasDecimais(BigDecimal valor) {
        DecimalFormat df = new DecimalFormat(MASCARA_DECIMAL_QUATRO_DIGITOS);
        if (valor != null) {
            return "R$ " + df.format(valor);
        }
        return "";
    }

    public static String formataPercentual(BigDecimal valor) {
        DecimalFormat df = new DecimalFormat("###,##0.000");

        if (valor != null) {
            return "% " + df.format(valor);
        }
        return "";
    }

    public static String formataUFM(BigDecimal valor) {
        DecimalFormat df = new DecimalFormat("###,##0.0000");

        if (valor != null) {
            return df.format(valor);
        }
        return "";
    }

    public static boolean isStringNulaOuVazia(String string) {
        if (stringNula(string)) {
            return true;
        }

        if (stringVazia(string)) {
            return true;
        }

        return false;
    }

    public static boolean stringNula(String string) {
        return string == null;
    }

    public static boolean stringVazia(String string) {
        return string == null || string.isEmpty();
    }

    public static void validarCampos(Object selecionado) throws ValidacaoException {
        EntidadeMetaData metadata = new EntidadeMetaData(selecionado.getClass());

        ValidacaoException exception = new ValidacaoException();

        for (AtributoMetadata object : metadata.getAtributos()) {
            if (object.getAtributo().isAnnotationPresent(br.com.webpublico.util.anotacoes.Obrigatorio.class)) {
                if (object.getString(selecionado).trim().isEmpty()) {
                    exception.adicionarMensagemDeCampoObrigatorio("O campo " + object.getEtiqueta() + " deve ser informado.");
                    continue;
                } else {
                    object.getString(selecionado);
                }
            }

            if (object.getAtributo().isAnnotationPresent(br.com.webpublico.util.anotacoes.Length.class)) {
                int max = (object.getAtributo().getAnnotation(br.com.webpublico.util.anotacoes.Length.class)).maximo();
                int min = (object.getAtributo().getAnnotation(br.com.webpublico.util.anotacoes.Length.class)).minimo();
                if (object.getString(selecionado).length() > max) {
                    exception.adicionarMensagemDeOperacaoNaoPermitida("O campo " + object.getEtiqueta() + " deve ter menos que " + max + " caracteres.");
                }
                if (object.getString(selecionado).length() < min) {
                    exception.adicionarMensagemDeOperacaoNaoPermitida("O campo " + object.getEtiqueta() + " deve ter mais que " + min + " caracteres!");
                }
            }
        }
        validarQuantidadeMaximaCaracter(exception);
        exception.lancarException();
    }

    @Deprecated
    public static Boolean validaCampos(Object selecionado) {
        EntidadeMetaData metadata = new EntidadeMetaData(selecionado.getClass());
        Boolean retorno = true;
        for (Iterator<AtributoMetadata> it = metadata.getAtributos().iterator(); it.hasNext(); ) {
            AtributoMetadata object = it.next();
            if (object.getAtributo().isAnnotationPresent(br.com.webpublico.util.anotacoes.Obrigatorio.class)) {
                if (object.getString(selecionado).trim().equals("") || ((AtributoMetadata) object).getString(selecionado).trim() == null) {
                    FacesUtil.addCampoObrigatorio("O campo " + object.getEtiqueta() + " deve ser informado!");
                    retorno = false;
                } else if (object.getAtributo().isAnnotationPresent(br.com.webpublico.util.anotacoes.Positivo.class)) {
                    Positivo positivo = object.getAtributo().getAnnotation(Positivo.class);
                    BigDecimal value = new BigDecimal(object.getString(selecionado));
                    if (!positivo.permiteZero() && isMenorOrIgualAZero(value)) {
                        FacesUtil.addCampoObrigatorio("O campo " + object.getEtiqueta() + " não pode ser menor ou igual a zero(0).");
                        retorno = false;
                    } else if (positivo.permiteZero() && BigDecimal.ZERO.compareTo(value) > 0) {
                        FacesUtil.addCampoObrigatorio("O campo " + object.getEtiqueta() + " não pode ser menor que zero(0).");
                        retorno = false;
                    }
                }
            }

            retorno = validarQuantidadeMaximaCaracter(selecionado, object, retorno);
        }
        return retorno;
    }

    public static boolean isMenorOrIgualAZero(BigDecimal value) {
        return BigDecimal.ZERO.compareTo(value) >= 0;
    }

    public static Boolean validarQuantidadeMaximaCaracter(Object selecionado) {
        EntidadeMetaData metadata = new EntidadeMetaData(selecionado.getClass());
        Boolean retorno = true;
        for (Iterator<AtributoMetadata> it = metadata.getAtributos().iterator(); it.hasNext(); ) {
            AtributoMetadata object = it.next();
            retorno = validarQuantidadeMaximaCaracter(selecionado, object, retorno);
        }
        return retorno;
    }


    public static void validarQuantidadeCaracter(Object selecionado, ValidacaoException exception) {
        EntidadeMetaData metadata = new EntidadeMetaData(selecionado.getClass());
        for (Iterator<AtributoMetadata> it = metadata.getAtributos().iterator(); it.hasNext(); ) {
            AtributoMetadata object = it.next();
            validarQuantidadeCaracter(selecionado, object, exception);
        }

        if (exception.temMensagens())
            throw exception;
    }

    private static void validarQuantidadeCaracter(Object selecionado, AtributoMetadata object, ValidacaoException ve) {
        if (object.getAtributo().isAnnotationPresent(br.com.webpublico.util.anotacoes.Length.class)) {
            int max = (object.getAtributo().getAnnotation(br.com.webpublico.util.anotacoes.Length.class)).maximo();
            int min = (object.getAtributo().getAnnotation(br.com.webpublico.util.anotacoes.Length.class)).minimo();

            if (object.getString(selecionado).length() > max) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo " + object.getEtiqueta() + " deve ter menos que " + max + " caracteres!");

            }
            if (object.getString(selecionado).length() < min) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo " + object.getEtiqueta() + " deve ter mais que " + min + " caracteres!");

            }
        }
    }

    private static void validarQuantidadeMaximaCaracter(Object selecionado, AtributoMetadata object, ValidacaoException ve) {
        if (object.getAtributo().isAnnotationPresent(br.com.webpublico.util.anotacoes.Length.class)) {
            int max = (object.getAtributo().getAnnotation(br.com.webpublico.util.anotacoes.Length.class)).maximo();
            int min = (object.getAtributo().getAnnotation(br.com.webpublico.util.anotacoes.Length.class)).minimo();
            if (object.getString(selecionado).length() > max) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo " + object.getEtiqueta() + " deve ter menos que " + max + " caracteres!");
            }
            if (object.getString(selecionado).length() < min) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo " + object.getEtiqueta() + " deve ter mais que " + min + " caracteres!");
            }
        }
    }

    @Deprecated
    private static Boolean validarQuantidadeMaximaCaracter(Object selecionado, AtributoMetadata object, Boolean retorno) {
        if (object.getAtributo().isAnnotationPresent(br.com.webpublico.util.anotacoes.Length.class)) {
            int max = (object.getAtributo().getAnnotation(br.com.webpublico.util.anotacoes.Length.class)).maximo();
            int min = (object.getAtributo().getAnnotation(br.com.webpublico.util.anotacoes.Length.class)).minimo();
            System.out.println("selecionado).length() " + object.getString(selecionado).length());
            if (object.getString(selecionado).length() > max) {
                FacesUtil.addCampoObrigatorio("O campo " + object.getEtiqueta() + " deve ter menos que " + max + " caracteres!");
                retorno = false;
            }
            if (object.getString(selecionado).length() < min) {
                FacesUtil.addCampoObrigatorio("O campo " + object.getEtiqueta() + " deve ter mais que " + min + " caracteres!");
                retorno = false;
            }
        }
        return retorno;
    }

    public static void validarCamposObrigatorios(Object selecionado, ValidacaoException ex) {
        EntidadeMetaData metadata = new EntidadeMetaData(selecionado.getClass());
        for (Iterator<AtributoMetadata> it = metadata.getAtributos().iterator(); it.hasNext(); ) {
            AtributoMetadata object = it.next();
            if (object.getAtributo().isAnnotationPresent(br.com.webpublico.util.anotacoes.Obrigatorio.class)) {
                if (object.getString(selecionado).trim().equals("") || ((AtributoMetadata) object).getString(selecionado).trim() == null) {
                    ex.adicionarMensagemDeCampoObrigatorio("O campo " + object.getEtiqueta() + " deve ser informado!");
                } else if (object.getAtributo().isAnnotationPresent(br.com.webpublico.util.anotacoes.Positivo.class)) {
                    Positivo positivo = object.getAtributo().getAnnotation(Positivo.class);
                    BigDecimal value = new BigDecimal(object.getString(selecionado));
                    if (!positivo.permiteZero() && isMenorOrIgualAZero(value)) {
                        ex.adicionarMensagemDeCampoObrigatorio("O campo " + object.getEtiqueta() + " deve ser maior que zero(0).");
                    } else if (positivo.permiteZero() && BigDecimal.ZERO.compareTo(value) > 0) {
                        ex.adicionarMensagemDeCampoObrigatorio("O campo " + object.getEtiqueta() + " deve ser maior ou igual a zero(0).");
                    }
                }
            }
            if (object.getAtributo().isAnnotationPresent(br.com.webpublico.util.anotacoes.Length.class)) {
                int max = (object.getAtributo().getAnnotation(br.com.webpublico.util.anotacoes.Length.class)).maximo();
                int min = (object.getAtributo().getAnnotation(br.com.webpublico.util.anotacoes.Length.class)).minimo();
                if (object.getString(selecionado).length() > max) {
                    ex.adicionarMensagemDeCampoObrigatorio("O campo " + object.getEtiqueta() + " deve ter menos que " + max + " caracteres!");
                }
                if (object.getString(selecionado).length() < min) {
                    ex.adicionarMensagemDeCampoObrigatorio("O campo " + object.getEtiqueta() + " deve ter mais que " + min + " caracteres!");
                }
            }
        }
        if (ex.temMensagens()) {
            throw ex;
        }
    }

    public static String zerosAEsquerda(Integer numero, Integer qtdDigitos) {
        String completo = numero.toString();
        while (completo.length() < qtdDigitos) {
            completo = "0" + completo;
        }
        return completo;
    }

    public static String zerosAEsquerda(String numero, Integer qtdDigitos) {
        String completo = numero;
        while (completo.length() < qtdDigitos) {
            completo = "0" + completo;
        }
        return completo;
    }

    public static String cortarString(String string, int limite) {
        if (string.length() > limite) {
            return string.substring(0, limite);
        }
        return string;
    }

    public static String removeMascaras(String texto) {
        if (texto != null) {
            return texto.replaceAll("[^a-zZ-z0-9]", "");
        }
        return "";
    }

    public static String substituiCaracterEspecial(String texto, String novoValor) {
        return texto.replaceAll("[^aA-zZ-z0-9]", novoValor);
    }

    public static Exception getRootCauseEJBException(Exception ex) {

        if (!(ex instanceof EJBException)) {
            //logger.debug("################################");
            //logger.debug("################################");
            //logger.debug("Exceção não é do tipo EJBException");
            //logger.debug("################################");
            //logger.debug("################################");
            return ex;
        }

        //logger.debug("################################");
        //logger.debug("################################");
        //logger.debug("Exceção do tipo EJBException");
        //logger.debug("################################");
        //logger.debug("################################");
        EJBException exceptionInicial = (EJBException) ex;
        Exception exceptionReal = exceptionInicial.getCausedByException();

        while (null != exceptionReal && exceptionInicial instanceof EJBException) {
            exceptionInicial = (EJBException) exceptionReal;
            //logger.debug("########## DEBUG" + exceptionInicial.getMessage());
            //logger.debug("########## DEBUG" + exceptionInicial.getCause());
            exceptionReal = exceptionInicial.getCausedByException();
        }

        return exceptionReal;
    }

    public static String formatador(String formato, String valor) {
        return new Formatter().format(formato, valor).toString();
    }

    public static void getRootCauseDataBaseException(Exception ex) throws Exception {
        throw (Exception) BuscaCausa.desenrolarException(ex);
    }

    public static List<SelectItem> getListSelectItem(String className, List<? extends Object> values) {
        if (className.contains("class ")) {
            className = className.replace("class ", "");
        }

        Class<?> classe = null;
        try {
            classe = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));

        if (classe.isEnum()) {
            for (Field field : classe.getDeclaredFields()) {
                toReturn.add(new SelectItem(field.getName(), getDescricaoDoEnum(classe, field)));
            }
        } else {
            for (Object obj : values) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
        }

        return toReturn;
    }

    public static List<String> getListOfEnumName(Enum... tipos) {
        return getListOfEnumName(Arrays.asList(tipos));
    }

    public static List<String> getListOfEnumName(List<? extends Enum> tipos) {
        List<String> tiposList = Lists.newArrayList();
        for (Enum tipo : tipos) {
            tiposList.add(tipo.name());
        }
        return tiposList;
    }

    public static List<String> getListOfEnumDescricao(List<? extends EnumComDescricao> valores) {
        List<String> retorno = Lists.newArrayList();
        for (EnumComDescricao valor : valores) {
            retorno.add(valor.getDescricao());
        }
        return retorno;
    }

    public static String getDescricaoDoEnum(Class classe, Field field) {
        try {
            Enum<?> valorDoFieldEnum = Enum.valueOf((Class<? extends Enum>) classe, field.getName());
            String nomeCampo = "descricao";
            String methodName = "get" + nomeCampo.substring(0, 1).toUpperCase() + nomeCampo.substring(1);
            Object valorDescricaoEnum = valorDoFieldEnum.getClass().getMethod(methodName, new Class[]{}).invoke(valorDoFieldEnum, new Object[]{});
            return valorDescricaoEnum.toString();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return field.getName();
        }
    }

    private static List<SelectItem> recuperarValuesEnum(String nomeDaClasse) {
        if (nomeDaClasse.contains("class ")) {
            nomeDaClasse = nomeDaClasse.replace("class ", "");
        }
        Class<?> classe = null;
        try {
            classe = Class.forName(nomeDaClasse);
        } catch (ClassNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro !", "Classe não encontrada : " + nomeDaClasse));
        }
        if (classe == null) {
            return null;
        }
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (Field field : classe.getDeclaredFields()) {
            if (field.isEnumConstant()) {
                String descricao = "";
                try {
                    Enum<?> valor = Enum.valueOf((Class<? extends Enum>) classe, field.getName());
                    String nomeCampo = "descricao";
                    String methodName = "get" + nomeCampo.substring(0, 1).toUpperCase() + nomeCampo.substring(1);
                    Object valorRecuperado = valor.getClass().getMethod(methodName, new Class[]{}).invoke(valor, new Object[]{});
                    descricao = valorRecuperado.toString();
                } catch (Exception e) {
                    descricao = field.getName();
                }
                retorno.add(new SelectItem(field.getName(), descricao));
            }
        }
        return retorno;
    }

    public static List<SelectItem> getListSelectItem(Object[] values) {
        return getListSelectItem(Lists.newArrayList(values));
    }


    public static List<SelectItem> getListSelectItem(List<? extends Object> values) {
        return getListSelectItem(values, true);
    }

    public static List<SelectItem> getListSelectItem(Object[] values, boolean ordenar) {
        return getListSelectItem(Lists.newArrayList(values), ordenar);
    }

    public static List<SelectItem> getListSelectItem(List<? extends Object> values, boolean ordenar) {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));

        for (Object obj : values) {
            String descricao;
            if (obj instanceof EnumComDescricao) {
                descricao = ((EnumComDescricao) obj).getDescricao();
            } else {
                descricao = obj.toString();
            }
            toReturn.add(new SelectItem(obj, descricao));
        }

        return ordenar ? ordenaSelectItem(toReturn) : toReturn;
    }

    public static List<SelectItem> getListSelectItemSemCampoVazio(Object[] values) {
        return getListSelectItemSemCampoVazio(values, true);
    }

    public static List<SelectItem> getListSelectItemSemCampoVazio(Object[] values, Boolean ordenar) {
        List<SelectItem> toReturn = new ArrayList<>();

        for (Object obj : Lists.newArrayList(values)) {
            String descricao;
            if (obj instanceof EnumComDescricao) {
                descricao = ((EnumComDescricao) obj).getDescricao();
            } else {
                descricao = obj.toString();
            }
            toReturn.add(new SelectItem(obj, descricao));
        }

        return ordenar ? ordenaSelectItem(toReturn) : toReturn;
    }

    public static String converterBooleanSimOuNao(Boolean valor) {
        return valor != null && valor ? "Sim" : "Não";
    }

    public static Integer converterZeroOuUm(Boolean valor) {
        return valor ? 1 : 0;
    }

    public static String link(String complementoLink, String conteudoLink) {
        complementoLink = complementoLink == null ? "" : complementoLink;
        return "<a href='" + FacesUtil.getRequestContextPath() + complementoLink + "'>" + conteudoLink + "</a>";
    }

    public static String linkBlack(String complementoLink, String conteudoLink) {
        complementoLink = complementoLink == null ? "" : complementoLink;
        return "<a target='_blank' href='" + FacesUtil.getRequestContextPath() + complementoLink + "'>" + conteudoLink + "</a>";
    }

    public static Date getUltimoDiaDoAno(Integer ano) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 31);
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.YEAR, ano);
        return c.getTime();
    }

    public static Boolean isMesValido(String mes) {
        Pattern patter = Pattern.compile("^[1][0-2]|[0][1-9]$");
        Matcher matcher = patter.matcher(mes);
        return matcher.matches();
    }

    public static String formatarValorComMascara(String valorInformado, String mascaraInformada, String caracteresControle) {
        // se valor nulo ou menor que 1
        if (valorInformado == null || valorInformado.length() < 1) {
            // retorna vazio
            return "";
        }

        int nCount = 0;
        String valorFormatado = "";

        // realiza interação em todas as posições da mascara
        for (int i = 0; i <= mascaraInformada.length(); i++) {
            try {
                char caracter = ' ';
                // captura caracter da mascara no indice informado
                caracter = mascaraInformada.charAt(i);

                // verifica se caracter capturado é igual aos caracteres de controle
                boolean bolMask = caracteresControle.contains(caracter + "");

                // se for igual a caracter de controle
                if (bolMask) {
                    // adiciona caracter de controle
                    valorFormatado += caracter + "";
                } else {
                    // senão irá adicionar o valor capturado a partir do indice informado
                    valorFormatado += valorInformado.charAt(nCount);
                    nCount++;
                }
            } catch (StringIndexOutOfBoundsException e) {
                // quando a formatação for concluída lança exception
                return valorFormatado;
            }
        }
        return valorFormatado;
    }

    public static void imprimeSQL(String sql, Query q) {
        String comandoSQL = sql;
        Set<Parameter<?>> param = q.getParameters();
        for (Parameter p : param) {
            String chave = ":" + p.getName();
            String valor = q.getParameterValue(p).toString();
            //logger.debug("chave: " + chave + "   valor: " + valor);
            comandoSQL = comandoSQL.replace(chave, valor);
        }
        logger.info("SQL gerado {}", comandoSQL);
    }


    public static List<SelectItem> ordenaSelectItem(List<SelectItem> itens) {
        Collections.sort(itens, new OrdenaSelecItem());
        return itens;
    }

    public static void ordenarListas(List... listas) {
        for (List lista : listas) {
            Collections.sort(lista);
        }
    }

    public static Long transformaHoraEmSegundos(String horario) {
        if (horario == null || horario.isEmpty()) {
            return 0l;
        }
        String hora = horario.substring(0, horario.indexOf(":"));
        String minuto = horario.substring(horario.indexOf(":") + 1, horario.length());
        Long horaConvertida = Long.parseLong(hora) * 60 * 60;
        Long minutoConvertido = Long.parseLong(minuto) * 60;
        if (horaConvertida < 0) {
            minutoConvertido = minutoConvertido * -1;
        }
        Long segundos = horaConvertida + minutoConvertido;
        return segundos;
    }

    public static String transformaSegundosEmHora(Long segundos) {
        if (segundos == null || segundos.equals(0l)) {
            return "00:00";
        }
        Boolean negativa = segundos < 0;
        segundos = negativa ? segundos * -1 : segundos;
        String horas = "00";
        String minutos = "00";
        Long horaLong = 0l;
        Long minutosLong = 0l;
        if (segundos >= 60 && segundos < 3600) {
            minutosLong = segundos / 60;
            minutos = (minutosLong < 10 ? "0" : "") + minutosLong.toString();
        } else if (segundos >= 3600) {
            horaLong = segundos / 3600;
            horas = (horaLong < 10 ? "0" : "") + horaLong.toString();
            Long resto = segundos % 3600;
            minutosLong = resto / 60;
            minutos = (minutosLong < 10 ? "0" : "") + minutosLong.toString();
        }
        return (negativa ? "-" : "") + horas + ":" + minutos;
    }

    public static boolean validaHoraPositiva(String horas) {
        String regex = "^(([0-9]){1,3}):([012345][0-9])$";
        Pattern padrao = Pattern.compile(regex);
        Matcher pesquisa = padrao.matcher(horas);
        return pesquisa.matches();
    }

    public static boolean validaHoraPositivaNegativa(String horas) {
        String regex = "^(([0-9]|[-][0-9]){1,3}):([012345][0-9])$";
        Pattern padrao = Pattern.compile(regex);
        Matcher pesquisa = padrao.matcher(horas);
        return pesquisa.matches();
    }

    public static org.w3c.dom.Document inicializarDOM(String xml) throws IOException, ParserConfigurationException, SAXException {
        org.w3c.dom.Document doc;
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory
            .newInstance();
        DocumentBuilder documentBuilder = documentFactory
            .newDocumentBuilder();
        doc = documentBuilder.parse(IOUtils.toInputStream(xml, "UTF-8"));
        return doc;
    }

    public static int getNumeroCasasDecimais(BigDecimal bigDecimal) {
        String string = bigDecimal.stripTrailingZeros().toPlainString();
        int index = string.indexOf(".");
        return index < 0 ? 0 : string.length() - index - 1;
    }

    public static String getApplicationPath(String path) {
        return ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath(path);
    }

    public static boolean listasIguais(List<?> l1, List<?> l2) {
        // make a copy of the list so the original list is not changed, and remove() is supported
        ArrayList<?> cp = new ArrayList<>(l1);
        for (Object o : l2) {
            if (!cp.remove(o)) {
                return false;
            }
        }
        return cp.isEmpty();
    }

    public static void validarDataMinima(Date data, String nomeDoCampo) {
        if (data == null) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(1980, 1, 1);
        if (data.before(calendar.getTime())) {
            ValidacaoException ve = new ValidacaoException();
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A " + nomeDoCampo + " não pode ser menor que 01/01/1980");
            throw ve;
        }
    }

    public static boolean validarCpf(String s_aux) {
        if (s_aux.isEmpty()) {
            return true;
        } else {
            s_aux = s_aux.replace(".", "");
            s_aux = s_aux.replace("-", "");
            s_aux = s_aux.replace("_", "");
//------- Rotina para CPF
            if (s_aux.length() == 11) {
                int d1, d2;
                int digito1, digito2, resto;
                int digitoCPF;
                String nDigResult;
                d1 = d2 = 0;
                digito1 = digito2 = resto = 0;
                logger.debug("s_aux: " + s_aux);
                for (int n_Count = 1; n_Count < s_aux.length() - 1; n_Count++) {
                    digitoCPF = Integer.valueOf(s_aux.substring(n_Count - 1, n_Count)).intValue();
//--------- Multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.
                    d1 = d1 + (11 - n_Count) * digitoCPF;
//--------- Para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.
                    d2 = d2 + (12 - n_Count) * digitoCPF;
                }
                ;
//--------- Primeiro resto da divisão por 11.
                resto = (d1 % 11);
//--------- Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
                if (resto < 2) {
                    digito1 = 0;
                } else {
                    digito1 = 11 - resto;
                }
                d2 += 2 * digito1;
//--------- Segundo resto da divisão por 11.
                resto = (d2 % 11);
//--------- Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
                if (resto < 2) {
                    digito2 = 0;
                } else {
                    digito2 = 11 - resto;
                }
//--------- Digito verificador do CPF que está sendo validado.
                String nDigVerific = s_aux.substring(s_aux.length() - 2, s_aux.length());
//--------- Concatenando o primeiro resto com o segundo.
                nDigResult = String.valueOf(digito1) + String.valueOf(digito2);
//--------- Comparar o digito verificador do cpf com o primeiro resto + o segundo resto.
                return nDigVerific.equals(nDigResult);
            } //-------- Rotina para CNPJ
            else {
                return false;
            }
        }
    }

    public static boolean isValorNegativo(BigDecimal soma) {
        if (soma == null) {
            return false;
        }
        return soma.compareTo(BigDecimal.ZERO) <= -1;
    }

    public static void validarCamposPositivos(Object selecionado, ValidacaoException ex) throws ValidacaoException {
        EntidadeMetaData metadata = new EntidadeMetaData(selecionado.getClass());

        for (Iterator<AtributoMetadata> it = metadata.getAtributos().iterator(); it.hasNext(); ) {
            AtributoMetadata object = it.next();
            if (object.getAtributo().isAnnotationPresent(br.com.webpublico.util.anotacoes.Positivo.class)) {
                Positivo positivo = object.getAtributo().getAnnotation(Positivo.class);
                BigDecimal value = new BigDecimal(object.getString(selecionado));
                if (!positivo.permiteZero() && BigDecimal.ZERO.compareTo(value) >= 0) {
                    ex.adicionarMensagemDeCampoObrigatorio("O campo " + object.getEtiqueta() + " não pode ser menor ou igual a zero(0).");
                } else if (positivo.permiteZero() && BigDecimal.ZERO.compareTo(value) > 0) {
                    ex.adicionarMensagemDeCampoObrigatorio("O campo " + object.getEtiqueta() + " não pode ser menor que zero(0).");
                }
            }
        }
        if (ex.temMensagens()) {
            throw ex;
        }
    }

    public static Object[] adicionarObjetoNoArray(Object[] array, Object newObject) {
        array = Arrays.copyOf(array, array.length + 1);
        array[array.length - 1] = newObject;
        return array;
    }

    public static <E extends Enum<E>> E traduzirEnum(Class<E> classeEnum, String valorString) {
        if (stringVazia(valorString)) {
            return null;
        }
        try {
            for (E valorEnum : EnumSet.allOf(classeEnum)) {
                if (valorEnum.name().equalsIgnoreCase(valorString)) {
                    return valorEnum;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static boolean isNull(Object atributo) {
        return atributo == null;
    }

    public static boolean isNotNull(Object atributo) {
        return !isNull(atributo);
    }

    public static String getIdsDaListaAsString(List lista) {
        String ids = "";
        for (Object obj : lista) {
            ids += ((Long) Persistencia.getId(obj)) + ",";
        }
        ids = (ids.substring(0, ids.length() - 1));
        return ids;
    }

    public static boolean isConsecutive(List<Integer> itens, int min) {
        Integer[] numbers = itens.toArray(new Integer[itens.size()]);
        Arrays.sort(numbers);
        if (numbers.length == min) {
            min--;
        }
        int count = 0;
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] != numbers[i - 1] + 1) {
                count = 0;
            }
            count++;
            if (count >= min) {
                return true;
            }
        }
        return count >= min;
    }

    public static <T> T readValue(String json, Class clazz) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try {
            SimpleModule module = new SimpleModule();
            module.addAbstractTypeMapping(OcorrenciaESocial.class, OcorrenciaESocialDTO.class);
            mapper.registerModule(module);
            T object = (T) mapper.readValue(json, clazz);
            return object;
        } catch (IOException e) {
            throw e;
        }
    }

    public static String getBase64Encode(byte[] src) {
        String requestValue = "";
        org.apache.commons.codec.binary.Base64 base64 = new Base64();
        requestValue = base64.encodeAsString(src);
        return requestValue;
    }

    public static String montarClausulaWhere(List<ParametrosFiltroNfseDTO> parametros, String juncao) {
        String clausula = "";
        if (parametros != null && !parametros.isEmpty()) {
            Integer index = 0;
            for (ParametrosFiltroNfseDTO filtro : parametros) {
                clausula += juncao;
                clausula += filtro.getCampo();
                clausula += " ";
                clausula += filtro.getOperacao().getDescricao();
                clausula += " ";
                clausula += " :param";
                clausula += index;
                filtro.setParametro("param" + index);
                index += 1;
            }
        }
        return clausula;
    }

    public static void adicionarParametros(Query q, List<ParametrosFiltroNfseDTO> filtros) {
        if (filtros != null && !filtros.isEmpty()) {
            for (ParametrosFiltroNfseDTO filtro : filtros) {
                q.setParameter(filtro.getParametro(), filtro.getValor() instanceof Boolean ? filtro.getValorLogico() ? '1' : '0' : filtro.getValor());
            }
        }
    }

    public static String concatenarParametros(List<ParametrosRelatorios> parametros, Integer local, String clausula) {
        StringBuilder sql = new StringBuilder();
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == local) {
                sql.append(clausula + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        return sql.toString();
    }

    public static InputStream getImagemInputStream(Arquivo arq) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (ArquivoParte a : arq.getPartes()) {
                buffer.write(a.getDados());
            }
            return new ByteArrayInputStream(buffer.toByteArray());
        } catch (Exception ex) {
            ex.getMessage();
        }
        Object object = new Object();
        return null;
    }

    public static String getStringVaziaSeNull(String value) {
        return Strings.isNullOrEmpty(value) ? "" : value;
    }

    public static String disfarcarLogin(String login) {
        if (login.length() == 11) {
            return login.substring(0, 2) + "******" + login.substring(9);
        } else {
            return login;
        }
    }

    public static class OrdenaSelecItem implements Comparator<SelectItem> {

        @Override
        public int compare(SelectItem o1, SelectItem o2) {
            try {
                return o1.getLabel().compareTo(o2.getLabel());  //To change body of implemented methods use File | Settings | File Templates.
            } catch (Exception e) {
                return Integer.MAX_VALUE;
            }
        }
    }

    public static String getNomeTabela(Class classe) {
        return classe.isAnnotationPresent(Table.class)
            && !Strings.isNullOrEmpty(((Table) classe.getAnnotation(Table.class)).name())
            ? ((Table) classe.getAnnotation(Table.class)).name() : classe.getSimpleName();
    }

    public static String getValorRemovendoRS(BigDecimal valor) {
        String valorFormatado = Util.formataValor(valor);
        valorFormatado = valorFormatado.replace("R$ ", "");
        return valorFormatado;
    }

    public static String buscarNomeDaClasse(Class movimento) {
        String nomeClasse = movimento.getSimpleName();
        if (movimento.isAnnotationPresent(Etiqueta.class)) {
            Etiqueta e = (Etiqueta) movimento.getAnnotation(Etiqueta.class);
            nomeClasse = e.value();
        }
        return nomeClasse;
    }

    public static Object getEJBViaLookupJavaModule(String nome) throws ExcecaoNegocioGenerica {
        return getFacadeViaLookup(JAVA_MODULE_PATH + nome);
    }

    public static Object getFacadeViaLookup(String nome) throws ExcecaoNegocioGenerica {
        try {
            return new InitialContext().lookup(nome);
        } catch (NamingException e) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar " + nome + " via lookup {}", e);
        }
    }

    public static String getBase64ToString(String arquivo64) {
        String data = arquivo64.split("base64,")[1];
        Base64 decoder = new Base64();
        byte[] decode = decoder.decode(data);
        return new String(decode);
    }

    public static InputStream getBase64ToInputStream(String arquivo64) throws IOException {
        return IOUtils.toInputStream(arquivo64, "UTF-8");
    }

    public static boolean isMaiorQueZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    public static String converterImgUrlParaBase64(String url) {
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();
            InputStream is = ucon.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }
            baos.flush();
            Base64 base64 = new Base64();
            return base64.encodeAsString(baos.toByteArray());
        } catch (Exception e) {
            logger.error("Error", e.toString());
        }
        return "";
    }

    public static String getValorSemPontoEVirgulas(BigDecimal valor) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        String r = nf.format(valor);
        r = StringUtil.retornaApenasNumeros(r);
        return r;
    }

    public static String formataQuandoDecimal(BigDecimal valor, TipoMascaraUnidadeMedida mascara) {
        try {
            DecimalFormat formatoDecimal = new DecimalFormat(mascara.getMascara());
            DecimalFormat formatoNormal = new DecimalFormat("#,###");
            if (valor != null) {
                if (valor.compareTo(BigDecimal.ZERO) > 0) {
                    return valor.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) > 0 ? formatoDecimal.format(valor) : formatoNormal.format(valor);
                } else if (valor.compareTo(BigDecimal.ZERO) == 0) {
                    return formatoDecimal.format(valor);
                }
            }
            return new DecimalFormat(MASCARA_DECIMAL_DOIS_DIGITOS).format(valor);
        } catch (NullPointerException e) {
            return new DecimalFormat(MASCARA_DECIMAL_DOIS_DIGITOS).format(valor);
        }
    }

    public static void intejarSistemaService(InjetaSistemaService injetaSistemaService) {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        SistemaService sistemaService = (SistemaService) ap.getBean("sistemaService");
        SistemaService sistemaService1 = new SistemaService();
        sistemaService1.setUsuarioAlternativo(sistemaService.getUsuarioAlternativo());
        ;
        sistemaService1.setIpAlternativo(((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRemoteAddr());
        injetaSistemaService.setSistemaService(sistemaService1);
    }

    public static EntityManager criarEntityManagerOpenViewReadOnly() {
        synchronized (criarEntityManagerOpenViewReadOnly) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            if (entitysManagerOpenViewReadOnly.containsKey(request)) {
                return entitysManagerOpenViewReadOnly.get(request);
            } else {
                EntityManagerFactory entityManagerFactory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext()).getBean(EntityManagerFactory.class);
                EntityManager em = entityManagerFactory.createEntityManager();
                Session session = (Session) em.getDelegate();
                session.setDefaultReadOnly(true);
                session.getTransaction().begin();
                entitysManagerOpenViewReadOnly.put(request, em);
                return em;
            }
        }
    }

    public static void fecharEntitysOpenViewReadOnlyCriadasParaRequisicao(HttpServletRequest request) {
        synchronized (criarEntityManagerOpenViewReadOnly) {
            HttpServletRequest request1 = getRequestFacade(request);
            if (entitysManagerOpenViewReadOnly.containsKey(request1)) {
                entitysManagerOpenViewReadOnly.get(request1).close();
                entitysManagerOpenViewReadOnly.remove(request1);
            }
        }
    }

    private static HttpServletRequest getRequestFacade(HttpServletRequest request) {
        ServletRequest wrapper = request;
        while (!wrapper.getClass().equals(RequestFacade.class)) {
            if (wrapper instanceof ServletRequestWrapper) {
                wrapper = ((ServletRequestWrapper) wrapper).getRequest();
            } else {
                break;
            }
        }
        return (HttpServletRequest) wrapper;
    }

    public static boolean checkInstaceof(Object ob, Class classe) {
        if (ob instanceof HibernateProxy) {
            return ((HibernateProxy) ob).getHibernateLazyInitializer().getImplementation().getClass().isAssignableFrom(classe);
        } else {
            return ob.getClass().isAssignableFrom(classe);
        }
    }

    public static <T> T recuperarObjetoHibernateProxy(Object ob) {
        if (ob instanceof HibernateProxy) {
            return (T) ((HibernateProxy) ob).getHibernateLazyInitializer().getImplementation();
        } else {
            return (T) ob;
        }
    }

    public static String gerarSenhaUsuarioPortalWeb() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while (sb.length() < 8) {
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.substring(0, 8);
    }

    public static String quebraLinha() {
        return "\r\n";
    }

    public static InputStream generateQRCodeImage(String text, int width, int height) {
        try {
            return BarCode.generateInputStreamQRCodePng(text, width, height);
        } catch (Exception e) {
            logger.error("Erro: ", e);
            return null;
        }
    }

    public static <T> T converterDeJsonParaObjeto(String jsonCadastroEconomicoDTO, Class<T> classeObjeto) {
        if (Strings.isNullOrEmpty(jsonCadastroEconomicoDTO)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonCadastroEconomicoDTO, classeObjeto);
        } catch (IOException e) {
            logger.error("Converter Json para Objeto", e);
        }
        return null;
    }

    public static String converterObjetoParaJson(Object objeto) {
        if (objeto == null) {
            return "";
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(objeto);
        } catch (IOException e) {
            logger.error("Converter Objeto para Json", e);
        }
        return "";
    }

    public static List<Long> montarParametroInPeloId(List objetos) {
        try {
            List<Long> retorno = Lists.newArrayList();
            for (Object obj : objetos) {
                retorno.add((Long) Persistencia.getId(obj));
            }
            return retorno;
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    public static String montarClausulaIn(List registros) {
        if (registros == null || registros.isEmpty()) return "";
        StringBuilder in = new StringBuilder();
        String juncao = "(";
        for (Object obj : registros) {
            in.append(juncao);
            in.append("'");
            in.append(obj);
            in.append("'");
            juncao = ", ";
        }
        in.append(") ");
        return in.toString();
    }

    public static <K> List<List<K>> partitionList(List<K> lista, int sublistSize) {
        if (lista.isEmpty()) {
            List<List<K>> retorno = new ArrayList<>();
            retorno.add(new ArrayList<K>());
            return retorno;
        } else {
            return Lists.partition(lista, sublistSize);
        }
    }

    public static UsuarioSistema recuperarUsuarioCorrente() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UsuarioSistema ? (UsuarioSistema)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String obterIpUsuario() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            WebAuthenticationDetails webAuthDetails = (WebAuthenticationDetails) auth.getDetails();
            return webAuthDetails.getRemoteAddress();
        } catch (Exception e) {
            return "Local";
        }
    }

    public static <T> List<Long> montarIdsList(List<T> lista) {
        try {
            List<Long> ids = Lists.newArrayList();
            if (lista != null) {
                for (T objeto : lista) {
                    Object id = Persistencia.getId(objeto);
                    if (id == null) {
                        id = recuperarIdObjeto(objeto);
                    }
                    if (id != null) {
                        ids.add((Long) id);
                    }
                }
            }
            return ids;
        } catch (Exception e) {
            logger.error("Erro ao montarIdsList. ", e);
            return Lists.newArrayList();
        }
    }

    public static Object recuperarIdObjeto(Object obj) {
        try {
            Field id = obj.getClass().getDeclaredField("id");
            id.setAccessible(true);
            return id.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object buscarEntidade(Class classe, Long id, EntityManager em) {
        try {
            return em.find(classe, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar referência do objeto: ", e);
            return null;
        }
    }

    public static Object buscarValorPorClasseAndSuperClasse(Class<?> classe, Object obj, String nomeCampo) {
        do {
            try {
                Field field = classe.getDeclaredField(nomeCampo);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception e) {
                logger.error("Erro ao buscar valor do campo.");
            }
        } while ((classe = classe.getSuperclass()) != null);
        return null;
    }

    public static UploadArquivo criarArquivoUpload(String nomeArquivo, String mimeType, byte[] bytes) {
        try {
            return new UploadArquivo(nomeArquivo, mimeType, bytes);
        } catch (Exception e) {
            logger.error("Erro ao criar arquivo de upload. ", e);
        }
        return null;
    }

    public static RestTemplate getRestTemplate() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory;
        clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        int timeout = 5000; //Não alterar o padrão de timeout, se preciso implemente individualmente o RESTTemplate e não use do útil
        clientHttpRequestFactory.setConnectTimeout(timeout);
        try {
            TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            };

            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

            CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

            clientHttpRequestFactory.setHttpClient(httpClient);
        } catch (Exception e) {
            logger.error("Erro ao criar rest template", e);
        }
        return new RestTemplate(clientHttpRequestFactory);
    }

    public static <T extends Number> List<Long> converterToLongList(List<T> numberList) {
        try {
            return Lists.transform(numberList, new Function<T, Long>() {
                @Override
                public Long apply(T number) {
                    return number.longValue();
                }
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static Map<String, Object> extrairPropriedadesDeObjetoParaMapa(Object object) {
        Map<String, Object> map = Maps.newHashMap();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object obj = field.get(object);
                if (obj != null && (!(obj instanceof String) || !((String) obj).isEmpty())) {
                    map.put(field.getName(), obj);
                }
            } catch (IllegalAccessException e) {
                logger.error("Erro ao extrair propriedade do objeto", e.toString());
            }
        }
        return map;
    }

    public static <K> boolean validaInicialFinal(Comparable<K> inicial, Comparable<K> finall) {
        if ((inicial != null && finall == null) || (finall != null && inicial == null)) {
            return false;
        }
        if (inicial != null && finall != null && finall.compareTo((K) inicial) < 0) {
            return false;
        }
        return true;
    }

    public static List<Field> getFieldsUpTo(Class<?> startClass,
                                            Class<?> exclusiveParent) {

        List<Field> currentClassFields = Lists.newArrayList(startClass.getDeclaredFields());
        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null &&
            (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
            List<Field> parentClassFields =
                (List<Field>) getFieldsUpTo(parentClass, exclusiveParent);
            currentClassFields.addAll(parentClassFields);
        }

        return currentClassFields;
    }

    public static <T> T recuperarSpringBean(Class<T> clazz) {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        return ap.getBean(clazz);
    }

    public static byte[] gerarHash(String frase, String algoritmo) {
        try {
            MessageDigest md = MessageDigest.getInstance(algoritmo);
            md.update(frase.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String stringHexa(byte[] bytes) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
            int parteBaixa = bytes[i] & 0xf;
            if (parteAlta == 0) {
                s.append('0');
            }
            s.append(Integer.toHexString(parteAlta | parteBaixa));
        }
        return s.toString();
    }

    public static String typeToXML(Object type) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(type.getClass());

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(type, stringWriter);
        return stringWriter.toString();
    }

    public static TransactionTemplate recuperarSpringTransaction(Propagation propagation, int secondsTimeout) {
        PlatformTransactionManager transactionManager = Util.recuperarSpringBean(PlatformTransactionManager.class);
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(propagation.value());
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        transactionTemplate.setTimeout(secondsTimeout);
        return transactionTemplate;
    }

    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }

    public static <T> T carregarEventoEsocial(String json, Class clazz) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try {
            SimpleModule module = new SimpleModule();
            module.addAbstractTypeMapping(OcorrenciaESocial.class, OcorrenciaESocialDTO.class);
            mapper.registerModule(module);
            T object = (T) mapper.readValue(json, clazz);
            return object;
        } catch (IOException e) {
            throw e;
        }
    }

    public static boolean isListNullOrEmpty(List<?> lista) {
        return lista == null || lista.isEmpty();
    }

    public static void loggingError(Class classe, String msg, Exception e) {
        logger.error("{} :: {}", classe, msg);
        logger.debug("{} ", msg, e);
    }

    public static boolean isStringIgual(String value1, String value2) {
        return (value1 == null && value2 == null) ||
            (value1 != null && value2 != null && value1.trim().equalsIgnoreCase(value2.trim()));
    }

    public static boolean isEntidadesIguais(SuperEntidade obj1, SuperEntidade obj2) {
        return (obj1 == null && obj2 == null) ||
            (obj1 != null && obj1.equals(obj2));
    }

    public static void validarHoraMinuto(ValidacaoException ve, Integer hora, Integer minuto, String campo) {
        if ((hora == null || minuto == null) || (hora == 0 && minuto == 0)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo " + campo + " deve ser informado.");
        } else if ((hora < 0 || hora > 23) || (minuto < 0 || minuto > 59)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo " + campo + " é invalido.");
        }
    }

    public static <T> String objectToJsonString(T object, Class<T> clazz) throws JsonProcessingException {
        String json = OBJECT_MAPPER.writerFor(clazz).writeValueAsString(object);
        JSONObject jsonObject = new JSONObject(json);
        return jsonObject.toString(4);
    }

    public static <T> T objectFromJsonString(String jsonString, Class<T> clazz) throws IOException {
        return OBJECT_MAPPER.readerFor(clazz).readValue(jsonString);
    }

    public static String getTranslate(String campo) {
        return "translate(" + campo + ",'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')";
    }

    public static Integer getProximoNumero(List<?> list) {
        try {
            return list.size() + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    public static RestTemplate getRestTemplateWithConnectTimeout() {
        return getRestTemplateWithConnectTimeout(10000);
    }

    public static RestTemplate getRestTemplateWithConnectTimeout(long connectTimeout) {
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setConnectTimeout(10000);
        return new RestTemplate(httpComponentsClientHttpRequestFactory);
    }

    public static BigDecimal converterValor(String valor) {
        try {
            DecimalFormat formato = new DecimalFormat("#0,00");
            formato.setMaximumFractionDigits(2);
            DecimalFormatSymbols padrao = new DecimalFormatSymbols();
            padrao.setDecimalSeparator('.');
            formato.setDecimalFormatSymbols(padrao);
            BigDecimal retorno = BigDecimal.valueOf(formato.parse(valor.trim().replaceAll(" ", "")).doubleValue());
            return retorno.setScale(2, RoundingMode.FLOOR);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public static String getUrlPortalContribuinte() {
        String urlPortalContribuinte = System.getenv("URL_PORTAL_CONTRIBUINTE");
        if (Strings.isNullOrEmpty(urlPortalContribuinte)) {
            urlPortalContribuinte = "http://localhost:8081";
        }
        if (urlPortalContribuinte.endsWith("/")) {
            urlPortalContribuinte = StringUtils.chop(urlPortalContribuinte);
        }
        return urlPortalContribuinte;
    }

    public static boolean isExercicioLogadoDiferenteExercicioAtual(Date dataLogada) {
        try {
            Integer exercicioLogado = DataUtil.getAno(dataLogada);
            Integer exercicioAtual = DataUtil.getAno(new Date());
            return !exercicioLogado.equals(exercicioAtual);
        } catch (NullPointerException npe) {
            return Boolean.FALSE;
        }
    }
}
