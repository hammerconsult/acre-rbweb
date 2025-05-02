package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.util.Comparator;


public class OrdenaResultadoParcelaPadrao implements Comparator<ResultadoParcela> {
    @Override
    public int compare(ResultadoParcela r1, ResultadoParcela r2) {
        int i = r1.getCadastro() != null && r2.getCadastro() != null ? r1.getCadastro().compareTo(r2.getCadastro()) : 0;
        if (i == 0) {
            i = r1.getDivida().compareTo(r2.getDivida());
        }
        if (i == 0) {
            i = r1.getVencimento().compareTo(r2.getVencimento());
        }
        return i;
    }
}
