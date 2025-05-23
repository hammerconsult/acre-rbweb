package br.com.webpublico.controle.administrativo;

import br.com.webpublico.entidades.PlanoContratacaoAnualObjetoCompra;
import br.com.webpublico.pncp.enums.OperacaoPncp;
import br.com.webpublico.pncp.enums.TipoEventoPncp;
import br.com.webpublico.pncp.service.PncpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PncpTransactionalHelper {

    @Autowired
    private PncpService pncpService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void enviar(TipoEventoPncp tipoEvento, OperacaoPncp operacao, Long idOrigem) {
        PncpService.getService().enviar(tipoEvento, operacao, idOrigem);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void enviarEmLote(TipoEventoPncp tipoEvento, OperacaoPncp operacao, List<PlanoContratacaoAnualObjetoCompra> itens) {
        PncpService.getService().enviarEmLote(tipoEvento, operacao, itens.stream()
            .map(PlanoContratacaoAnualObjetoCompra::getId)
            .collect(Collectors.toList()));
    }

}
