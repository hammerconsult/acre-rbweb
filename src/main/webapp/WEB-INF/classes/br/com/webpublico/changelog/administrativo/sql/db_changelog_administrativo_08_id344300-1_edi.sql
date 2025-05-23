update INVENTARIOBENS invet
set invet.UNIDADEADMINISTRATIVA_ID = (select vw.SUBORDINADA_ID
                                      from INVENTARIOBENS iv
                                               inner join vwhierarquiaadministrativa vw on vw.id = iv.ADMINISTRATIVA_ID
                                      where iv.id = invet.id),
    invet.UNIDADEORCAMENTARIA_ID = (select vw.SUBORDINADA_ID
                                    from INVENTARIOBENS iv
                                             inner join vwhierarquiaorcamentaria vw on vw.id = iv.ORCAMENTARIA_ID
                                    where iv.id = invet.id);
