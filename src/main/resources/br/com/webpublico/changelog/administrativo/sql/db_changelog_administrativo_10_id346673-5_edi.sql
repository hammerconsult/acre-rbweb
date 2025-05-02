merge into execucaoprocesso ex
    using (select expro.id                  as id_exec,
                  exata.ataregistropreco_id as id_origem,
                  'ATA_REGISTRO_PRECO'      as origem
           from execucaoprocesso expro
                    inner join execucaoprocessoata exata on exata.execucaoprocesso_id = expro.id) dados
    on (dados.id_exec = ex.id)
    when matched then
        update
            set ex.idorigem = dados.id_origem,
                ex.origem   = dados.origem;


merge into execucaoprocesso ex
    using (select expro.id                             as id_exec,
                  exdisp.dispensalicitacao_id          as id_origem,
                  'DISPENSA_LICITACAO_INEXIGIBILIDADE' as origem
           from execucaoprocesso expro
                    inner join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = expro.id) dados
    on (dados.id_exec = ex.id)
    when matched then
        update
            set ex.idorigem = dados.id_origem,
                ex.origem   = dados.origem;
