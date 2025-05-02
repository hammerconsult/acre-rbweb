/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import java.math.BigDecimal;

/**
 * @author claudio
 */
public class ValorPorExtenso {

    public static String valorPorExtenso(BigDecimal valor) {
        return valorPorExtenso(valor, true);
    }

    public static String valorPorExtenso(BigDecimal valor, boolean real) {
        if (valor.compareTo(BigDecimal.ZERO) == 0) {
            return ("Zero");
        }
        BigDecimal abs = valor.abs();

        long inteiro = (long) abs.longValue(); // parte inteira do valor
        double resto = (valor.subtract(new BigDecimal(inteiro)).doubleValue()); // parte fracionária do valor

        String vlrS = String.valueOf(inteiro);
        if (vlrS.length() > 15) {
            return ("Erro: valor superior a 999 trilhões.");
        }

        String s = "", saux, vlrP;
        String centavos = String.valueOf((int) Math.round(resto * 100));

        String[] unidade = {"", "Um", "Dois", "Três", "Quatro", "Cinco",
                "Seis", "Sete", "Oito", "Nove", "Dez", "Onze",
                "Doze", "Treze", "Quatorze", "Quinze", "Dezesseis",
                "Dezessete", "Dezoito", "Dezenove"};
        String[] centena = {"", "Cento", "Duzentos", "Trezentos",
                "Quatrocentos", "Quinhentos", "Seiscentos",
                "Setecentos", "Oitocentos", "Novecentos"};
        String[] dezena = {"", "", "Vinte", "Trinta", "Quarenta", "Cinquenta",
                "Sessenta", "Setenta", "Oitenta", "Noventa"};
        String[] qualificaS = {"", "Mil", "Milhão", "Bilhão", "Trilhão"};
        String[] qualificaP = {"", "Mil", "Milhões", "Bilhões", "Trilhões"};

        // definindo o extenso da parte inteira do valor
        int n, unid, dez, cent, tam, i = 0;
        boolean umReal = false, tem = false;
        while (!vlrS.equals("0")) {
            tam = vlrS.length();

            // retira do valor a 1a. parte, 2a. parte, por exemplo, para 123456789:
            // 1a. parte = 789 (centena)
            // 2a. parte = 456 (mil)
            // 3a. parte = 123 (milhões)
            if (tam > 3) {
                vlrP = vlrS.substring(tam - 3, tam);
                vlrS = vlrS.substring(0, tam - 3);
            } else { // última parte do valor
                vlrP = vlrS;
                vlrS = "0";
            }
            if (!vlrP.equals("000")) {
                saux = "";
                if (vlrP.equals("100")) {
                    saux = "Cem";
                } else {
                    n = Integer.parseInt(vlrP, 10);  // para n = 371, tem-se:
                    cent = n / 100;                  // cent = 3 (centena trezentos)
                    dez = (n % 100) / 10;            // dez  = 7 (dezena setenta)
                    unid = (n % 100) % 10;           // unid = 1 (unidade um)
                    if (cent != 0) {
                        saux = centena[cent];
                    }
                    if ((n % 100) <= 19) {
                        if (saux.length() != 0 && cent != 0 && dez == 0 && unid == 0) {
                            saux = saux + unidade[n % 100];
                        } else if (saux.length() != 0) {
                            saux = saux + " e " + unidade[n % 100];
                        } else {
                            saux = unidade[n % 100];
                        }
                    } else {
                        if (saux.length() != 0) {
                            saux = saux + " e " + dezena[dez];
                        } else {
                            saux = dezena[dez];
                        }
                        if (unid != 0) {
                            if (saux.length() != 0) {
                                saux = saux + " e " + unidade[unid];
                            } else {
                                saux = unidade[unid];
                            }
                        }
                    }
                }
                if (vlrP.equals("1") || vlrP.equals("001")) {
                    if (i == 0) // 1a. parte do valor (um real)
                    {
                        umReal = true;
                    } else {
                        saux = saux + " " + qualificaS[i];
                    }
                } else if (i != 0) {
                    saux = saux + " " + qualificaP[i];
                }
                if (s.length() != 0) {
                    s = saux + " e " + s;
                } else {
                    s = saux;
                }
            }
            if (((i == 0) || (i == 1)) && s.length() != 0) {
                tem = true; // tem centena ou mil no valor
            }
            i = i + 1; // próximo qualificador: 1- mil, 2- milhão, 3- bilhão, ...
        }

        if (real) {
            if (s.length() != 0) {
                if (umReal) {
                    s = s + " Real";
                } else if (tem) {
                    s = s + " Reais";
                } else {
                    s = s + " de Reais";
                }
            }
        }
        // definindo o extenso dos centavos do valor
        if (!centavos.equals("0")) { // valor com centavos
            if (s.length() != 0) // se não é valor somente com centavos
            {
                s = s + " e ";
            }
            if (centavos.equals("1")) {
                s = s + "Um ";
                if (real) {
                    s = s + "Centavo";
                }
            } else {
                n = Integer.parseInt(centavos, 10);
                if (n <= 19) {
                    s = s + unidade[n];
                } else {

                    // para n = 37, tem-se:
                    unid = n % 10;   // unid = 37 % 10 = 7 (unidade sete)
                    dez = n / 10;    // dez  = 37 / 10 = 3 (dezena trinta)
                    s = s + dezena[dez];
                    if (unid != 0) {
                        s = s + " e " + unidade[unid];
                    }
                }
                if (real) {
                    s = s + " Centavos";
                }
            }
        }
        return (s);
    }
}
