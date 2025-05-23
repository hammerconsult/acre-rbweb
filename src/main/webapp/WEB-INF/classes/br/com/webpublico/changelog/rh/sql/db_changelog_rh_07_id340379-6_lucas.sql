update reintegracao r
set EXONERACAORESCISAO_ID = (select max(e.id)
                             from EXONERACAORESCISAO e
                             where VINCULOFP_ID = r.CONTRATOFP_ID
                               and (e.DATARESCISAO < r.DATAREINTEGRACAO or e.DATARESCISAO = r.DATAREINTEGRACAO)
                               and e.DATARESCISAO = (select max(exo.DATARESCISAO)
                                                     from EXONERACAORESCISAO exo
                                                     where VINCULOFP_ID = r.CONTRATOFP_ID
                                                       and (exo.DATARESCISAO < r.DATAREINTEGRACAO or
                                                            exo.DATARESCISAO = r.DATAREINTEGRACAO)))
where r.id in (select REINTEGRACAO.id from REINTEGRACAO where REINTEGRACAO.EXONERACAORESCISAO_ID is null)
