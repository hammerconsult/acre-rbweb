create
or replace procedure proc_transfere_ponto_comercial(p_codigo_lotacao integer,
                                                p_codigo_localizacao_origem integer,
                                                p_codigo_localizacao_destino integer)
    as
begin
update pontocomercial
set localizacao_id = (select l.id
                      from localizacao l
                               inner join lotacaovistoriadora lv on lv.id = l.LOTACAOVISTORIADORA_ID
                      where lv.codigo = p_codigo_lotacao
                        and l.codigo = p_codigo_localizacao_destino)
where id in (select pc.id
             from localizacao l
                      inner join lotacaovistoriadora lv on lv.id = l.LOTACAOVISTORIADORA_ID
                      inner join pontocomercial pc on pc.LOCALIZACAO_ID = l.ID
             where lv.CODIGO = p_codigo_lotacao
               and l.codigo = p_codigo_localizacao_origem);

delete
from localizacao
where id = (select l.id
            from localizacao l
                     inner join lotacaovistoriadora lv on lv.id = l.LOTACAOVISTORIADORA_ID
            where lv.CODIGO = p_codigo_lotacao
              and l.codigo = p_codigo_localizacao_origem);
end;
