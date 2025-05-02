/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Arthur
 */
public class Perfil implements Serializable {

    private List<String> componentes = new ArrayList<String>();
    private Map<String, Boolean> renderizados = new HashMap<String, Boolean>();
    private Map<String, Boolean> requeridos = new HashMap<String, Boolean>();

    public Perfil() {
        inicializaRenderizados();
        inicializaRequeridos();
    }

    public Boolean ehRenderizado(String componente) {
        Boolean retorno;
        retorno = this.renderizados.get(componente);
        if (retorno == null) {
            return true;
//            throw new RuntimeException("zz");
        }
        return retorno;
    }

    public Boolean ehRequerido(String componente) {
        Boolean retorno;
        retorno = this.requeridos.get(componente);
        if (retorno == null) {
            return false;
//            throw new RuntimeException("zz");
        }
        return retorno;
    }

    private void inicializaRenderizados() {
        for (String comp : componentes) {
            this.renderizados.put(comp, Boolean.TRUE);
        }
    }

    private void inicializaRequeridos() {
        for (String comp : componentes) {
            this.requeridos.put(comp, Boolean.TRUE);
        }
    }

    protected void modificaAtributo(String comp, Boolean renderizado, Boolean requerido) {
        this.renderizados.put(comp, renderizado);
        this.requeridos.put(comp, requerido);
    }

    public Boolean validarPessoa(Pessoa p) throws Exception {
        Boolean retorno = true;
//        if (p.getEnderecos().size() == 0) {
//            retorno = false;
//            throw new Exception("A pessoa deve ter pelo menos um endereço!");
//        }
//        if (!valida_CpfCnpj(String.valueOf(p.getCpf_Cnpj()))) {
//            retorno = false;
//            throw new Exception("O CPF/CNPJ é inválido!");
//        }
        return retorno;
    }

    public boolean valida_CpfCnpj(String s_aux) {
        s_aux = s_aux.replace(".", "");
        s_aux = s_aux.replace("-", "");
        s_aux = s_aux.replace("/", "");
//------- Rotina para CPF
        if (s_aux.length() == 11) {
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
}
