/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.CategoriaPCS;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ProgressaoPCS;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
public class UtilRH {

    public static void ordenarProgressoes(List<ProgressaoPCS> progressoes) {
        Collections.sort(progressoes, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                ProgressaoPCS p1 = (ProgressaoPCS) o1;
                ProgressaoPCS p2 = (ProgressaoPCS) o2;

                return p1.getDescricao().compareTo(p2.getDescricao());
            }
        });
    }

    public static void ordenarCategorias(List<CategoriaPCS> categorias) {
        Collections.sort(categorias, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                CategoriaPCS p1 = (CategoriaPCS) o1;
                CategoriaPCS p2 = (CategoriaPCS) o2;

                return p1.getDescricao().compareTo(p2.getDescricao());
            }
        });
    }

     public static BigDecimal truncate(String s) {
        int l = s.length();
        StringBuffer sb = new StringBuffer();
        if (l != 0 && l > 3) {
            for (int i = 0; i < l; i++) {
                if (s.charAt(i) == '.' && (l - i - 1) > 2) {
                    sb.append(s.charAt(i));
                    sb.append(s.charAt(++i));
                    sb.append(s.charAt(++i));
                    break;
                } else {
                    sb.append(s.charAt(i));
                }
            }
            String s1 = sb.toString();
            double d1 = Double.parseDouble(s1);
            return BigDecimal.valueOf(d1);
        } else {
            return new BigDecimal(s);
        }
    }

    public static UnidadeOrganizacional getUnidadeOrganizacional() {
        SistemaControlador sc = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        return sc.getUnidadeOrganizacionalOrcamentoCorrente();
    }

    public static Exercicio getExercicio() {
        SistemaControlador sc = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        return sc.getExercicioCorrente();
    }

    public static Date getDataOperacao() {
        SistemaControlador sc = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        return sc.getDataOperacao();
    }

    public static String getDataOperacaoFormatada() {
        SistemaControlador sc = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        return new SimpleDateFormat("dd/MM/yyyy").format(sc.getDataOperacao());
    }

    /**
     * quando deve-se voltar um mes para, no caso de o mes vir 0 deve retornar
     * para dezenbro = 12.
     *
     * @param valor
     * @return
     */
    public static int getMesZeroDezembroCorreto(int valor) {
        if (valor == 0) {
            return 12;
        }
        return valor;
    }

    /**
     * A regra abaixo para a contagem de averbação foi solicitada pelo Suporte da DTI (atualmente a Márcia).
     * Para retornar o tempo de averbação foi solicitado a seguinte fórmula:
     * 30 dias equivale a 1 mês
     * 12 meses equivale a 1 ano
     *
     * @param quantidadeDeDias
     * @return
     */
    public static int[] getTempoDeAverbacao(long quantidadeDeDias) {
        int dias = 0;
        int meses = 0;
        int anos = 0;
        for (int i = 1; i <= quantidadeDeDias; i++) {
            dias += 1;
            if (dias == 30) {
                meses += 1;
                dias = 0;
            }
            if (meses == 12) {
                anos += 1;
                meses = 0;
            }
        }
        return new int[] {anos, meses, dias};
    }

    public static DateTime definirUltimaHoraDoDia(DateTime ultimoDiaDoMesDoCalculo) {
        return ultimoDiaDoMesDoCalculo.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
    }
}
