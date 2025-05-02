package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroESocial;
import br.com.webpublico.enums.rh.esocial.TipoClasseESocial;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.enums.SituacaoESocial;

public interface EventoESocialService {

    RegistroESocial criarRegistro(Long identificador, TipoClasseESocial tipo, TipoArquivoESocial tipoArquivoESocial, SituacaoESocial situacao, ConfiguracaoEmpregadorESocial empregador, String observacao);
}
