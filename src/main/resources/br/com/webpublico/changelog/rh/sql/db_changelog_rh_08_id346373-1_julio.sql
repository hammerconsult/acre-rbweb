delete from PeriodoAquisitivoFL where basecargo_id IN (
    select bcargo.id from basecargo bcargo
    inner join baseperiodoaquisitivo baseperiodo on bcargo.baseperiodoaquisitivo_id = baseperiodo.id
    where baseperiodo.id = 6190048
)
  AND contratofp_id = 639027995;
