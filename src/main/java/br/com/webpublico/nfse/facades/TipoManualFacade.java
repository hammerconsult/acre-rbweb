package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.ManualNfse;
import br.com.webpublico.nfse.domain.TipoManual;
import br.com.webpublico.nfse.domain.dtos.ManualDTO;
import br.com.webpublico.nfse.domain.dtos.TipoManualDTO;
import br.com.webpublico.nfse.domain.dtos.TipoManualManualDTO;
import br.com.webpublico.nfse.exceptions.NfseValidacaoException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

/**
 * Created by william on 24/08/17.
 */
@Stateless
public class TipoManualFacade extends AbstractFacade<TipoManual> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ManualNfseFacade manualNfseFacade;

    public TipoManualFacade() {
        super(TipoManual.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }




    public List<TipoManualManualDTO> buscarTipoManualComManuais() {
        List<TipoManualManualDTO> retorno = Lists.newArrayList();
        Map<TipoManualDTO, List<ManualDTO>> map = Maps.newHashMap();
        System.out.println("buscar manuais");
        List<ManualDTO> manuais = ManualNfse.toListManualNfseDTO(manualNfseFacade.buscarManualParaExibicao());
        if (!manuais.isEmpty()) {
            for (ManualDTO manualDTO : manuais) {
                if (!map.containsKey(manualDTO.getTipoManualDTO())) {
                    map.put(manualDTO.getTipoManualDTO(), new ArrayList());
                }
                map.get(manualDTO.getTipoManualDTO()).add(manualDTO);
            }
        }
        for (TipoManualDTO tipoManualDTO : map.keySet()) {
            TipoManualManualDTO item = new TipoManualManualDTO();
            item.setTipoManualDTO(tipoManualDTO);
            item.setManualDTO(map.get(tipoManualDTO));
            Collections.sort(item.getManualDTO(), new Comparator<ManualDTO>() {
                @Override
                public int compare(ManualDTO o1, ManualDTO o2) {
                    return o1.getOrdem().compareTo(o2.getOrdem());
                }
            });
            retorno.add(item);
        }
        Collections.sort(retorno, new Comparator<TipoManualManualDTO>() {
            @Override
            public int compare(TipoManualManualDTO o1, TipoManualManualDTO o2) {
                return o1.getTipoManualDTO().getOrdem().compareTo(o2.getTipoManualDTO().getOrdem());
            }
        });
        return retorno;
    }

}
