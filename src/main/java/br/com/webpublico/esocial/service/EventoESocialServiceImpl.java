package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroESocial;
import br.com.webpublico.enums.rh.esocial.TipoClasseESocial;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import org.springframework.beans.factory.annotation.Autowired;

public class EventoESocialServiceImpl implements EventoESocialService {

    @Autowired
    private RegistroESocialService service;

    @Override
    public RegistroESocial criarRegistro(Long identificador, TipoClasseESocial tipo, TipoArquivoESocial tipoArquivoESocial, SituacaoESocial situacao, ConfiguracaoEmpregadorESocial empregador, String observacao) {
        RegistroESocial registroESocial = new RegistroESocial(identificador, tipo, tipoArquivoESocial, situacao, empregador, observacao);
        return service.save(registroESocial);
    }
}
