package br.com.webpublico.util;

import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.Telefone;
import br.com.webpublico.enums.TipoTelefone;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 13/06/14
 * Time: 11:14
 * To change this template use File | Settings | File Templates.
 */
public class ManadUtil {
    public static String getDataSemBarra(Date data) {
        return StringUtil.removeCaracteresEspeciaisSemEspaco(DataUtil.getDataFormatada(data));
    }

    public static String getValor(BigDecimal valor) {
        return valor.toString();
    }

    public static void quebraLinha(StringBuilder retorno) {
        retorno.append("\r\n");
    }

    public static void criaLinha(Integer ordem, String valor, StringBuilder retorno) {
        retorno.append(valor);
        retorno.append("|");
    }

    public static void criaLinhaSemPipe(Integer ordem, String valor, StringBuilder retorno) {
        retorno.append(valor);
    }

    public static EnderecoCorreio getEnderecoPessoa(Pessoa p) {
        if (p.getEnderecoPrincipal() != null) {
            return p.getEnderecoPrincipal();
        }

        if (p.getEnderecoscorreio().size() >= 1) {
            return p.getEnderecoscorreio().get(0);
        }

        throw new ExcecaoNegocioGenerica("Erro ao gerar o Arquivo para a  Pessoa " + p.toString() + ". Não existe endereço configurado. ");
    }

    public static Telefone getFaxPessoa(Pessoa p) {
        for (Telefone telefone : p.getTelefones()) {
            if (telefone.getTipoFone().equals(TipoTelefone.FAX)) {
                return telefone;
            }
        }
        return null;
    }

    public static Telefone getTelefonePessoa(Pessoa p) {
        if (p.getTelefonePrincipal() != null) {
            return p.getTelefonePrincipal();
        }

        if (p.getTelefones().size() >= 1) {
            return p.getTelefones().get(0);
        }

        throw new ExcecaoNegocioGenerica("Erro ao gerar o Arquivo para a  Pessoa " + p.toString() + ". Não existe telefone configurado. ");
    }
}
