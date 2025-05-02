package br.com.webpublico.nfse.domain.pgdas.util;

import br.com.webpublico.entidadesauxiliares.AssistenteArquivoPGDAS;

import java.util.List;

public interface RegistroLinhaPgdas {

    void criarLinha(AssistenteArquivoPGDAS assistente,
                    List<String> pipes);
}
