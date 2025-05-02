update documentooficial documento set documento.modelodoctooficial_id = (
    select
        (select distinct mdo.id from modelodoctooficial mdo
            inner join (select max(vigenciainicial) as inicial, modelo.tipodoctooficial_id
                        from modelodoctooficial modelo
                        group by modelo.tipodoctooficial_id
            )modeloVigente on mdo.vigenciainicial = modeloVigente.inicial
        and mdo.tipodoctooficial_id = modeloVigente.tipodoctooficial_id
        where mdo.tipodoctooficial_id = mdof.tipodoctooficial_id)
    as modeloVigente
    from documentooficial df
    inner join modelodoctooficial mdof on mdof.id = df.modelodoctooficial_id
    where documento.id = df.id
)
